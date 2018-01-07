package nl.kolkos.cryptoManager.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import nl.kolkos.cryptoManager.Currency;


@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Long> {
	Currency findByCurrencyISOCode(String currencyISOCode);
}
