package com.team13.mapstory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private final String AUTH_TOKEN_HEADER = "Authorization";

    private Info info() {
        return new Info()
                .title("MapStory API")
                .description("MapStory API 테스트")
                .version("1.0.0");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .addSecurityItem(new SecurityRequirement().addList(AUTH_TOKEN_HEADER))
                .components(new Components()
                        .addSecuritySchemes(AUTH_TOKEN_HEADER, new SecurityScheme()
                                .name(AUTH_TOKEN_HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer"))
                );
    }
}