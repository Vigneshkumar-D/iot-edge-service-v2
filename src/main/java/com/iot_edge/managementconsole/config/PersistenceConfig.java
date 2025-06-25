package com.iot_edge.managementconsole.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {
        @NotNull
        @Override
        public Optional<String> getCurrentAuditor() {
            if (SecurityContextHolder.getContext() == null) {
                return Optional.of("SYSTEM");
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                return Optional.of("SYSTEM");
            }
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }
}
