package nl.kolkos.cryptoManager.repositories;


import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import nl.kolkos.cryptoManager.Coin;


@Repository
public interface CoinRepository extends CrudRepository<Coin, Long> {
	Coin findById(Long id);
	
	List<Coin> findAllByOrderByCoinMarketCapCoinSymbol();
	
	Coin findByCoinMarketCapCoinSymbol(String coinMarketCapSymbol);
	
}

