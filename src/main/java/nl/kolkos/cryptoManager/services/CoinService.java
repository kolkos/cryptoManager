package nl.kolkos.cryptoManager.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public void save(Coin coin) {
		coinRepository.save(coin);
	}
	
	public List<Coin> findAllByOrderByCoinMarketCapCoinSymbol(){
		return coinRepository.findAllByOrderByCoinMarketCapCoinSymbol();
	}
	
	public Iterable<Coin> findAllCoins(){
		return coinRepository.findAll();
	}
	
	public Coin findById(long coinId) {
		Coin coin = coinRepository.findById(coinId);
		
		// get the values for this coin
		coin = this.getCoinValues(coin);
		
		return coin;
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
	
	public void updateCoinValues() {
		// get the coins
		Iterable<Coin> coinList = coinRepository.findAll();
		// loop
		for(Coin coin : coinList) {
			this.updateCoinValues(coin);
		}
	}
	
	public Coin getCoinValues(Coin coin) {
		// dates for calculation
		Calendar now = Calendar.getInstance();
		
		Calendar nowMinus1hour = Calendar.getInstance();
		nowMinus1hour.add(Calendar.HOUR_OF_DAY, -1);
		
		Calendar nowMinus24hours = Calendar.getInstance();
		nowMinus24hours.add(Calendar.HOUR_OF_DAY, -24);
		
		Calendar nowMinus1week = Calendar.getInstance();
		nowMinus1week.add(Calendar.DAY_OF_WEEK, -7);
		
		// get the last known coin value
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), now.getTime());
		
		// get the values for last hour, last day, last week
		double valueLast1Hour = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), nowMinus1hour.getTime());
		double valueLast24Hours = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), nowMinus24hours.getTime());
		double valueLast1Week = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), nowMinus1week.getTime());
		
		// now calculate the differnce in percentage
		double winLoss1h = ((lastKnownValue - valueLast1Hour)*100)/lastKnownValue;
		double winLoss1d = ((lastKnownValue - valueLast24Hours)*100)/lastKnownValue;
		double winLoss1w = ((lastKnownValue - valueLast1Week)*100)/lastKnownValue;
		
		// add it to the coin
		coin.setCurrentCoinValue(lastKnownValue);
		coin.setWinLoss1h(winLoss1h);
		coin.setWinLoss1d(winLoss1d);
		coin.setWinLoss1w(winLoss1w);
		
		return coin;
	}
	
	public List<Coin> listAllCoins(String sortBy, String direction){
		Iterable<Coin> coinList = coinRepository.findAll();
		List<Coin> coins = new ArrayList<>();
		
		// convert to list
		for(Coin coin : coinList) {
			// get the coin values
			coin = this.getCoinValues(coin);
			
			// add the coin to the list
			coins.add(coin);
		}
		
		// now sort the coins
		switch (sortBy) {
			case "name":
				coins = this.sortByCoinName(coins, direction);
				break;
			case "symbol":
				coins = this.sortByCoinSymbol(coins, direction);
				break;
			case "currentValue":
				coins = this.sortByCurrentCoinValue(coins, direction);
				break;
			case "winLoss1h":
				coins = this.sortByWinLoss1h(coins, direction);
				break;
			case "winLoss1d":
				coins = this.sortByWinLoss1d(coins, direction);
				break;
			case "winLoss1w":
				coins = this.sortByWinLoss1w(coins, direction);
				break;
			default:
				coins = this.sortByCoinName(coins, "ASC");
				break;
		}
		
		return coins;
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
	
}
