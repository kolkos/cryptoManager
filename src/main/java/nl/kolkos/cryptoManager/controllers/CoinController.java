package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import nl.kolkos.cryptoManager.FormOptions;
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
	
	
	@GetMapping("/results")
    public String coinResults(
    		@RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
    		@RequestParam(name = "direction", defaultValue = "ASC") String direction,
    		Model model) {
		
		List<Coin> coinList = coinRepository.findAllByOrderByCoinMarketCapCoinSymbol();
		
		model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
		
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		
		// loop through list
		for(Coin coin : coinList) {
			double currentCoinValue;
			try {
				org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(coin.getCoinMarketCapCoin().getId(), "EUR");
				currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				currentCoinValue = 0;
			}
			
			// register this value
			CoinValue coinValue = new CoinValue();
			coinValue.setCoin(coin);
			coinValue.setValue(currentCoinValue);
			coinValueRepository.save(coinValue);
			
			// set the value into the coin object
			coin.setCurrentCoinValue(currentCoinValue);
			
			// now get the following values from the database
			// - average last hour
			// - average last 24 hours
			// - average last 7 days
			
			// now determine the begin and end time
			Calendar now = Calendar.getInstance();
			
			Calendar nowMinus1hour = Calendar.getInstance();
			nowMinus1hour.add(Calendar.HOUR_OF_DAY, -1);
			
			Calendar nowMinus24hours = Calendar.getInstance();
			nowMinus24hours.add(Calendar.HOUR_OF_DAY, -24);
			
			Calendar nowMinus1week = Calendar.getInstance();
			nowMinus1week.add(Calendar.DAY_OF_WEEK, -7);
			
			// get the average values for the periods
			double avgValueLast1Hour = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coin.getId(), nowMinus1hour.getTime(), now.getTime());
			double avgValueLast24Hours = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coin.getId(), nowMinus24hours.getTime(), now.getTime());
			double avgValueLast1Week = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coin.getId(), nowMinus1week.getTime(), now.getTime());
			
			// now calculate the percentages
			
			double winLoss1h = ((currentCoinValue - avgValueLast1Hour)*100)/currentCoinValue;
			double winLoss1d = ((currentCoinValue - avgValueLast24Hours)*100)/currentCoinValue;
			double winLoss1w = ((currentCoinValue - avgValueLast1Week)*100)/currentCoinValue;
			
			// finally set these values in the object
			coin.setWinLoss1h(winLoss1h);
			coin.setWinLoss1d(winLoss1d);
			coin.setWinLoss1w(winLoss1w);
			
		}
		
		// now sort the coins
		switch (sortBy) {
			case "name":
				coinList = this.sortByCoinName(coinList, direction);
				break;
			case "symbol":
				coinList = this.sortByCoinSymbol(coinList, direction);
				break;
			case "currentValue":
				coinList = this.sortByCurrentCoinValue(coinList, direction);
				break;
			case "winLoss1h":
				coinList = this.sortByWinLoss1h(coinList, direction);
				break;
			case "winLoss1d":
				coinList = this.sortByWinLoss1d(coinList, direction);
				break;
			case "winLoss1w":
				coinList = this.sortByWinLoss1w(coinList, direction);
				break;
			default:
				coinList = this.sortByCoinName(coinList, "ASC");
				break;
		}
		
		
		//model.addAttribute("coin", new Coin());
		model.addAttribute("coinList", coinList);
		
        return "coin_results";
    }
	
	public List<Coin> sortByCoinName(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			return coinList.stream()
					.sorted((coin1, coin2) -> coin1.getCoinMarketCapCoin().getName().compareTo(coin2.getCoinMarketCapCoin().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return coinList.stream()
					.sorted((coin1, coin2) -> coin2.getCoinMarketCapCoin().getName().compareTo(coin1.getCoinMarketCapCoin().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Coin> sortByCoinSymbol(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			return coinList.stream()
					.sorted((coin1, coin2) -> coin1.getCoinMarketCapCoin().getSymbol().compareTo(coin2.getCoinMarketCapCoin().getSymbol()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return coinList.stream()
					.sorted((coin1, coin2) -> coin2.getCoinMarketCapCoin().getSymbol().compareTo(coin1.getCoinMarketCapCoin().getSymbol()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Coin> sortByCurrentCoinValue(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			coinList.sort(Comparator.comparingDouble(Coin::getCurrentCoinValue));
		}else {
			coinList.sort(Comparator.comparingDouble(Coin::getCurrentCoinValue).reversed());
		}
		return coinList;
	}
	
	public List<Coin> sortByWinLoss1h(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1h));
		}else {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1h).reversed());
		}
		return coinList;
	}
	
	public List<Coin> sortByWinLoss1d(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1d));
		}else {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1d).reversed());
		}
		return coinList;
	}
	
	public List<Coin> sortByWinLoss1w(List<Coin> coinList, String direction){
		if(direction.equals("ASC")) {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1w));
		}else {
			coinList.sort(Comparator.comparingDouble(Coin::getWinLoss1w).reversed());
		}
		return coinList;
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
		
		
		FormOptions formOptions = new FormOptions();
		
		
		List<FormOption> hourOptions = formOptions.defaultSetHourOptions();
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		
		// add the minute options
		List<FormOption> minuteOptions = formOptions.defaultSetMinuteOptions();
		
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		// get the name of the coin
		Coin coin = coinRepository.findById(coinId);
		CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
		model.addAttribute("coinName",cmcCoin.getName());
		
		// update the coin price
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
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		
		
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
