package accounts

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    
    @Bean
    fun accountsApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Accounts Service API")
                    .description("RESTful API for managing bank accounts in a microservices architecture. " +
                                "This service is discoverable via Eureka and consumed by other microservices.")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("David Borrel Seral")
                            .email("871643@unizar.es")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0")
                    )
            )
    }
}
