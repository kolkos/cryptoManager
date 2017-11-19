package nl.kolkos.cryptoManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortfolioChartLine {
	private Date date;
	private List<PortfolioChartLineWallet> portfolioChartLineWallets = new ArrayList<>();
	private double totalInvested;

	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<PortfolioChartLineWallet> getPortfolioChartLineWallets() {
		return portfolioChartLineWallets;
	}
	public void setPortfolioChartLineWallets(List<PortfolioChartLineWallet> portfolioChartLineWallets) {
		this.portfolioChartLineWallets = portfolioChartLineWallets;
	}
	public double getTotalInvested() {
		return totalInvested;
	}
	public void setTotalInvested(double totalInvested) {
		this.totalInvested = totalInvested;
	}

	

	
}
