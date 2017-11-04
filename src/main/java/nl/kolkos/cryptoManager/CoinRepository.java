package nl.kolkos.cryptoManager;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import nl.kolkos.cryptoManager.Coin;

//This will be AUTO IMPLEMENTED by Spring into a Bean called CoinRepository
//CRUD refers Create, Read, Update, Delete

@Repository("coinRepository")
public interface CoinRepository extends CrudRepository<Coin, Long> {
	
}

