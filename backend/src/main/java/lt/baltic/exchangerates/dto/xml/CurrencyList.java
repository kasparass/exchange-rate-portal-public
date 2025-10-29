package lt.baltic.exchangerates.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "CcyTbl")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CurrencyList {
    @XmlElement(name = "CcyNtry")
    private CurrencyEntry[] entries;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CurrencyEntry {
        @XmlElement(name = "Ccy")
        private String code;

        @XmlElement(name = "CcyNm")
        private CurrencyName[] names;

        @XmlElement(name = "CcyNbr")
        private String number;

        @XmlElement(name = "CcyMnrUnts")
        private int decimalPlaces;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CurrencyName {
        @XmlAttribute(name = "lang")
        private String language;

        @XmlValue
        private String value;
    }
}