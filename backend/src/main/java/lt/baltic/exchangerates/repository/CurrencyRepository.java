package lt.baltic.exchangerates.repository;

import lt.baltic.exchangerates.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
    // Basic CRUD operations are provided by JpaRepository
}