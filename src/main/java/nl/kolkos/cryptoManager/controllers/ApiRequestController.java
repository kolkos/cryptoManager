package nl.kolkos.cryptoManager.controllers;

import java.io.IOException;
import java.util.ArrayList;
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

import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolio;
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
	
	
}
