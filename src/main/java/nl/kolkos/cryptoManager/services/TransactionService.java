package nl.kolkos.cryptoManager.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
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
	
	@Resource(name = "currency")
	private Currency currency;

	
	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
	}
	
	public Transaction findByTransactionDateAndTransactionTypeAndAmountAndValue(Date transactionDate, TransactionType transactionType, double amount, double value) {
		return transactionRepository.findByTransactionDateAndTransactionTypeAndAmountAndValue(transactionDate, transactionType, amount, value);
	}
	
	public int countByWalletPortfolioUsersEmail(String email){
		return transactionRepository.countByWalletPortfolioUsersEmail(email);
	}
	
	public Transaction findById(long transactionId) {
		return transactionRepository.findById(transactionId);
	}
	
	public void delete(Transaction transaction) {
		transactionRepository.delete(transaction);
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
		transaction.setCurrency(currency);
		
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
	
	public boolean checkIfTransactionExists(long transactionId) {
		boolean exists = true;
		
		Transaction transaction = transactionRepository.findById(transactionId);
		if (transaction == null) {
			exists = false;
		}
		transaction = null;
		return exists;
	}
	
	@Transactional
	public void transactionMaintenanceFixMissingCurrency() {
		// get the transactions
		List<Transaction> transactions = transactionRepository.findByCurrencyIsNull();
		// loop throught the transactions
		for(Transaction transaction : transactions) {
			System.out.println(String.format("%s: Setting currency '%s' to transaction %d", new Date(), currency.getCurrencyISOCode(), transaction.getId()));
			transaction.setCurrency(currency);
			transactionRepository.save(transaction);
		}
	}
	
	@Transactional
	public void transactionMaintenanceConvertCurrency() {
		// get the transactions with another currency then the current
		List<Transaction> transactions = transactionRepository.findByCurrencyNot(currency);
		
		HashMap<String, Double> currencyValues = new HashMap<>();
		ApiRequestHandler apiHandler = new ApiRequestHandler();
		
		for(Transaction transaction : transactions) {
			
			String registeredCurrency = transaction.getCurrency().getCurrencyISOCode();
			String currentCurrency = currency.getCurrencyISOCode();
			
			// check if the registered currency is already in the hash
			// if not, register the value
			boolean runConversion = true;
			if(!currencyValues.containsKey(registeredCurrency)) {
				// does not exist, get the transfer rate
				try {
					JSONObject jsonObject = apiHandler.requestCurrencyConversion(registeredCurrency, currentCurrency);
					
					// get the value
					double rate = jsonObject.getJSONObject("rates").getDouble(currentCurrency);
					
					System.out.println(String.format("Using conversion rate (%s-%s): %f", registeredCurrency, currentCurrency, rate));
					
					// register this rate
					currencyValues.put(registeredCurrency, rate);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runConversion = false;
				}
			}
			if(runConversion) {
				String logLine = String.format("%s: Converting value from transaction %d from '%s' to '%s'", 
						new Date(),
						transaction.getId(),
						registeredCurrency,
						currentCurrency);
				System.out.println(logLine);
				
				// calculate the new coin value
				double newTransactionValue = transaction.getValue() * currencyValues.get(registeredCurrency);
				BigDecimal bd = new BigDecimal(newTransactionValue);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				double roundedTransactionValue = bd.doubleValue();
				
				logLine = String.format("%s: %s-%s: %f * %f = %f", 
						new Date(),
						registeredCurrency,
						currentCurrency, 
						transaction.getValue(),
						currencyValues.get(registeredCurrency),
						roundedTransactionValue);
				
				System.out.println(logLine);
				
				transaction.setCurrency(currency);
				transaction.setValue(roundedTransactionValue);
				transactionRepository.save(transaction);	
			}
		}
		
	}
	
}
