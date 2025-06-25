package com.iot_edge.managementconsole.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "web")
public class WebConfigProperties {

    private Cors cors;

    public static class Cors {
        private String[] allowedOrigins;
        private String[] allowedMethods;
        private long maxAge;
        private String[] allowedHeaders;
        private String[] exposedHeaders;

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public String[] getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String[] allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(long maxAge) {
            this.maxAge = maxAge;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String[] allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public String[] getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(String[] exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }
    }
}
