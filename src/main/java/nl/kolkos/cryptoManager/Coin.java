package nl.kolkos.cryptoManager;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

import javax.persistence.*;
import org.hibernate.validator.constraints.NotBlank;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "coin")
@EntityListeners(AuditingEntityListener.class)
public class Coin implements Serializable{
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