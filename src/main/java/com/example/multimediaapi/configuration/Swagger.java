package com.example.multimediaapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class Swagger {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.multimediaapi"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "multimedia API",
                "The Multimedia API provides a robust and flexible interface for managing and interacting with multimedia content. With this API, you can upload media files, create and manage playlists, form user groups, and perform various other operations related to multimedia content.",
                "v1",
                "Terms Of Service",
                new Contact("Helpidio Mateus", "", "helpidiom@gmail.com"),
                "License", "", Collections.emptyList());
    }
}
