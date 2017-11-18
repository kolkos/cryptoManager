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
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.repositories.WithdrawalRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/withdrawal") // This means URL's start with /demo (after Application path)
public class WithdrawalController {
	@Autowired
	@Qualifier(value = "walletRepository")
	private WalletRepository walletRepository;
	
	@Autowired
	@Qualifier(value = "withdrawalRepository")
	private WithdrawalRepository withdrawalRepository;
	
	@Autowired
	@Qualifier(value = "coinRepository")
	private CoinRepository coinRepository;
	
	@Autowired
	@Qualifier(value = "portfolioRepository")
	private PortfolioRepository portfolioRepository;
	
	@GetMapping("/")
    public String forwardWithdrawalForm(Model model) {

        return "redirect:/withdrawal/results";
    }
	
	@GetMapping("/add")
    public String depositForm(Model model) {
		model.addAttribute("withdrawal", new Withdrawal());
        model.addAttribute("wallet", new Wallet());
        model.addAttribute("walletList", walletRepository.findAll());
        
        return "withdrawal_form";
    }
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public String addNewWithdrawal (
			@RequestParam Date withdrawalDate,
			@RequestParam Wallet wallet,
			@RequestParam double amount,
			@RequestParam double withdrawalValue,
			@RequestParam String remarks,
			@RequestParam(value="addAnotherWithdrawal", required=false) boolean addAnotherWithdrawal,
			@RequestParam(value="toCash", required=false) boolean toCash,
			Model model) {

		
		model.addAttribute("withdrawal", new Withdrawal());
		
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setWithdrawalDate(withdrawalDate);
		withdrawal.setWallet(wallet);
		withdrawal.setAmount(amount);
		withdrawal.setWithdrawalValue(withdrawalValue);
		withdrawal.setRemarks(remarks);
		withdrawal.setToCash(toCash);
		
		// now save it
		withdrawalRepository.save(withdrawal);
		
		
		
		String redirect;
		if(addAnotherWithdrawal) {
			redirect = "redirect:/withdrawal/add";
		}else {
			redirect = "redirect:/withdrawal/results";
		}
		
		
		return redirect;
	}
	
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Withdrawal> getAllDeposits() {
		// This returns a JSON or XML with the users
		return withdrawalRepository.findAll();
	}
	
	@GetMapping("/results")
    public String depositResults(
    		@RequestParam(value="filterByCoin", required=false) Coin coinFilter,
    		@RequestParam(value="filterByWallet", required=false) Wallet walletFilter,
    		@RequestParam(value="filterByPortfolio", required=false) Portfolio portfolioFilter,
    		Model model) {
		//model.addAttribute("portfolio", new Portfolio());
        //model.addAttribute("wallet", new Wallet());
        //model.addAttribute("coin", new Coin());
		
		
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
        model.addAttribute("walletList", walletRepository.findAll());
        model.addAttribute("portfolioList", portfolioRepository.findAll());
        
        String filterCoinId = "%";
        String filterWalletId = "%";
        String filterPortfolioId = "%";
        
        if(coinFilter != null) {
        		model.addAttribute("selectedCoin", coinFilter.getId());
        		filterCoinId = coinFilter.getId().toString();
        }
        if(walletFilter != null) {
	    		model.addAttribute("selectedWallet", walletFilter.getId());
	    		filterWalletId = walletFilter.getId().toString();
	    }
        if(portfolioFilter != null) {
	    		model.addAttribute("selectedPortfolio", portfolioFilter.getId());
	    		filterPortfolioId = portfolioFilter.getId().toString();
	    }
        
        // get all the deposits
 		List<Withdrawal> withdrawals = withdrawalRepository.filterResults(filterCoinId, filterWalletId, filterPortfolioId);
 		
 		// create the Api Handler object
 		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
 		
 		// loop through the deposits
 		for(Withdrawal withdrawal : withdrawals) {
 			// get the wallet for this deposit
 			Wallet wallet = withdrawal.getWallet();
 			
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
 			double currentWithdrawalValue = withdrawal.getAmount() * currentCoinValue;
 			// add this to this deposit
 			withdrawal.setCurrentWithdrawalValue(currentWithdrawalValue);
 			
 			// calculate the difference between the current value and the purchase value
 			double currentWithdrawalDifference = withdrawal.getWithdrawalValue() - currentWithdrawalValue;
 			// add this to this deposit
 			withdrawal.setCurrentWithdrawalDifference(currentWithdrawalDifference);
 			
 			
 		}
 		// now add the deposits to the model
 		model.addAttribute("withdrawals", withdrawals);
        
        
 		return "withdrawal_results";
    }
}
