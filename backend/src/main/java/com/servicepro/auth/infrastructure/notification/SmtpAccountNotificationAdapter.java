package com.servicepro.auth.infrastructure.notification;

import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.config.AuthLinksProperties;
import com.servicepro.shared.infrastructure.mail.HtmlTemplateRenderer;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public class SmtpAccountNotificationAdapter implements AccountNotificationGateway {

    private static final DateTimeFormatter EXPIRATION_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm 'UTC'");

    private final JavaMailSender mailSender;
    private final HtmlTemplateRenderer htmlTemplateRenderer;
    private final AuthLinksProperties authLinksProperties;
    private final String fromAddress;

    @Async("mailTaskExecutor")
    @Override
    public void sendWelcomeEmail(User user) {
        try {
            String htmlBody = htmlTemplateRenderer.render(
                    "signup-welcome",
                    Map.of(
                            "userName", user.getName(),
                            "userEmail", user.getEmail(),
                            "userRole", formatRole(user.getRole()),
                            "loginUrl", authLinksProperties.getLoginUrl(),
                            "year", String.valueOf(OffsetDateTime.now(ZoneOffset.UTC).getYear())
                    )
            );
            sendHtml(user.getEmail(), "Bem-vindo ao ServicePro", htmlBody);
        } catch (RuntimeException exception) {
            log.error("Falha ao enviar email de boas-vindas para {}", user.getEmail(), exception);
        }
    }

    @Async("mailTaskExecutor")
    @Override
    public void sendPasswordResetEmail(User user, String resetLink, OffsetDateTime expiresAt) {
        try {
            String htmlBody = htmlTemplateRenderer.render(
                    "password-reset",
                    Map.of(
                            "userName", user.getName(),
                            "resetLink", resetLink,
                            "expiresAt", EXPIRATION_FORMATTER.format(expiresAt.withOffsetSameInstant(ZoneOffset.UTC)),
                            "year", String.valueOf(OffsetDateTime.now(ZoneOffset.UTC).getYear())
                    )
            );
            sendHtml(user.getEmail(), "Redefinicao de senha - ServicePro", htmlBody);
        } catch (RuntimeException exception) {
            log.error("Falha ao enviar email de redefinicao de senha para {}", user.getEmail(), exception);
        }
    }

    private void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao enviar email para " + to, exception);
        }
    }

    private String formatRole(Role role) {
        return switch (role) {
            case CLIENT -> "Cliente";
            case PROVIDER -> "Prestador";
            case ADMIN -> "Administrador";
            case SUPPORT -> "Suporte";
            case FINANCE -> "Financeiro";
        };
    }
}
