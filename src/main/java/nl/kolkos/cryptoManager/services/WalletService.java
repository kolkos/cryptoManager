package nl.kolkos.cryptoManager.services;

import java.util.Calendar;
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
	
	public Wallet findById(long walletId) {
		return walletRepository.findById(walletId);
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
				
		wallet.setCurrentWalletAmount(currentBalance);
		wallet.setCurrentWalletValue(currentValue);
		wallet.setCurrentWalletInvestment(totalInvested);
		
		return wallet;
	}
}
