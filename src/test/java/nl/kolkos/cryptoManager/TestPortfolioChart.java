package nl.kolkos.cryptoManager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TestPortfolioChart {

	@Test
	public void test() {
		int lastHours = 24;
		
		List<String> wallets = new ArrayList<>();
		wallets.add("Wallet #1");
		wallets.add("Wallet #2");
		wallets.add("Wallet #3");
		
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR_OF_DAY, -lastHours);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		//end.add(Calendar.HOUR, 1);
		end.set(Calendar.SECOND, 0);
		
		List<PortfolioChartLine> portfolioChartLines = new ArrayList<>();
		
		
		
		for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.MINUTE, 60), date = start.getTime()) {
			PortfolioChartLine portfolioChartLine = new PortfolioChartLine();
			
			// add the date
			portfolioChartLine.setDate(date);
			
			Random r = new Random();
			double investment = 100 + (1000 - 100) * r.nextDouble();
			portfolioChartLine.setTotalInvested(investment);
			
			// loop through FAKE wallets
			for(String wallet : wallets) {
				
				
				PortfolioChartLineWallet portfolioChartLineWallet = new PortfolioChartLineWallet();
								
				double coinValue = 10 + (500 - 10) * r.nextDouble();
				
				System.out.println(wallet);
				System.out.println(coinValue);
				
				portfolioChartLineWallet.setWalletName(wallet);
				portfolioChartLineWallet.setWalletValue(coinValue);
				
				// now push it to the portfolioChartLine
				portfolioChartLine.getPortfolioChartLineWallets().add(portfolioChartLineWallet);

			}
			
			// push it to the list
			portfolioChartLines.add(portfolioChartLine);
			
		}
		
		// now print it
		for(PortfolioChartLine portfolioChartLine : portfolioChartLines) {
			System.out.println("-------");
			System.out.println("- " + portfolioChartLine.getDate());
			System.out.println("- " + portfolioChartLine.getTotalInvested());
			for(PortfolioChartLineWallet portfolioChartLineWallet : portfolioChartLine.getPortfolioChartLineWallets()) {
				System.out.println("-- " + portfolioChartLineWallet.getWalletName());
				System.out.println("-- " + portfolioChartLineWallet.getWalletValue());
			}
			
		}
	}
	

}
