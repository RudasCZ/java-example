package com.homework.morosystems.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSpecConfig {

    @Bean
    public GroupedOpenApi externalOpenApi() {
        return GroupedOpenApi.builder()
                .group("APP REST API")
                .addOpenApiCustomizer(openApi -> {
                    OpenAPI parsed = new OpenAPIV3Parser()
                            .read("src/main/resources/apispec/app-openapi3.yaml");
                    openApi.info(parsed.getInfo());
                    openApi.paths(parsed.getPaths());
                    openApi.components(parsed.getComponents());
                })
                .build();
    }
}
