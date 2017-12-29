package nl.kolkos.cryptoManager.services;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;

@Service
public class CoinService {
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	public Coin findById(Long id) {
		return coinRepository.findById(id);
	}
	
	public List<Coin> findAllByOrderByCoinMarketCapCoinSymbol(){
		return coinRepository.findAllByOrderByCoinMarketCapCoinSymbol();
	}
	
	public Iterable<Coin> findAllCoins(){
		return coinRepository.findAll();
	}
	
	public Coin findByCoinMarketCapCoinSymbol(String coinMarketCapSymbol) {
		Coin coin = coinRepository.findByCoinMarketCapCoinSymbol(coinMarketCapSymbol);
		if(coin == null) {
			return null;
		}
		
		Calendar now = Calendar.getInstance();
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), now.getTime());
		coin.setCurrentCoinValue(lastKnownValue);
		
		return coin;
	}
	
	public String updateCoinValues(Coin coin) {
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		String status = "OK";
		
		double currentCoinValue;
		try {
			org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(coin.getCoinMarketCapCoin().getId(), "EUR");
			currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentCoinValue = 0;
			status = "ERROR";
		}
		
		// register this value
		CoinValue coinValue = new CoinValue();
		coinValue.setCoin(coin);
		coinValue.setValue(currentCoinValue);
		coinValueRepository.save(coinValue);
		
		return status;
		
	}
	
}
