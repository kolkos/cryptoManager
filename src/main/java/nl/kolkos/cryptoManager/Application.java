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
import nl.kolkos.cryptoManager.services.CoinValueService;
import nl.kolkos.cryptoManager.services.CurrencyService;
import nl.kolkos.cryptoManager.services.TransactionService;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class Application {
	
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private CoinValueService coinValueService;
	
	@Autowired
	private TransactionService transactionService;
	
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
            
            // create the required objects
            this.createRequiredObjects();
            
            // fix the missing currencies for coin_value
            coinValueService.coinValueMaintenanceMissingCurrency();
            // if necessary convert the currencies for coin values
            coinValueService.coinValueMaintenanceChangedCurrency();
            
            // fix the missing Currency for the transactions
            transactionService.transactionMaintenanceFixMissingCurrency();
            // if necessary convert the currencies for the transactions
            transactionService.transactionMaintenanceConvertCurrency();
            
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
    		
    		// create the Euro currency
    		Currency euro = currencyService.findByCurrencyISOCode("EUR");
    		if(euro == null) {
    			System.out.println("Create the EUR currency");
    			// does not exist, create it
    			euro = new Currency();
    			euro.setCurrencyName("Euro");
    			euro.setCurrencyISOCode("EUR");
    			euro.setCurrencySymbol("&euro;"); // HTML code
    			euro.setCurrencyGlyph("glyphicon glyphicon-eur");
    			currencyService.save(euro);
    		}
    		
    		// create the Dollar currency
    		Currency dollar = currencyService.findByCurrencyISOCode("USD");
    		if(dollar == null) {
    			System.out.println("Create the USD currency");
    			// does not exist, create it
    			dollar = new Currency();
    			dollar.setCurrencyName("Dollar");
    			dollar.setCurrencyISOCode("USD");
    			dollar.setCurrencySymbol("&dollar;"); // HTML code
    			dollar.setCurrencyGlyph("glyphicon glyphicon-usd");
    			currencyService.save(dollar);
    		}
    		
        
    }

    
}
