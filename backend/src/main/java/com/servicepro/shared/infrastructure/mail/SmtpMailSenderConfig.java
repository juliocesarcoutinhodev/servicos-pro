package com.servicepro.shared.infrastructure.mail;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

@Configuration
@RequiredArgsConstructor
public class SmtpMailSenderConfig {

    private final AppMailProperties appMailProperties;

    @Bean
    @ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        validateRequired(appMailProperties.getHost(), "APP_MAIL_HOST");
        validateRequired(appMailProperties.getUsername(), "APP_MAIL_USERNAME");
        validateRequired(appMailProperties.getPassword(), "APP_MAIL_PASSWORD");

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(appMailProperties.getHost());
        sender.setPort(appMailProperties.getPort());
        sender.setUsername(appMailProperties.getUsername());
        sender.setPassword(appMailProperties.getPassword());

        AppMailProperties.Smtp smtp = appMailProperties.getSmtp();
        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.smtp.auth", String.valueOf(smtp.isAuth()));
        properties.put("mail.smtp.starttls.enable", String.valueOf(smtp.isStarttlsEnable()));
        properties.put("mail.smtp.ssl.enable", String.valueOf(smtp.isSslEnable()));
        properties.put("mail.smtp.connectiontimeout", String.valueOf(smtp.getConnectionTimeout()));
        properties.put("mail.smtp.timeout", String.valueOf(smtp.getTimeout()));
        properties.put("mail.smtp.writetimeout", String.valueOf(smtp.getWriteTimeout()));
        properties.put("mail.debug", String.valueOf(smtp.isDebug()));

        return sender;
    }

    private static void validateRequired(String value, String envName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(
                    envName + " e obrigatoria quando APP_MAIL_ENABLED=true"
            );
        }
    }
}
