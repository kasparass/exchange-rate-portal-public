package lt.baltic.exchangerates.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "bank.api")
@Data
public class BankApiProperties {
    private String baseUrl;
    private String type;
    private int connectTimeout;
    private int readTimeout;
}