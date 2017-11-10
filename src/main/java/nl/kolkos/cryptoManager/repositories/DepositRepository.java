package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Wallet;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface DepositRepository extends CrudRepository<Deposit, Long> {
	List<Deposit> findByWallet(Wallet wallet);
	
	
	@Query(value="SELECT COALESCE((SELECT SUM(amount) FROM deposit WHERE wallet_id = ?1), 0) AS aantal", nativeQuery = true)
	double getSumOfAmountForWalletId(long walletId);
	
	@Query(value="SELECT COALESCE((SELECT SUM(purchase_value) FROM deposit WHERE wallet_id = ?1), 0) AS aantal", nativeQuery = true)
	double getSumOfPurchaseValueForWalletId(long walletId);
}
