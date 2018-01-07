package nl.kolkos.cryptoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.services.CoinService;
import nl.kolkos.cryptoManager.services.CoinValueService;

@Component
@ComponentScan(basePackages = { "nl.kolkos.cryptoManager.*" })
public class ScheduledTasks {
	@Autowired
	private CoinService coinService;
		
	@Scheduled(fixedRate = 300_000)
    public void reportCurrentTime() {
        // use the service
		coinService.updateCoinValues();
	
    }
	
	
}
