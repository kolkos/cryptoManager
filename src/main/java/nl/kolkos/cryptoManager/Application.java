package nl.kolkos.cryptoManager;

import java.util.Arrays;
import java.sql.Date;

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
	
	@Autowired
	@Qualifier(value = "settingsRepository")
	private SettingsRepository settingsRepository;
	
	@Autowired
	@Qualifier(value = "depositRepository")
	private DepositRepository depositRepository;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	
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
        
        
        // create deposits
        Date date = new Date(1489186800000L);
        Deposit deposit1 = new Deposit();
        deposit1.setDepositDate(date);
        deposit1.setAmount(0.51000327);
        deposit1.setPurchaseValue(43.66);
        deposit1.setRemarks("Fake BTC deposit 1");
        deposit1.setWallet(walletBTC);
        
        Deposit deposit2 = new Deposit();
        deposit2.setDepositDate(date);
        deposit2.setAmount(6.666);
        deposit2.setPurchaseValue(43.66);
        deposit2.setRemarks("Fake LTC deposit 1");
        deposit2.setWallet(walletLTC);
        
        // save the deposits
        depositRepository.save(deposit1);
        depositRepository.save(deposit2);
        
        
        // create fake coin values
        CoinValue coinValue1 = new CoinValue();
        coinValue1.setCoin(coinBTC);
        coinValue1.setValue(7170.36);
        
        CoinValue coinValue2 = new CoinValue();
        coinValue2.setCoin(coinLTC);
        coinValue2.setValue(62.54);
        
        // save the values
        coinValueRepository.save(coinValue1);
        coinValueRepository.save(coinValue2);
        
        // create the settings
        Settings settingCurrency = new Settings();
        settingCurrency.setOption("currency");
        settingCurrency.setValue("EUR");
        
        Settings settingBotToken = new Settings();
        settingBotToken.setOption("bot_token");
        settingBotToken.setValue("429491716:AAHJIRsPvRkRzpYRIdznxZEXgIJtYZm77M0");
        
        Settings settingBotUsername = new Settings();
        settingBotUsername.setOption("bot_username");
        settingBotUsername.setValue("geenGezeikIedereenRijkBot");
               
        // save the settings
        settingsRepository.save(settingCurrency);
        settingsRepository.save(settingBotToken);
        settingsRepository.save(settingBotUsername);
        
    }
    
    
    
}
