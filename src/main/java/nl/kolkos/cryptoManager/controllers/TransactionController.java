package nl.kolkos.cryptoManager.controllers;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;
import nl.kolkos.cryptoManager.repositories.TransactionRepository;
import nl.kolkos.cryptoManager.repositories.TransactionTypeRepository;
import nl.kolkos.cryptoManager.services.CoinService;
import nl.kolkos.cryptoManager.services.PortfolioService;
import nl.kolkos.cryptoManager.services.TransactionService;
import nl.kolkos.cryptoManager.services.UserService;
import nl.kolkos.cryptoManager.services.WalletService;

@Controller
@RequestMapping(path="/transaction")
public class TransactionController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private CoinService CoinService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	
	@Autowired TransactionRepository transactionRepository;
	
	@Autowired
	private TransactionService transactionService;
	

	
	@GetMapping("/add")
    public String depositForm(Model model) {
		model.addAttribute("transaction", new Transaction());
		model.addAttribute("transactionTypes", transactionTypeRepository.findAll());
        model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
        
        return "transaction_form";
    }
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public String addNewWithdrawal (
			@RequestParam Date transactionDate,
			@RequestParam TransactionType transactionType,
			@RequestParam Wallet wallet,
			@RequestParam double amount,
			@RequestParam double value,
			@RequestParam String remarks,
			@RequestParam(value="addAnotherTransaction", required=false) boolean addAnotherTransaction,
			@RequestParam(value="toCash", required=false) boolean toCash,
			Model model) {
		
		
		// correct the withdrawn to cash, if the transaction type is deposit
		if(transactionType.getType().equals("Deposit")) {
			toCash = false;
		}
		
		
		Transaction transaction = new Transaction();
		transaction.setTransactionDate(transactionDate);
		transaction.setTransactionType(transactionType);
		transaction.setWallet(wallet);
		transaction.setAmount(amount);
		transaction.setValue(value);
		transaction.setRemarks(remarks);
		transaction.setWithdrawnToCash(toCash);
		
		// save the transaction
		transactionService.save(transaction);
		
		String redirect;
		if(addAnotherTransaction) {
			redirect = "redirect:/transaction/add";
		}else {
			redirect = "redirect:/transaction/results";
		}
		
		return redirect;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/results")
	public String showResults(Model model, 
			@RequestParam(name = "search", required=false) String search,
    			@RequestParam(name = "sortBy", defaultValue = "transactionDate") String sortBy,
    			@RequestParam(name = "direction", defaultValue = "DESC") String direction) {
		
		String usersEmail = userService.findLoggedInUsername();
		
		model.addAttribute("coinList", CoinService.findAllByOrderByCoinMarketCapCoinSymbol());
		model.addAttribute("transactionTypes", transactionTypeRepository.findAll());
        model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(usersEmail));
        model.addAttribute("portfolioList", portfolioService.findByUsers_email(usersEmail));
		
        
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
        
        
        
        	model.addAttribute("transactions", transactionService.findByWalletPortfolioUsersEmail(usersEmail, search, sortBy, direction));
        	
        
		
        
        
		return "transaction_results";
	}
	
}
