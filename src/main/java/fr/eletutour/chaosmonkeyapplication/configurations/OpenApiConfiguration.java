package fr.eletutour.chaosmonkeyapplication.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Value("${application.title}")
    private String title;

    @Value("${application.description}")
    private String description;

    @Value("${application.version}")
    private String version;

    @Value("${application.blog.url}")
    private String blogUrl;

    @Value("${application.license.name}")
    private String licenseName;

    @Value("${application.license.url}")
    private String licenseUrl;

    @Value("${application.terms.url}")
    private String termsUrl;

    @Value("${application.server.url}")
    private String serverUrl;

    @Value("${application.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url(serverUrl)
                        .description(serverDescription))
                .specVersion(SpecVersion.V31)
                .tags(List.of(new Tag().name("ChaosMonkey").description("ChaosMonkey tag"), new Tag().name("Chaos Engineering").description("Chaos Engineering tag")))
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name("Erwan Le Tutour")
                                .url(blogUrl))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name(licenseName)
                                .url(licenseUrl))
                        .termsOfService(termsUrl));
    }
}
