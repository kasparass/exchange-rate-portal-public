package lt.baltic.exchangerates.dto.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyListUnmarshallingTest {

    @Test
    public void testUnmarshallCurrencyList() throws Exception {
        // Create JAXB context for our class
        JAXBContext context = JAXBContext.newInstance(CurrencyList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Enable schema validation
        unmarshaller.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());

        // Load test XML file from resources
        try (InputStream is = new ClassPathResource("xml-response-examples/currency-list.xml").getInputStream()) {
            System.out.println("Starting unmarshalling...");
            // Unmarshal XML to object
            CurrencyList currencyList = (CurrencyList) unmarshaller.unmarshal(is);

            // Verify the result
            assertNotNull(currencyList);
            assertNotNull(currencyList.getEntries());
            assertTrue(currencyList.getEntries().length > 0);

            // Test first currency entry
            CurrencyList.CurrencyEntry firstEntry = currencyList.getEntries()[0];
            assertNotNull(firstEntry.getCode());
            assertNotNull(firstEntry.getNumber());
            assertTrue(firstEntry.getNames().length > 0);

            // Print some debug info
            System.out.println("Successfully unmarshalled currency list:");
            System.out.println("Total currencies: " + currencyList.getEntries().length);
            System.out.println("First currency: " + firstEntry.getCode());
            System.out.println("First currency name (EN): " +
                    java.util.Arrays.stream(firstEntry.getNames())
                            .filter(n -> "EN".equalsIgnoreCase(n.getLanguage()))
                            .findFirst()
                            .map(CurrencyList.CurrencyName::getValue)
                            .orElse("Not found"));
        }
    }
}