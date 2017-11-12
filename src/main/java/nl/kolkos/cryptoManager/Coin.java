package nl.kolkos.cryptoManager;



import javax.persistence.*;
import org.hibernate.validator.constraints.NotBlank;




@Entity // This tells Hibernate to make a table out of this class
@Table(name = "coin")
public class Coin {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "coin_market_cap_coin_id")
	private CoinMarketCapCoin coinMarketCapCoin;
	
			
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

	
}
