package nl.kolkos.cryptoManager.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinValue;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface CoinValueRepository extends CrudRepository<CoinValue, Long> {
	List<CoinValue> findByCoin_Id(Long coinId);
	
	List<CoinValue> findTop10ByCoinOrderByRequestDateDesc(Coin coin);
	List<CoinValue> findTop100ByCoinOrderByRequestDateDesc(Coin coin);
	
	
	List<CoinValue> findByCoin_IdAndRequestDateBetween(long coinId, Date dateIntervalStart, Date dateIntervalEnd);
	
	
	List<CoinValue> findByRequestDateBetween(Date dateIntervalStart, Date dateIntervalEnd);
	
	
	@Query(value="SELECT COALESCE((SELECT avg(value) as avgValue FROM coin_value WHERE coin_id = ?1 AND request_date BETWEEN ?2 AND ?3), 0) as avgValue", nativeQuery = true)
	double findAvgByCoin_IdAndRequestDateBetween(long coinId, Date dateIntervalStart, Date dateIntervalEnd);
	
}
