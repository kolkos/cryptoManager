package nl.kolkos.cryptoManager.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.TransactionRepository;

@Service
public class TransactionService {
	private final static int PAGESIZE = 25;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
	}
	
	public List<Transaction> findByWalletPortfolioUsersEmail(int pageNumber, String columnName, String direction, String email, String search){
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		// Wallet
		// Portfolio
		// Coin
		
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email, request).getContent();
		//List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email, request).getContent();
		
		// now add the current values
 		for(Transaction transaction : transactions) {
 			Wallet wallet = transaction.getWallet();
 			
 			// get the coin for this wallet
 			Coin coin = wallet.getCoin();
 			
 			Date date = new Date();
 			double currentCoinValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), date);
 			
 			double currentValue = transaction.getAmount() * currentCoinValue;
 			transaction.setCurrentValue(currentValue);
 			
 			double currentDifference = 0;
 			if(transaction.getTransactionType().getType().equals("Deposit")) {
 				currentDifference = currentValue - transaction.getValue();
 			}else {
 				currentDifference = transaction.getValue() - currentValue;
 			}
 			transaction.setCurrentDifference(currentDifference);
 			
 		}
		
		return transactions;
	}
	
	public List<Transaction> findByWalletPortfolioUsersEmail(int pageNumber, String columnName, String direction, String email){
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		//List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email, request, spec).getContent();
		List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email, request).getContent();
		
		// now add the current values
 		for(Transaction transaction : transactions) {
 			Wallet wallet = transaction.getWallet();
 			
 			// get the coin for this wallet
 			Coin coin = wallet.getCoin();
 			
 			Date date = new Date();
 			double currentCoinValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), date);
 			
 			double currentValue = transaction.getAmount() * currentCoinValue;
 			transaction.setCurrentValue(currentValue);
 			
 			double currentDifference = 0;
 			if(transaction.getTransactionType().getType().equals("Deposit")) {
 				currentDifference = currentValue - transaction.getValue();
 			}else {
 				currentDifference = transaction.getValue() - currentValue;
 			}
 			transaction.setCurrentDifference(currentDifference);
 			
 		}
		
		return transactions;
	}
	
	public int countByWalletPortfolioUsersEmail(String email){
		return transactionRepository.countByWalletPortfolioUsersEmail(email);
	}
	
}
