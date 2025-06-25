//package com.iot_edge.managementconsole.config;
//
//import io.minio.MinioClient;
//import io.minio.errors.MinioException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MinioConfig {
//
//    @Value("${cloud.minio.url}")
//    private String minioUrl;
//
//    @Value("${cloud.minio.accessKey}")
//    private String accessKey;
//
//    @Value("${cloud.minio.secretKey}")
//    private String secretKey;
//
//    @Value("${cloud.minio.region}")
//    private String region;
//
//    @Bean
//    public MinioClient minioClient() throws MinioException {
//        try {
//            // Ensure that you're using the correct protocol (HTTPS) and the correct MinIO API endpoint
//            return MinioClient.builder()
//                    .endpoint(minioUrl)  // Use the external MinIO URL
//                    .credentials(accessKey, secretKey)
//                    .build();
//        } catch (Exception e) {
//            // Catch other exceptions
//            throw new RuntimeException("Unknown error", e);
//        }
//    }
//}
