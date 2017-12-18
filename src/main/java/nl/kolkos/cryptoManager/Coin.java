package nl.kolkos.cryptoManager;



import javax.persistence.*;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity // This tells Hibernate to make a table out of this class
@Table(name = "coin")
public class Coin {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "coin_market_cap_coin_id")
	private CoinMarketCapCoin coinMarketCapCoin;
	
	
	@Transient
	private double currentCoinValue;
	
	@Transient
	@JsonIgnore
	private double winLoss1h;
	
	@Transient
	@JsonIgnore
	private double winLoss1d;
	
	@Transient
	@JsonIgnore
	private double winLoss1w;
	
	public Coin() {
		
	}
	
	public Coin(Coin coinToCopy) {
		this.id = coinToCopy.id;
		this.coinMarketCapCoin = coinToCopy.coinMarketCapCoin;
		this.currentCoinValue = coinToCopy.currentCoinValue;
		
	}
			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public CoinMarketCapCoin getCoinMarketCapCoin() {
		return coinMarketCapCoin;
	}
	
	public void setCoinMarketCapCoin(CoinMarketCapCoin coinMarketCapCoin) {
		this.coinMarketCapCoin = coinMarketCapCoin;
	}
	public double getCurrentCoinValue() {
		return currentCoinValue;
	}
	public void setCurrentCoinValue(double currentCoinValue) {
		this.currentCoinValue = currentCoinValue;
	}
	public double getWinLoss1h() {
		return winLoss1h;
	}
	public void setWinLoss1h(double winLoss1h) {
		this.winLoss1h = winLoss1h;
	}
	public double getWinLoss1d() {
		return winLoss1d;
	}
	public void setWinLoss1d(double winLoss1d) {
		this.winLoss1d = winLoss1d;
	}
	public double getWinLoss1w() {
		return winLoss1w;
	}
	public void setWinLoss1w(double winLoss1w) {
		this.winLoss1w = winLoss1w;
	}

	
}
