package nl.kolkos.cryptoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;

@Component
public class ScheduledTasks {
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	
	@Scheduled(fixedRate = 300_000)
    public void reportCurrentTime() {
        // get all the coins from the database
		Iterable<Coin> coins = coinRepository.findAll();
		
		// create the API Request object
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		
		// loop through the coins
		for(Coin coin : coins) {
			// get the CMC coin for this coin
			CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
			
			
			double currentCoinValue = 0;
			// get the current coin value from API
			try {
				org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(cmcCoin.getId(), "EUR");
				currentCoinValue = Double.parseDouble((String) json.get("price_eur"));				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// now save this coin value
			CoinValue coinValue = new CoinValue();
			coinValue.setCoin(coin);
			coinValue.setValue(currentCoinValue);
			
			// save to db
			coinValueRepository.save(coinValue);
			
		}
    }
}
