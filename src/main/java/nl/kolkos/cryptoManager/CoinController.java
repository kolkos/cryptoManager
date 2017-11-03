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
    public String forwardCoinList(Model model) {
        model.addAttribute("coin", new Coin());
        return "redirect:/coin/list";
    }
	
	@GetMapping("/add")
    public String coinForm(Model model) {
		model.addAttribute("coin", new Coin());
        return "coin_form";
    }
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody String addNewCoin (
			@RequestParam String coinName,
			@RequestParam String description) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
				
		Coin coin = new Coin();
		coin.setCoinName(coinName);
		coin.setDescription(description);
		coinRepository.save(coin);
				
		String message = String.format("Coin '%s' created", coinName);
		
		
		return message;
		
	}
	
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Coin> getAllWallets() {
		// This returns a JSON or XML with the users
		return coinRepository.findAll();
	}
	
}
