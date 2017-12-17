package nl.kolkos.cryptoManager.api.objects;

import java.util.List;


import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Wallet;

/**
 * This object is used by the API. It extends the default Portfolio object
 * @author antonvanderkolk
 *
 */
public class ApiPortfolio extends Portfolio{
	// attached wallets
	private List<Wallet> wallets;
	
	// calculated values
	private double currentTotalPortfolioValue;
	private double currentTotalPortfolioInvestment;
	private double currentTotalPortfolioProfitLoss;
	private double currentTotalPortfolioROI;
	
	public List<Wallet> getWallets() {
		return wallets;
	}
	public void setWallets(List<Wallet> wallets) {
		this.wallets = wallets;
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
	
	
	
}
