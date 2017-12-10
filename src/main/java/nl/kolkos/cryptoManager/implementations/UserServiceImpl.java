package nl.kolkos.cryptoManager.implementations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Role;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.repositories.UserRepository;
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
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}
	
	public boolean checkPortfolioRightsForCurrentUser(long portfolioId) {
		boolean userHasAccess = false;
		
		// first get the portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// get the list of authenticated users
		List<User> authenticatedUsers = portfolio.getUsers();
		
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
	
}
