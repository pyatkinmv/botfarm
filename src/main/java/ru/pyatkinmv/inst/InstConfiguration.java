package ru.pyatkinmv.inst;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class InstConfiguration {
    @Component
    @Data
    @ConfigurationProperties(prefix = "instagram")
    static class InstagramProperties {
        private String username;
        private String password;
    }
}
