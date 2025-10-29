package lt.baltic.exchangerates.service;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lt.baltic.exchangerates.client.BankOfLithuaniaClient;
import lt.baltic.exchangerates.dto.xml.CurrencyList;
import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final BankOfLithuaniaClient bankClient;

    @Transactional
    public void updateCurrencyList() throws JAXBException {
        CurrencyList currencyList = bankClient.getCurrencyList();
        
        Arrays.stream(currencyList.getEntries())
            .map(this::mapToCurrency)
            .forEach(currencyRepository::save);
    }

    private Currency mapToCurrency(CurrencyList.CurrencyEntry entry) {
        Currency currency = new Currency();
        currency.setCode(entry.getCode());
        currency.setNumber(entry.getNumber());
        currency.setDecimalPlaces(entry.getDecimalPlaces());

        // Map names based on language
        Arrays.stream(entry.getNames()).forEach(name -> {
            if ("EN".equalsIgnoreCase(name.getLanguage())) {
                currency.setNameEn(name.getValue());
            } else if ("LT".equalsIgnoreCase(name.getLanguage())) {
                currency.setNameLt(name.getValue());
            }
        });

        return currency;
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepository.findById(code);
    }
}