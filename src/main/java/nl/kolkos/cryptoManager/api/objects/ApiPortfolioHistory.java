package nl.kolkos.cryptoManager.api.objects;

import java.util.Date;
import java.util.List;

import nl.kolkos.cryptoManager.Wallet;

public class ApiPortfolioHistory{
	private Date date;
	// calculated values
	private double currentTotalPortfolioValue;
	private double currentTotalPortfolioInvestment;
	private double currentTotalPortfolioProfitLoss;
	private double currentTotalPortfolioROI;
	private List<Wallet> wallets;
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getCurrentTotalPortfolioValue() {
		return currentTotalPortfolioValue;
	}

	public void setCurrentTotalPortfolioValue(double currentTotalPortfolioValue) {
		this.currentTotalPortfolioValue = currentTotalPortfolioValue;
	}

	public double getCurrentTotalPortfolioInvestment() {
		return currentTotalPortfolioInvestment;
	}

	public void setCurrentTotalPortfolioInvestment(double currentTotalPortfolioInvestment) {
		this.currentTotalPortfolioInvestment = currentTotalPortfolioInvestment;
	}

	public double getCurrentTotalPortfolioProfitLoss() {
		return currentTotalPortfolioProfitLoss;
	}

	public void setCurrentTotalPortfolioProfitLoss(double currentTotalPortfolioProfitLoss) {
		this.currentTotalPortfolioProfitLoss = currentTotalPortfolioProfitLoss;
	}

	public double getCurrentTotalPortfolioROI() {
		return currentTotalPortfolioROI;
	}

	public void setCurrentTotalPortfolioROI(double currentTotalPortfolioROI) {
		this.currentTotalPortfolioROI = currentTotalPortfolioROI;
	}

	public List<Wallet> getWallets() {
		return wallets;
	}

	public void setWallets(List<Wallet> wallets) {
		this.wallets = wallets;
	}

	
	
	
}
