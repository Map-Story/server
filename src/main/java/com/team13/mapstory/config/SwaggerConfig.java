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

    private final String AUTH_TOKEN_COOKIE = "Authorization";
    private final String REFRESH_TOKEN_COOKIE = "RefreshToken";

    private Info info() {
        return new Info()
                .title("MapStory API")
                .description("MapStory API 테스트<br> 쿠키 값을 기반으로 유저 정보를 파악하여 처리 [Swagger API에서는 편의를 위해 쿠키 정보를 입력하는 것을 제거함]")
                .version("1.0.0");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .addSecurityItem(new SecurityRequirement().addList(AUTH_TOKEN_COOKIE).addList(REFRESH_TOKEN_COOKIE))
                .components(new Components()
                        .addSecuritySchemes(AUTH_TOKEN_COOKIE, new SecurityScheme()
                                .name(AUTH_TOKEN_COOKIE)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name(AUTH_TOKEN_COOKIE)
                                .description("JWT Token in Authorization cookie"))
                        .addSecuritySchemes(REFRESH_TOKEN_COOKIE, new SecurityScheme()
                                .name(REFRESH_TOKEN_COOKIE)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name(REFRESH_TOKEN_COOKIE)
                                .description("Refresh Token in refreshToken cookie"))
                );
    }
}