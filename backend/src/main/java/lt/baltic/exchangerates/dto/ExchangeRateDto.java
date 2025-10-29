package lt.baltic.exchangerates.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExchangeRateDto {
    private String currencyCode;
    private String currencyName;
    private BigDecimal rate;
    private LocalDate date;
}