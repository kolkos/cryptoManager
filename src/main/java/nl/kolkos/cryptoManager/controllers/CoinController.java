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
import nl.kolkos.cryptoManager.FormOption;
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
		
		List<FormOption> hourOptions = new ArrayList<>();
		FormOption hourOption = new FormOption("1", "Last 1 hour");
		hourOptions.add(hourOption);
		hourOption = new FormOption("2", "Last 2 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("3", "Last 3 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("4", "Last 4 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("5", "Last 5 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("24", "Last 24 Hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("48", "Last 2 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("168", "Last 7 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("336", "Last 14 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("720", "Last 30 days");
		hourOptions.add(hourOption);
		
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		
		// add the minute options
		List<FormOption> minuteOptions = new ArrayList<>();
		FormOption minuteOption = new FormOption("5", "5 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10", "10 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("15", "15 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("30", "30 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("60", "1 hour");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("120", "2 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("300", "5 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("720", "12 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("1440", "1 day");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10080", "1 week");
		minuteOptions.add(minuteOption);
		
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
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
		
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, intervalInMinutes), date = start.getTime()) {

			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar lastMinute = Calendar.getInstance();
			lastMinute.setTime(date);
			lastMinute.add(Calendar.MINUTE, intervalInMinutes);
			lastMinute.add(Calendar.SECOND, -1);

	
			double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), lastMinute.getTime());
			
			
			
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
