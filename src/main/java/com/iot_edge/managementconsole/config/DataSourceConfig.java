//package com.iot_edge.managementconsole.config;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.persistence.EntityManagerFactory;
//import liquibase.integration.spring.SpringLiquibase;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//
//@Configuration
//@EnableTransactionManagement
//@Slf4j
//public class DataSourceConfig {
//
//    @Value("${spring.profiles.active}")
//    private String activeEnvironment;
//
//    @Autowired
//    private ResourceLoader resourceLoader;
//
//    //    PRIMARY DATASOURCE PROPERTIES
//    @Value("${spring.datasource.primary.jdbc-url}")
//    private String primaryJdbcUrl;
//    @Value("${spring.datasource.primary.username}")
//    private String primaryUsername;
//    @Value("${spring.datasource.primary.password}")
//    private String primaryPassword;
//
//    @Primary
//    @Bean
//    public DataSource primaryDataSource() throws IOException {
//        return getDataSource(primaryJdbcUrl, primaryUsername, primaryPassword);
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.primary.liquibase")
//    public LiquibaseProperties primaryLiquibaseProperties() {
//        return new LiquibaseProperties();
//    }
//
//    @Bean
//    public SpringLiquibase primaryLiquibase() throws IOException {
//        return springLiquibase(primaryDataSource(), primaryLiquibaseProperties());
//    }
//
//    private static SpringLiquibase springLiquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
//        SpringLiquibase liquibase = new SpringLiquibase();
//        liquibase.setDataSource(dataSource);
//        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
//        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
//        liquibase.setContexts(liquibaseProperties.getContexts());
//        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
//        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
//        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
//        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
//        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
//        liquibase.setShouldRun(liquibaseProperties.isEnabled());
//        return liquibase;
//    }
//
//    @Primary
//    @Bean(name = "entityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("primaryDataSource") DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.baseel.lms.five_million")
//                .persistenceUnit("primary")
//                .build();
//    }
//
//    @Bean(name = "primaryTransactionManager")
//    @Primary
//    public PlatformTransactionManager primaryTransactionManager(
//            @Qualifier("entityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
//        return new JpaTransactionManager(primaryEntityManagerFactory);
//    }
//
//    private DataSource getDataSource(String secondaryJdbcUrl, String secondaryUsername, String secondaryPassword) throws IOException {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(secondaryJdbcUrl);
//        config.setUsername(secondaryUsername);
//        config.setPassword(secondaryPassword);
//        config.setDriverClassName("org.postgresql.Driver");
//
//        if (activeEnvironment.equals("dev")) {
//            configureSslCertificates(config, "dev");
//        } else if (activeEnvironment.equals("uat")) {
//            configureSslCertificates(config, "uat");
//        } else if (activeEnvironment.equals("prod")) {
//            configureSslCertificates(config, "prod");
//        } else {
//            log.info("Configuring SSL CERT for LOCAL...");
//            // If needed, set up local SSL configuration or skip entirely
//        }
//
//        return new HikariDataSource(config);
//    }
//
//    private void configureSslCertificates(HikariConfig config, String environment) throws IOException {
//        log.info("Configuring SSL CERT for {}...", environment.toUpperCase());
//
//        // Load certificates from classpath
//        Resource rootCertResource =
//                resourceLoader.getResource("classpath:certs/" + environment + "/root.crt");
//        Resource clientCertResource =
//                resourceLoader.getResource("classpath:certs/" + environment + "/client.crt");
//        Resource clientKeyResource =
//                resourceLoader.getResource("classpath:certs/" + environment + "/client_pkcs8_der.key");
//
//        // Create temporary files
//        Path tempRootCert = Files.createTempFile("root", ".crt");
//        Path tempClientCert = Files.createTempFile("client", ".crt");
//        Path tempClientKey = Files.createTempFile("client_pkcs8", ".der");
//
//        // Copy resources to the temporary files
//        try (InputStream rootCertStream = rootCertResource.getInputStream();
//             InputStream clientCertStream = clientCertResource.getInputStream();
//             InputStream clientKeyStream = clientKeyResource.getInputStream()) {
//
//            Files.copy(rootCertStream, tempRootCert, StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(clientCertStream, tempClientCert, StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(clientKeyStream, tempClientKey, StandardCopyOption.REPLACE_EXISTING);
//        }
//
//        // Set SSL properties
//        config.addDataSourceProperty("sslmode", "verify-full");
//        config.addDataSourceProperty("sslrootcert", tempRootCert.toAbsolutePath().toString());
//        config.addDataSourceProperty("sslcert", tempClientCert.toAbsolutePath().toString());
//        config.addDataSourceProperty("sslkey", tempClientKey.toAbsolutePath().toString());
//    }
//
//    @EnableJpaRepositories(
//            basePackages = "com.baseel.lms.five_million.repository",
//            entityManagerFactoryRef = "entityManagerFactory",
//            transactionManagerRef = "primaryTransactionManager"
//    )
//    public class PrimaryDatabaseConfig {
//    }
//
//}