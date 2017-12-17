package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;

@Service
public class ApiRequestService {
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	public Set<Portfolio> getPortfoliosForApiKey(String apiKey){
		Set<Portfolio> portfolios = new HashSet<>();
		
		// check if the api key exists
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		
		
		return portfolios;
	}
	
}
