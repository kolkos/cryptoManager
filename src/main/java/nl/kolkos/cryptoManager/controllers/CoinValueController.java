package nl.kolkos.cryptoManager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/coinValue") // This means URL's start with /demo (after Application path)
public class CoinValueController {
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	@GetMapping(path="/list")
	public @ResponseBody Iterable<CoinValue> getAllValues() {
		// This returns a JSON or XML with the users
		return coinValueRepository.findAll();
	}
	
	@RequestMapping(value = "/byCoinId/{coinId}", method = RequestMethod.GET)
	public ResponseEntity<List<CoinValue>> getWalletsByPortfolioId(@PathVariable("coinId") long coinId) {
		// This returns a JSON or XML with the users
		List<CoinValue> coinValues = coinValueRepository.findByCoin_Id(coinId);
		if (coinValues.isEmpty()) {
            System.out.println("Nothing found");
			return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<CoinValue>>(coinValues, HttpStatus.OK);
		

	}
	
}
