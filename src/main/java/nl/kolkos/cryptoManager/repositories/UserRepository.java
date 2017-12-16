package nl.kolkos.cryptoManager.repositories;


import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import nl.kolkos.cryptoManager.User;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
	 User findByEmail(String email);
	 User findById(int userId);
	 
	 long countByEmail(String email);
	 
	 Set<User> findByPortfolios_Id(Long portfolioId);
	 
	 
}