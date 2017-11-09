package nl.kolkos.cryptoManager;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	
}
