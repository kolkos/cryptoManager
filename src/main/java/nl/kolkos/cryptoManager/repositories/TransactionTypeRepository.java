package nl.kolkos.cryptoManager.repositories;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.TransactionType;

public interface TransactionTypeRepository extends CrudRepository<TransactionType, Long> {
	
}
