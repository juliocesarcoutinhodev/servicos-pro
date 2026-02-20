package com.servicepro.shared.infrastructure.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {

    private boolean enabled = false;
    private String from = "no-reply@servicepro.app";
    private String host = "";
    private int port = 587;
    private String username = "";
    private String password = "";
    private Smtp smtp = new Smtp();

    @Getter
    @Setter
    public static class Smtp {
        private boolean auth = true;
        private boolean starttlsEnable = true;
        private boolean sslEnable = false;
        private int connectionTimeout = 5000;
        private int timeout = 5000;
        private int writeTimeout = 5000;
        private boolean debug = false;
    }
}
