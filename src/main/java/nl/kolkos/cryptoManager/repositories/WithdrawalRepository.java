package nl.kolkos.cryptoManager.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface WithdrawalRepository extends PagingAndSortingRepository<Withdrawal, Long> {
	List<Withdrawal> findByWallet(Wallet wallet);
	
	Withdrawal findById(Long id);
	
	Withdrawal findByWithdrawalDateAndAmountAndWithdrawalValue(Date withdrawalDate, double amount, double withdrawalValue);
	
	Page<Withdrawal> findByWalletPortfolioUsersEmail(String email, Pageable pageable);
	
	
	
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
	
	
	// get the total of withdrawn coins before a date
	@Query(value="SELECT COALESCE((SELECT SUM(amount) as totalAmount FROM withdrawal WHERE wallet_id = ?1 AND withdrawal_date <= ?2), 0) AS totalAmount", nativeQuery = true)
	double getSumOfAmountForWalletIdAndBeforeWithdrawalDate(long walletId, Date depositDate);
	
	// get the total of the investment before a date
	@Query(value="SELECT COALESCE((SELECT SUM(withdrawal_value) as totalPurchaseValue FROM withdrawal WHERE wallet_id = ?1 AND withdrawal_date <= ?2 AND to_cash = 1),0) as totalWithdrawanValue", nativeQuery = true)
	double getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(long walletId, Date depositDate);
	
	@Query(value="SELECT COUNT(id) FROM withdrawal;", nativeQuery = true)
	int getNumberOfDeposits();
	
}
