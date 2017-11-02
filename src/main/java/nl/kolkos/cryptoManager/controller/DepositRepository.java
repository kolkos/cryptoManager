package nl.kolkos.cryptoManager.controller;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.controller.Deposit;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface DepositRepository extends CrudRepository<Deposit, Long> {

}
