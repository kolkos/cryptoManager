package nl.kolkos.cryptoManager.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolio;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolioHistory;
import nl.kolkos.cryptoManager.api.objects.ApiWalletHistory;
import nl.kolkos.cryptoManager.services.ApiRequestService;


@Controller
@RequestMapping(path="/api/request") 
public class ApiRequestController {
	@Autowired
	private ApiRequestService apiRequestService;


	@ExceptionHandler(IllegalArgumentException.class)
	void handleBadRequests(HttpServletResponse response, String message) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value(), message);
	}
	
	@GetMapping(path="/test")
	public @ResponseBody Iterable<String> testApiAccess() {
		List<String> testje = new ArrayList<>();
		
		testje.add("Regel 1");
		testje.add("Regel 2");
		testje.add("Regel 3");
		
		return testje; 
	}
	
	@GetMapping(path="/help")
	public @ResponseBody HashMap<String, String> apiHelp() {
		LinkedHashMap<String, String> helpMap = new LinkedHashMap<>();
		
		helpMap.put("/api/request/help", "This help text");
		helpMap.put("/api/request/{API Key}/test", "Test API key existence");
		helpMap.put("/api/request/{API Key}/portfolio", "Get all portfolio's this API key has access to");
		helpMap.put("/api/request/{API Key}/portfolio/{Portfolio ID}", "Get detailed information for the chosen portfolio (by ID)");
		helpMap.put("/api/request/{API Key}/portfolio/{Portfolio ID}/history/{period}/{interval}", "Get historical data for the chosen portfolio. Period and interval require the following format: 1m, 2h, 3d, 4w, etc.");
		helpMap.put("/api/request/{API Key}/wallet", "Get all wallets this API key has access to");
		helpMap.put("/api/request/{API Key}/wallet/{Wallet ID}", "Get detailed information for the chosen wallet (by ID)");
		helpMap.put("/api/request/{API Key}/wallet/{Wallet ID}/history/{period}/{interval}", "Get historical data for the chosen wallet. Period and interval require the following format: 1m, 2h, 3d, 4w, etc.");
		helpMap.put("/api/request/{API Key}/coin", "Get the current value for all the registered coins");
		helpMap.put("/api/request/{API Key}/coin/id/{Coin ID}", "Get the current value of the selected coin (by ID)");
		helpMap.put("/api/request/{API Key}/coin/symbol/{Coin Symbol}", "Get the current value of the selected coin (by Symbol)");
		helpMap.put("/api/request/{API Key}/coin/update", "Request an update of the coin values (coin values are automatically updated every 5 minutes)");

		
		return helpMap;
	}
	
	@GetMapping(path="/{apiKey}/test")
	public @ResponseBody HashMap<String, String> testApiKey(@PathVariable("apiKey") String apiKey) {
		LinkedHashMap<String, String> reply = new LinkedHashMap<>();
		
		// check if api key exists
		if(apiRequestService.checkApiKeyExists(apiKey)) {
			reply.put("status", "ok");
		}else {
			reply.put("status", "error");
			reply.put("reason", "unknown API key");
		}
		
		return reply;
	}
	
	@GetMapping(path="/{apiKey}/coin/update")
	public @ResponseBody HashMap<String, String> updateCoinValues(@PathVariable("apiKey") String apiKey) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		return apiRequestService.updateCoins();
	}
	
	@GetMapping(path="/{apiKey}/portfolio")
	public @ResponseBody Iterable<Portfolio> getPortfolios(@PathVariable("apiKey") String apiKey) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		Iterable<Portfolio> result = apiRequestService.getPortfoliosForKey(apiKey);
		
		return result;
	}
	
	@GetMapping(path="/{apiKey}/portfolio/{portfolioId}")
	public @ResponseBody ApiPortfolio getPortfolioDetails(@PathVariable("apiKey") String apiKey,
			@PathVariable("portfolioId") long portfolioId) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// check access for this api key
		if(!apiRequestService.checkPortfolioAccessForApiKey(apiKey, portfolioId)) {
			throw new IllegalArgumentException("The API key does not have access to this portfolio"); 
		}
		
		
		return apiRequestService.getPortfolioById(portfolioId);
	}
	
	@GetMapping(path="/{apiKey}/portfolio/{portfolioId}/history/{period}/{interval}")
	public @ResponseBody Iterable<ApiPortfolioHistory> getHistoryForPortfolio(@PathVariable("apiKey") String apiKey,
			@PathVariable("portfolioId") long portfolioId,
			@PathVariable("period") String period,
			@PathVariable("interval") String interval) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// check access for this api key
		if(!apiRequestService.checkPortfolioAccessForApiKey(apiKey, portfolioId)) {
			throw new IllegalArgumentException("The API key does not have access to this portfolio"); 
		}
		
		int periodInMinutes = apiRequestService.translateTimeStringToMinutes(period);
		int intervalInMinutes = apiRequestService.translateTimeStringToMinutes(interval);
		
		// now check if the number of results is allowed
		if(!apiRequestService.checkIfNumberOfResultsIsAllowed(periodInMinutes, intervalInMinutes)) {
			// it isn't allowed, calculate the number of intervals and report to the user
			int nrOfResults = periodInMinutes / intervalInMinutes;
			
			throw new IllegalArgumentException("The requested period with interval creates " + nrOfResults + " results. This is too much. Please specify a smaller period or a larger interval."); 
		}
		
		return apiRequestService.getPortfolioHistory(portfolioId, periodInMinutes, intervalInMinutes);
	}
	
	@GetMapping(path="/{apiKey}/wallet")
	public @ResponseBody Iterable<Wallet> getWallets(@PathVariable("apiKey") String apiKey) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// get the wallets for this api key
		Iterable<Wallet> result = apiRequestService.findByPortfolioApiKeysApiKey(apiKey);
		
		return result;
	}
	
	@GetMapping(path="/{apiKey}/wallet/{walletId}")
	public @ResponseBody Wallet getSingleWallet(@PathVariable("apiKey") String apiKey,
			@PathVariable("walletId") long walletId) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// check if the api key has access
		if(!apiRequestService.checkWalletAccessForApiKey(apiKey, walletId)) {
			throw new IllegalArgumentException("The API key does not have access to this wallet"); 
		}
		
		Wallet wallet = apiRequestService.getWalletById(walletId);
		
		return wallet;
	}
	
	@GetMapping(path="/{apiKey}/wallet/{walletId}/history/{period}/{interval}")
	public @ResponseBody Iterable<ApiWalletHistory> getHistoryForWallet(@PathVariable("apiKey") String apiKey,
			@PathVariable("walletId") long walletId,
			@PathVariable("period") String period,
			@PathVariable("interval") String interval) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// check access for this api key
		if(!apiRequestService.checkWalletAccessForApiKey(apiKey, walletId)) {
			throw new IllegalArgumentException("The API key does not have access to this portfolio"); 
		}
		
		int periodInMinutes = apiRequestService.translateTimeStringToMinutes(period);
		int intervalInMinutes = apiRequestService.translateTimeStringToMinutes(interval);
		
		// now check if the number of results is allowed
		if(!apiRequestService.checkIfNumberOfResultsIsAllowed(periodInMinutes, intervalInMinutes)) {
			// it isn't allowed, calculate the number of intervals and report to the user
			int nrOfResults = periodInMinutes / intervalInMinutes;
			
			throw new IllegalArgumentException("The requested period with interval creates " + nrOfResults + " results. This is too much. Please specify a smaller period or a larger interval."); 
		}
		
		return apiRequestService.getWalletHistory(walletId, periodInMinutes, intervalInMinutes);
	}
	
	@GetMapping(path="/{apiKey}/coin")
	public @ResponseBody Iterable<Coin> getCoins(@PathVariable("apiKey") String apiKey) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		// get the wallets for this api key
		Iterable<Coin> result = apiRequestService.getCoins();
		
		return result;
	}
	
	@GetMapping(path="/{apiKey}/coin/id/{coinId}")
	public @ResponseBody Coin getSingleCoin(@PathVariable("apiKey") String apiKey,
			@PathVariable("coinId") long coinId) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
				
		Coin coin = apiRequestService.getSingleCoin(coinId);
		
		return coin;
	}
	
	@GetMapping(path="/{apiKey}/coin/symbol/{coinSymbol}")
	public @ResponseBody Coin getSingleCoinBySymbol(@PathVariable("apiKey") String apiKey,
			@PathVariable("coinSymbol") String coinSymbol) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		Coin coin = apiRequestService.getCoinBySymbol(coinSymbol);
		System.out.println(coin);
		if(coin == null) {
			throw new IllegalArgumentException("Unknown coin '" + coinSymbol + "'"); 
		}
		
		return coin;
	}
	
	
}
