package nl.kolkos.cryptoManager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface WithdrawalRepository extends CrudRepository<Withdrawal, Long> {
	List<Withdrawal> findByWallet(Wallet wallet);
	
	@Query(value="SELECT * FROM withdrawal WHERE id IN( " + 
			"	SELECT withdrawal.id FROM withdrawal, wallet, portfolio, coin " + 
			"	WHERE withdrawal.wallet_id = wallet.id " + 
			"	AND wallet.coin_id = coin.id " + 
			"	AND wallet.portfolio_id = portfolio.id " + 
			"	AND coin.id LIKE ?1 " + 
			"	AND wallet.id LIKE ?2 " + 
			"	AND portfolio.id LIKE ?3)", nativeQuery = true)
	List<Withdrawal> filterResults(String coinId, String walletId, String PortfolioId);
	
	// get the total amount of coins withdrawn for the wallet
	@Query(value="SELECT COALESCE((SELECT SUM(amount) FROM withdrawal WHERE wallet_id = ?1), 0) AS amount", nativeQuery = true)
	double getSumOfAmountForWalletId(long walletId);
	
	// get the total value of withdrawals to cash for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(withdrawal_value) FROM withdrawal WHERE wallet_id = ?1 AND to_cash = 1), 0) AS withdrawnToCash", nativeQuery = true)
	double getSumOfWithdrawalsToCashForWalletId(long walletId);
	
	// get the total value of all withdrawals for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(withdrawal_value) FROM withdrawal WHERE wallet_id = ?1), 0) AS withdrawnToCash", nativeQuery = true)
	double getSumOfWithdrawalsForWalletId(long walletId);
	
	
}
