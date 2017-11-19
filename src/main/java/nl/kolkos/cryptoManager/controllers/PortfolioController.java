package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import nl.kolkos.cryptoManager.CoinValue;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.FormOption;
import nl.kolkos.cryptoManager.FormOptions;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.PortfolioChartLine;
import nl.kolkos.cryptoManager.PortfolioChartLineWallet;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.repositories.WithdrawalRepository;

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
	@Qualifier(value = "withdrawalRepository")
	private WithdrawalRepository withdrawalRepository;
	
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
			
			// get the cmc coin
			CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
			
			// get the total number of coins
			double currentWalletAmount = depositRepository.getSumOfAmountForWalletId(wallet.getId());
			
			// get the current market value for this coin
			double currentCoinValue = 0;
			
			try {
				org.json.JSONObject json = apiRequestHandler.currentCoinValueApiRequest(cmcCoin.getId(), "EUR");
				currentCoinValue = Double.parseDouble((String) json.get("price_eur"));
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
	
	
	// list all 
	@GetMapping(path="/test")
	public @ResponseBody List<PortfolioChartLine> testList() {
		// This returns a JSON or XML with the users
		
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(1L);
		
		// now determine the begin and end time
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -24);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, 0);
		
		List<PortfolioChartLine> portfolioChartLines = new ArrayList<>();
		
		// loop through the times
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, 60), date = start.getTime()) {
			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar endInterval = Calendar.getInstance();
			endInterval.setTime(date);
			endInterval.add(Calendar.MINUTE, 60);
			endInterval.add(Calendar.SECOND, -1);
			
			PortfolioChartLine portfolioChartLine = new PortfolioChartLine();
			portfolioChartLine.setDate(endInterval.getTime());
			
			double totalInvestment = 0;
			
			// loop through the wallets again
			for(Wallet wallet : wallets) {
				long walletId = wallet.getId();
				long coinId = wallet.getCoin().getId();
				
				// ---- get the totals for deposits
				// get the total purchase value
				double totalPurchaseValue = depositRepository.getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(walletId, endInterval.getTime());
				// get the amount purchased
				double totalAmountDeposited = depositRepository.getSumOfAmountForWalletIdAndBeforeDepositDate(walletId, endInterval.getTime());
				
				// --- get the values for the withdrawals
				// get the to cash value
				double totalWithDrawnToCashValue = withdrawalRepository.getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(walletId, endInterval.getTime());
				// get the total amount of withdrawals
				double totalAmountWithdrawn = withdrawalRepository.getSumOfAmountForWalletIdAndBeforeWithdrawalDate(walletId, endInterval.getTime());
				
				// get the total amount
				double totalAmount = totalAmountDeposited - totalAmountWithdrawn;
				// calculate the investment
				double totalInvested = totalPurchaseValue - totalWithDrawnToCashValue;
				
				// get the value of the coin for this moment
				double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), endInterval.getTime());
				
				
				
				// add this investment to the total investment
				totalInvestment += totalInvested;
				
				double value = avgValue * totalAmount;
				
				PortfolioChartLineWallet portfolioChartLineWallet = new PortfolioChartLineWallet();
				portfolioChartLineWallet.setWalletName(wallet.getAddress());
				portfolioChartLineWallet.setWalletValue(value);
				
				// now push it to the portfolioChartLine
				portfolioChartLine.getPortfolioChartLineWallets().add(portfolioChartLineWallet);
				
			}
			
			// add the total investment
			portfolioChartLine.setTotalInvested(totalInvestment);
			
			// push it to the list
			portfolioChartLines.add(portfolioChartLine);
			
		}
		
		return portfolioChartLines;
		
		
	}
	
	
	@RequestMapping(value = "/chart/{portfolioId}", method = RequestMethod.GET)
    public String coinChart(
    		@PathVariable("portfolioId") long portfolioId,
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
		
		// set the get parameters
		model.addAttribute("lastHours", lastHours);
		model.addAttribute("intervalInMinutes", intervalInMinutes);
		
		
		FormOptions formOptions = new FormOptions();
		List<FormOption> hourOptions = formOptions.defaultSetHourOptions();
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		// add the minute options
		List<FormOption> minuteOptions = formOptions.defaultSetMinuteOptions();
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		
		// get the wallets
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		
		List<String> walletAddresses = new ArrayList<>();
		
		// loop the wallets to update the current coin value
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		for(Wallet wallet : wallets) {
			// add the wallet name to the list
			walletAddresses.add(wallet.getAddress());
			
			
			// get the coin for this wallet
			Coin coin = wallet.getCoin();
			
			
			// nog get the cmc Coin by this coin
			CoinMarketCapCoin cmcCoin = coin.getCoinMarketCapCoin();
			
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
		}
		
		// now determine the begin and end time
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, 0);
		
		List<PortfolioChartLine> portfolioChartLines = new ArrayList<>();
		
		// loop through the times
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, intervalInMinutes), date = start.getTime()) {
			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar endInterval = Calendar.getInstance();
			endInterval.setTime(date);
			endInterval.add(Calendar.MINUTE, intervalInMinutes);
			endInterval.add(Calendar.SECOND, -1);
			
			PortfolioChartLine portfolioChartLine = new PortfolioChartLine();
			portfolioChartLine.setDate(endInterval.getTime());
			
			double totalInvestment = 0;
			
			// loop through the wallets again
			for(Wallet wallet : wallets) {
				long walletId = wallet.getId();
				long coinId = wallet.getCoin().getId();
				
				// ---- get the totals for deposits
				// get the total purchase value
				double totalPurchaseValue = depositRepository.getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(walletId, endInterval.getTime());
				// get the amount purchased
				double totalAmountDeposited = depositRepository.getSumOfAmountForWalletIdAndBeforeDepositDate(walletId, endInterval.getTime());
				
				// --- get the values for the withdrawals
				// get the to cash value
				double totalWithDrawnToCashValue = withdrawalRepository.getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(walletId, endInterval.getTime());
				// get the total amount of withdrawals
				double totalAmountWithdrawn = withdrawalRepository.getSumOfAmountForWalletIdAndBeforeWithdrawalDate(walletId, endInterval.getTime());
				
				// get the total amount
				double totalAmount = totalAmountDeposited - totalAmountWithdrawn;
				// calculate the investment
				double totalInvested = totalPurchaseValue - totalWithDrawnToCashValue;
				
				// get the value of the coin for this moment
				double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), endInterval.getTime());
				
				// prevent 0 by using the last known value
				if(avgValue == 0) {
					System.out.println("avgValue = 0...");
					avgValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coinId, startInterval.getTime());
				}
				
				
				// add this investment to the total investment
				totalInvestment += totalInvested;
				
				double value = avgValue * totalAmount;
				
				PortfolioChartLineWallet portfolioChartLineWallet = new PortfolioChartLineWallet();
				portfolioChartLineWallet.setWalletName(wallet.getAddress());
				portfolioChartLineWallet.setWalletValue(value);
				
				// now push it to the portfolioChartLine
				portfolioChartLine.getPortfolioChartLineWallets().add(portfolioChartLineWallet);
				
			}
			
			// add the total investment
			portfolioChartLine.setTotalInvested(totalInvestment);
			
			// push it to the list
			portfolioChartLines.add(portfolioChartLine);
			
		}
		
		model.addAttribute("portfolioChartLines",portfolioChartLines);
		model.addAttribute("walletAddresses",walletAddresses);
		
		
		return "portfolio_chart";
	}
	
}
