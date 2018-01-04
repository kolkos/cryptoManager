package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.FormOption;
import nl.kolkos.cryptoManager.FormOptions;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.WalletChartLine;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;

import nl.kolkos.cryptoManager.services.PortfolioService;
import nl.kolkos.cryptoManager.services.TransactionService;
import nl.kolkos.cryptoManager.services.UserService;
import nl.kolkos.cryptoManager.services.WalletService;

import org.springframework.beans.factory.annotation.Qualifier;



@Controller    // This means that this class is a Controller
@RequestMapping(path="/wallet") // This means URL's start with /demo (after Application path)
public class WalletController {
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	@Qualifier(value = "coinRepository")
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;	
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
		
	@GetMapping("/add")
    public String walletForm(Model model) {
		model.addAttribute("wallet", new Wallet());
		//model.addAttribute("coin", new Coin());
		//model.addAttribute("portfolio", new Portfolio());
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
		model.addAttribute("portfolioList", portfolioService.findByUsers_email(userService.findLoggedInUsername()));
		
        return "wallet_form";
    }
	
	@GetMapping("/results")
    public String walletResults(Model model) {
		
		model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
		
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
		walletService.saveWallet(wallet);
		
		//return message;
		return "redirect:/wallet/results";
		
	}
	
	// edit wallet - GET
	@RequestMapping(value = "/edit/{walletId}", method = RequestMethod.GET)
	public String editWalletForm(@PathVariable("walletId") long walletId, Model model) {
		// check if the wallet exists
		if(!walletService.checkIfWalletExists(walletId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		// check if the current user has access to this wallet
		boolean access = userService.checkIfCurrentUserIsAuthorizedToWallet(walletId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		model.addAttribute("wallet", wallet);
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
		model.addAttribute("portfolioList", portfolioService.findByUsers_email(userService.findLoggedInUsername()));
		
		return "wallet_edit"; 
	}
	
	// edit wallet - GET
	@RequestMapping(value = "/delete/{walletId}", method = RequestMethod.POST)
	public String deleteWallet(@PathVariable("walletId") long walletId, Model model) {
		// check if the wallet exists
		if(!walletService.checkIfWalletExists(walletId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		// check if the current user has access to this wallet
		boolean access = userService.checkIfCurrentUserIsAuthorizedToWallet(walletId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		Wallet wallet = walletService.findById(walletId);
		
		// use the service to delete the wallet
		// this will automatically delete the attached deposits and withdrawals
		walletService.deleteWallet(wallet);
		
		
		return "redirect:/wallet/results";
	}
	
	// edit wallet - POST
	@RequestMapping(value = "/edit/{walletId}", method = RequestMethod.POST)
	public String editWallet(@PathVariable("walletId") long walletId, 
			@RequestParam Portfolio portfolio,
			@RequestParam Coin coin,
			@RequestParam String address,
			@RequestParam String description,
			Model model) {
		// check if the wallet exists
		if(!walletService.checkIfWalletExists(walletId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		// check if the current user has access to this wallet
		boolean access = userService.checkIfCurrentUserIsAuthorizedToWallet(walletId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		wallet.setAddress(address);
		wallet.setDescription(description);
		wallet.setPortfolio(portfolio);
		wallet.setCoin(coin);
		walletService.saveWallet(wallet);
				
		return "redirect:/wallet/details/" + walletId;
	}
		
	// get wallet details
	@RequestMapping(value = "/details/{walletId}", method = RequestMethod.GET)
	public String getWalletsByPortfolioId(@PathVariable("walletId") long walletId, Model model) {
		// check if the wallet exists
		if(!walletService.checkIfWalletExists(walletId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		// check if the current user has access to this wallet
		boolean access = userService.checkIfCurrentUserIsAuthorizedToWallet(walletId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		Wallet wallet = walletService.findById(walletId);
		
		// get the values for the wallet
		wallet = walletService.getWalletValues(wallet);
		model.addAttribute("wallet", wallet);
		
		// get the transactions
		model.addAttribute("transactions", transactionService.findByWallet(wallet));
		
		
		return "wallet_details";
	}
	
	@RequestMapping(value = "/chart/{walletId}", method = RequestMethod.GET)
    public String coinChart(
    		@PathVariable("walletId") long walletId,
    		@RequestParam(value="lastHours", required=false) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=false) Integer intervalInMinutes,
    		Model model) {
        
		// check if the wallet exists
		if(!walletService.checkIfWalletExists(walletId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		// check if the current user has access to this wallet
		boolean access = userService.checkIfCurrentUserIsAuthorizedToWallet(walletId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		
		// check if the values are null
		if(lastHours == null) {
			lastHours = 1;
		}
		if(intervalInMinutes == null) {
			intervalInMinutes = 5;
		}
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		FormOptions formOptions = new FormOptions();
		// add the periods
		List<FormOption> hourOptions = formOptions.defaultSetHourOptions();
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		// add the interval options
		List<FormOption> minuteOptions = formOptions.defaultSetMinuteOptions();
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		// get the wallet
		Wallet wallet = walletService.findById(walletId);
		model.addAttribute("wallet",wallet);
		model.addAttribute("walletAddress", wallet.getCensoredWalletAddress());
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, 0);
		
		// get the last known value, just to be sure
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(wallet.getCoin().getId(), end.getTime());
		
		List<WalletChartLine> walletChartLines = new ArrayList<>();
		
		// loop through the dates
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, intervalInMinutes), date = start.getTime()) {

			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar lastMinute = Calendar.getInstance();
			lastMinute.setTime(date);
			lastMinute.add(Calendar.MINUTE, intervalInMinutes);
			lastMinute.add(Calendar.SECOND, -1);
			
			
			// get the historical values for this wallet
			Wallet historicalWallet = new Wallet(wallet);
			historicalWallet = walletService.getWalletHistoricalValues(historicalWallet, startInterval.getTime(), lastMinute.getTime());
			
			
			// get the value of the coin for this moment
			double walletValue = historicalWallet.getCurrentWalletValue();
			
			// if the value is 0, use the last known value
			if(walletValue == 0) {
				walletValue = lastKnownValue;
			}else {
				// set the last know value to the current value
				lastKnownValue = walletValue;
			}
			
						
			// add it to a wallet chart line object
			WalletChartLine walletChartLine = new WalletChartLine();
			walletChartLine.setDate(lastMinute.getTime());
			walletChartLine.setValue(walletValue);
			walletChartLine.setTotalInvested(historicalWallet.getCurrentWalletInvestment());
						
			// add it to the list
			walletChartLines.add(walletChartLine);
			
			// done, destroy the historical wallet
			historicalWallet = null;
		}
		
		model.addAttribute("walletChartLines", walletChartLines);
		
        return "wallet_chart";
    }
}
