package nl.kolkos.cryptoManager.repositories;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Withdrawal;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface WithdrawalRepository extends CrudRepository<Withdrawal, Long> {

}
