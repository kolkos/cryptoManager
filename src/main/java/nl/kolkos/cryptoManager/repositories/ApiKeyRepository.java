package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.User;


public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {
	ApiKey findApiKeyByApiKey(String apiKey);
	
	ApiKey findById(Long apiKeyId);

	List<ApiKey> findByUser(User user);
	
	
	
}
