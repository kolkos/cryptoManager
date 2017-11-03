package nl.kolkos.cryptoManager;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Coin;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface CoinRepository extends CrudRepository<Coin, Long> {
		
}
