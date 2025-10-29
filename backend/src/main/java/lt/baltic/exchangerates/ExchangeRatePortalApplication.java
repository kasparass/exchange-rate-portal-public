package lt.baltic.exchangerates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExchangeRatePortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatePortalApplication.class, args);
    }
}