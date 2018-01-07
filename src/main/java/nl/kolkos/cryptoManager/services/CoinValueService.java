package nl.kolkos.cryptoManager.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;

@Service
public class CoinValueService {
	@Resource(name = "currency")
	private Currency currency;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	/**
	 * Just add a currency to the values with currency_id = null
	 */
	@Transactional
	public void coinValueMaintenanceMissingCurrency() {
		List<CoinValue> coinValues = coinValueRepository.findByCurrencyIsNull();
		// loop through the coin values
		for(CoinValue coinValue : coinValues) {
			String logLine = String.format("%s: Adding missing currency '%s' to coin value of '%s' for coin '%s'.", new Date(), currency.getCurrencyISOCode(), coinValue.getRequestDate(), coinValue.getCoin().getCoinMarketCapCoin().getSymbol());
			System.out.println(logLine);
			
			coinValue.setCurrency(currency);
			// save it
			coinValueRepository.save(coinValue);
		}
	}
	
	@Transactional
	public void coinValueMaintenanceChangedCurrency() {
		
		HashMap<String, Double> currencyValues = new HashMap<>();
		
		ApiRequestHandler apiHandler = new ApiRequestHandler();
		
		// get all the coin values where the currency is not the current currency
		List<CoinValue> coinValues = coinValueRepository.findByCurrencyNot(currency);
		for(CoinValue coinValue : coinValues) {
			// get the registered currency
			String registeredCurrency = coinValue.getCurrency().getCurrencyISOCode();
						
			// check if the registered currency is already in the hash
			// if not, register the value
			boolean runConversion = true;
			if(!currencyValues.containsKey(registeredCurrency)) {
				// does not exist, get the transfer rate
				try {
					JSONObject jsonObject = apiHandler.requestCurrencyConversion(registeredCurrency, currency.getCurrencyISOCode());
					
					// get the value
					double rate = jsonObject.getJSONObject("rates").getDouble(currency.getCurrencyISOCode());
					
					System.out.println(String.format("Using conversion rate (%s-%s): %f", registeredCurrency, currency.getCurrencyISOCode(), rate));
					
					// register this rate
					currencyValues.put(registeredCurrency, rate);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runConversion = false;
				}
			}
			if(runConversion) {
				String logLine = String.format("%s: Change currency from '%s' to '%s' for coin value of '%s' for coin '%s'.",
						new Date(), 
						registeredCurrency,
						currency.getCurrencyISOCode(), 
						coinValue.getRequestDate(), 
						coinValue.getCoin().getCoinMarketCapCoin().getSymbol());
				System.out.println(logLine);
				
				// calculate the new coin value
				double newCoinValue = coinValue.getValue() * currencyValues.get(registeredCurrency);
				BigDecimal bd = new BigDecimal(newCoinValue);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				double roundedTransactionValue = bd.doubleValue();
				
				logLine = String.format("%s: %s-%s: %f * %f = %f", 
						new Date(),
						registeredCurrency,
						currency.getCurrencyISOCode(), 
						coinValue.getValue(),
						currencyValues.get(registeredCurrency),
						roundedTransactionValue);
				
				System.out.println(logLine);
				
				// save the new values
				coinValue.setCurrency(currency);
				coinValue.setValue(roundedTransactionValue);
				coinValueRepository.save(coinValue);
			}
			
			
			
			
		}
		
	}
}
