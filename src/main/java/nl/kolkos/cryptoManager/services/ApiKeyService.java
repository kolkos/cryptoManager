package nl.kolkos.cryptoManager.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.ApiKeyRepository;

@Service
public class ApiKeyService {
	@Autowired
	ApiKeyRepository apiKeyRepository;
	
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
}
