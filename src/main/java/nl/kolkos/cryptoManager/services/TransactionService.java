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
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;
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
	
	public Transaction findByTransactionDateAndTransactionTypeAndAmountAndValue(Date transactionDate, TransactionType transactionType, double amount, double value) {
		return transactionRepository.findByTransactionDateAndTransactionTypeAndAmountAndValue(transactionDate, transactionType, amount, value);
	}
	
	public int countByWalletPortfolioUsersEmail(String email){
		return transactionRepository.countByWalletPortfolioUsersEmail(email);
	}
	
	public List<Transaction> findByWallet(Wallet wallet){
		List<Transaction> transactions = transactionRepository.findByWallet(wallet);
		
		// get the current values for the transactions
		for(Transaction transaction : transactions) {
			transaction = this.getCurrentTransactionValue(transaction);
		}
		
		return transactions;
	}
	
	public Transaction getCurrentTransactionValue(Transaction transaction) {
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
		
		return transaction;
	}
	
	public void createTransaction(Date transactionDate, double amount, double value, Wallet wallet, String transactionRemarks, TransactionType transactionType) {
		Transaction transaction = new Transaction();
		transaction.setTransactionDate(transactionDate);
		transaction.setAmount(amount);
		transaction.setValue(value);
		transaction.setWallet(wallet);
		transaction.setRemarks(transactionRemarks);
		transaction.setTransactionType(transactionType);
		
		// save it
		transactionRepository.save(transaction);
	}
	
	public List<Transaction> findByWalletPortfolioUsersEmail(String email, String search, String column, String direction){
		List<Transaction> transactions = transactionRepository.findByWalletPortfolioUsersEmail(email);
				
		// now add the current values
 		for(Transaction transaction : transactions) {
 			transaction = getCurrentTransactionValue(transaction);
 		}
 		
 		// now filter and sort
 		transactions = transactionFilterSortingService.filterAndSortTransactions(transactions, search, column, direction);
		
		return transactions;
	}
	
	public double getSumOfAmountForWalletId(long walletId, long transactionTypeId) {
		return transactionRepository.getSumOfAmountForWalletId(walletId, transactionTypeId);
	}
	
	public double getSumOfValueForWalletId(long walletId, long transactionTypeId) {
		return transactionRepository.getSumOfValueForWalletId(walletId, transactionTypeId);
	}
	
	public double getSumOfAmountForWalletIdAndBeforeTransactionDate(long walletId, long transactionTypeId, Date transactionDate) {
		return transactionRepository.getSumOfAmountForWalletIdAndBeforeTransactionDate(walletId, transactionTypeId, transactionDate);
	}
	
	public double getSumOfValueForWalletIdAndBeforeTransactionDate(long walletId, long transactionTypeId, Date transactionDate) {
		return transactionRepository.getSumOfValueForWalletIdAndBeforeTransactionDate(walletId, transactionTypeId, transactionDate);
	}
	
	public void deleteTransaction(Transaction transaction) {
		transactionRepository.delete(transaction);
	}
	
	public void deleteWithdrawal(List<Transaction> transactions) {
		for(Transaction transaction : transactions) {
			this.deleteTransaction(transaction);
		}
	}
	
}
