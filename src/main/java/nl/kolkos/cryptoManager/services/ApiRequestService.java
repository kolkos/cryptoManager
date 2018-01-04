package nl.kolkos.cryptoManager.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.PortfolioChartLine;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolio;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolioHistory;
import nl.kolkos.cryptoManager.api.objects.ApiWalletHistory;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Service
public class ApiRequestService {
	private final static int MAX_NUMBER_OF_RESULTS = 100;
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	@Autowired
	private CoinService coinService;
	
	public boolean checkApiKeyExists(String apiKey) {
		boolean keyExists = false;
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		if(apiKeyRequester != null) {
			keyExists = true;
		}
		return keyExists;
	}
	 
	public boolean checkPortfolioAccessForApiKey(String apiKey, long portfolioId) {
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		Portfolio portfolio = portfolioService.findById(portfolioId);
		
		boolean access = false;
		
		if(portfolio.getApiKeys().contains(apiKeyRequester)) {
			access = true;
		}
		
		return access;
	}
	
	public boolean checkWalletAccessForApiKey(String apiKey, long walletId) {
		boolean access = false;
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		// get the portfolio id for this wallet
		long portfolioId = wallet.getPortfolio().getId();
		// now use the checkPortfolioAccessForApiKey method
		access = this.checkPortfolioAccessForApiKey(apiKey, portfolioId);
		
		return access;
	}
	
	public Set<Portfolio> getPortfoliosForKey(String apiKey){
		return portfolioService.findByApiKeys_apiKey(apiKey);
	}
	
	public List<Wallet> findByPortfolioApiKeysApiKey(String apiKey){
		// get the wallets for the api key
		List<Wallet> wallets = walletService.findByPortfolioApiKeysApiKey(apiKey);
		
		// now get the values for the wallets
		for(Wallet wallet : wallets) {
			wallet = walletService.getWalletValues(wallet);
		}
		
		
		return wallets;
	}
	
	public ApiPortfolio getPortfolioById(long portfolioId) {
		// get the normal Portfolio object
		Portfolio portfolio = portfolioService.findById(portfolioId);
		
		// now add it's elements to the ApiPortfolio object
		ApiPortfolio apiPortfolio = new ApiPortfolio();
		apiPortfolio.setId(portfolio.getId());
		apiPortfolio.setName(portfolio.getName());
		apiPortfolio.setDescription(portfolio.getDescription());
		
		// now get the values from the wallets
		double currentTotalPortfolioValue = 0;
		double currentTotalPortfolioInvestment = 0;
		// get the wallets by this portfolio
		List<Wallet> wallets = walletService.getWalletsByPortfolio(portfolio);
		// loop through the wallets
		for(Wallet wallet : wallets) {
			wallet = walletService.getWalletValues(wallet);
			// now get the value and add it to the total
			currentTotalPortfolioValue += wallet.getCurrentWalletValue();
			currentTotalPortfolioInvestment += wallet.getCurrentWalletInvestment();
		}
		
		double profitLoss = currentTotalPortfolioValue - currentTotalPortfolioInvestment;
		double roi = profitLoss / currentTotalPortfolioInvestment;
		
		apiPortfolio.setWallets(wallets);
		
		apiPortfolio.setCurrentTotalPortfolioInvestment(currentTotalPortfolioInvestment);
		apiPortfolio.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
		apiPortfolio.setCurrentTotalPortfolioProfitLoss(profitLoss);
		apiPortfolio.setCurrentTotalPortfolioROI(roi);
		
		
		return apiPortfolio;
	}
	
	public Wallet getWalletById(long walletId) {
		Wallet wallet = walletService.findById(walletId);
		
		// add additional information
		wallet = walletService.getWalletValues(wallet);
		
		return wallet;
	}
	
	public Iterable<Coin> getCoins(){
		// first get all the registerd coins
		Iterable<Coin> coins = coinService.findAllCoins();
		
		// current time, used for the database query
		Calendar now = Calendar.getInstance();
		
		// loop through the coins and get the last known value
		for(Coin coin : coins) {
			double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), now.getTime());
			coin.setCurrentCoinValue(lastKnownValue);
		}
		
		return coins;
	}
	
	public LinkedHashMap<String, String> updateCoins(){
		
		LinkedHashMap<String, String> result = new LinkedHashMap<>();
		
		// get the coins
		Iterable<Coin> coins = coinService.findAllCoins();
		
		// loop through coins
		for(Coin coin : coins) {
			// actually update the coin
			
			
			result.put(coin.getCoinMarketCapCoin().getSymbol(), coinService.updateCoinValues(coin));
		}
		
		return result;
	}
	
	public Coin getSingleCoin(long coinId) {
		Coin coin = coinService.findById(coinId);
		Calendar now = Calendar.getInstance();
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), now.getTime());
		coin.setCurrentCoinValue(lastKnownValue);
		return coin;
	}
	
	
	
	public int translateTimeStringToMinutes(String timeString) {
		
		Pattern pattern = Pattern.compile("(^\\d{1,2})([MHDWYmhdwy]{1}$)");
		Matcher matcher = pattern.matcher(timeString);
		
		int minutes = 0;
		
		if (matcher.find()) {
			// first get the multiplier (the number of the chosen identifier)
			int multiplier = Integer.parseInt(matcher.group(1));
			
			// now get the number of minutes for the identifier (hours, minutes, etc)
			String identifier = matcher.group(2);
			
			switch (identifier) {
				case "m":
					minutes = multiplier;
					break;
				case "h":
					minutes = multiplier * 60;
					break;
				case "d":
					minutes = (multiplier * 60) * 24;
					break;
				case "w":
					minutes = ((multiplier * 60) * 24) * 7;
					break;
				case "y":
					minutes = ((multiplier * 60) * 24) * 365;
					break;
	
				default:
					break;
			}
			
		}
		
		return minutes;
	}
	
	public boolean checkIfNumberOfResultsIsAllowed(int period, int interval) {
		boolean allowed = true;
		// calculate the number of results
		int numberOfResults = period / interval;
		if(numberOfResults > MAX_NUMBER_OF_RESULTS) {
			allowed = false;
		}
		
		return allowed;
	}
	
	public Iterable<ApiPortfolioHistory> getPortfolioHistory(long portfolioId, int period, int interval){
		// now determine the begin and end time
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, -period);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, 0);
		
		List<ApiPortfolioHistory> apiPortfolioHistoryList = new ArrayList<>();
		
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, interval), date = start.getTime()) {
			
			
			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar endInterval = Calendar.getInstance();
			endInterval.setTime(date);
			endInterval.add(Calendar.MINUTE, interval);
			endInterval.add(Calendar.SECOND, -1);
			
			// create the ApiPortfolioHistory object
			ApiPortfolioHistory apiPortfolioHistory = new ApiPortfolioHistory();
			apiPortfolioHistory.setDate(date);
			
			
			// now get the values from the wallets
			double currentTotalPortfolioValue = 0;
			double currentTotalPortfolioInvestment = 0;
			// get the wallets by this portfolio
			List<Wallet> wallets = walletService.findByPortfolio_Id(portfolioId);
			List<Wallet> historicalWallets = new ArrayList<>();
			// loop through the wallets
			for(Wallet wallet : wallets) {
				// create a copy of this wallet
				Wallet historicalWallet = new Wallet(wallet);
				historicalWallet = walletService.getWalletHistoricalValues(historicalWallet, startInterval.getTime(), endInterval.getTime());
				// now get the value and add it to the total
				currentTotalPortfolioValue += historicalWallet.getCurrentWalletValue();
				currentTotalPortfolioInvestment += historicalWallet.getCurrentWalletInvestment();
				
				// create a copy of the coin
				Coin historicalCoin = new Coin(historicalWallet.getCoin());
				historicalWallet.setCoin(historicalCoin);
				historicalWallets.add(historicalWallet);
				
			}
			
			double profitLoss = currentTotalPortfolioValue - currentTotalPortfolioInvestment;
			double roi = profitLoss / currentTotalPortfolioInvestment;
			
			apiPortfolioHistory.setWallets(historicalWallets);
			
			apiPortfolioHistory.setCurrentTotalPortfolioInvestment(currentTotalPortfolioInvestment);
			apiPortfolioHistory.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
			apiPortfolioHistory.setCurrentTotalPortfolioProfitLoss(profitLoss);
			apiPortfolioHistory.setCurrentTotalPortfolioROI(roi);
			
			apiPortfolioHistoryList.add(apiPortfolioHistory);
		}
		
		return apiPortfolioHistoryList;
	}
	
	public Iterable<ApiWalletHistory> getWalletHistory(long walletId, int period, int interval){
		// now determine the begin and end time
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, -period);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, 0);
		
		List<ApiWalletHistory> apiWalletHistoryList = new ArrayList<>();
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, interval), date = start.getTime()) {
			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar endInterval = Calendar.getInstance();
			endInterval.setTime(date);
			endInterval.add(Calendar.MINUTE, interval);
			endInterval.add(Calendar.SECOND, -1);
			
			// get the historical values for this wallet
			wallet = walletService.getWalletHistoricalValues(wallet, startInterval.getTime(), endInterval.getTime());
			
			// create a copy of the wallet
			Wallet historicalWallet = new Wallet(wallet);
			
			
			// create the ApiWalletHistory object
			ApiWalletHistory apiWalletHistory = new ApiWalletHistory();
			apiWalletHistory.setDate(date);
			// add the copy of the wallet
			apiWalletHistory.setWallet(historicalWallet);
			
			Coin historicalCoin = new Coin(wallet.getCoin());
			historicalWallet.setCoin(historicalCoin);
			
			
			apiWalletHistoryList.add(apiWalletHistory);
		}
		
		return apiWalletHistoryList;
	}
	
	public Coin getCoinBySymbol(String symbol) {
		return coinService.findByCoinMarketCapCoinSymbol(symbol);
	}
}
