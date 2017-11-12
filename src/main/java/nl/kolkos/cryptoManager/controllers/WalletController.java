package nl.kolkos.cryptoManager.controllers;

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

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;

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
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;	
	
	@Autowired
	@Qualifier(value = "portfolioRepository")
	private PortfolioRepository portfolioRepository;
	
	@Autowired
	@Qualifier(value = "depositRepository")
	private DepositRepository depositRepository;
	
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
		
		model.addAttribute("coinList", coinRepository.findAllByOrderByCoinMarketCapCoinSymbol());
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
				
		
		
		//return message;
		return "redirect:/wallet/results";
		
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
	
	// get wallet details
	@RequestMapping(value = "/showWallet/{walletId}", method = RequestMethod.GET)
	public String getWalletsByPortfolioId(@PathVariable("walletId") long walletId, Model model) {
		Wallet wallet = walletRepository.findById(walletId);
		
		// add this wallet to the model
		model.addAttribute("wallet", wallet);
		
		// get the coin from the wallet
		Coin coin = wallet.getCoin();
		
		// get the cmc coin
		CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
		
		// receive the current value
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		double currentCoinValue;
		try {
			org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(cmcCoin.getId(), "EUR");
			currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentCoinValue = 0;
		}
		
		// register this result
		CoinValue coinValue = new CoinValue();
		coinValue.setCoin(coin);
		coinValue.setValue(currentCoinValue);
		
		coinValueRepository.save(coinValue);
		
		// get the sum of all the deposited coins (amount) for this wallet
		double currentBalance = depositRepository.getSumOfAmountForWalletId(wallet.getId());
		// add to the model
		model.addAttribute("currentBalance", currentBalance);
		
		
		// get the total value for this wallet
		double currentValue = currentBalance * currentCoinValue;
		// add to the model
		model.addAttribute("currentValue", currentValue);
		
		// get the sum of all deposits (value) for this wallet
		double totalDeposited = depositRepository.getSumOfPurchaseValueForWalletId(wallet.getId());
		// add to the model
		model.addAttribute("totalDeposited", totalDeposited);
		
		// withdrawn is not implemented yet
		double totalWithdrawn = 0;
		// add to the model
		model.addAttribute("totalWithdrawn", totalWithdrawn);
		
		double totalInvested = totalDeposited - totalWithdrawn;
		// add to the model
		model.addAttribute("totalInvested", totalInvested);
		
		double profitLoss = currentValue - totalInvested;
		// add to the model
		model.addAttribute("profitLoss", profitLoss);
		
		
		// get the deposits for this wallet
		List<Deposit> deposits = depositRepository.findByWallet(wallet);
		
		// loop through the deposits
		for(Deposit deposit : deposits) {
			// calculate the current value of the deposit
			double currentDepositValue = deposit.getAmount() * currentCoinValue;
			// set this value
			deposit.setCurrentDepositValue(currentDepositValue);
			
			double currentDepositDifference = currentDepositValue - deposit.getPurchaseValue();
			// set this value
			deposit.setCurrentDepositDifference(currentDepositDifference);
						
		}
		
		// add the deposits to the model
		model.addAttribute("deposits", deposits);
		
		
		
		return "wallet_details";
	}
}
