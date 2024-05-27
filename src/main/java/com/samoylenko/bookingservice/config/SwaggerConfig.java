package com.samoylenko.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private final Contact contact = new Contact()
            .url("https://t.me/user6658")
            .name("Alexander Samoylenko");

    private final Info info = new Info()
            .title("Booking Service")
            .version("0.1")
            .contact(contact);

    @Bean
    public OpenAPI myOpenApi() {
        return new OpenAPI()
                .info(info);
    }
}
