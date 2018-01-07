package nl.kolkos.cryptoManager.controllers;

import java.sql.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nl.kolkos.cryptoManager.Currency;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
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
	
	@Autowired
	private TransactionService transactionService;
	
	@Resource(name = "currency")
	private Currency currency;

	
	@GetMapping("/add")
    public String depositForm(Model model) {
		model.addAttribute("transaction", new Transaction());
		model.addAttribute("transactionTypes", transactionTypeRepository.findAll());
        model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
        model.addAttribute("currency", currency);
        
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
			Model model) {
		
		Transaction transaction = new Transaction();
		transaction.setTransactionDate(transactionDate);
		transaction.setTransactionType(transactionType);
		transaction.setWallet(wallet);
		transaction.setAmount(amount);
		transaction.setValue(value);
		transaction.setRemarks(remarks);
		
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
	
	@GetMapping("/advanced")
    public String depositFormAdvanced(Model model) {
		model.addAttribute("transaction", new Transaction());
		model.addAttribute("transactionTypes", transactionTypeRepository.findAll());
        model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
        
        
        return "transaction_form_advanced";
    }
	
	@PostMapping(path="/advanced") // Map ONLY POST Requests
	public String addAdvancedWithdrawal (
			@RequestParam Date transactionDate,
			@RequestParam long fromWallet,
			@RequestParam double amountSold,
			@RequestParam long toWallet,
			@RequestParam double amountBought,
			@RequestParam double transactionValue,
			@RequestParam String remarks,
			@RequestParam(value="addAnotherTransaction", required=false) boolean addAnotherTransaction,
			Model model) {
		
		
		TransactionType withdrawal = transactionTypeRepository.findByType("Withdrawal");
		TransactionType deposit = transactionTypeRepository.findByType("Deposit");
		
		// check if the user is authorized to the wallets
		boolean accessFromWallet = userService.checkIfCurrentUserIsAuthorizedToWallet(fromWallet);
		boolean accessToWallet = userService.checkIfCurrentUserIsAuthorizedToWallet(fromWallet);
		if(!accessFromWallet || !accessToWallet) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		// now get the wallets
		Wallet fromWalletObject = walletService.findById(fromWallet);
		Wallet toWalletObject = walletService.findById(toWallet);
		
		// check if both wallets exists
		if(fromWalletObject == null || toWalletObject == null) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "wallet");
			return "error_page";
		}
		
		// append the from and to coin to the transaction
		String fromCoinSymbol = fromWalletObject.getCoin().getCoinMarketCapCoin().getSymbol();
		String toCoinSymbol = toWalletObject.getCoin().getCoinMarketCapCoin().getSymbol();
		remarks = String.format("%s -> %s (%s)", fromCoinSymbol, toCoinSymbol, remarks);
		
		// now create and save the transactions
		// the withdrawal
		transactionService.createTransaction(transactionDate, amountSold, transactionValue, fromWalletObject, remarks, withdrawal);
		// the deposit
		transactionService.createTransaction(transactionDate, amountBought, transactionValue, toWalletObject, remarks, deposit);
		
		String redirect;
		if(addAnotherTransaction) {
			redirect = "redirect:/transaction/advanced";
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
        model.addAttribute("currency", currency);
        
        model.addAttribute("transactions", transactionService.findByWalletPortfolioUsersEmail(usersEmail, search, sortBy, direction));
        
		return "transaction_results";
	}
	
	@RequestMapping(value = "/details/{transactionId}", method = RequestMethod.GET)
	public String showTransactionDetails(@PathVariable("transactionId") long transactionId, Model model) {
		// check if the deposit exists
		if(!transactionService.checkIfTransactionExists(transactionId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToTransaction(transactionId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		
		// get the entity
		Transaction transaction = transactionService.findById(transactionId);
		
		model.addAttribute("transaction", transaction);
		model.addAttribute("currency", currency);
		
		return "transaction_details";
	}
	
	@PostMapping(path="/delete") // Map ONLY POST Requests
	public String deleteTransaction (
			@RequestParam(value="transactionId", required=true) long transactionId,
			@RequestParam(value="confirmDelete", required=true) boolean confirmDelete,
			Model model) {
		// check if the deposit exists
		if(!transactionService.checkIfTransactionExists(transactionId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToTransaction(transactionId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		// get the entity
		Transaction transaction = transactionService.findById(transactionId);
		
		if(confirmDelete) {
			transactionService.delete(transaction);
		}
		
		
		return "redirect:/transaction/results";
	}
	
	@RequestMapping(value = "/edit/{transactionId}", method = RequestMethod.GET)
	public String showTransactionEditForm(@PathVariable("transactionId") long transactionId, Model model) {
		// check if the deposit exists
		if(!transactionService.checkIfTransactionExists(transactionId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToTransaction(transactionId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		
		// get the entity
		Transaction transaction = transactionService.findById(transactionId);
		model.addAttribute("currency", currency);
		model.addAttribute("transaction", transaction);
		model.addAttribute("transactionTypes", transactionTypeRepository.findAll());
        model.addAttribute("walletList", walletService.findByPortfolioUsersEmail(userService.findLoggedInUsername()));
		
		return "transaction_edit";
	}
	
	@RequestMapping(value = "/edit/{transactionId}", method = RequestMethod.POST)
    public String updateDeposit(@PathVariable("transactionId") long transactionId, 
    		@RequestParam Date transactionDate,
		@RequestParam TransactionType transactionType,
		@RequestParam Wallet wallet,
		@RequestParam double amount,
		@RequestParam double value,
		@RequestParam String remarks,
    		Model model) {
		// check if the deposit exists
		if(!transactionService.checkIfTransactionExists(transactionId)) {
			model.addAttribute("notFoundError", true);
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		// check if the current user has access to this deposit
		boolean access = userService.checkIfCurrentUserIsAuthorizedToTransaction(transactionId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("authorizationError", true);
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "transaction");
			return "error_page";
		}
		
		Transaction transaction = transactionService.findById(transactionId);
		transaction.setTransactionDate(transactionDate);
		transaction.setTransactionType(transactionType);
		transaction.setWallet(wallet);
		transaction.setAmount(amount);
		transaction.setValue(value);
		transaction.setRemarks(remarks);
		
		transactionService.save(transaction);
		
        return "redirect:/transaction/details/" + transactionId;
    }
}
