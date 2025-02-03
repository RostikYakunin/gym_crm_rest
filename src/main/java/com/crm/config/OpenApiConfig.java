package com.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.crm", "org.springdoc"})
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi init() {
        return GroupedOpenApi.builder()
                .group("all")
                .packagesToScan("com.crm")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI ApiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("GYM CRM API")
                                .description("Api documentation from managing Gym Crm`s clients")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("CRM Support")
                                                .email("support@crm.com")
                                                .url("https://crm.com")
                                )
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                                )
                );
    }
}