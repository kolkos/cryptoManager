package nl.kolkos.cryptoManager.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	@Autowired
	private TransactionFilterSortingService transactionFilterSortingService;
	
	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
	}
	
	
	
	public int countByWalletPortfolioUsersEmail(String email){
		return transactionRepository.countByWalletPortfolioUsersEmail(email);
	}
	
	public List<Transaction> findByWalletPortfolioUsersEmail(String email, String search, String column, String direction){
		List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email);
				
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
 		
 		// now filter and sort
 		transactions = transactionFilterSortingService.filterAndSortTransactions(transactions, search, column, direction);
		
		return transactions;
	}
	
	
	
	
	
}
