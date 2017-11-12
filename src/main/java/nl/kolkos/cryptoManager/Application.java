package nl.kolkos.cryptoManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import nl.kolkos.cryptoManager.repositories.SettingsRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;


@SpringBootApplication
@EnableScheduling
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
	
	@Autowired
	@Qualifier(value = "coinMarketCapCoinRepository")
	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	
	
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
            
            //this.getAllCoinsFromCMC();
            //this.populateDB();

        };
    }
    
    public void getAllCoinsFromCMC() {
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
    		
		try {
			// get the data from the API as a JSON array
			JSONArray jsonArray = apiRequestHandler.getAllCMCCoins();
			
			// loop through the array items
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				// get the id
				String id = jsonObject.getString("id");
				
				// get the name
				String name = jsonObject.getString("name");
				
				// get the symbol
				String symbol = jsonObject.getString("symbol");
				
				// check if this id already exist
				
				if(coinMarketCapCoinRepository.findById(id) == null) {
					// cmc coin does not exist, create it
					CoinMarketCapCoin cmcCoin = new CoinMarketCapCoin();
					cmcCoin.setId(id);
					cmcCoin.setName(name);
					cmcCoin.setSymbol(symbol);
					
					// save this coin
					coinMarketCapCoinRepository.save(cmcCoin);
				}
				
				
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void populateDB() {
    		// first add some basic symbols
    		List<String> symbols = new ArrayList<>();
    		symbols.add("BTC");
    		symbols.add("LTC");
    		symbols.add("ETH");
    		symbols.add("XRP");
    		symbols.add("BCH");
    		
    		// loop through the symbols
    		for(String symbol : symbols) {
    			// get the CMC coin for this symbol
    			CoinMarketCapCoin cmcCoin = coinMarketCapCoinRepository.findBySymbol(symbol);
    			
    			// create the coin for this symbol
    			Coin coin = new Coin();
    			coin.setCoinMarketCapCoin(cmcCoin);
    			
    			// save the coin
    			coinRepository.save(coin);
    		}
    	
        // create the portfolios
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setName("Geen gezeik iedereen rijk");
        portfolio1.setDescription("Geen gezeik iedereen rijk portfolio");
        
        // save the portfolios
        portfolioRepository.save(portfolio1);
        
                
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
