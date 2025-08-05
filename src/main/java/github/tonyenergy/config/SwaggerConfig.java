package github.tonyenergy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger config
 *
 * @Author: Tony
 * @Date: 2025/5/5
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("OCPP API")
                        .version("1.0")
                        .description("OCPP API documentation.\n\nThis is the backend API for managing charging stations using OCPP protocol.")
                        .contact(new Contact()
                                .name("Tony Liu")
                                .email("lthbb584@gmail.com")
                                .url("https://tonyenergy.github.io"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("OCPP")
                .packagesToScan("github.tonyenergy.controller")
                .build();
    }
}
