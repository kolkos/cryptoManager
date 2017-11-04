package nl.kolkos.cryptoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/coin") // This means URL's start with /demo (after Application path)
public class CoinController {
	@Autowired
	private CoinRepository coinRepository;
	
	
	@GetMapping("/")
    public String forwardRepositoryList(Model model) {
        return "redirect:/portfolio/list";
    }
	
	// send the form
	@GetMapping("/add")
    public String coinForm(Model model) {
		model.addAttribute("coin", new Coin());
        return "coin_form";
    }
	
	// handle the form
	@PostMapping(path="/add") // Map ONLY Post Requests
	public @ResponseBody String addNewCoin (
			@RequestParam String description,
			@RequestParam String coinName) {
				
		Coin coin = new Coin();
		coin.setDescription(description);
		coin.setCoinName(coinName);
		coinRepository.save(coin);
				
		String message = String.format("Coin '%s' created", coinName);
		
		
		return message;
		
	}
	
	// list all 
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Coin> getAllCoins() {
		// This returns a JSON or XML with the users
		return coinRepository.findAll();
	}
}
