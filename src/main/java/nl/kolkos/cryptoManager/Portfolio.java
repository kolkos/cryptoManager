package nl.kolkos.cryptoManager;

import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity // This tells Hibernate to make a table out of this class
public class Portfolio {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	private String name;
	private String description;
	
	// user rights
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_portfolio", joinColumns = @JoinColumn(name = "portfolio_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;
	
	// user rights
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "apikey_portfolio", joinColumns = @JoinColumn(name = "portfolio_id"), inverseJoinColumns = @JoinColumn(name = "api_key_id"))
    private Set<ApiKey> apiKeys;
	
	@Transient
	private double portfolioTotalValue;
	
	@Transient
	private double portfolioTotalDeposited;
	
	@Transient
	private double portfolioTotalWithdrawn;
	
	@Transient
	private double portfolioTotalInvestment;
		
	@Transient
	private double portfolioProfitLoss;
	
	@Transient
	private double portfolioROI;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonIgnore
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	@JsonIgnore
	public Set<ApiKey> getApiKeys() {
		return apiKeys;
	}
	public void setApiKeys(Set<ApiKey> apiKeys) {
		this.apiKeys = apiKeys;
	}
	
	public double getPortfolioTotalValue() {
		return portfolioTotalValue;
	}
	public void setPortfolioTotalValue(double portfolioTotalValue) {
		this.portfolioTotalValue = portfolioTotalValue;
	}
	public double getPortfolioTotalDeposited() {
		return portfolioTotalDeposited;
	}
	public void setPortfolioTotalDeposited(double portfolioTotalDeposited) {
		this.portfolioTotalDeposited = portfolioTotalDeposited;
	}
	public double getPortfolioTotalWithdrawn() {
		return portfolioTotalWithdrawn;
	}
	public void setPortfolioTotalWithdrawn(double portfolioTotalWithdrawn) {
		this.portfolioTotalWithdrawn = portfolioTotalWithdrawn;
	}
	public double getPortfolioTotalInvestment() {
		return portfolioTotalInvestment;
	}
	public void setPortfolioTotalInvestment(double portfolioTotalInvestment) {
		this.portfolioTotalInvestment = portfolioTotalInvestment;
	}
	public double getPortfolioProfitLoss() {
		return portfolioProfitLoss;
	}
	public void setPortfolioProfitLoss(double portfolioProfitLoss) {
		this.portfolioProfitLoss = portfolioProfitLoss;
	}
	public double getPortfolioROI() {
		return portfolioROI;
	}
	public void setPortfolioROI(double portfolioROI) {
		this.portfolioROI = portfolioROI;
	}
	


}
