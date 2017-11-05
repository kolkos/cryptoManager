package nl.kolkos.cryptoManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/wallet") // This means URL's start with /demo (after Application path)
public class WalletController {

	@Autowired
	@Qualifier(value = "walletRepository")
	private WalletRepository walletRepository;
	
	@Autowired
	@Qualifier(value = "coinRepository")
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "portfolioRepository")
	private PortfolioRepository portfolioRepository;
	
	@GetMapping("/")
    public String forwardWalletList(Model model) {
        model.addAttribute("wallet", new Wallet());
        return "redirect:/wallet/list";
    }
	
	
	@GetMapping("/add")
    public String walletForm(Model model) {
		model.addAttribute("wallet", new Wallet());
		//model.addAttribute("coin", new Coin());
		//model.addAttribute("portfolio", new Portfolio());
		
		model.addAttribute("coinList", coinRepository.findAll());
		model.addAttribute("portfolioList", portfolioRepository.findAll());
		
        return "wallet_form";
    }
	
	@GetMapping("/results")
    public String walletResults(Model model) {
		model.addAttribute("wallet", new Wallet());
		model.addAttribute("coin", new Coin());
		model.addAttribute("portfolio", new Portfolio());
		

		model.addAttribute("walletList", walletRepository.findAll());
		
        return "wallet_results";
    }
	
	@PostMapping(path="/add") // Map ONLY Post Requests
	public String addNewWallet (
			@RequestParam Portfolio portfolio,
			@RequestParam Coin coin,
			@RequestParam String address,
			@RequestParam String description,
			Model model) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		model.addAttribute("wallet", new Wallet());
		
		Wallet wallet = new Wallet();

		wallet.setAddress(address);
		wallet.setDescription(description);
		wallet.setPortfolio(portfolio);
		wallet.setCoin(coin);
		walletRepository.save(wallet);
				
		String message = String.format("Wallet '%s' created", address);
		
		
		//return message;
		return "redirect:/wallet/list";
		
	}
	
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Wallet> getAllWallets() {
		// This returns a JSON or XML with the users
		return walletRepository.findAll();
	}
	
	@RequestMapping(value = "/byPortfolioId/{portfolioId}", method = RequestMethod.GET)
	public ResponseEntity<List<Wallet>> getWalletsByPortfolioId(@PathVariable("portfolioId") long portfolioId) {
		// This returns a JSON or XML with the users
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		if (wallets.isEmpty()) {
            System.out.println("Nothing found");
			return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Wallet>>(wallets, HttpStatus.OK);
		

	}
}
