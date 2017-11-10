package nl.kolkos.cryptoManager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/portfolio") // This means URL's start with /demo (after Application path)
public class PortfolioController {
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	@Autowired
	@Qualifier(value = "walletRepository")
	private WalletRepository walletRepository;
	
	@Autowired
	@Qualifier(value = "depositRepository")
	private DepositRepository depositRepository;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;	
	
	// forward to list
	@GetMapping("/")
    public String forwardRepositoryList(Model model) {
        return "redirect:/portfolio/list";
    }
	
	// send the form
	@GetMapping("/add")
    public String portfolioForm(Model model) {
		model.addAttribute("portfolio", new Portfolio());
        return "portfolio_form";
    }
	
	// handle the form
	@PostMapping(path="/add") // Map ONLY Post Requests
	public @ResponseBody String addNewPortfolio (
			@RequestParam String description,
			@RequestParam String name) {
				
		Portfolio portfolio = new Portfolio();
		portfolio.setDescription(description);
		portfolio.setName(name);
		portfolioRepository.save(portfolio);
				
		String message = String.format("Portfolio '%s' created", name);
		
		
		return message;
		
	}
	
	@GetMapping("/results")
    public String portfolioResults(Model model) {
		//model.addAttribute("coin", new Coin());
		model.addAttribute("portfolioList", portfolioRepository.findAll());
		
        return "portfolio_results";
    }
	
	// list all 
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Portfolio> getAllPortfolios() {
		// This returns a JSON or XML with the users
		return portfolioRepository.findAll();
	}
	
	// get portfolio details
	@RequestMapping(value = "/showPortfolio/{portfolioId}", method = RequestMethod.GET)
	public String getWalletsByPortfolioId(@PathVariable("portfolioId") long portfolioId, Model model) {
		// get the details
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		// add to model		
		model.addAttribute("portfolio", portfolio);
		
		// now get the attached wallets
		List<Wallet> wallets = walletRepository.findByPortfolio(portfolio);
		
		// get the amount of wallets
		int countWallets = wallets.size();
		// add to the model
		model.addAttribute("countWallets", countWallets);
		
		// create the api request object
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		
		// loop the wallets to get their values
		double totalPortfolioValue = 0;
		for(Wallet wallet : wallets) {
			// get the coin from this wallet
			Coin coin = wallet.getCoin();
			
			// get the total number of coins
			double currentWalletAmount = depositRepository.getSumOfAmountForWalletId(wallet.getId());
			
			// get the current market value for this coin
			double currentCoinValue = 0;
			
			try {
				org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(coin.getCoinName(), "EUR");
				currentCoinValue = Double.parseDouble((String) json.get("last"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// calculate the value of this wallet
			double currentWalletValue = currentWalletAmount * currentCoinValue;
			
			// add this value to the totalPortfolioValue
			totalPortfolioValue += currentWalletValue;
			
			// add this values to the wallet
			wallet.setCurrentWalletAmount(currentWalletAmount);
			wallet.setCurrentWalletValue(currentWalletValue);
			
			
			// save the coin values for future reference
			CoinValue coinValue = new CoinValue();
			coinValue.setCoin(coin);
			coinValue.setValue(currentCoinValue);
			coinValueRepository.save(coinValue);
			
		}
		
		// add the total portfolio value to the model
		model.addAttribute("totalPortfolioValue", totalPortfolioValue);
		
		// add the wallets to the model
		model.addAttribute("wallets", wallets);
		
		
		return "portfolio_details";
	}
	
}
