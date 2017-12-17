package nl.kolkos.cryptoManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import nl.kolkos.cryptoManager.repositories.CoinMarketCapCoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.repositories.SettingsRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.services.UserService;


@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class Application {
	
//	@Autowired
//	@Qualifier(value = "walletRepository")
//	private WalletRepository walletRepository;
//	
//	@Autowired
//	@Qualifier(value = "coinRepository")
//	private CoinRepository coinRepository;
//	
//	@Autowired
//	@Qualifier(value = "portfolioRepository")
//	private PortfolioRepository portfolioRepository;
//	
//	@Autowired
//	@Qualifier(value = "settingsRepository")
//	private SettingsRepository settingsRepository;
//	
//	@Autowired
//	@Qualifier(value = "depositRepository")
//	private DepositRepository depositRepository;
//	
//	@Autowired
//	@Qualifier(value = "coinValueRepository")
//	private CoinValueRepository coinValueRepository;
//	
//	@Autowired
//	@Qualifier(value = "coinMarketCapCoinRepository")
//	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	

	
	
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
            
            

        };
    }
    
    

    
}
