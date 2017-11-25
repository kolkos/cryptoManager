package nl.kolkos.cryptoManager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class TestStream {

	public List<Deposit> createFakeDepositList() {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.SECOND, 0);
		
		java.sql.Date date = new java.sql.Date(start.getTimeInMillis());  
		
		CoinMarketCapCoin coinMarketCapCoin = new CoinMarketCapCoin();
		coinMarketCapCoin.setId("tst");
		coinMarketCapCoin.setName("Test Coin");
		coinMarketCapCoin.setSymbol("TST");
		
		// create a coin
		Coin coin = new Coin();
		coin.setId(1L);
		coin.setCoinMarketCapCoin(coinMarketCapCoin);
		
		
		// create a portfolio
		Portfolio portfolio = new Portfolio();
		portfolio.setId(1L);
		portfolio.setName("test portfolio");
		portfolio.setDescription("Test Portfolio");
		
		// create a wallet
		Wallet wallet1 = new Wallet();
		wallet1.setId(1L);
		wallet1.setAddress("TST1-ADDRESS");
		wallet1.setDescription("TEST Wallet 1");
		wallet1.setPortfolio(portfolio);
		wallet1.setCoin(coin);
		
		// create another wallet
		Wallet wallet2 = new Wallet();
		wallet2.setId(2L);
		wallet2.setAddress("TST2-ADDRESS");
		wallet2.setDescription("TEST Wallet 2");
		wallet2.setPortfolio(portfolio);
		wallet2.setCoin(coin);
		
		// create a test deposit
		Deposit deposit1 = new Deposit();
		deposit1.setId(1L);
		deposit1.setAmount(0.001);
		deposit1.setDepositDate(date);
		deposit1.setPurchaseValue(25);
		deposit1.setRemarks("Test deposit 1");
		deposit1.setWallet(wallet1);
		deposit1.setCurrentDepositValue(5);
		
		// create a test deposit
		Deposit deposit2 = new Deposit();
		deposit2.setId(2L);
		deposit2.setAmount(0.006);
		deposit2.setDepositDate(date);
		deposit2.setPurchaseValue(33);
		deposit2.setRemarks("Test deposit 2");
		deposit2.setWallet(wallet2);
		deposit2.setCurrentDepositValue(44);
		
		// create a test deposit
		Deposit deposit3 = new Deposit();
		deposit3.setId(3L);
		deposit3.setAmount(1);
		deposit3.setDepositDate(date);
		deposit3.setPurchaseValue(500);
		deposit3.setRemarks("Test deposit 3");
		deposit3.setWallet(wallet1);
		deposit3.setCurrentDepositValue(12);
		
		
		List<Deposit> deposits = new ArrayList<>();
		deposits.add(deposit1);
		deposits.add(deposit2);
		deposits.add(deposit3);
		
		return deposits;
	}
	
	
	@Test
	public void test() {
		
		List<Deposit> deposits = this.createFakeDepositList();
		System.out.println("---- Voor filteren");
		this.printObject(deposits);
		
		System.out.println("---- alleen coin");
		List<Deposit> results = this.filterResults(deposits, 1L, 0, 0);
		this.printObject(results);
		
		System.out.println("---- alleen wallet");
		results = this.filterResults(deposits, 0, 1L, 0);
		this.printObject(results);
		
		System.out.println("---- alleen portfolio");
		results = this.filterResults(deposits, 0, 0, 1);
		this.printObject(results);
		
		System.out.println("---- coin en wallet");
		results = this.filterResults(deposits, 0, 1, 0);
		this.printObject(results);
		
		

		 
	}
	
	public void printObject(List<Deposit> deposits) {
		System.out.println("=====");
		// loop through deposits
		for(Deposit deposit : deposits) {
						
			System.out.println(String.format("Deposit ID: %d", deposit.getId()));
			System.out.println(String.format("Deposit Date: %s", deposit.getDepositDate()));
			System.out.println(String.format("Deposit Amount: %f", deposit.getAmount()));
			
			System.out.println(String.format("Deposit value on deposit: %f", deposit.getPurchaseValue()));
			System.out.println(String.format("Deposit current value: %f", deposit.getCurrentDepositValue()));
			
			// calculate the difference
			double diff = deposit.getCurrentDepositValue() - deposit.getPurchaseValue();
			deposit.setCurrentDepositDifference(diff);
			
			System.out.println(String.format("Deposit current value: %f", deposit.getCurrentDepositValue()));
			
			System.out.println(String.format("Deposit remarks: %s", deposit.getRemarks()));
			
			// get the wallet info
			System.out.println(String.format("  Wallet ID: %d", deposit.getWallet().getId()));
			System.out.println(String.format("  Wallet address: %s", deposit.getWallet().getAddress()));
			System.out.println(String.format("  Wallet description: %s", deposit.getWallet().getDescription()));
			
			// get the coin info
			System.out.println(String.format("    Coin ID: %d", deposit.getWallet().getCoin().getId()));
			
			// get the cmc info
			System.out.println(String.format("      CMC Coin ID: %s", deposit.getWallet().getCoin().getCoinMarketCapCoin().getId()));
			System.out.println(String.format("      CMC Coin Name: %s", deposit.getWallet().getCoin().getCoinMarketCapCoin().getName()));
			System.out.println(String.format("      CMC Coin Symbol: %s", deposit.getWallet().getCoin().getCoinMarketCapCoin().getSymbol()));
					
			// get the portfolio info
			System.out.println(String.format("    Portfolio ID: %d", deposit.getWallet().getPortfolio().getId()));
			System.out.println(String.format("    Portfolio Name: %s", deposit.getWallet().getPortfolio().getName()));
			System.out.println(String.format("    Portfolio Description: %s", deposit.getWallet().getPortfolio().getDescription()));
			
			
			System.out.println("---");
		}
	}
	
	public List<Deposit> filterResults(List<Deposit> deposits, long coinId, long walletId, long portfolioId){
		List<Deposit> results = deposits;

		
		if(walletId > 0L) {
			results = this.filterByWalletId(results, walletId);
		}

		
		if(coinId > 0L) {
			results = this.filterByCoinId(results, coinId);
		}

		
		
		
		if(portfolioId > 0L) {
			results = this.filterByPortfolioId(results, portfolioId);
		}

		return results;
	}
	
	public List<Deposit> filterByCoinId(List<Deposit> deposits, long coinId){
		List<Deposit> results = deposits.stream()
				.filter(x -> x.getWallet().getCoin().getId() == coinId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return results;
	}
	
	public List<Deposit> filterByWalletId(List<Deposit> deposits, long walletId){
		List<Deposit> results = deposits.stream()
				.filter(x -> x.getWallet().getId() == walletId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return results;
	}
	
	public List<Deposit> filterByPortfolioId(List<Deposit> deposits, long portfolioId){
		List<Deposit> results = deposits.stream()
				.filter(x -> x.getWallet().getPortfolio().getId() == portfolioId)
				.map(s -> s)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return results;
	}

}
