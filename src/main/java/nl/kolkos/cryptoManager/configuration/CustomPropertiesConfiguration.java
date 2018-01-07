package nl.kolkos.cryptoManager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.services.CoinValueService;
import nl.kolkos.cryptoManager.services.CurrencyService;

@Configuration
@ComponentScan(basePackages = { "nl.kolkos.cryptoManager.*" })
@PropertySource("classpath:application.yml")
public class CustomPropertiesConfiguration {
	@Autowired
	private Environment env;
	
	@Autowired
	private CurrencyService currencyService;
	
	public void config() {
        env.getProperty("custom.mail.send.from");
        env.getProperty("custom.currency");
    }
	
	@Bean
	public Currency currency() {
		Currency currency = currencyService.findByCurrencyISOCode(env.getProperty("custom.currency"));
		return currency;
	}
	

}
