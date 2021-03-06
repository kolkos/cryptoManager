package nl.kolkos.cryptoManager;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity // This tells Hibernate to make a table out of this class
public class Wallet {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

	private String address;
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "coin_id")
	private Coin coin;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
	
	// these fields don't need to be in the database since there are calculated
	@Transient
	private double currentWalletValue;
	
	@Transient
	private double currentWalletAmount;
	
	@Transient
	private double currentWalletInvestment;
	
	@Transient
	private double currentWalletDeposited;
	
	@Transient
	private double currentWalletWithdrawn;
	
	@Transient
	private double currentWalletProfitLoss;
	
	@Transient
	private double currentWalletROI;
	
	public Wallet() {
		
	}
	
	public Wallet(Wallet walletToCopy) {
		this.id = walletToCopy.id;
		this.address = walletToCopy.address;
		this.description = walletToCopy.description;
		this.coin = walletToCopy.coin;
		this.portfolio = walletToCopy.portfolio;
		this.currentWalletValue = walletToCopy.currentWalletValue;
		this.currentWalletAmount = walletToCopy.currentWalletAmount;
		this.currentWalletInvestment = walletToCopy.currentWalletInvestment;
		this.currentWalletProfitLoss = walletToCopy.currentWalletProfitLoss;
		this.currentWalletROI = walletToCopy.currentWalletROI;
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	public void setCoin(Coin coin) {
		this.coin = coin;
	}
	public Coin getCoin() {
		return this.coin;
	}
	
	@JsonIgnore
	public Portfolio getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public double getCurrentWalletValue() {
		return currentWalletValue;
	}
	public void setCurrentWalletValue(double currentWalletValue) {
		this.currentWalletValue = currentWalletValue;
	}
	public double getCurrentWalletAmount() {
		return currentWalletAmount;
	}
	public void setCurrentWalletAmount(double currentWalletAmount) {
		this.currentWalletAmount = currentWalletAmount;
	}
	
	public double getCurrentWalletInvestment() {
		return currentWalletInvestment;
	}
	public void setCurrentWalletInvestment(double currentWalletInvestment) {
		this.currentWalletInvestment = currentWalletInvestment;
	}
	public double getCurrentWalletProfitLoss() {
		return currentWalletProfitLoss;
	}

	public void setCurrentWalletProfitLoss(double currentWalletProfitLoss) {
		this.currentWalletProfitLoss = currentWalletProfitLoss;
	}

	public double getCurrentWalletROI() {
		return currentWalletROI;
	}

	public void setCurrentWalletROI(double currentWalletROI) {
		this.currentWalletROI = currentWalletROI;
	}

	/**
	 * Because I use the google charts api, I don't want to send the whole wallet address to google
	 * @return the first and last 5 characters of the wallet address
	 */
	public String getCensoredWalletAddress() {
		String firstPart = this.address.substring(0, Math.min(this.address.length(), 5));
		String lastPart = this.address.substring(this.address.length() -5);
		
		String censoredWallet = firstPart + "xxx" + lastPart;
		
		return censoredWallet;
	}

	public double getCurrentWalletWithdrawn() {
		return currentWalletWithdrawn;
	}

	public void setCurrentWalletWithdrawn(double currentWalletWithdrawn) {
		this.currentWalletWithdrawn = currentWalletWithdrawn;
	}

	public double getCurrentWalletDeposited() {
		return currentWalletDeposited;
	}

	public void setCurrentWalletDeposited(double currentWalletDeposited) {
		this.currentWalletDeposited = currentWalletDeposited;
	}
	
	
}
