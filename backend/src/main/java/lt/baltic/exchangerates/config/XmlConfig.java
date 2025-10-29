package lt.baltic.exchangerates.config;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lt.baltic.exchangerates.dto.xml.CurrencyList;
import lt.baltic.exchangerates.dto.xml.ExchangeRateList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XmlConfig {
    @Bean
    public JAXBContext currencyListJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(CurrencyList.class);
    }

    @Bean
    public JAXBContext exchangeRateListJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(ExchangeRateList.class);
    }
}