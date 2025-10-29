package lt.baltic.exchangerates.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, BankApiProperties properties) {
        return builder
            .setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
            .setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
            .build();
    }
}