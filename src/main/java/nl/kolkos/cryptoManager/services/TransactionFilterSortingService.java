package nl.kolkos.cryptoManager.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Transaction;

@Service
public class TransactionFilterSortingService {
	
	
	public List<Transaction> filterAndSortTransactions(List<Transaction> transactions, String search, String column, String direction){
		// split the search parameters
		HashMap<String, String> criteria = new HashMap<>();
		boolean filterResults = false;
		if(search != null && !search.equals("")) {
			criteria = this.splitSearchCriteria(search);
			filterResults = true;
		}
		
		if(filterResults) {
			// check if a wallet is set
			if(criteria.containsKey("wallet")) {
				long walletId = Long.parseLong(criteria.get("wallet"));
				transactions = this.filterByWallet(transactions, walletId);
			}
			
			if(criteria.containsKey("type")) {
				long transactionType = Long.parseLong(criteria.get("type"));
				transactions = this.filterByType(transactions, transactionType);
			}
			
			if(criteria.containsKey("portfolio")) {
				long portfolioId = Long.parseLong(criteria.get("portfolio"));
				transactions = this.filterByPortfolio(transactions, portfolioId);
			}
			
			if(criteria.containsKey("coin")) {
				long coinId = Long.parseLong(criteria.get("coin"));
				transactions = this.filterByCoin(transactions, coinId);
			}
			
		}
		
		// sort the results
		switch (column) {
		case "transactionDate":
			transactions = this.sortByTransactionDate(transactions, direction);
			break;
		case "transactionType":
			transactions = this.sortByTransactionType(transactions, direction);
			break;
		case "walletAddress":
			transactions = this.sortByWalletAddress(transactions, direction);
			break;
		case "coinSymbol":
			transactions = this.sortByCoinSymbol(transactions, direction);
			break;
		case "portfolioName":
			transactions = this.sortByPortfolioName(transactions, direction);
			break;
		case "amount":
			transactions = this.sortByAmount(transactions, direction);
			break;
		case "value":
			transactions = this.sortByValue(transactions, direction);
			break;
		case "currentValue":
			transactions = this.sortByCurrentValue(transactions, direction);
			break;
		case "difference":
			transactions = this.sortByDifference(transactions, direction);
			break;
		case "withdrawnToCash":
			transactions = this.sortByWithdrawnToCash(transactions, direction);
			break;
		case "remarks":
			transactions = this.sortByRemarks(transactions, direction);
			break;
		default:
			break;
		}
		
		return transactions;
	}
	
	private HashMap<String, String> splitSearchCriteria(String search){
		HashMap<String, String> criteria = new HashMap<>();
		
		String[] keyValuePairs = search.split(",");
		// loop through the key value pairs
		for(String keyValuePair : keyValuePairs) {
			// split into key and value
			String[] keyValue = keyValuePair.split(":");
			String key = keyValue[0];
			String value = keyValue[1];
			
			criteria.put(key, value);
		}
		
		return criteria;
	}
	
	/*
	 * ========================================================
	 * Filtering the results
	 * ========================================================
	 */
	public List<Transaction> filterByWallet(List<Transaction> transactions, long walletId){
		return transactions.stream()
				.filter(x -> x.getWallet().getId() == walletId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	public List<Transaction> filterByPortfolio(List<Transaction> transactions, long portfolioId){
		return transactions.stream()
				.filter(x -> x.getWallet().getPortfolio().getId() == portfolioId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	public List<Transaction> filterByCoin(List<Transaction> transactions, long coinId){
		return transactions.stream()
				.filter(x -> x.getWallet().getCoin().getId() == coinId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	public List<Transaction> filterByType(List<Transaction> transactions, long typeId){
		return transactions.stream()
				.filter(x -> x.getTransactionType().getId() == typeId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/*
	 * ========================================================
	 * Sorting the results
	 * ========================================================
	 */
	
	public List<Transaction> sortByTransactionDate(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getTransactionDate().compareTo(transaction2.getTransactionDate()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getTransactionDate().compareTo(transaction1.getTransactionDate()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Transaction> sortByTransactionType(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getTransactionType().getType().compareTo(transaction2.getTransactionType().getType()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getTransactionType().getType().compareTo(transaction1.getTransactionType().getType()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Transaction> sortByWalletAddress(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getWallet().getAddress().compareTo(transaction2.getWallet().getAddress()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getWallet().getAddress().compareTo(transaction1.getWallet().getAddress()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Transaction> sortByCoinSymbol(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getWallet().getCoin().getCoinMarketCapCoin().getSymbol().compareTo(transaction2.getWallet().getCoin().getCoinMarketCapCoin().getSymbol()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getWallet().getCoin().getCoinMarketCapCoin().getSymbol().compareTo(transaction1.getWallet().getCoin().getCoinMarketCapCoin().getSymbol()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Transaction> sortByPortfolioName(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getWallet().getPortfolio().getName().compareTo(transaction2.getWallet().getPortfolio().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getWallet().getPortfolio().getName().compareTo(transaction1.getWallet().getPortfolio().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	public List<Transaction> sortByAmount(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			transactions.sort(Comparator.comparingDouble(Transaction::getAmount));
		}else {
			transactions.sort(Comparator.comparingDouble(Transaction::getAmount).reversed());
		}
		
		return transactions;
	}
	
	public List<Transaction> sortByValue(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			transactions.sort(Comparator.comparingDouble(Transaction::getValue));
		}else {
			transactions.sort(Comparator.comparingDouble(Transaction::getValue).reversed());
		}
		
		return transactions;
	}
	
	public List<Transaction> sortByCurrentValue(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			transactions.sort(Comparator.comparingDouble(Transaction::getCurrentValue));
		}else {
			transactions.sort(Comparator.comparingDouble(Transaction::getCurrentValue).reversed());
		}
		
		return transactions;
	}
	
	public List<Transaction> sortByDifference(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			transactions.sort(Comparator.comparingDouble(Transaction::getCurrentDifference));
		}else {
			transactions.sort(Comparator.comparingDouble(Transaction::getCurrentDifference).reversed());
			
		}
		
		return transactions;
	}
	
	public List<Transaction> sortByWithdrawnToCash(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			transactions.sort(Comparator.comparing(Transaction::isWithdrawnToCash));
		}else {
			transactions.sort(Comparator.comparing(Transaction::isWithdrawnToCash).reversed());
			
		}
		
		return transactions;
	}
	
	public List<Transaction> sortByRemarks(List<Transaction> transactions, String direction) {
		if(direction.equals("ASC")) {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction1.getRemarks().compareTo(transaction2.getRemarks()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return transactions.stream()
					.sorted((transaction1, transaction2) -> transaction2.getRemarks().compareTo(transaction1.getRemarks()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
}
