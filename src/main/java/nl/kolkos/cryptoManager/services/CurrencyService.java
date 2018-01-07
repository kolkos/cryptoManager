package nl.kolkos.cryptoManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.repositories.CurrencyRepository;

@Service
public class CurrencyService {
	@Autowired
	private CurrencyRepository currencyRepository;
	
	public Currency findByCurrencyISOCode(String currencyISOCode) {
		return currencyRepository.findByCurrencyISOCode(currencyISOCode);
	}
	
	public void save(Currency currency) {
		currencyRepository.save(currency);
	}
}
