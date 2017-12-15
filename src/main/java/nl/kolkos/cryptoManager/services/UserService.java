package nl.kolkos.cryptoManager.services;

import java.util.Set;

import nl.kolkos.cryptoManager.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
	public void updateUser(User user);
	public String findLoggedInUsername();
	public boolean checkIfCurrentUserIsAuthorizedToPortfolio(long portfolioId);
	public boolean checkIfCurrentUserIsAuthorizedToWallet(long walletId);
	public boolean checkIfCurrentUserIsAuthorizedToDeposit(long depositId);
	public long countByEmail(String email);
	public Set<User> findByPortfolios_Id(Long portfolioId);
}