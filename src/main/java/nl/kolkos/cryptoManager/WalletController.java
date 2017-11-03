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
@RequestMapping(path="/wallet") // This means URL's start with /demo (after Application path)
public class WalletController {
	@Autowired
	private WalletRepository walletRepository;
	
	@GetMapping("/")
    public String forwardWalletList(Model model) {
        model.addAttribute("wallet", new Wallet());
        return "redirect:/wallet/list";
    }
	
	@GetMapping("/add")
    public String walletForm(Model model) {
		model.addAttribute("wallet", new Wallet());
        return "wallet_form";
    }
	
	@PostMapping(path="/add") // Map ONLY GET Requests
	public @ResponseBody String addNewWallet (
			@RequestParam int portfolioId,
			@RequestParam int coinId,
			@RequestParam String address,
			@RequestParam String description) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
				
		Wallet wallet = new Wallet();
		wallet.setPortfolioId(portfolioId);
		wallet.setCoinId(coinId);
		wallet.setAddress(address);
		wallet.setDescription(description);
		walletRepository.save(wallet);
				
		String message = String.format("Wallet '%s' created", address);
		
		
		return message;
		
	}
	
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Wallet> getAllWallets() {
		// This returns a JSON or XML with the users
		return walletRepository.findAll();
	}
	
}
