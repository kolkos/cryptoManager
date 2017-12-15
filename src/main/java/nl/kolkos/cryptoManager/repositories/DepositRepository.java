package nl.kolkos.cryptoManager.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Wallet;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface DepositRepository extends PagingAndSortingRepository<Deposit, Long> {


	Deposit findById(Long id);
	
	List<Deposit> findByWallet(Wallet wallet);
	
	List<Deposit> findAllByOrderByDepositDateAsc();
	
	Page<Deposit> findByWalletPortfolioUsersEmail(String email, Pageable pageable);
	
	@Query(value="SELECT COALESCE((SELECT SUM(amount) FROM deposit WHERE wallet_id = ?1), 0) AS amount", nativeQuery = true)
	double getSumOfAmountForWalletId(long walletId);
	
	@Query(value="SELECT COALESCE((SELECT SUM(purchase_value) FROM deposit WHERE wallet_id = ?1), 0) AS purchaseValue", nativeQuery = true)
	double getSumOfPurchaseValueForWalletId(long walletId);
	
	
	@Query(value="SELECT * FROM deposit WHERE id IN( " + 
			"    SELECT deposit.id FROM deposit, wallet, portfolio, coin " + 
			"	WHERE deposit.wallet_id = wallet.id " + 
			"	AND wallet.coin_id = coin.id " + 
			"	AND wallet.portfolio_id = portfolio.id " + 
			"	AND coin.id LIKE ?1 " + 
			"	AND wallet.id LIKE ?2 " + 
			"	AND portfolio.id LIKE ?3)"
			+ "ORDER BY deposit_date DESC", nativeQuery = true)
	List<Deposit> filterResults(String coinId, String walletId, String PortfolioId);
	
	@Query(value="SELECT COALESCE((SELECT SUM(amount) as totalAmount FROM deposit WHERE wallet_id = ?1 AND deposit_date <= ?2), 0) AS totalAmount", nativeQuery = true)
	double getSumOfAmountForWalletIdAndBeforeDepositDate(long walletId, Date depositDate);
	
	@Query(value="SELECT COALESCE((SELECT SUM(purchase_value) as totalPurchaseValue FROM deposit WHERE wallet_id = ?1 AND deposit_date <= ?2),0) as totalPurchaseValue", nativeQuery = true)
	double getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(long walletId, Date depositDate);
	
	
	Page<Deposit> findDepositByWallet_Coin_IdAndWallet_IdAndWallet_Portfolio_Id(long coinId, long walletId, long PortfolioId, Pageable pageable);
	
	@Query(value="SELECT COUNT(id) FROM deposit;", nativeQuery = true)
	int getNumberOfDeposits();
	
	
}
