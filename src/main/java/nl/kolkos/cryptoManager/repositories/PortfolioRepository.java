package nl.kolkos.cryptoManager.repositories;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Portfolio;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
		
	
}
