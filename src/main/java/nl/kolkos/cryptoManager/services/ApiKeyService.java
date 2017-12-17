package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.ApiKeyRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;

@Service
public class ApiKeyService {
	@Autowired
	ApiKeyRepository apiKeyRepository;
	
	@Autowired
	PortfolioRepository portfolioRepository;
	
	public ApiKey findApiKeyByApiKey(String apiKey) {
		return apiKeyRepository.findApiKeyByApiKey(apiKey);
	}
	
	public boolean checkValidApiKey(String apiKey) {
		boolean validKey = false;
		try{
		    UUID uuid = UUID.fromString(apiKey);
		    validKey = true;
		} catch (IllegalArgumentException exception){
			validKey = false;
		}
		
		return validKey;
	}
	
	public void saveApiKey(ApiKey apiKey) {
		apiKeyRepository.save(apiKey);
	}
	
	public List<ApiKey> findByUser(User user){
		return apiKeyRepository.findByUser(user);
	}
	
	public String generateRandomKey() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
		
	public ApiKey findById(Long apiKeyId) {
		return apiKeyRepository.findById(apiKeyId);
	}
	
	public void removeApiKey(ApiKey apiKey) {
		// get the portfolios for this apiKey
		Set<Portfolio> portfolios = apiKey.getPortfolios();
		Iterator<Portfolio> iter = portfolios.iterator();
		while(iter.hasNext()) {
			Portfolio portfolio = iter.next();
			portfolio.getApiKeys().remove(apiKey);
			
		}
		
		// now put an empty list into the api key
		portfolios = new HashSet<>();
		apiKey.setPortfolios(portfolios);
		
		// now save the api key object
		apiKeyRepository.save(apiKey);
		
		// finally delete the API key
		apiKeyRepository.delete(apiKey);
		
	}
	
}
