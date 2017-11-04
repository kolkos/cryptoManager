package nl.kolkos.cryptoManager;


import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController    // This means that this class is a Controller
@RequestMapping(path="/coin") // This means URL's start with /demo (after Application path)
public class CoinController {
	@Autowired
	private CoinRepository coinRepository;
	
	// Get All Coins
	@GetMapping("/coins")
	public List<Coin> getAllNotes() {
	    return coinRepository.findAll();
	}
	
	
	// Create a new Coin
	@PostMapping("/coins")
	public Coin createCoin(@Valid @RequestBody Coin coin) {
	    return coinRepository.save(coin);
	}
	
	// Get a Single Coin
	@GetMapping("/coins/{id}")
	public ResponseEntity<Coin> getNoteById(@PathVariable(value = "id") Long coinId) {
	    Coin coin = coinRepository.findOne(coinId);
	    if(coin == null) {
	        return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.ok().body(coin);
	}
	
	// Update a Coin
	@PutMapping("/coins/{id}")
	public ResponseEntity<Coin> updateNote(@PathVariable(value = "id") Long coinId, 
	                                       @Valid @RequestBody Coin coinDetails) {
	    Coin coin = coinRepository.findOne(coinId);
	    if(coin == null) {
	        return ResponseEntity.notFound().build();
	    }

	    coin.setCoinName(coinDetails.getCoinName());
	    coin.setDescription(coinDetails.getDescription());

	    Coin updatedCoin = coinRepository.save(coin);
	    return ResponseEntity.ok(updatedCoin);
	}
	
	// Delete a Coin
	@DeleteMapping("/coins/{id}")
	public ResponseEntity<Coin> deleteNote(@PathVariable(value = "id") Long coinId) {
	    Coin coin = coinRepository.findOne(coinId);
	    if(coin == null) {
	        return ResponseEntity.notFound().build();
	    }

	    coinRepository.delete(coin);
	    return ResponseEntity.ok().build();
	}
}
