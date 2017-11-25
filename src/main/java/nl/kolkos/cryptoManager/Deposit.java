package nl.kolkos.cryptoManager;



import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity // This tells Hibernate to make a table out of this class
public class Deposit {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	private Date depositDate;
	private double amount;
	private double purchaseValue;
	private String remarks;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	
	
	
	// these fields don't need to be in the database since there are calculated
	@Transient
	private double currentDepositValue;
	
	@Transient
	private double currentDepositDifference;
		
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public double getCurrentDepositValue() {
		return currentDepositValue;
	}
	
	public void setCurrentDepositValue(double currentDepositValue) {
		this.currentDepositValue = currentDepositValue;
	}
	public double getCurrentDepositDifference() {
		return currentDepositDifference;
	}
	public void setCurrentDepositDifference(double currentDepositDifference) {
		this.currentDepositDifference = currentDepositDifference;
	}
	
	
	
	
}
