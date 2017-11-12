package nl.kolkos.cryptoManager.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/deposit") // This means URL's start with /demo (after Application path)
public class DepositController {
	@Autowired
	@Qualifier(value = "depositRepository")
	private DepositRepository depositRepository;
	
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
    public String forwardDepositForm(Model model) {
        model.addAttribute("deposit", new Deposit());
        return "deposit_form";
    }
	
	@GetMapping("/add")
    public String depositForm(Model model) {
        model.addAttribute("deposit", new Deposit());
        model.addAttribute("wallet", new Wallet());
        model.addAttribute("walletList", walletRepository.findAll());
        
        return "deposit_form";
    }
	
	@GetMapping("/testForm")
    public String filterForm(Model model) {
		model.addAttribute("portfolio", new Portfolio());
        model.addAttribute("wallet", new Wallet());
        model.addAttribute("coin", new Coin());
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
        model.addAttribute("walletList", walletRepository.findAll());
        model.addAttribute("portfolioList", portfolioRepository.findAll());
        
        return "deposit_temp_filter_form";
    }
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public String addNewDeposit (
			@RequestParam Date depositDate,
			@RequestParam Wallet wallet,
			@RequestParam double amount,
			@RequestParam double purchaseValue,
			@RequestParam String remarks,
			@RequestParam(value="addAnotherDeposit", required=false) boolean addAnotherDeposit,
			Model model) {

		
		model.addAttribute("deposit", new Deposit());
		
		Deposit deposit = new Deposit();
		deposit.setDepositDate(depositDate);
		deposit.setWallet(wallet);
		deposit.setAmount(amount);
		deposit.setPurchaseValue(purchaseValue);
		deposit.setRemarks(remarks);
		
		depositRepository.save(deposit);
		
		String redirect;
		if(addAnotherDeposit) {
			redirect = "redirect:/deposit/add";
		}else {
			redirect = "redirect:/deposit/results";
		}
		
		
		return redirect;
	}

	@GetMapping(path="/list")
	public @ResponseBody Iterable<Deposit> getAllDeposits() {
		// This returns a JSON or XML with the users
		return depositRepository.findAll();
	}
	
	@GetMapping("/results")
    public String depositResults(Model model) {
		// get all the deposits
		List<Deposit> deposits = depositRepository.findAllByOrderByDepositDateAsc();
		
		// create the Api Handler object
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		
		// loop through the deposits
		for(Deposit deposit : deposits) {
			// get the wallet for this deposit
			Wallet wallet = deposit.getWallet();
			
			// get the coin for this wallet
			Coin coin = wallet.getCoin();
			
			// get the cmc coin
			CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
			
			// get the current value for this coin
			double currentCoinValue = 0;
			try {
				org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(cmcCoin.getId(), "EUR");
				currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// calculate the current value of this deposit
			double currentDepositValue = deposit.getAmount() * currentCoinValue;
			// add this to this deposit
			deposit.setCurrentDepositValue(currentDepositValue);
			
			// calculate the difference between the current value and the purchase value
			double currentDepositDifference = currentDepositValue - deposit.getPurchaseValue();
			// add this to this deposit
			deposit.setCurrentDepositDifference(currentDepositDifference);
			
			
		}
		// now add the deposits to the model
		model.addAttribute("deposits", deposits);
		
        return "deposit_results";
    }
	
}
