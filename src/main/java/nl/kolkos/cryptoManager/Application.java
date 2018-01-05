package nl.kolkos.cryptoManager;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.repositories.TransactionTypeRepository;




@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class Application {
	
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
            
            this.createRequiredObjects();

        };
    }
    
    public void createRequiredObjects() {
    		// check if the role ADMIN exists
    		Role adminRole = roleRepository.findByRole("ADMIN");
    		if(adminRole == null) {
    			System.out.println("Create ADMIN role");
    			adminRole = new Role();
    			adminRole.setRole("ADMIN");
    			roleRepository.save(adminRole);
    		}
    		
    		// check if the role USER exists
    		Role userRole = roleRepository.findByRole("USER");
    		if(userRole == null) {
    			System.out.println("Create USER role");
    			userRole = new Role();
    			userRole.setRole("USER");
    			roleRepository.save(userRole);
    		}
    		
    		// check if the deposit type Deposit exists
    		TransactionType deposit = transactionTypeRepository.findByType("Deposit");
    		if(deposit == null) {
    			System.out.println("Create transaction type deposit");
    			deposit = new TransactionType();
    			deposit.setType("Deposit");
    			transactionTypeRepository.save(deposit);
    		}
    		
    		// check if the deposit type Withdrawal exists
    		TransactionType withdrawal = transactionTypeRepository.findByType("Withdrawal");
    		if(withdrawal == null) {
    			System.out.println("Create transaction type withdrawal");
    			withdrawal = new TransactionType();
    			withdrawal.setType("Withdrawal");
    			transactionTypeRepository.save(withdrawal);
    		}
    		
    }

    
}
