package nl.kolkos.cryptoManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity // This tells Hibernate to make a table out of this class
public class CoinMarketCapCoin {
	@Id
	private String id;
	private String name;
	private String symbol;
	
	@Transient
	private boolean synced;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public boolean isSynced() {
		return synced;
	}
	public void setSynced(boolean synced) {
		this.synced = synced;
	}
	
	
}
