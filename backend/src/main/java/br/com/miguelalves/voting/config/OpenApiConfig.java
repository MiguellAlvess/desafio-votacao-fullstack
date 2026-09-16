package br.com.miguelalves.voting.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Voting API",
        description = "API REST para gerenciamento de associados, pautas, sessões de votação e votos.",
        version = "v1"))
public class OpenApiConfig {
}
