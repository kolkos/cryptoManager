package nl.kolkos.cryptoManager;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Currency {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String currencyName;
	private String currencyISOCode;
	private String currencySymbol;
	private String currencyGlyph;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyISOCode() {
		return currencyISOCode;
	}
	public void setCurrencyISOCode(String currencyISOCode) {
		this.currencyISOCode = currencyISOCode;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public String getCurrencyGlyph() {
		return currencyGlyph;
	}
	public void setCurrencyGlyph(String currencyGlyph) {
		this.currencyGlyph = currencyGlyph;
	}
	
}
