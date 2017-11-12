package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinMarketCapCoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/coin") // This means URL's start with /demo (after Application path)
public class CoinController {
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	@Autowired
	@Qualifier(value = "coinMarketCapCoinRepository")
	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	
	@GetMapping("/")
    public String forwardRepositoryList(Model model) {
        return "redirect:/portfolio/list";
    }
	
	// send the form
	@GetMapping("/add")
    public String coinForm(Model model) {
		
		// find all the cmc coins
		model.addAttribute("coin", new Coin());
		model.addAttribute("cmcCoinList", coinMarketCapCoinRepository.findAllByOrderBySymbolAsc());
        return "coin_form";
    }
	
	// handle the form
	@PostMapping(path="/add") // Map ONLY Post Requests
	public String addNewCoin (
			@RequestParam CoinMarketCapCoin coinMarketCapCoin,
			Model model) {
				
		Coin coin = new Coin();
		coin.setCoinMarketCapCoin(coinMarketCapCoin);
		
		coinRepository.save(coin);
				
		
		return "redirect:/coin/results";
		
	}
	
	// list all 
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Coin> getAllCoins() {
		// This returns a JSON or XML with the users
		
		return coinRepository.findAll();
	}
	
	@GetMapping("/results")
    public String coinResults(Model model) {
		//model.addAttribute("coin", new Coin());
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
		
        return "coin_results";
    }
	
	// get coin details
	@RequestMapping(value = "/showCoin/{coinId}", method = RequestMethod.GET)
	public String getWalletsByPortfolioId(@PathVariable("coinId") long coinId, Model model) {
		Coin coin = coinRepository.findById(coinId);
		
		// get the cmc coin
		CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
		
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		double currentCoinValue;
		try {
			org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(cmcCoin.getId(), "EUR");
			currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentCoinValue = 0;
		}
		
		// register this result
		CoinValue coinValue = new CoinValue();
		coinValue.setCoin(coin);
		coinValue.setValue(currentCoinValue);
		
		coinValueRepository.save(coinValue);
		
		
		model.addAttribute("coin", coin);
		model.addAttribute("coinId", coinId);
		model.addAttribute("currentCoinValue", currentCoinValue);
		model.addAttribute("coinValues", coinValueRepository.findTop10ByCoinOrderByRequestDateDesc(coin));
		
		
		return "coin_details";
	}
	
	@RequestMapping(value = "/chart/{coinId}", method = RequestMethod.GET)
    public String coinChart(
    		@PathVariable("coinId") long coinId,
    		@RequestParam(value="lastHours", required=false) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=false) Integer intervalInMinutes,
    		Model model) {
        
		// check if the values are null
		if(lastHours == null) {
			lastHours = 1;
		}
		if(intervalInMinutes == null) {
			intervalInMinutes = 5;
		}
		
		// get the name of the coin
		Coin coin = coinRepository.findById(coinId);
		CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
		model.addAttribute("coinName",cmcCoin.getName());
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		//end.add(Calendar.HOUR, 1);
		end.set(Calendar.SECOND, 0);
		
		
		List<CoinValue> avgCoinValues = new ArrayList<>();
		
		double lastKnownValue = 0;
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, intervalInMinutes), date = start.getTime()) {

			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar lastMinute = Calendar.getInstance();
			lastMinute.setTime(date);
			lastMinute.add(Calendar.MINUTE, intervalInMinutes);
			lastMinute.add(Calendar.SECOND, -1);

			//System.out.println("  lastMinute    =>" + lastMinute.getTime());
			
			
			// now get the value
			//List<CoinValue> coinValues = coinValueRepository.findByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), lastMinute.getTime());
			
			//List<CoinValue> coinValues = coinValueRepository.findByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), lastMinute.getTime());
			
			double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), lastMinute.getTime());
			
			System.out.println(avgValue);
			
			if(avgValue == 0) {
				avgValue = lastKnownValue;
			}else {
				lastKnownValue = avgValue;
			}
			
			// add this to a CoinValue object
			CoinValue avgCoinValue = new CoinValue();
			avgCoinValue.setRequestDate(startInterval.getTime());
			avgCoinValue.setValue(avgValue);
			
			// and add it to the list
			avgCoinValues.add(avgCoinValue);
			
		}
		
		model.addAttribute("coinValues", avgCoinValues);
		
        return "coin_chart";
    }
	
}
