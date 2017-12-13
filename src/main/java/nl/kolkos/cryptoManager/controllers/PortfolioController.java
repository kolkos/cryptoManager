package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import nl.kolkos.cryptoManager.FormOption;
import nl.kolkos.cryptoManager.FormOptions;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.PortfolioChartLine;
import nl.kolkos.cryptoManager.PortfolioChartLineWallet;
import nl.kolkos.cryptoManager.PortfolioLineChartRoiValue;
import nl.kolkos.cryptoManager.PortfolioPieChartValue;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.CoinValueRepository;
import nl.kolkos.cryptoManager.repositories.DepositRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.UserRepository;
import nl.kolkos.cryptoManager.repositories.WalletRepository;
import nl.kolkos.cryptoManager.repositories.WithdrawalRepository;
import nl.kolkos.cryptoManager.services.UserService;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/portfolio") // This means URL's start with /portfolio (after Application path)
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
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
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
	public String addNewPortfolio (
			@RequestParam String description,
			@RequestParam String name, 
			Model model) {
				
		Portfolio portfolio = new Portfolio();
		portfolio.setDescription(description);
		portfolio.setName(name);
		
		
		String username = userService.findLoggedInUsername();
		User currentUser = userService.findUserByEmail(username);
		
		
		// create a empty set of users for this portfolio
		Set<User> users = new HashSet<>();
		// add this user
		users.add(currentUser);
		
		// add this set to the portfolio
		portfolio.setUsers(users);
		
		// get the current portfolio set for this user
		Set<Portfolio> portfolios = portfolioRepository.findByUsers_email(username);
		// add this portfolio to the set
		portfolios.add(portfolio);
		// now add the portfolio to the current user
		currentUser.setPortfolios(portfolios);
		
		// finally save both objects
		userService.updateUser(currentUser);
		portfolioRepository.save(portfolio);
		
		
				
		return "redirect:/portfolio/results";
		
	}
	
	
	@GetMapping("/results")
    public String portfolioResults(Model model) {

		// get the portfolios for the logged in user
		Set<Portfolio> portfolioList = portfolioRepository.findByUsers_email(userService.findLoggedInUsername());
		
		model.addAttribute("portfolioList", portfolioList);
		
		
        return "portfolio_results";
        
        
    }
	
	// list all 
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Portfolio> getAllPortfolios() {
		// This returns a JSON or XML with the users
		return portfolioRepository.findAll();
	}
	
	// handle get for the access page
	@RequestMapping(value = "/access/{portfolioId}", method = RequestMethod.GET)
	public String grantAccessToPortfolio(@PathVariable("portfolioId") long portfolioId, Model model) {
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		// get the user with access to this portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		model.addAttribute("users", portfolio.getUsers());
		
		return "portfolio_access";
	}
	
	// handle post for the access page
	@RequestMapping(value = "/access/{portfolioId}", method = RequestMethod.POST)
	public String addUserAccessToPortfolio(@PathVariable("portfolioId") long portfolioId, 
			@RequestParam(value="mail", required=false) String mail,
			Model model) {
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		// check if the e-mail address exists
		if(userService.countByEmail(mail) > 0) {
			
			// get the user object for the mail address
			User newtUserForPortfolio = userService.findUserByEmail(mail);
			
			// get the current portfolio object
			Portfolio currentPortfolio = portfolioRepository.findById(portfolioId);
			
			// get the current list of users for the portfolio
			Set<User> users = userService.findByPortfolios_Id(portfolioId);
			// add the new user to the portfolio set
			users.add(newtUserForPortfolio);
						
			// get the current portfolio set for this user
			Set<Portfolio> portfolios = portfolioRepository.findByUsers_email(mail);
			// add this portfolio to this set
			portfolios.add(currentPortfolio);
			
			// set the portfolio set to the new user
			newtUserForPortfolio.setPortfolios(portfolios);
			
			// set the user set to this portfolio
			currentPortfolio.setUsers(users);
			
			// finally save the changes to the object
			portfolioRepository.save(currentPortfolio);
			userService.updateUser(newtUserForPortfolio);
			
			model.addAttribute("success", mail + " added");
		}else {
			model.addAttribute("error", mail + " does not exist.");
		}
		
		return "portfolio_access";
	}
	
	// get portfolio details
	@RequestMapping(value = "/showPortfolio/{portfolioId}", method = RequestMethod.GET)
	public String getWalletsByPortfolioId(@PathVariable("portfolioId") long portfolioId, Model model) {
		
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		
		// get the details
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		// add to model		
		model.addAttribute("portfolio", portfolio);
		model.addAttribute("portfolioId", portfolioId);
		
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
	
	
	
	
	
	
	@RequestMapping(value = "/chart/{portfolioId}", method = RequestMethod.GET)
    public String coinChart(
    		@PathVariable("portfolioId") long portfolioId,
    		@RequestParam(value="lastHours", required=false) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=false) Integer intervalInMinutes,
    		Model model) {
		
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
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
		model.addAttribute("portfolioId", portfolioId);
		
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		model.addAttribute("portfolioName", portfolio.getName());
		
		
		FormOptions formOptions = new FormOptions();
		List<FormOption> hourOptions = formOptions.defaultSetHourOptions();
		// now add to the model
		model.addAttribute("hourOptions",hourOptions);
		
		// add the minute options
		List<FormOption> minuteOptions = formOptions.defaultSetMinuteOptions();
		// now add to the model
		model.addAttribute("minuteOptions",minuteOptions);
		
		
		
		
		
		return "portfolio_chart";
	}
	
	@RequestMapping(value = "/areachart", method = RequestMethod.GET)
    public String createAreaChartPortfolioValue(
    		@RequestParam(value="portfolioId", required=true) Long portfolioId,
    		@RequestParam(value="lastHours", required=true) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=true) Integer intervalInMinutes,
    		Model model) {
		
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		// get the wallets
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		
		List<String> walletAddresses = new ArrayList<>();
		
		// loop the wallets to update the current coin value
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		for(Wallet wallet : wallets) {
			// add the wallet name to the list
			walletAddresses.add(wallet.getCensoredWalletAddress() + " (" + wallet.getCoin().getCoinMarketCapCoin().getSymbol() + ")");
			
			
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
				portfolioChartLineWallet.setWalletName(wallet.getCensoredWalletAddress() + " (" + wallet.getCoin().getCoinMarketCapCoin().getSymbol() + ")");
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
		
		return "portfolio_areachart";
	}
	

	@RequestMapping(value = "/piechart", method = RequestMethod.GET)
    public String createPieChartPortfolioDistribution(
    		@RequestParam(value="portfolioId", required=true) Long portfolioId,
    		Model model) {
		
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		// get the portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// get the wallets
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		
		
		List<PortfolioPieChartValue> portfolioPieChartValues = new ArrayList<>();
		
		
		// loop the wallets to update the current coin value
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		for(Wallet wallet : wallets) {
			
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
			
			// get the current amount
			double totalAmountDeposited = depositRepository.getSumOfAmountForWalletId(wallet.getId());
			double totalAmountWithdrawn = withdrawalRepository.getSumOfAmountForWalletId(wallet.getId());
			double totalAmount = totalAmountDeposited - totalAmountWithdrawn;
			
			// calculate the total value
			double currentWalletValue = totalAmount * currentCoinValue;
			
			// now add to the PortfolioPieChartValue object
			PortfolioPieChartValue portfolioPieChartValue = new PortfolioPieChartValue();
			portfolioPieChartValue.setWalletAddress(wallet.getCensoredWalletAddress() + " (" + cmcCoin.getSymbol() + ")");
			portfolioPieChartValue.setCurrentWalletValue(currentWalletValue);
			
			// add to the list
			portfolioPieChartValues.add(portfolioPieChartValue);
		}
		
		model.addAttribute("portfolioPieChartValues",portfolioPieChartValues);
		model.addAttribute("portfolioName",portfolio.getName());
		
		return "portfolio_piechart";
	}
	
	@RequestMapping(value = "/linechart", method = RequestMethod.GET)
    public String createLineChartPortfolioROI(
    		@RequestParam(value="portfolioId", required=true) Long portfolioId,
    		@RequestParam(value="lastHours", required=true) Integer lastHours,
    		@RequestParam(value="intervalInMinutes", required=true) Integer intervalInMinutes,
    		Model model) {
		
		// check if the current user has access to this portfolio
		boolean access = userService.checkIfCurrentUserIsAuthorizedToPortfolio(portfolioId);
		if(!access) {
			User user = userService.findUserByEmail(userService.findLoggedInUsername());
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "portfolio");
			return "not_authorized";
		}
		
		// get the wallets
		List<Wallet> wallets = walletRepository.findByPortfolio_Id(portfolioId);
		
		// get the portfolio
		Portfolio portfolio = portfolioRepository.findById(portfolioId);
		
		// loop the wallets to update the current coin value
		ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
		for(Wallet wallet : wallets) {
						
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
		
		List<PortfolioLineChartRoiValue> portfolioLineChartRoiValues = new ArrayList<>();
		
		// loop through the times
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, intervalInMinutes), date = start.getTime()) {
			Calendar startInterval = Calendar.getInstance();
			startInterval.setTime(date);
			startInterval.set(Calendar.SECOND, 0);
			
			Calendar endInterval = Calendar.getInstance();
			endInterval.setTime(date);
			endInterval.add(Calendar.MINUTE, intervalInMinutes);
			endInterval.add(Calendar.SECOND, -1);
			
			PortfolioLineChartRoiValue portfolioLineChartRoiValue = new PortfolioLineChartRoiValue();
			
			
			double totalValue = 0;
			double totalInvestment = 0;
			
			// loop through the wallets again
			for(Wallet wallet : wallets) {
				long walletId = wallet.getId();
				long coinId = wallet.getCoin().getId();
				
				// ---- get the totals for deposits
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
				
				double avgValue = coinValueRepository.findAvgByCoin_IdAndRequestDateBetween(coinId, startInterval.getTime(), endInterval.getTime());
				
				// prevent 0 by using the last known value
				if(avgValue == 0) {
					
					avgValue = coinValueRepository.findLastKnownValueBeforeRequestDate(coinId, startInterval.getTime());
				}
				
				// calculate the value
				double value = avgValue * totalAmount;
				// add to the totalValue
				totalValue += value;
				
				// calculate the investment for this wallet
				totalInvestment += totalInvested;
			}
			
			// add to the portfolioLineChartRoiValue
			portfolioLineChartRoiValue.setDate(endInterval.getTime());
			
			// calculate the roi
			double profitLoss = totalValue - totalInvestment;
			double roi = profitLoss / totalInvestment;
			
			portfolioLineChartRoiValue.setRoi(roi);
			
			// add to the list
			portfolioLineChartRoiValues.add(portfolioLineChartRoiValue);
		}
		
		model.addAttribute("portfolioLineChartRoiValues",portfolioLineChartRoiValues);
		model.addAttribute("portfolioName",portfolio.getName());
		
		return "portfolio_linechart";
	}
	
	
}
