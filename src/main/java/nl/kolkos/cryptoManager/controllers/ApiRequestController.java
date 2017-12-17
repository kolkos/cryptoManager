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
import nl.kolkos.cryptoManager.services.ApiKeyService;
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
	public @ResponseBody Iterable<Portfolio> getPortfolioDetails(@PathVariable("apiKey") String apiKey,
			@PathVariable("portfolioId") long portfolioId) {
		// check if api key exists
		if(!apiRequestService.checkApiKeyExists(apiKey)) {
			throw new IllegalArgumentException("Unknown API Key"); 
		}
		
		Iterable<Portfolio> result = apiRequestService.getPortfoliosForKey(apiKey);
		
		return null;
	}
	
	@GetMapping(path="/error")
	public @ResponseBody Iterable<String> testError() {
		throw new IllegalArgumentException("Test fout"); 
	}
	
}
