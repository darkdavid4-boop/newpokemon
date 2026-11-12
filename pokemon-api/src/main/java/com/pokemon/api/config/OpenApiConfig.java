package com.pokemon.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pokémon REST API")
                        .version("1.0.0")
                        .description("API RESTful completa para la gestión de Pokédex y Pokémon desarrollada con Spring Boot 3 y Java 21.")
                        .contact(new Contact()
                                .name("Pokémon API Team")
                                .url("https://github.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
