package bf.kvill.spring_phone_book.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI phoneBookOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Phone Book API")
                        .description("API REST pour la gestion de contacts avec authentification JWT")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MIAGE L3 DevOps")
                                .email("contact@phonebook.local"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Entrez le token JWT obtenu via /api/auth/login (format: Bearer <token>)")));
    }
}
