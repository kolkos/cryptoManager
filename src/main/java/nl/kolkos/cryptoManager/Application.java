package nl.kolkos.cryptoManager;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class Application {
	
	@Autowired
	@Qualifier(value = "walletRepository")
	private WalletRepository walletRepository;
	
	@Autowired
	@Qualifier(value = "coinRepository")
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "portfolioRepository")
	private PortfolioRepository portfolioRepository;
	
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
            
            
            this.populateDB();

        };
    }
    
    public void populateDB() {
    		// create the coins
        Coin coinBTC = new Coin();
        coinBTC.setCoinName("BTC");
        coinBTC.setDescription("Bitcoin");
        
        Coin coinLTC = new Coin();
        coinLTC.setCoinName("LTC");
        coinLTC.setDescription("Litecoin");
        
        Coin coinETH = new Coin();
        coinETH.setCoinName("ETH");
        coinETH.setDescription("Ethereum");
        
        Coin coinXRP = new Coin();
        coinXRP.setCoinName("XRP");
        coinXRP.setDescription("Ripple");
        
        // save coins
        coinRepository.save(coinBTC);
        coinRepository.save(coinLTC);
        coinRepository.save(coinETH);
        coinRepository.save(coinXRP);
        
        
        // create the portfolios
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setName("Geen gezeik iedereen rijk");
        portfolio1.setDescription("Geen gezeik iedereen rijk portfolio");
        
        // save the portfolios
        portfolioRepository.save(portfolio1);
        
        // add fake wallets
        Wallet walletBTC = new Wallet();
        walletBTC.setAddress("FAKE-BTC-ADDRESS");
        walletBTC.setDescription("Fake Bitcoin wallet");
        walletBTC.setCoin(coinBTC);
        walletBTC.setPortfolio(portfolio1);
        
        Wallet walletLTC = new Wallet();
        walletLTC.setAddress("FAKE-LTC-ADDRESS");
        walletLTC.setDescription("Fake Litecoin wallet");
        walletLTC.setCoin(coinLTC);
        walletLTC.setPortfolio(portfolio1);
        
        // save the wallets
        walletRepository.save(walletBTC);
        walletRepository.save(walletLTC);
        
    }
    
    
    
}
