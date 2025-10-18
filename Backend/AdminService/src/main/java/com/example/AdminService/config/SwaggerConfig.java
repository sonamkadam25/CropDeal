package com.example.AdminService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	//http://localhost:8084/swagger-ui/index.html
	 @Bean
	    public OpenAPI myCustomConfig() {
	        final String securitySchemeName = "bearerAuth";

	        return new OpenAPI()
	            .info(new Info()
	                .title("CropDeal Admin API")
	                .description("By Sonam"))
	            .components(new Components()
	                .addSecuritySchemes(securitySchemeName,
	                    new SecurityScheme()
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme("bearer")
	                        .bearerFormat("JWT")
	                        .in(SecurityScheme.In.HEADER)
	                        .name("Authorization")))
	            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
	    }
}
