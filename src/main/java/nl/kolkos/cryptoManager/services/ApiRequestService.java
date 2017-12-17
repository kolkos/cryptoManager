package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.api.objects.ApiPortfolio;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Service
public class ApiRequestService {
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	
	
	public boolean checkApiKeyExists(String apiKey) {
		boolean keyExists = false;
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		if(apiKeyRequester != null) {
			keyExists = true;
		}
		return keyExists;
	}
	
	public boolean checkPortfolioAccessForApiKey(String apiKey, long portfolioId) {
		ApiKey apiKeyRequester = apiKeyService.findApiKeyByApiKey(apiKey);
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		boolean access = false;
		
		if(portfolio.getApiKeys().contains(apiKeyRequester)) {
			access = true;
		}
		
		return access;
	}
	
	public boolean checkWalletAccessForApiKey(String apiKey, long walletId) {
		boolean access = false;
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		// get the portfolio id for this wallet
		long portfolioId = wallet.getPortfolio().getId();
		// now use the checkPortfolioAccessForApiKey method
		access = this.checkPortfolioAccessForApiKey(apiKey, portfolioId);
		
		return access;
	}
	
	public Set<Portfolio> getPortfoliosForKey(String apiKey){
		return portfolioRepository.findByApiKeys_apiKey(apiKey);
	}
	
	public List<Wallet> findByPortfolioApiKeysApiKey(String apiKey){
		// get the wallets for the api key
		List<Wallet> wallets = walletService.findByPortfolioApiKeysApiKey(apiKey);
		
		// now get the values for the wallets
		for(Wallet wallet : wallets) {
			wallet = walletService.getWalletValues(wallet);
		}
		
		
		return wallets;
	}
	
	public ApiPortfolio getPortfolioById(long portfolioId) {
		// get the normal Portfolio object
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// now add it's elements to the ApiPortfolio object
		ApiPortfolio apiPortfolio = new ApiPortfolio();
		apiPortfolio.setId(portfolio.getId());
		apiPortfolio.setName(portfolio.getName());
		apiPortfolio.setDescription(portfolio.getDescription());
		
		// now get the values from the wallets
		double currentTotalPortfolioValue = 0;
		double currentTotalPortfolioInvestment = 0;
		// get the wallets by this portfolio
		List<Wallet> wallets = walletService.getWalletsByPortfolio(portfolio);
		// loop through the wallets
		for(Wallet wallet : wallets) {
			wallet = walletService.getWalletValues(wallet);
			// now get the value and add it to the total
			currentTotalPortfolioValue += wallet.getCurrentWalletValue();
			currentTotalPortfolioInvestment += wallet.getCurrentWalletInvestment();
		}
		
		double profitLoss = currentTotalPortfolioValue - currentTotalPortfolioInvestment;
		double roi = profitLoss / currentTotalPortfolioInvestment;
		
		apiPortfolio.setWallets(wallets);
		
		apiPortfolio.setCurrentTotalPortfolioInvestment(currentTotalPortfolioInvestment);
		apiPortfolio.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
		apiPortfolio.setCurrentTotalPortfolioProfitLoss(profitLoss);
		apiPortfolio.setCurrentTotalPortfolioROI(roi);
		
		
		return apiPortfolio;
	}
	
	public Wallet getWalletById(long walletId) {
		Wallet wallet = walletService.findById(walletId);
		
		// add additional information
		wallet = walletService.getWalletValues(wallet);
		
		return wallet;
	}
	
	
	
}
