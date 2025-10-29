package lt.baltic.exchangerates.service;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lt.baltic.exchangerates.client.BankOfLithuaniaClient;
import lt.baltic.exchangerates.dto.xml.ExchangeRateList;
import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.model.ExchangeRate;
import lt.baltic.exchangerates.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyService currencyService;
    private final BankOfLithuaniaClient bankClient;

    @Transactional
    public void updateCurrentRates() throws JAXBException {
        ExchangeRateList rateList = bankClient.getCurrentRates();
        saveRates(rateList);
    }

    @Transactional
    public void updateHistoricalRates(String currencyCode, LocalDate fromDate, LocalDate toDate)
            throws JAXBException {
        ExchangeRateList rateList = bankClient.getRatesForCurrency(currencyCode, fromDate, toDate);
        saveRates(rateList);
    }

    private void saveRates(ExchangeRateList rateList) {
        if (rateList == null || rateList.getRates() == null) {
            return;
        }

        Arrays.stream(rateList.getRates())
                .forEach(rate -> {
                    Map<String, BigDecimal> amounts = Arrays.stream(rate.getAmounts())
                            .collect(Collectors.toMap(
                                    ExchangeRateList.CurrencyAmount::getCurrency,
                                    ExchangeRateList.CurrencyAmount::getAmount));

                    // Skip if EUR is not present (base currency)
                    if (!amounts.containsKey("EUR")) {
                        return;
                    }
                    BigDecimal eurAmount = amounts.get("EUR");

                    amounts.forEach((code, amount) -> {
                        if (!"EUR".equals(code)) {
                            Currency currency = currencyService.getCurrencyByCode(code)
                                    .orElseThrow(() -> new RuntimeException("Currency not found: " + code));

                            ExchangeRate exchangeRate = exchangeRateRepository
                                    .findByCurrencyAndDate(currency, rate.getDate())
                                    .orElse(new ExchangeRate());

                            exchangeRate.setCurrency(currency);
                            exchangeRate.setDate(rate.getDate());
                            exchangeRate.setRate(amount.divide(eurAmount, java.math.RoundingMode.HALF_UP));

                            exchangeRateRepository.save(exchangeRate);
                        }
                    });
                });
    }

    public List<ExchangeRate> getCurrentRates() throws JAXBException {
        List<ExchangeRate> rates = exchangeRateRepository.findLatestRates();
        if (!rates.isEmpty()) {
            return rates;
        }

        updateCurrentRates();
        return exchangeRateRepository.findLatestRates();
    }

    public List<ExchangeRate> getHistoricalRates(String currencyCode, LocalDate fromDate, LocalDate toDate) {
        Currency currency = currencyService.getCurrencyByCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + currencyCode));

        return exchangeRateRepository.findByCurrencyAndDateBetweenOrderByDateAsc(
                currency, fromDate, toDate);
    }
}