package nl.kolkos.cryptoManager.implementations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Role;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.repositories.UserRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.services.TransactionService;
import nl.kolkos.cryptoManager.services.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
	private PortfolioRepository portfolioRepository;
    
    @Autowired
	private WalletRepository walletRepository;
       
    @Autowired
    private TransactionService transactionService;
    
    
    public String findLoggedInUsername() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (!(authentication instanceof AnonymousAuthenticationToken)) {
    	    String currentUserName = authentication.getName();
    	    return currentUserName;
    	}

        return null;
    }
	
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user) {		
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRepeatPassword(bCryptPasswordEncoder.encode(user.getRepeatPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        
        Set<User> users = new HashSet<>();
        users.add(user);
        userRole.setUsers(users);
        roleRepository.save(userRole);
        
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}
	
	public void updateUser(User user) {
		// only update the user
		userRepository.save(user);
	}
	
	public void updateUser(User user, boolean passwordChanged) {
		if(passwordChanged) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setRepeatPassword(bCryptPasswordEncoder.encode(user.getRepeatPassword()));
		}
		userRepository.save(user);
	}
	
	public boolean checkIfCurrentUserIsAuthorizedToPortfolio(long portfolioId) {
		boolean userHasAccess = false;
		
		// first get the portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// get the list of authenticated users
		Set<User> authenticatedUsers = portfolio.getUsers();
		
		String userName = this.findLoggedInUsername();
		// get the user object
		User currentUser = userRepository.findByEmail(userName);
		
		// now loop through the authenticated users and see of the current user is autorized
		for(User user : authenticatedUsers) {
			if(user.equals(currentUser)) {
				userHasAccess = true;
				break;
			}
		}
		
		
		return userHasAccess;
	}
	
	public boolean checkIfCurrentUserIsAuthorizedToWallet(long walletId) {
		// first get the wallet object
		Wallet wallet = walletRepository.findById(walletId);
		
		// get the portfolio id for this wallet
		long portfolioId = wallet.getPortfolio().getId();
		
		// because I'm lazy, use the checkIfCurrentUserIsAuthorizedToPortfolio method
		return this.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
	}
	
//	public boolean checkIfCurrentUserIsAuthorizedToDeposit(long depositId) {
//		// get the deposit object
//		Deposit deposit = depositRepository.findById(depositId);
//		
//		// get the wallet -> portfolio -> id
//		return this.checkIfCurrentUserIsAuthorizedToPortfolio(deposit.getWallet().getPortfolio().getId());
//	}
//	
//	public boolean checkIfCurrentUserIsAuthorizedToWithdrawal(long withdrawalId) {
//		// get the deposit object
//		Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId);
//		
//		// get the wallet -> portfolio -> id
//		return this.checkIfCurrentUserIsAuthorizedToPortfolio(withdrawal.getWallet().getPortfolio().getId());
//	}
	
	public boolean checkIfCurrentUserIsAuthorizedToTransaction(long transactionId) {
		Transaction transaction = transactionService.findById(transactionId);
		
		return this.checkIfCurrentUserIsAuthorizedToPortfolio(transaction.getWallet().getPortfolio().getId());
	}
	
	public long countByEmail(String email) {
		return userRepository.countByEmail(email);
	}
	
	public Set<User> findByPortfolios_Id(Long portfolioId) {
		return userRepository.findByPortfolios_Id(portfolioId);
	}
	
	public User findById(int userId) {
		return userRepository.findById(userId);
	}
	
}
