package com.servicepro.auth.infrastructure.notification;

import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.infrastructure.config.AuthLinksProperties;
import com.servicepro.shared.infrastructure.mail.AppMailProperties;
import com.servicepro.shared.infrastructure.mail.HtmlTemplateRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class AccountNotificationGatewayConfig {

    @Bean
    public AccountNotificationGateway accountNotificationGateway(
            AppMailProperties appMailProperties,
            ObjectProvider<JavaMailSender> javaMailSenderProvider,
            HtmlTemplateRenderer htmlTemplateRenderer,
            AuthLinksProperties authLinksProperties
    ) {
        if (!appMailProperties.isEnabled()) {
            log.info("Email desabilitado (app.mail.enabled=false).");
            return new NoOpAccountNotificationAdapter();
        }

        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            throw new IllegalStateException(
                    "APP_MAIL_ENABLED=true, mas JavaMailSender nao foi inicializado. " +
                            "Verifique APP_MAIL_HOST, APP_MAIL_PORT, APP_MAIL_USERNAME e APP_MAIL_PASSWORD."
            );
        }

        if (!StringUtils.hasText(appMailProperties.getFrom())) {
            throw new IllegalStateException("APP_MAIL_FROM e obrigatoria quando APP_MAIL_ENABLED=true");
        }

        log.info("Email habilitado com provider SMTP.");
        return new SmtpAccountNotificationAdapter(
                javaMailSender,
                htmlTemplateRenderer,
                authLinksProperties,
                appMailProperties.getFrom()
        );
    }
}
