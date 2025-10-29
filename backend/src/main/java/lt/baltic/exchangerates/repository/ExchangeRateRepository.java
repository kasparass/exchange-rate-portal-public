package lt.baltic.exchangerates.repository;

import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrencyAndDate(Currency currency, LocalDate date);
    
    List<ExchangeRate> findByCurrencyAndDateBetweenOrderByDateAsc(
        Currency currency, LocalDate startDate, LocalDate endDate);

    @Query("SELECT er FROM ExchangeRate er WHERE er.date = " +
           "(SELECT MAX(er2.date) FROM ExchangeRate er2)")
    List<ExchangeRate> findLatestRates();
    
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN false ELSE true END FROM ExchangeRate e")
    boolean isEmpty();
}