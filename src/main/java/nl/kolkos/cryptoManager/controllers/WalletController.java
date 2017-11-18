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
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.ApiRequestHandler;
import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.FormOption;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.WalletChartLine;
import nl.kolkos.cryptoManager.Withdrawal;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.repositories.WithdrawalRepository;

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
	
	@Autowired
	@Qualifier(value = "withdrawalRepository")
	private WithdrawalRepository withdrawalRepository;
	
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
		double currentBalance = 0;
		double totalAmountDeposited = depositRepository.getSumOfAmountForWalletId(wallet.getId());
		double totalAmountWithdrawn = withdrawalRepository.getSumOfAmountForWalletId(wallet.getId());
		currentBalance = totalAmountDeposited - totalAmountWithdrawn;
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
		
		// total withdrawn from wallet
		double totalWithdrawn = withdrawalRepository.getSumOfWithdrawalsForWalletId(wallet.getId());
		// add to the model
		model.addAttribute("totalWithdrawn", totalWithdrawn);
		
		// total withdrawn from wallet
		double totalWithdrawnToCash = withdrawalRepository.getSumOfWithdrawalsToCashForWalletId(wallet.getId());
		// add to the model
		model.addAttribute("totalWithdrawnToCash", totalWithdrawnToCash);
		
		// calculate the investment
		double totalInvested = totalDeposited - totalWithdrawnToCash;
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
			
			// calculate the difference
			double currentDepositDifference = currentDepositValue - deposit.getPurchaseValue();
			// set this value
			deposit.setCurrentDepositDifference(currentDepositDifference);
						
		}
		
		// add the deposits to the model
		model.addAttribute("deposits", deposits);
		
		
		// now get the withdrawals
		List<Withdrawal> withdrawals = withdrawalRepository.findByWallet(wallet);
		for(Withdrawal withdrawal : withdrawals) {
			// calculate the current value of the withdrawal
			double currentWithdrawalValue = withdrawal.getAmount() * currentCoinValue;
			withdrawal.setCurrentWithdrawalValue(currentWithdrawalValue);
			
			// calculate the difference between the current value and the purchase value
 			double currentWithdrawalDifference = withdrawal.getWithdrawalValue() - currentWithdrawalValue;
 			// add this to this deposit
 			withdrawal.setCurrentWithdrawalDifference(currentWithdrawalDifference);
			
		}
		model.addAttribute("withdrawals", withdrawals);
		
		
		
		return "wallet_details";
	}
	
	@RequestMapping(value = "/chart/{walletId}", method = RequestMethod.GET)
    public String coinChart(
    		@PathVariable("walletId") long walletId,
    		@RequestParam(value="lastHours", required=false) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=false) Integer intervalInMinutes,
    		Model model) {
        
		// check if the values are null
		if(lastHours == null) {
			lastHours = 1;
		}
		if(intervalInMinutes == null) {
			intervalInMinutes = 5;
		}
		
		List<FormOption> hourOptions = new ArrayList<>();
		FormOption hourOption = new FormOption("1", "Last 1 hour");
		hourOptions.add(hourOption);
		hourOption = new FormOption("2", "Last 2 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("3", "Last 3 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("4", "Last 4 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("5", "Last 5 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("24", "Last 24 Hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("48", "Last 2 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("168", "Last 7 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("336", "Last 14 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("720", "Last 30 days");
		hourOptions.add(hourOption);
		
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		
		// add the minute options
		List<FormOption> minuteOptions = new ArrayList<>();
		FormOption minuteOption = new FormOption("5", "5 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10", "10 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("15", "15 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("30", "30 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("60", "1 hour");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("120", "2 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("300", "5 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("720", "12 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("1440", "1 day");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10080", "1 week");
		minuteOptions.add(minuteOption);
		
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		// get the wallet
		Wallet wallet = walletRepository.findById(walletId);
		
		model.addAttribute("walletId",walletId);
		
		// get the coin for this wallet
		Coin coin = wallet.getCoin();
		long coinId = coin.getId();
		
		// nog get the cmc Coin by this coin
		
		CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
		model.addAttribute("coinName",cmcCoin.getName());
		model.addAttribute("walletAddress",wallet.getAddress());
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		// update the coin price
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
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		//end.add(Calendar.HOUR, 1);
		end.set(Calendar.SECOND, 0);
		
		// get the last known value, just to be sure
		double lastKnownValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coinId, start.getTime());
		
		
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
			
			
			// ---- get the totals for deposits
			// get the total purchase value
			double totalPurchaseValue = depositRepository.getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(walletId, lastMinute.getTime());
			
			// get the amount purchased
			double totalAmountDeposited = depositRepository.getSumOfAmountForWalletIdAndBeforeDepositDate(walletId, lastMinute.getTime());
			
			
			// --- get the values for the withdrawals
			// get the to cash value
			double totalWithDrawnToCashValue = withdrawalRepository.getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(walletId, lastMinute.getTime());
			
			// get the total amount of withdrawals
			double totalAmountWithdrawn = withdrawalRepository.getSumOfAmountForWalletIdAndBeforeWithdrawalDate(walletId, lastMinute.getTime());
			
			// get the total amount
			double totalAmount = totalAmountDeposited - totalAmountWithdrawn;
			
			
			// calculate the investment
			double totalInvested = totalPurchaseValue - totalWithDrawnToCashValue;
			
			
			// get the value of the coin for this moment
			double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), lastMinute.getTime());
			
			// if the avg value is 0, then use the last known value
			if(avgValue == 0) {
				avgValue = lastKnownValue;
			}else {
				lastKnownValue = avgValue;
			}
			
			// calculate the value for this moment
			double value = avgValue * totalAmount;
			
						
			// add it to a wallet chart line object
			WalletChartLine walletChartLine = new WalletChartLine();
			walletChartLine.setDate(lastMinute.getTime());
			walletChartLine.setValue(value);
			walletChartLine.setTotalInvested(totalInvested);
			
			
			// add it to the list
			walletChartLines.add(walletChartLine);
			
		}
		
		model.addAttribute("walletChartLines", walletChartLines);
		
        return "wallet_chart";
    }
}
