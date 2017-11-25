package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.repositories.CoinMarketCapCoinRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/cmc") // This means URL's start with /cmc (after Application path)
public class CoinMarketCapCoinController {
	@Autowired
	@Qualifier(value = "coinMarketCapCoinRepository")
	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	
	// send the form
	@GetMapping("/sync")
    public String coinForm(Model model) {
		
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		
		List<CoinMarketCapCoin> cmcCoins = new ArrayList<>();
		
		try {
			// get the data from the API as a JSON array
			JSONArray jsonArray = apiRequestHandler.getAllCMCCoins();
			
			// loop through the array items
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				// get the id
				String id = jsonObject.getString("id");
				
				// get the name
				String name = jsonObject.getString("name");
				
				// get the symbol
				String symbol = jsonObject.getString("symbol");
				
				// create the coin
				CoinMarketCapCoin cmcCoin = new CoinMarketCapCoin();
				cmcCoin.setId(id);
				cmcCoin.setName(name);
				cmcCoin.setSymbol(symbol);
				cmcCoin.setSynced(false);
				
				// check if this id already exist
				if(coinMarketCapCoinRepository.findById(id) == null) {
					// set synced to true
					cmcCoin.setSynced(true);
					
					// coin does not exist, save this coin
					coinMarketCapCoinRepository.save(cmcCoin);
				}
				
				// add the coin to the list (for the details)
				cmcCoins.add(cmcCoin);
				
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// add the coins to the model
		model.addAttribute("cmcCoins", cmcCoins);

        return "cmc_sync";
    }
}
