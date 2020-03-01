package org.trenkmann.restsample.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author andreas trenkmann
 */
@Configuration
public class OpenAPIConfig {

  @Bean
  public GroupedOpenApi mp3OpenApi() {
    String[] paths = {"/mp3/**", "/mp3s/**"};
    String[] packagedToMatch = {"org.trenkmann.restsample"};
    return GroupedOpenApi.builder()
        .setGroup("mp3")
        .pathsToMatch(paths)
        .packagesToScan(packagedToMatch)
        .build();
  }

  @Bean
  public GroupedOpenApi orderOpenApi() {
    String[] paths = {"/order/**", "/orders/**"};
    return GroupedOpenApi.builder()
        .setGroup("order")
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public GroupedOpenApi cartOpenApi() {
    String[] paths = {"/cart/**", "/carts/**", "/plainCart/**"};
    return GroupedOpenApi.builder()
        .setGroup("cart")
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components().addSecuritySchemes("basicScheme",
            new SecurityScheme().type(Type.HTTP).scheme("basic")))
        .info(new Info().title("MP3-Order API").description(
            "This is a sample MP3 Order service. <br> You find the description as yml-file <a href=/v3/api-docs.yaml>here</a> ")
            .termsOfService("http://swagger.io/terms/")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }

}
