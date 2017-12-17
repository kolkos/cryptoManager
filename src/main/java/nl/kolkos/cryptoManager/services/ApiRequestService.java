package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;

@Service
public class ApiRequestService {
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	public boolean checkApiKeyExists(String apiKey) {
		boolean keyExists = false;
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		if(apiKeyRequester != null) {
			keyExists = true;
		}
		return keyExists;
	}
	
	public Set<Portfolio> getPortfoliosForKey(String apiKey){
		
		
		return portfolioRepository.findByApiKeys_apiKey(apiKey);
	}
	
}
