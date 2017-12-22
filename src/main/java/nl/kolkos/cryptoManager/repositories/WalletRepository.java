package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;


//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete
@Repository
@EnableJpaRepositories("nl.kolkos.cryptoManager")
public interface WalletRepository extends CrudRepository<Wallet, Long> {
	List<Wallet> findByPortfolio_Id(Long portfolioId);
	
	List<Wallet> findByPortfolio(Portfolio portfolio);
	
	List<Wallet> findByPortfolioUsersEmail(String email);
	
	List<Wallet> findByPortfolioApiKeysApiKey(String apiKey);
	
	Wallet findById(Long id);
	
	Wallet findByAddress(String address);
	
}
