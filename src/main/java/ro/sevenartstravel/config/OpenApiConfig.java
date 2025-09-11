package ro.sevenartstravel.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Seven Arts Travel API",
                version = "1.0",
                description = "REST API documentation for managing artists, artworks, movements, and user messages."
        )
)
public class OpenApiConfig {

}

