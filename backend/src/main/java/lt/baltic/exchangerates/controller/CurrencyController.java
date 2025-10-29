package lt.baltic.exchangerates.controller;

import lombok.RequiredArgsConstructor;
import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.service.CurrencyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }
}