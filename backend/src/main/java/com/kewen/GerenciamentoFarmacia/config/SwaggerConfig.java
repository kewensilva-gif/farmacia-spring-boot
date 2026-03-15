package com.kewen.GerenciamentoFarmacia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI farmaciaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gerenciamento Farmácia")
                        .description("Documentação da API de gerenciamento da farmácia")
                        .version("v1")
                        .contact(new Contact().name("Equipe Farmácia"))
                        .license(new License().name("Uso interno")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
