package nl.kolkos.cryptoManager.services;

import nl.kolkos.cryptoManager.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
	public String findLoggedInUsername();
	public boolean checkPortfolioRightsForCurrentUser(long portfolioId);
}