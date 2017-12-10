package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Portfolio;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
	Portfolio findById(Long id);	
	
	List<Portfolio> findByUsers_email(String email);
	
	
}
