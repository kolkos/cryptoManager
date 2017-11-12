package nl.kolkos.cryptoManager.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;


import nl.kolkos.cryptoManager.CoinMarketCapCoin;


//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface CoinMarketCapCoinRepository extends CrudRepository<CoinMarketCapCoin, String> {
	CoinMarketCapCoin findBySymbol(String symbol);
	
	List<CoinMarketCapCoin> findAllByOrderBySymbolAsc();
	
	CoinMarketCapCoin findById(String id);
}
