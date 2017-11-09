package nl.kolkos.cryptoManager;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.Deposit;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface DepositRepository extends CrudRepository<Deposit, Long> {
	List<Deposit> findByWallet(Wallet wallet);
	
	
	@Query(value="SELECT SUM(amount) FROM deposit WHERE wallet_id = ?1", nativeQuery = true)
	double getSumOfAmountForWalletId(long walletId);
	
	@Query(value="SELECT SUM(purchase_value) FROM deposit WHERE wallet_id = ?1", nativeQuery = true)
	double getSumOfPurchaseValueForWalletId(long walletId);
}
