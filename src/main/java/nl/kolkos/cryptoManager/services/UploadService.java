package nl.kolkos.cryptoManager.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Coin;
import nl.kolkos.cryptoManager.CoinMarketCapCoin;
import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Transaction;
import nl.kolkos.cryptoManager.TransactionType;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;
import nl.kolkos.cryptoManager.repositories.CoinMarketCapCoinRepository;
import nl.kolkos.cryptoManager.repositories.CoinRepository;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.TransactionTypeRepository;

@Service
public class UploadService {
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private CoinRepository coinRepository;
	
//	@Autowired
//	private DepositService depositService;
//	
//	@Autowired
//	private WithdrawalService withdrawalService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TransactionTypeRepository transactionTypeRepository;
	
	@Autowired
	private CoinMarketCapCoinRepository coinMarketCapCoinRepository;
	
	
	private Date parseDate(String date) throws IllegalArgumentException, ParseException{
		if(!date.matches("^\\d{4}\\-\\d{2}\\-\\d{2}$")) {
			throw new IllegalArgumentException("The transaction date should be in the following format: yyyy-MM-dd");
		}
		
		// try to parse the date
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Date newDate = formatter.parse(date);
		
		return newDate;
	}
	
	private Double parseStringToDouble(String transactionAmount) throws IllegalArgumentException {
		if(!transactionAmount.matches("(^\\d{1,})(\\,|\\.)(\\d{2,}$)")) {
			throw new IllegalArgumentException("The value, or amount, may only contain numbers and . or ,.");
		}
		
		double newAmount = new Double(transactionAmount);
		
		return newAmount;
	}
	
	private Portfolio getOrCreatePortfolio(String portfolioName, String portfolioDescription) {
		// check if the portfolio exists
		Portfolio portfolio = portfolioService.findByName(portfolioName);
		if(portfolio == null) {
			// portfolio does not exist, create it
			portfolio = portfolioService.createNewPortfolio(portfolioName, portfolioDescription);
			
		}
		
		return portfolio;
	}
	
	private Coin getOrCreateCoin(String coinMarketCapSymbol) {
		Coin coin = coinRepository.findByCoinMarketCapCoinSymbol(coinMarketCapSymbol);
		if(coin == null) {
			coin = new Coin();
			// get the coin market cap coin
			CoinMarketCapCoin cmcCoin = coinMarketCapCoinRepository.findBySymbol(coinMarketCapSymbol);
			coin.setCoinMarketCapCoin(cmcCoin);
			// save the coin
			coinRepository.save(coin);
			
		}
		
		return coin;
	}
	
	private boolean checkIfCoinMarketCapSymbolExists(String coinMarketCapSymbol) {
		CoinMarketCapCoin cmcCoin = coinMarketCapCoinRepository.findBySymbol(coinMarketCapSymbol);
		boolean exists = false;
		
		if(cmcCoin != null) {
			exists = true;
		}
		
		return exists;
	}
	
	private Wallet getOrCreateWallet(String walletAddress, String walletDescription, Portfolio portfolio, Coin coin) {
		// check if wallet exists
		Wallet wallet = walletService.findByAddress(walletAddress);
		if(wallet == null) {
			// wallet does not exist, create it
			wallet = walletService.createWallet(walletAddress, walletDescription, portfolio, coin);
		}
		
		return wallet;
	}
	
//	private String createDeposit(Date depositDate, double amount, double purchaseValue, Wallet wallet, String transactionRemarks) {
//		// check if the deposit already exists
//		// I can't know for sure, but I assume if the date, the amount and the purchase value are equal, the deposit already exists.
//		Deposit deposit = depositService.findByDepositDateAndAmountAndPurchaseValue(depositDate, amount, purchaseValue);
//		if(deposit != null) {
//			// deposit exists, return
//			return "Deposit already exists, skipping...";
//			
//		}
//		// create a deposit
//		depositService.createDeposit(depositDate, amount, purchaseValue, wallet, transactionRemarks);
//		
//		return String.format("Deposit of '%f' on '%s' for '€%f' registered", amount, depositDate, purchaseValue);
//	}
//	
//	private String createWithdrawal(Date withdrawalDate, double amount, double withdrawalValue, Wallet wallet, String transactionRemarks, boolean toCash) {
//		// check if the deposit already exists
//		// I can't know for sure, but I assume if the date, the amount and the withdrawal value are equal, the withdrawal already exists.
//		Withdrawal withdrawal = withdrawalService.findByWithdrawalDateAndAmountAndWithdrawalValue(withdrawalDate, amount, withdrawalValue);
//		if(withdrawal != null) {
//			return "Withdrawal already exists, skipping...";
//		}
//				
//				
//		withdrawalService.createWithdrawal(withdrawalDate, amount, withdrawalValue, wallet, transactionRemarks, toCash);
//		
//		return String.format("Withdrawal of '%f' on '%s' for '€%f' registered", amount, withdrawalDate, withdrawalValue);
//	}
	
	private String createTransaction(Date transactionDate, double amount, double value, Wallet wallet, String transactionRemarks, boolean toCash, TransactionType transactionType) {
		// check if the deposit already exists
		// I can't know for sure, but I assume if the date, the amount and the withdrawal value are equal, the withdrawal already exists.
		Transaction transaction = transactionService.findByTransactionDateAndTransactionTypeAndAmountAndValue(transactionDate, transactionType, amount, value);
		if(transaction != null) {
			return "Transaction already exists, skipping...";
		}
				
		transactionService.createTransaction(transactionDate, amount, value, wallet, transactionRemarks, toCash, transactionType);		
		
		
		return String.format("Transaction of '%f' on '%s' for '€%f' registered", amount, transactionDate, value);
	}
	 
	
	public List<LinkedHashMap<String, String>> handleFile(String filePath, boolean containsHeader, String separator){
		/*
		 * The file NEEDS to contain the following fields:
		 * 		00 Portfolio name 			-> (Required) String
		 * 		01 Portfolio description 	-> (Optional) String
		 * 		02 Wallet address 			-> (Required) String 
		 * 		03 Wallet description 		-> (Optional) String 
		 * 		04 Coin Symbol 				-> (Required) String 
		 * 		05 Transaction date 			-> (Required) Date		-> convert to Date
		 * 		06 Transaction type 			-> (Required) String		-> must be Deposit or Withdrawal
		 * 		07 Withdrawal to cash 		-> (Required) String		-> ignored if type is deposit. value must be Yes or No
		 * 		08 Transaction amount		-> (Required) Double		-> convert to Double, replace comma with point
		 * 		09 Transaction price			-> (Required) Double 	-> convert to Double, replace comma with point	
		 * 		10 Trasaction remarks		-> (Optional) String
		 */
		
		List<LinkedHashMap<String, String>> results = new ArrayList<>();
		
		
		
		// check if the file contains a header, if so, skip this line
		boolean skip = false;
		if(containsHeader) {
			skip = true;
		}
		
		// parse the file, line by line
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			
            while ((line = br.readLine()) != null) {
            		// check if the line needs to be skipped
            		if(skip) {
            			// skip this line
            			skip = false;
            			continue;
            		}
            		
            		// every line gets it own result hashmap
            		LinkedHashMap<String, String> lineResults = new LinkedHashMap<>();
            		
            		// split the line
            		String[] fields = line.split(separator);
            		
            		// check if the line contains 10 fields
            		if(fields.length != 11) {
            			lineResults.put("Error", "Each line needs to have exactly 11 fields.");
            			results.add(lineResults);
            			// skip this line
            			continue;
            		}
            		
            		// now attach the fields to variables
            		String portfolioName = fields[0];
            		String portfolioDescription = fields[1];
            		
            		String walletAddress = fields[2];
            		String walletDescription = fields[3];
            		
            		String coinSymbol = fields[4];
            		
            		// try to parse the date
            		Date transactionDate = new Date();
            		try {
					transactionDate = this.parseDate(fields[5]);
				} catch (IllegalArgumentException | ParseException e) {
					// Error parsing date
					e.printStackTrace();
					
					// add to the results
					lineResults.put("Error", e.getMessage());
	        			results.add(lineResults);
	        			// skip this line
	        			continue;
				}
            		
            		String transactionType = fields[6].toLowerCase();
            		
            		if(! transactionType.equals("withdrawal") && ! transactionType.equals("deposit")) {
            			// unknown transaction type
            			lineResults.put("Error", "Transaction type must be 'deposit' or 'withdrawal'.");
	        			results.add(lineResults);
	        			// skip this line
	        			continue;
            		}
            		            		
            		boolean toCash = false;
            		if(transactionType.equals("withdrawal")) {
            			// if the transaction type is withdrawal (which it is), the field withdrawalToCash plays a part
            			// check if the value is yes or no
            			String withdrawalToCash = fields[7].toLowerCase();
            			if(withdrawalToCash.equals("yes")) {
            				toCash = true;
            			}else if(withdrawalToCash.equals("no")){
            				toCash = false;
            			}else {
            				// the value does not equal yes or no, this is an error
            				// unknown transaction type
                			lineResults.put("Error", "'Withdrawal to cash' should be 'yes' or 'no'.");
    	        				results.add(lineResults);
    	        				// skip this line
    	        				continue;
            			}
            		}
            		
            		// try to parse the amount
            		Double transactionAmount = 0D;
            		try {
            			transactionAmount = this.parseStringToDouble(fields[8]);
            		} catch(IllegalArgumentException e) {
            			// add to the results
    					lineResults.put("Error", e.getMessage());
    	        			results.add(lineResults);
    	        			// skip this line
    	        			continue;
            		}
            		
            		// same trich for the value
            		Double transactionValue = 0D;
            		try {
            			transactionValue = this.parseStringToDouble(fields[9]);
            		} catch(IllegalArgumentException e) {
            			// add to the results
    					lineResults.put("Error", e.getMessage());
    	        			results.add(lineResults);
    	        			// skip this line
    	        			continue;
            		}
            		
            		String transactionRemarks = fields[10];
            		
            		// get or create the portfolio
            		Portfolio portfolio = this.getOrCreatePortfolio(portfolioName, portfolioDescription);
            		lineResults.put("Portfolio", String.format("Using portfolio '%s'", portfolioName));
            		
            		
            		// check if the CoinMarketCap coin exists
            		if(! this.checkIfCoinMarketCapSymbolExists(coinSymbol)) {
            			// add to the results
    					lineResults.put("Error", "Unknown coin '" + coinSymbol + "'");
    	        			results.add(lineResults);
    	        			// skip this line
    	        			continue;
            		}
            		
            		// get or create the coin
            		Coin coin = this.getOrCreateCoin(coinSymbol);
            		lineResults.put("Coin", String.format("Using coin '%s'", coinSymbol));
            		
            		// get or create the wallet
            		Wallet wallet = this.getOrCreateWallet(walletAddress, walletDescription, portfolio, coin);
            		lineResults.put("Wallet", String.format("Using wallet '%s'", walletAddress));
            		
            		// get the transaction type
            		transactionType = transactionType.substring(0,1).toUpperCase() + transactionType.substring(1);
            		TransactionType type = transactionTypeRepository.findByType(transactionType);
            		
            		
            		// create a transaction
            		String transactionResult = this.createTransaction(transactionDate, transactionAmount, transactionValue, wallet, transactionRemarks, toCash, type);
            		lineResults.put("Transaction", transactionResult);
            		
            		
            		            		
            		results.add(lineResults);
            	


            }

        } catch (IOException e) {
            e.printStackTrace();
            LinkedHashMap<String, String> lineResults = new LinkedHashMap<>();
            lineResults.put("Error", "Error handling file.");
            results.add(lineResults);
        }
		
		return results;
	}
	
	public String createLine(List<String> values, String seperator) {
		String line = "";
		
		int listLength = values.size();
		
		// loop through the values
		int iterationNr = 1;
		for(String value : values) {
			line += value;
			if(iterationNr == listLength) {
				// if this is the last value, append a line break
				line += "\r\n";
			}else {
				// not the last value, append a sperator
				line += seperator;
			}
			
			iterationNr++;
		}
		
		return line;
	}
	
	/**
	 * This method creates an export of all the transactions (including wallets and portfolio's the user is authorized to.
	 * @param containsHeader when true, an header is included
	 * @param separator the field seperator
	 * @return string of the file location
	 * @throws IOException 
	 */
	public String exportTransactions(boolean containsHeader, String separator) throws IOException {
		/*
		 * The export contains the following fields:
		 * 		00 Portfolio name 			-> (Required) String
		 * 		01 Portfolio description 	-> (Optional) String
		 * 		02 Wallet address 			-> (Required) String 
		 * 		03 Wallet description 		-> (Optional) String 
		 * 		04 Coin Symbol 				-> (Required) String 
		 * 		05 Transaction date 			-> (Required) Date		-> convert to Date
		 * 		06 Transaction type 			-> (Required) String		-> must be Deposit or Withdrawal
		 * 		07 Withdrawal to cash 		-> (Required) String		-> ignored if type is deposit. value must be Yes or No
		 * 		08 Transaction amount		-> (Required) Double		-> convert to Double, replace comma with point
		 * 		09 Transaction value			-> (Required) Double 	-> convert to Double, replace comma with point	
		 * 		10 Trasaction remarks		-> (Optional) String
		 * 
		 * These are the same fields as in the import
		 */
		String fileContentBuffer = "";
		
		if(containsHeader) {
			List<String> titleFields = new ArrayList<>();
			titleFields.add("Portfolio name");
			titleFields.add("Portfolio description");
			titleFields.add("Wallet address");
			titleFields.add("Wallet description");
			titleFields.add("Coin Symbol");
			titleFields.add("Transaction date");
			titleFields.add("Transaction type");
			titleFields.add("Withdrawal to cash");
			titleFields.add("Transaction amount");
			titleFields.add("Transaction value");
			titleFields.add("Trasaction remarks");
			
			fileContentBuffer += this.createLine(titleFields, separator);
		}
		
		// now get the portfolios
		Set<Portfolio> portfolios = portfolioService.findByUsers_email(userService.findLoggedInUsername());
		// loop through portfolios
		for(Portfolio portfolio : portfolios) {
			String portfolioName = portfolio.getName();
			String portfolioDescription = portfolio.getDescription();
			
			// get the wallets in this portfolio
			List<Wallet> wallets = walletService.getWalletsByPortfolio(portfolio);
			// loop through the wallets
			for(Wallet wallet : wallets) {
				// get the wallet values
				String walletAddress = wallet.getAddress();
				String walletDescription = wallet.getDescription();
				// get the attached coin symbol
				String coinSymbol = wallet.getCoin().getCoinMarketCapCoin().getSymbol();
				
				
				
				// Get the transactions for this wallet
				List<Transaction> transactions = transactionService.findByWallet(wallet);
				for(Transaction transaction : transactions) {
					// get the deposit values
					String transactionDate = transaction.getTransactionDate().toString();
					String transactionType = transaction.getTransactionType().getType();
					
					String withdrawalToCash = "no";
					// if toCash is true, the values is yes
					if(transaction.isWithdrawnToCash()) {
						withdrawalToCash = "yes";
					}
					
					String transactionAmount = String.format("%f", transaction.getAmount());
					String transactionValue = String.format("%f", transaction.getValue());
					String transactionRemarks = transaction.getRemarks();
					
					// now add all the values to a list, and parse it to a line
					List<String> withdrawalLine = new ArrayList<>();
					withdrawalLine.add(portfolioName);
					withdrawalLine.add(portfolioDescription);
					withdrawalLine.add(walletAddress);
					withdrawalLine.add(walletDescription);
					withdrawalLine.add(coinSymbol);
					withdrawalLine.add(transactionDate);
					withdrawalLine.add(transactionType);
					withdrawalLine.add(withdrawalToCash);
					withdrawalLine.add(transactionAmount);
					withdrawalLine.add(transactionValue);
					withdrawalLine.add(transactionRemarks);
					
					fileContentBuffer += this.createLine(withdrawalLine, separator);
				}
				
			}
		}
		
		// write the content to a file
		UUID uuid = UUID.randomUUID();
		String filePath = "/tmp/" + uuid + ".csv";
		
		FileOutputStream outputStream = new FileOutputStream(filePath);
	    byte[] strToBytes = fileContentBuffer.getBytes();
	    outputStream.write(strToBytes);
	    outputStream.close();
		
		
		return filePath;
	}
	
} 
