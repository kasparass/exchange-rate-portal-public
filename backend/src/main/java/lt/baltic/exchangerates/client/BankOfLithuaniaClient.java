package lt.baltic.exchangerates.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lt.baltic.exchangerates.config.BankApiProperties;
import lt.baltic.exchangerates.dto.xml.CurrencyList;
import lt.baltic.exchangerates.dto.xml.ExchangeRateList;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import java.io.StringReader;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BankOfLithuaniaClient {
    private final BankApiProperties properties;
    private final RestTemplate restTemplate;
    private final JAXBContext currencyListJaxbContext;
    private final JAXBContext exchangeRateListJaxbContext;

    public CurrencyList getCurrencyList() throws JAXBException {
        String response = callApi("/getCurrencyList", null);
        return (CurrencyList) currencyListJaxbContext.createUnmarshaller()
            .unmarshal(new StringReader(response));
    }

    public ExchangeRateList getCurrentRates() throws JAXBException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("tp", properties.getType());
        
        String response = callApi("/getCurrentFxRates", params);
        return (ExchangeRateList) exchangeRateListJaxbContext.createUnmarshaller()
            .unmarshal(new StringReader(response));
    }

    public ExchangeRateList getRatesForCurrency(String currencyCode, LocalDate fromDate, LocalDate toDate) 
            throws JAXBException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("tp", properties.getType());
        params.add("ccy", currencyCode);
        params.add("dtFrom", fromDate.toString());
        params.add("dtTo", toDate.toString());
        
        String response = callApi("/getFxRatesForCurrency", params);
        return (ExchangeRateList) exchangeRateListJaxbContext.createUnmarshaller()
            .unmarshal(new StringReader(response));
    }

    private String callApi(String endpoint, MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        return restTemplate.postForObject(
            properties.getBaseUrl() + endpoint,
            request,
            String.class
        );
    }
}