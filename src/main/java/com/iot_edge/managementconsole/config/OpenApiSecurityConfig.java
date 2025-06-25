package com.iot_edge.managementconsole.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "cookieAuth",                      // name must match what's used in @SecurityRequirement
        type = SecuritySchemeType.APIKEY,         // use APIKEY for cookies
        in = SecuritySchemeIn.COOKIE,             // use COOKIE instead of HEADER
        paramName = "AUTH_TOKEN"
)
public class OpenApiSecurityConfig {
}
