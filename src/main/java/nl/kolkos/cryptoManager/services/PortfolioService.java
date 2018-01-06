package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;

@Service
public class PortfolioService {
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	// the portfolio repository methods
	public Portfolio findById(Long id) {
		Portfolio portfolio = portfolioRepository.findById(id);
		portfolio = this.getPortfolioValue(portfolio);
		return portfolio;
	}
	
	public Portfolio findByName(String name) {
		Portfolio portfolio = portfolioRepository.findByName(name);
		return portfolio;
	}
	
	public Set<Portfolio> findByUsers_email(String email){
		Set<Portfolio> portfolios = portfolioRepository.findByUsers_email(email);
		portfolios = this.getPortfolioValue(portfolios);
		return portfolios;
	}
	
	public Set<Portfolio> findByApiKeys_apiKey(String apiKey){
		Set<Portfolio> portfolios = portfolioRepository.findByApiKeys_apiKey(apiKey);
		portfolios = this.getPortfolioValue(portfolios);
		return portfolios;
	}
	
	// default functions
	public void savePortfolio(Portfolio portfolio) {
		portfolioRepository.save(portfolio);
	}
	
//	public Iterable<Portfolio> findAll() {
//		return portfolioRepository.findAll();
//	}
	
	public Set<Portfolio> getPortfolioValue(Set<Portfolio> portfolios){
		// loop through portfolios
		for(Portfolio portfolio : portfolios) {
			portfolio = this.getPortfolioValue(portfolio);
		}
		
		return portfolios;
	}
	
	public Portfolio getPortfolioValue(Portfolio portfolio) {
		// get the wallets for this portfolio
		List<Wallet> wallets = walletService.getWalletsByPortfolio(portfolio);
		double portfolioTotalValue = 0;
		double portfolioTotalDeposited = 0;
		double portfolioTotalWithdrawn = 0;
		double portfolioTotalInvestment = 0;
				
		// get the wallet values (using the wallet service)
		wallets = walletService.getWalletValues(wallets);
		
		// now loop through the wallets to calculate the values
		for(Wallet wallet : wallets) {
			portfolioTotalValue += wallet.getCurrentWalletValue();
			portfolioTotalDeposited += wallet.getCurrentWalletDeposited();
			portfolioTotalWithdrawn += wallet.getCurrentWalletWithdrawn();
			portfolioTotalInvestment += wallet.getCurrentWalletInvestment();
		}
		
		double portfolioProfitLoss = portfolioTotalValue - portfolioTotalInvestment;
		double portfolioROI = portfolioProfitLoss / portfolioTotalDeposited;
		
		
		// now set the calculated values
		portfolio.setPortfolioTotalValue(portfolioTotalValue);
		portfolio.setPortfolioTotalDeposited(portfolioTotalDeposited);
		portfolio.setPortfolioTotalWithdrawn(portfolioTotalWithdrawn);
		portfolio.setPortfolioTotalInvestment(portfolioTotalInvestment);
		portfolio.setPortfolioProfitLoss(portfolioProfitLoss);
		portfolio.setPortfolioROI(portfolioROI);
		
		return portfolio;
	}
	
	public boolean checkIfPortfolioExists(long portfolioId) {
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		boolean exists = true;
		if(portfolio == null) {
			exists = false;
		}
		return exists;
	}
	
	// custom function
	public Portfolio createNewPortfolio(String name, String description) {
		Portfolio portfolio = new Portfolio();
		portfolio.setDescription(description);
		portfolio.setName(name);
		
		
		String username = userService.findLoggedInUsername();
		User currentUser = userService.findUserByEmail(username);
		
		
		// create a empty set of users for this portfolio
		Set<User> users = new HashSet<>();
		// add this user
		users.add(currentUser);
		
		// add this set to the portfolio
		portfolio.setUsers(users);
		
		// get the current portfolio set for this user
		Set<Portfolio> portfolios = portfolioRepository.findByUsers_email(username);
		// add this portfolio to the set
		portfolios.add(portfolio);
		// now add the portfolio to the current user
		currentUser.setPortfolios(portfolios);
		
		// finally save both objects
		userService.updateUser(currentUser);
		portfolioRepository.save(portfolio);
		
		return portfolio;
	}
	
	public void updatePortfolio(long portfolioId, String name, String description) {
		// get the portfolio object
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		// update the data
		portfolio.setDescription(description);
		portfolio.setName(name);
		
		// save the portfolio
		portfolioRepository.save(portfolio);
	}
	
	public void addUserAccessToPortfolio(long portfolioId, String mail) {
		// get the user object for the mail address
		User newtUserForPortfolio = userService.findUserByEmail(mail);
		
		// get the current portfolio object
		Portfolio currentPortfolio = portfolioRepository.findById(portfolioId);
		
		// get the current list of users for the portfolio
		Set<User> users = userService.findByPortfolios_Id(portfolioId);
		// add the new user to the portfolio set
		users.add(newtUserForPortfolio);
					
		// get the current portfolio set for this user
		Set<Portfolio> portfolios = portfolioRepository.findByUsers_email(mail);
		// add this portfolio to this set
		portfolios.add(currentPortfolio);
		
		// set the portfolio set to the new user
		newtUserForPortfolio.setPortfolios(portfolios);
		
		// set the user set to this portfolio
		currentPortfolio.setUsers(users);
		
		// finally save the changes to the object
		portfolioRepository.save(currentPortfolio);
		userService.updateUser(newtUserForPortfolio);
	}
	
	public String removeUserAccessToPortfolio(long portfolioId, int userId) {
		// check how many users are left
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		int nrOfUsers = portfolio.getUsers().size();
		if(nrOfUsers <= 1) {
			return "<div class='alert alert-warning'>You can't remove the last user.</div>";
		}
		
		// checks OK, remove the selected user
		User user = userService.findById(userId);
		// get the portfolios for this user
		Set<Portfolio> portfolios = user.getPortfolios();
		
		// get the users for the portfolio
		Set<User> users = portfolio.getUsers();
		
		// remove the elements from both objects
		portfolios.remove(portfolio);
		users.remove(user);
		
		// save both objects
		userService.updateUser(user);
		portfolioRepository.save(portfolio);
		
		return "<div class='alert alert-success'>User removed.</div>";
	}
	
	public void addApiAccessToPortfolio(ApiKey apiKey, long portfolioId) {
		// get the current portfolios
		Set<Portfolio> portfolios = apiKey.getPortfolios();
		
		// get the portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		// get the list with api keys which currently have access
		Set<ApiKey> apiKeys = portfolio.getApiKeys();
		
		// now add to both objects
		apiKeys.add(apiKey);
		portfolios.add(portfolio);
		
		// save both objects
		apiKeyService.saveApiKey(apiKey);
		portfolioRepository.save(portfolio);
	}
	
	public String removeApiAccessToPortfolio(long portfolioId, long apiKeyId) {
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// checks OK, remove the selected API Key
		ApiKey apiKey = apiKeyService.findById(apiKeyId);
		
		// get the portfolios for this user
		Set<Portfolio> portfolios = apiKey.getPortfolios();
		
		// get the users for the portfolio
		Set<ApiKey> apiKeys = portfolio.getApiKeys();
		
		// remove the elements from both objects
		portfolios.remove(portfolio);
		apiKeys.remove(apiKey);
		
		// save both objects
		apiKeyService.saveApiKey(apiKey);
		portfolioRepository.save(portfolio);
		
		
		return "<div class='alert alert-success'>API Key removed.</div>";
	}
	
	public void deletePortfolio(Portfolio portfolio) {
		// get the attached wallets
		List<Wallet> wallets = walletService.findByPortfolio_Id(portfolio.getId());
		// delete these wallets
		walletService.deleteWallet(wallets);
		
		
		// just create a empty set of users
		Set<User> users = new HashSet<>();
		// and overwrite the existing users in the portfolio
		portfolio.setUsers(users);
				
		// create a empty set of opi keys
		Set<ApiKey> apiKeys = new HashSet<>();
		// and overwrite the existing api keys
		portfolio.setApiKeys(apiKeys);
		
		// first update the portfolio
		portfolioRepository.save(portfolio);
		
		
		
		// now delete the portfolio
		portfolioRepository.delete(portfolio);
	}
	
	
	
}
