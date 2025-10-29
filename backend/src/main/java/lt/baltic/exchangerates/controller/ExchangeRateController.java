package lt.baltic.exchangerates.controller;

import lombok.RequiredArgsConstructor;
import lt.baltic.exchangerates.dto.ExchangeRateDto;
import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.model.ExchangeRate;

import lt.baltic.exchangerates.service.ExchangeRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.xml.bind.JAXBException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rates")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @GetMapping("/current")
    public List<ExchangeRateDto> getCurrentRates() throws ResponseStatusException {
        try {
            return exchangeRateService.getCurrentRates().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (JAXBException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching exchange rates", e);
        }
    }

    @GetMapping("/{currencyCode}/{fromDate}/{toDate}")
    public List<ExchangeRateDto> getHistoricalRates(
            @PathVariable String currencyCode,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return exchangeRateService.getHistoricalRates(currencyCode, fromDate, toDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ExchangeRateDto mapToDto(ExchangeRate exchangeRate) {
        Currency currency = exchangeRate.getCurrency();
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setCurrencyCode(currency.getCode());
        dto.setCurrencyName(currency.getNameEn());
        dto.setRate(exchangeRate.getRate());
        dto.setDate(exchangeRate.getDate());
        return dto;
    }
}