package nl.kolkos.cryptoManager;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import nl.kolkos.cryptoManager.Wallet;


//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete
@Repository
@EnableJpaRepositories("nl.kolkos.cryptoManager")
public interface WalletRepository extends CrudRepository<Wallet, Long> {
	
}
