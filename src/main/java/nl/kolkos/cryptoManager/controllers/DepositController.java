package nl.kolkos.cryptoManager.controllers;

import java.sql.Date;
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
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.services.DepositService;
import nl.kolkos.cryptoManager.services.UserService;

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
	
	@Autowired
	private DepositService depositService;
	
	@Autowired
	private UserService userService;
		
	@GetMapping("/add")
    public String depositForm(Model model) {
        model.addAttribute("deposit", new Deposit());
        model.addAttribute("wallet", new Wallet());
        model.addAttribute("walletList", walletRepository.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
        
        return "deposit_form";
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
	
	@RequestMapping(value = "/edit/{depositId}", method = RequestMethod.GET)
    public String editDepositForm(@PathVariable("depositId") long depositId, Model model) {
		// check if the deposit exists
		if(!depositService.checkIfDepositExists(depositId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToDeposit(depositId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		Deposit deposit = depositService.findById(depositId);
		model.addAttribute("deposit", deposit);
        
        
        model.addAttribute("walletList", walletRepository.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
        
        return "deposit_edit";
    }
	
	@RequestMapping(value = "/edit/{depositId}", method = RequestMethod.POST)
    public String updateDeposit(@PathVariable("depositId") long depositId, 
    		@RequestParam Date depositDate,
		@RequestParam Wallet wallet,
		@RequestParam double amount,
		@RequestParam double purchaseValue,
		@RequestParam String remarks,
    		Model model) {
		// check if the deposit exists
		if(!depositService.checkIfDepositExists(depositId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToDeposit(depositId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		Deposit deposit = depositService.findById(depositId);
		deposit.setDepositDate(depositDate);
		deposit.setWallet(wallet);
		deposit.setAmount(amount);
		deposit.setPurchaseValue(purchaseValue);
		deposit.setRemarks(remarks);
		
		depositService.save(deposit);
		
        return "redirect:/deposit/details/" + depositId;
    }
	
	
	@GetMapping("/results")
    public String depositResults(
    		@RequestParam(value="filterByCoin", required=false) Coin coinFilter,
    		@RequestParam(value="filterByWallet", required=false) Wallet walletFilter,
    		@RequestParam(value="filterByPortfolio", required=false) Portfolio portfolioFilter,
    		@RequestParam(name = "page", defaultValue = "1") int pageNumber,
		@RequestParam(name = "sortBy", defaultValue = "depositDate") String sortBy,
		@RequestParam(name = "direction", defaultValue = "DESC") String direction,
    		Model model) {
		
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
        model.addAttribute("walletList", walletRepository.findAll());
        model.addAttribute("portfolioList", portfolioRepository.findAll());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        
        model.addAttribute("numberOfResults", depositService.getNumberOfDeposits());
        model.addAttribute("page", pageNumber);
        
        
        // get all the deposits
 		List<Deposit> deposits = depositService.findByWalletPortfolioUsersEmail(pageNumber, sortBy, direction, userService.findLoggedInUsername());
 		
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
	
	@RequestMapping(value = "/details/{depositId}", method = RequestMethod.GET)
	public String showDepositDetails(@PathVariable("depositId") long depositId, Model model) {
		// check if the deposit exists
		if(!depositService.checkIfDepositExists(depositId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToDeposit(depositId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		
		// get the entity
		Deposit deposit = depositRepository.findById(depositId);
		
		model.addAttribute("deposit", deposit);
		
		return "deposit_details";
	}
	
	@PostMapping(path="/delete") // Map ONLY POST Requests
	public String deleteDeposit (
			@RequestParam(value="depositId", required=true) long depositId,
			@RequestParam(value="confirmDelete", required=true) boolean confirmDelete,
			Model model) {
		// check if the deposit exists
		if(!depositService.checkIfDepositExists(depositId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToDeposit(depositId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "deposit");
			return "error_page";
		}
		
		Deposit deposit = depositService.findById(depositId);
		
		if(confirmDelete) {
			depositRepository.delete(deposit);
		}
		
		
		return "redirect:/deposit/results";
	}
	
}
