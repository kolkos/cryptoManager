package nl.kolkos.cryptoManager;



import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity // This tells Hibernate to make a table out of this class
public class Deposit {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	
	private Date depositDate;
	private double amount;
	private double purchaseValue;
	private String remarks;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getDepositDate() {
		return depositDate;
	}
	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getPurchaseValue() {
		return purchaseValue;
	}
	public void setPurchaseValue(double purchaseValue) {
		this.purchaseValue = purchaseValue;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Wallet getWallet() {
		return wallet;
	}
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	

	
	
}
