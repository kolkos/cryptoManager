package nl.kolkos.cryptoManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import nl.kolkos.cryptoManager.repositories.CoinValueRepository;



@Entity // This tells Hibernate to make a table out of this class
@Table(name = "coin")
public class Coin {
	@Autowired
	@Transient
	@Qualifier(value = "coinValueRepository")
	private CoinValueRepository coinValueRepository;
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String coinName;
	
	private String description;
			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}
