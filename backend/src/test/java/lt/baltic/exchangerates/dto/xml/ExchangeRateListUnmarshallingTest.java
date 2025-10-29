package lt.baltic.exchangerates.dto.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateListUnmarshallingTest {

    @Test
    public void testUnmarshallCurrencyRates() throws Exception {
        // Create JAXB context for our class
        JAXBContext context = JAXBContext.newInstance(ExchangeRateList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Enable schema validation
        unmarshaller.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());

        // Load test XML file from resources
        try (InputStream is = new ClassPathResource("xml-response-examples/eur-usd-last-days.xml").getInputStream()) {
            // Unmarshal XML to object
            ExchangeRateList rateList = (ExchangeRateList) unmarshaller.unmarshal(is);

            // Verify the result
            assertNotNull(rateList);
            assertNotNull(rateList.getRates());
            assertTrue(rateList.getRates().length > 0);

            // Test first rate entry
            ExchangeRateList.ExchangeRateEntry firstEntry = rateList.getRates()[0];
            assertNotNull(firstEntry.getType());
            assertEquals("EU", firstEntry.getType());
            assertNotNull(firstEntry.getDate());
            assertEquals(LocalDate.parse("2025-10-22"), firstEntry.getDate());

            // Test currency amounts
            assertNotNull(firstEntry.getAmounts());
            assertEquals(2, firstEntry.getAmounts().length);

            // Verify EUR amount (base currency)
            ExchangeRateList.CurrencyAmount eurAmount = firstEntry.getAmounts()[0];
            assertEquals("EUR", eurAmount.getCurrency());
            assertEquals(new BigDecimal("1"), eurAmount.getAmount());

            // Verify USD amount
            ExchangeRateList.CurrencyAmount usdAmount = firstEntry.getAmounts()[1];
            assertEquals("USD", usdAmount.getCurrency());
            assertEquals(new BigDecimal("1.1587"), usdAmount.getAmount());

            // Calculate and verify the exchange rate
            BigDecimal rate = usdAmount.getAmount().divide(eurAmount.getAmount(), java.math.RoundingMode.HALF_UP);
            assertEquals(new BigDecimal("1.1587"), rate);
        }
    }
}