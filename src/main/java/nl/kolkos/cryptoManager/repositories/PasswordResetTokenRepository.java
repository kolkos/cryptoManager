package nl.kolkos.cryptoManager.repositories;

import org.springframework.data.repository.CrudRepository;

import nl.kolkos.cryptoManager.PasswordResetToken;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long>{
	PasswordResetToken findByToken(String token);
}
