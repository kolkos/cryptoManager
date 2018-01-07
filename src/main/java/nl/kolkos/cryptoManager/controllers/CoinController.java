package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
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
import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.FormOption;
import nl.kolkos.cryptoManager.FormOptions;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.configuration.CustomPropertiesConfiguration;
import nl.kolkos.cryptoManager.repositories.CoinMarketCapCoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.services.CoinService;
import nl.kolkos.cryptoManager.services.CurrencyService;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/coin") // This means URL's start with /demo (after Application path)
public class CoinController {
	@Autowired
	private CoinService coinService;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	@Autowired
	@Qualifier(value = "coinMarketCapCoinRepository")
	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	

	@Resource(name = "currency")
	private Currency currency;
	
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
		
		coinService.save(coin);
		return "redirect:/coin/results";
	}
	
	// send the form
	@GetMapping("/update")
    public String updateCoinValues(Model model) {
		// update coin values
		coinService.updateCoinValues();
		
		return "redirect:/coin/results";
    }
	
	
	@GetMapping("/results")
    public String coinResults(
    		@RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
    		@RequestParam(name = "direction", defaultValue = "ASC") String direction,
    		Model model) {
		
		model.addAttribute("currency", currency);
		model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
		
        List<Coin> coinList = coinService.listAllCoins(sortBy, direction);
		
		model.addAttribute("coinList", coinList);
		
        return "coin_results";
    }
	
	
	// get coin details
	@RequestMapping(value = "/details/{coinId}", method = RequestMethod.GET)
	public String getPortfolioDetailsForId(@PathVariable("coinId") long coinId, Model model) {
		Coin coin = coinService.findById(coinId);
		
		model.addAttribute("currency", currency);
		model.addAttribute("coin", coin);
		
		
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
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		
		FormOptions formOptions = new FormOptions();
				
		List<FormOption> hourOptions = formOptions.defaultSetHourOptions();
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
				
		// add the minute options
		List<FormOption> minuteOptions = formOptions.defaultSetMinuteOptions();
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		Coin coin = coinService.findById(coinId);
		model.addAttribute("coin",coin);
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		//end.add(Calendar.HOUR, 1);
		end.set(Calendar.SECOND, 0);
		
		// get the last known value, just to be sure
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coinId, start.getTime());
		
		
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
			// if the value is 0 then use the last known value
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
