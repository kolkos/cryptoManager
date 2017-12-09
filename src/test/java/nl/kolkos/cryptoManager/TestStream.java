package nl.kolkos.cryptoManager;

import static org.assertj.core.api.Assertions.setAllowComparingPrivateFields;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
		Coin coin1 = new Coin();
		coin1.setId(1L);
		coin1.setCoinMarketCapCoin(coinMarketCapCoin);
		coin1.setCurrentCoinValue(1300);
		
		// create a coin
		Coin coin2 = new Coin();
		coin2.setId(1L);
		coin2.setCoinMarketCapCoin(coinMarketCapCoin);
		coin2.setCurrentCoinValue(9);
		
		
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
		wallet1.setCoin(coin1);
		
		// create another wallet
		Wallet wallet2 = new Wallet();
		wallet2.setId(2L);
		wallet2.setAddress("TST2-ADDRESS");
		wallet2.setDescription("TEST Wallet 2");
		wallet2.setPortfolio(portfolio);
		wallet2.setCoin(coin2);
		
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
	
	@Test
	public void testSortingCoin() throws Exception {
		// create some cmc coins
		CoinMarketCapCoin cmc1 = new CoinMarketCapCoin();
		cmc1.setId("CMC1");
		cmc1.setName("A CoinMarketCap Coin nr 1");
		cmc1.setSymbol("CMC1");
		
		CoinMarketCapCoin cmc2 = new CoinMarketCapCoin();
		cmc2.setId("CMC2");
		cmc2.setName("Z CoinMarketCap Coin nr 2");
		cmc2.setSymbol("CMC2");
		
		CoinMarketCapCoin cmc3 = new CoinMarketCapCoin();
		cmc3.setId("CMC3");
		cmc3.setName("M CoinMarketCap Coin nr 3");
		cmc3.setSymbol("CMC3");
		
		
		// create some coins
		Coin coin1 = new Coin();
		coin1.setCoinMarketCapCoin(cmc1);
		
		// average values 
		double curVal = 1234;
		double avg1h = 1200;
		double avg1d = 1000;
		double avg1w = 1500;
		
		double winLoss1h = ((curVal - avg1h)*100)/curVal;
		double winLoss1d = ((curVal - avg1d)*100)/curVal;
		double winLoss1w = ((curVal - avg1w)*100)/curVal;
		
		coin1.setCurrentCoinValue(curVal);
		coin1.setWinLoss1h(winLoss1h);
		coin1.setWinLoss1d(winLoss1d);
		coin1.setWinLoss1w(winLoss1w);
		
		
		// create some coins
		Coin coin2 = new Coin();
		coin2.setCoinMarketCapCoin(cmc2);
		
		// average values 
		curVal = 6;
		avg1h = 9;
		avg1d = 12;
		avg1w = 15;
		
		winLoss1h = ((curVal - avg1h)*100)/curVal;
		winLoss1d = ((curVal - avg1d)*100)/curVal;
		winLoss1w = ((curVal - avg1w)*100)/curVal;
		
		coin2.setCurrentCoinValue(curVal);
		coin2.setWinLoss1h(winLoss1h);
		coin2.setWinLoss1d(winLoss1d);
		coin2.setWinLoss1w(winLoss1w);
		
		// create some coins
		Coin coin3 = new Coin();
		coin3.setCoinMarketCapCoin(cmc3);
		
		// average values 
		curVal = 15_000;
		avg1h = 12_000;
		avg1d = 11_000;
		avg1w = 10_000;
		
		winLoss1h = ((curVal - avg1h)*100)/curVal;
		winLoss1d = ((curVal - avg1d)*100)/curVal;
		winLoss1w = ((curVal - avg1w)*100)/curVal;
		
		coin3.setCurrentCoinValue(curVal);
		coin3.setWinLoss1h(winLoss1h);
		coin3.setWinLoss1d(winLoss1d);
		coin3.setWinLoss1w(winLoss1w);
		
		// now add these coins to the list
		List<Coin> coins = new ArrayList<>();
		coins.add(coin1);
		coins.add(coin2);
		coins.add(coin3);
		
		System.out.println("-- UNSORTED --");
		this.printCoinObjects(coins);
		
		
		
		System.out.println("-- SORTED BY current coin value ASC --");
		coins = this.sortByCurrentCoinValue(coins, "ASC");
		this.printCoinObjects(coins);
		
		System.out.println("-- SORTED BY current coin value DESC --");
		coins = this.sortByCurrentCoinValue(coins, "DESC");
		this.printCoinObjects(coins);
	
		System.out.println("-- SORTED BY 1w win/loss ASC --");
		coins = this.sortByWinLoss1h(coins, "ASC");
		this.printCoinObjects(coins);
		
		System.out.println("-- SORTED BY 1w win/loss DESC --");
		coins = this.sortByWinLoss1h(coins, "DESC");
		this.printCoinObjects(coins);
		
		System.out.println("-- SORTED Coin Name ASC --");
		coins = this.sortByCoinName(coins, "ASC");
		this.printCoinObjects(coins);
		
		System.out.println("-- SORTED Coin Name DESC --");
		coins = this.sortByCoinName(coins, "DESC");
		this.printCoinObjects(coins);
		
		
	}
	
	public List<Coin> sortByCurrentCoinValue(List<Coin> coins, String order){
		if(order.equals("ASC")) {
			coins.sort(Comparator.comparingDouble(Coin::getCurrentCoinValue));
		}else {
			coins.sort(Comparator.comparingDouble(Coin::getCurrentCoinValue).reversed());
		}
		return coins;
	}
	
	public List<Coin> sortByWinLoss1h(List<Coin> coins, String order){
		if(order.equals("ASC")) {
			coins.sort(Comparator.comparingDouble(Coin::getWinLoss1h));
		}else {
			coins.sort(Comparator.comparingDouble(Coin::getWinLoss1h).reversed());
		}
		return coins;
	}
	
	public List<Coin> sortByCoinName(List<Coin> coins, String order){
		if(order.equals("ASC")) {
			return coins.stream()
					.sorted((coin1, coin2) -> coin1.getCoinMarketCapCoin().getName().compareTo(coin2.getCoinMarketCapCoin().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}else {
			return coins.stream()
					.sorted((coin1, coin2) -> coin2.getCoinMarketCapCoin().getName().compareTo(coin1.getCoinMarketCapCoin().getName()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		
	}
	
	public void printCoinObjects(List<Coin> coins) {
		// loop throug coins
		int coinNr = 1;
		for(Coin coin : coins) {
			System.out.println(String.format("Coin #%d {", coinNr));
			
			// get the coin market cap values
			System.out.println("  CMC Values:");
			System.out.println(String.format("    CMC ID: %s", coin.getCoinMarketCapCoin().getId()));
			System.out.println(String.format("    CMC Symbol: %s", coin.getCoinMarketCapCoin().getSymbol()));
			System.out.println(String.format("    CMC Name: %s", coin.getCoinMarketCapCoin().getName()));
			
			// get the coin values
			System.out.println("  Coin Values:");
			System.out.println(String.format("    Current value %f", coin.getCurrentCoinValue()));
			System.out.println(String.format("    Win/Loss 1h %f", coin.getWinLoss1h()));
			System.out.println(String.format("    Win/Loss 1d %f", coin.getWinLoss1d()));
			System.out.println(String.format("    Win/Loss 1w %f", coin.getWinLoss1w()));
			
			System.out.println("}");
			coinNr++;
			
		}
	}

}
