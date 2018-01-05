package nl.kolkos.cryptoManager.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.PasswordResetToken;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	public String generateRandomKey() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	public String createPasswordResetToken(User user) {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setUser(user);
		
		// create a token
		String token = this.generateRandomKey();
		passwordResetToken.setToken(token);
		
		// set the expiry date
		passwordResetToken.setExpiryDate();
		
		// save it
		passwordResetTokenRepository.save(passwordResetToken);
		
		// return the token
		return token;
	}
	
	public PasswordResetToken findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}
}
