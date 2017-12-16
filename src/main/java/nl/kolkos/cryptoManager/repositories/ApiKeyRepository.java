package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.User;


public interface ApiKeyRepository extends CrudRepository<ApiKey, String> {
	ApiKey findApiKeyByApiKey(String apiKey);

	List<ApiKey> findByUser(User user);
}
