package nl.kolkos.cryptoManager.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;

@Service
public class PortfolioService {
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApiKeyService apiKeyService;
	
	// the portfolio repository methods
	public Portfolio findById(Long id) {
		return portfolioRepository.findById(id);
	}
	
	public Portfolio findByName(String name) {
		return portfolioRepository.findByName(name);
	}
	
	public Set<Portfolio> findByUsers_email(String email){
		return portfolioRepository.findByUsers_email(email);
	}
	
	public Set<Portfolio> findByApiKeys_apiKey(String apiKey){
		return portfolioRepository.findByApiKeys_apiKey(apiKey);
	}
	
	// default functions
	public void savePortfolio(Portfolio portfolio) {
		portfolioRepository.save(portfolio);
	}
	
	public Iterable<Portfolio> findAll() {
		return portfolioRepository.findAll();
	}
	
	// custom function
	public void createNewPortfolio(String name, String description) {
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
	
	
	
}
