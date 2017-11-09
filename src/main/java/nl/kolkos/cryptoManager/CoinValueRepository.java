package nl.kolkos.cryptoManager;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.CoinValue;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface CoinValueRepository extends CrudRepository<CoinValue, Long> {
	List<CoinValue> findByCoin_Id(Long coinId);
	
	List<CoinValue> findTop10ByCoinOrderByRequestDateDesc(Coin coin);
	
}
