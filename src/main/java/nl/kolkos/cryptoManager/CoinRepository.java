package nl.kolkos.cryptoManager;


import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import nl.kolkos.cryptoManager.Coin;


@Repository
public interface CoinRepository extends CrudRepository<Coin, Long> {

}

