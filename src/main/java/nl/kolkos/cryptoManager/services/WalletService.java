package nl.kolkos.cryptoManager.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;

import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.TransactionTypeRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Service
public class WalletService {
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	@Autowired 
	private TransactionService transactionService;
	
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	
	public List<Wallet> getWalletsByPortfolio(Portfolio portfolio){
		List<Wallet> wallets = walletRepository.findByPortfolio(portfolio);
		
		// get the actual values
		wallets = this.getWalletValues(wallets);
		
		return wallets;
	}
	
	public List<Wallet> findByPortfolioApiKeysApiKey(String apiKey){
		List<Wallet> wallets = walletRepository.findByPortfolioApiKeysApiKey(apiKey);
		return wallets;
	}
	
	public List<Wallet> findByPortfolio_Id(Long portfolioId){
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		// get the actual values
		wallets = this.getWalletValues(wallets);
		return wallets;
	}
	
	public Wallet findById(long walletId) {
		return walletRepository.findById(walletId);
	}
	
	public Wallet findByAddress(String address) {
		return walletRepository.findByAddress(address);
	}
	
	public List<Wallet> findByPortfolioUsersEmail(String email){
		List<Wallet> wallets = walletRepository.findByPortfolioUsersEmail(email);
		
		// get the values for each wallet
		for(Wallet wallet : wallets) {
			wallet = this.getWalletValues(wallet);
		}
		
		return wallets;
	}
	
	public void saveWallet(Wallet wallet) {
		walletRepository.save(wallet);
	}
	
	public boolean checkIfWalletExists(long walletId) {
		boolean exists = false;
		Wallet wallet = walletRepository.findById(walletId);
		if(wallet != null) {
			exists = true;
		}
		wallet = null;
		return exists;
	}
	
	public Wallet createWallet(String address, String description, Portfolio portfolio, Coin coin) {
		Wallet wallet = new Wallet();
		wallet.setAddress(address);
		wallet.setDescription(description);
		wallet.setCoin(coin);
		wallet.setPortfolio(portfolio);
		
		// save it
		walletRepository.save(wallet);
		
		return wallet;
	}
		
	public Wallet getWalletValues(Wallet wallet) {
		// get the attached coin
		Coin coin = wallet.getCoin();
		// now get the current coin value
		Calendar now = Calendar.getInstance();
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), now.getTime());
		coin.setCurrentCoinValue(lastKnownValue);
		
		
		TransactionType deposit = transactionTypeRepository.findByType("Deposit");
		TransactionType withdrawal = transactionTypeRepository.findByType("Withdrawal");
		
		
		// get the current amount of this wallet
		double totalAmountDeposited = transactionService.getSumOfAmountForWalletId(wallet.getId(), deposit.getId());
		double totalAmountWithdrawn = transactionService.getSumOfAmountForWalletId(wallet.getId(), withdrawal.getId());
		double currentBalance = totalAmountDeposited - totalAmountWithdrawn;
				
		// calculate the current value for this wallet
		double currentValue = currentBalance * lastKnownValue;
		
		// now get the investment
		double totalDeposited = transactionService.getSumOfValueForWalletId(wallet.getId(), deposit.getId());
		double totalWithdrawn = transactionService.getSumOfValueForWalletId(wallet.getId(), withdrawal.getId());
		double totalInvested = totalDeposited - totalWithdrawn;
		
		// the win/loss
		double profitLoss = currentValue - totalInvested;
		
		// calculate the roi
		double roi = profitLoss / totalInvested;
		
				
		wallet.setCurrentWalletAmount(currentBalance);
		wallet.setCurrentWalletValue(currentValue);
		wallet.setCurrentWalletInvestment(totalInvested);
		wallet.setCurrentWalletDeposited(totalDeposited);
		wallet.setCurrentWalletWithdrawn(totalWithdrawn);
		wallet.setCurrentWalletProfitLoss(profitLoss);
		wallet.setCurrentWalletROI(roi);
		
		
		return wallet;
	}
	
	public List<Wallet> getWalletValues(List<Wallet> wallets){
		// loop through wallets
		for(Wallet wallet : wallets) {
			wallet = this.getWalletValues(wallet);
		}
		
		return wallets;
	}
	
	public Wallet getWalletHistoricalValues(Wallet wallet, Date dateIntervalStart, Date dateIntervalEnd) {
		// get the attached coin
		Coin coin = wallet.getCoin();
		
		TransactionType deposit = transactionTypeRepository.findByType("Deposit");
		TransactionType withdrawal = transactionTypeRepository.findByType("Withdrawal");
		
		// get the average value for the coin in the interval
		double avgCoinValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coin.getId(), dateIntervalStart, dateIntervalEnd);
		// prevent 0 by using the last known value
		if(avgCoinValue == 0) {
			avgCoinValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), dateIntervalStart);
		}
		coin.setCurrentCoinValue(avgCoinValue);
		
		// get the amount of deposited coins
		double totalAmountDeposited = transactionService.getSumOfAmountForWalletIdAndBeforeTransactionDate(wallet.getId(), deposit.getId(), dateIntervalEnd);
		
		// get the amount of withdrawn coins
		double totalAmountWithdrawn = transactionService.getSumOfAmountForWalletIdAndBeforeTransactionDate(wallet.getId(), withdrawal.getId(), dateIntervalEnd);
		
		double currentBalance = totalAmountDeposited - totalAmountWithdrawn;
		
		// calculate the current value for this wallet
		double currentValue = currentBalance * avgCoinValue;
		
		// now get the investment
		double totalDeposited = transactionService.getSumOfValueForWalletIdAndBeforeTransactionDate(wallet.getId(), deposit.getId(), dateIntervalEnd);
		double totalWithdrawn = transactionService.getSumOfValueForWalletIdAndBeforeTransactionDate(wallet.getId(), withdrawal.getId(), dateIntervalEnd);
		double totalInvested = totalDeposited - totalWithdrawn;
		
		// the win/loss
		double profitLoss = currentValue - totalInvested;
		
		// calculate the roi
		double roi = profitLoss / totalInvested;
		
		wallet.setCoin(coin);
		wallet.setCurrentWalletAmount(currentBalance);
		wallet.setCurrentWalletValue(currentValue);
		wallet.setCurrentWalletInvestment(totalInvested);
		wallet.setCurrentWalletProfitLoss(profitLoss);
		wallet.setCurrentWalletROI(roi);
		
		return wallet;
	}
	
	public void deleteWallet(Wallet wallet) {
		// find the transactions
		List<Transaction> transactions = transactionService.findByWallet(wallet);
		transactionService.deleteWithdrawal(transactions);
		
		// now delete the wallet itself
		walletRepository.delete(wallet);
	}
	
	public void deleteWallet(List<Wallet> wallets) {
		for(Wallet wallet : wallets) {
			this.deleteWallet(wallet);
		}
	}
}
