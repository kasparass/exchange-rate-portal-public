package lt.baltic.exchangerates.dto.xml;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@XmlRootElement(name = "FxRates")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ExchangeRateList {
    @XmlElement(name = "FxRate")
    private ExchangeRateEntry[] rates;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ExchangeRateEntry {
        @XmlElement(name = "Tp")
        private String type;

        @XmlElement(name = "Dt")
        @XmlJavaTypeAdapter(LocalDateAdapter.class)
        private LocalDate date;

        @XmlElement(name = "CcyAmt")
        private CurrencyAmount[] amounts;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CurrencyAmount {
        @XmlElement(name = "Ccy")
        private String currency;

        @XmlElement(name = "Amt")
        private BigDecimal amount;
    }
}