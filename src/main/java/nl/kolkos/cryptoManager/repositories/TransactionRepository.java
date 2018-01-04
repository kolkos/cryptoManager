package nl.kolkos.cryptoManager.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
	List<Transaction> findByWallet(Wallet wallet);
	
	Transaction findById(Long id);
	
	Transaction findByTransactionDateAndTransactionTypeAndAmountAndValue(Date transactionDate, TransactionType transactionType, double amount, double value);
	
	List<Transaction> findByTransactionType(TransactionType type);
	
	Page<Transaction> findByWalletPortfolioUsersEmail(String email, Pageable pageable, Specification<Transaction> spec);
	Page<Transaction> findByWalletPortfolioUsersEmail(String email, Pageable pageable);
	List<Transaction> findByWalletPortfolioUsersEmail(String email);
	
	int countByWalletPortfolioUsersEmail(String email);
	
	// get the sum of the amount for the transaction type on this moment, for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(amount) FROM transaction WHERE wallet_id = ?1 AND transaction_type_id = ?2), 0) AS amountOnThisMoment", nativeQuery = true)
	double getSumOfAmountForWalletId(long walletId, long transactionTypeId);
	
	// get the sum of the value for the transaction type on this moment, for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(value) FROM transaction WHERE wallet_id = ?1 AND transaction_type_id = ?2), 0) AS valueOnThisMoment", nativeQuery = true)
	double getSumOfValueForWalletId(long walletId, long transactionTypeId);
	
	// get the sum of the amount for the transaction type before a specified date, for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(amount) as totalAmount FROM transaction WHERE wallet_id = ?1 AND transaction_type_id = ?2 AND transaction_date <= ?3), 0) AS totalAmount", nativeQuery = true)
	double getSumOfAmountForWalletIdAndBeforeTransactionDate(long walletId, long transactionTypeId, Date transactionDate);
	
	// get the sum of the value for the transaction type before a specified date, for this wallet
	@Query(value="SELECT COALESCE((SELECT SUM(value) as totalAmount FROM transaction WHERE wallet_id = ?1 AND transaction_type_id = ?2 AND transaction_date <= ?3), 0) AS totalAmount", nativeQuery = true)
	double getSumOfValueForWalletIdAndBeforeTransactionDate(long walletId, long transactionTypeId, Date transactionDate);
	
	
}
