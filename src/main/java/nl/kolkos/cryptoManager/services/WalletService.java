package nl.kolkos.cryptoManager.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Service
public class WalletService {
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private CoinValueRepository coinValueRepository;
	
	@Autowired
	private DepositService depositService;
	
	@Autowired
	private WithdrawalService withdrawalService;
	
	public List<Wallet> getWalletsByPortfolio(Portfolio portfolio){
		List<Wallet> wallets = walletRepository.findByPortfolio(portfolio);
		return wallets;
	}
	
	public List<Wallet> findByPortfolioApiKeysApiKey(String apiKey){
		List<Wallet> wallets = walletRepository.findByPortfolioApiKeysApiKey(apiKey);
		return wallets;
	}
	
	public List<Wallet> findByPortfolio_Id(Long portfolioId){
		return walletRepository.findByPortfolio_Id(portfolioId);
	}
	
	public Wallet findById(long walletId) {
		return walletRepository.findById(walletId);
	}
	
	public Wallet findByAddress(String address) {
		return walletRepository.findByAddress(address);
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
		
		// get the current amount of this wallet
		double totalAmountDeposited = depositService.getSumOfAmountForWalletId(wallet.getId());
		double totalAmountWithdrawn = withdrawalService.getSumOfAmountForWalletId(wallet.getId());
		double currentBalance = totalAmountDeposited - totalAmountWithdrawn;
				
		// calculate the current value for this wallet
		double currentValue = currentBalance * lastKnownValue;
		
		// now get the investment
		double totalDeposited = depositService.getSumOfPurchaseValueForWalletId(wallet.getId());
		double totalWithdrawnToCash = withdrawalService.getSumOfWithdrawalsToCashForWalletId(wallet.getId());
		double totalInvested = totalDeposited - totalWithdrawnToCash;
		
		// the win/loss
		double profitLoss = currentValue - totalInvested;
		
		// calculate the roi
		double roi = profitLoss / totalInvested;
		
				
		wallet.setCurrentWalletAmount(currentBalance);
		wallet.setCurrentWalletValue(currentValue);
		wallet.setCurrentWalletInvestment(totalInvested);
		wallet.setCurrentWalletProfitLoss(profitLoss);
		wallet.setCurrentWalletROI(roi);
		
		
		return wallet;
	}
	
	public Wallet getWalletHistoricalValues(Wallet wallet, Date dateIntervalStart, Date dateIntervalEnd) {
		// get the attached coin
		Coin coin = wallet.getCoin();
				
		// get the average value for the coin in the interval
		double avgCoinValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coin.getId(), dateIntervalStart, dateIntervalEnd);
		// prevent 0 by using the last known value
		if(avgCoinValue == 0) {
			avgCoinValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coin.getId(), dateIntervalStart);
		}
		coin.setCurrentCoinValue(avgCoinValue);
		
		// get the amount of deposited coins
		double totalAmountDeposited = depositService.getSumOfAmountForWalletIdAndBeforeDepositDate(wallet.getId(), dateIntervalEnd);
		
		// get the amount of withdrawn coins
		double totalAmountWithdrawn = withdrawalService.getSumOfAmountForWalletIdAndBeforeWithdrawalDate(wallet.getId(), dateIntervalEnd);
		
		double currentBalance = totalAmountDeposited - totalAmountWithdrawn;
		
		// calculate the current value for this wallet
		double currentValue = currentBalance * avgCoinValue;
		
		// now get the investment
		double totalDeposited = depositService.getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(wallet.getId(), dateIntervalEnd);
		double totalWithdrawnToCash = withdrawalService.getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(wallet.getId(), dateIntervalEnd);
		double totalInvested = totalDeposited - totalWithdrawnToCash;
		
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
}
