package com.servicepro.shared.infrastructure.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class HtmlTemplateRenderer {

    private static final String TEMPLATE_BASE_PATH = "mail-templates/";
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    public String render(String templateName, Map<String, String> values) {
        String template = templateCache.computeIfAbsent(templateName, this::loadTemplate);
        String rendered = template;

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value = entry.getValue() == null ? "" : escapeHtml(entry.getValue());
            rendered = rendered.replace("{{" + entry.getKey() + "}}", value);
        }

        return rendered;
    }

    private String loadTemplate(String templateName) {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_BASE_PATH + templateName + ".html");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao carregar template de email: " + templateName, exception);
        }
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
