package nl.kolkos.cryptoManager.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
	List<Transaction> findByWallet(Wallet wallet);
	
	Transaction findById(Long id);
	
	Transaction findByTransactionDateAndTransactionTypeAndAmountAndValue(Date transactionDate, TransactionType transactionType, double amount, double value);
	
	Page<Transaction> findByWalletPortfolioUsersEmail(String email, Pageable pageable, Specification<Transaction> spec);
	Page<Transaction> findByWalletPortfolioUsersEmail(String email, Pageable pageable);
	
	int countByWalletPortfolioUsersEmail(String email);
}
