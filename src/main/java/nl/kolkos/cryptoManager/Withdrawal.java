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
public class Withdrawal {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	private Date withdrawalDate;
	private double amount;
	private double withdrawalValue;
	private String remarks;
	private boolean toCash;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	
	// these fields don't need to be in the database since there are calculated
	@Transient
	private double currentWithdrawalValue;
	
	@Transient
	private double currentWithdrawalDifference;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getWithdrawalDate() {
		return withdrawalDate;
	}

	public void setWithdrawalDate(Date withdrawalDate) {
		this.withdrawalDate = withdrawalDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getWithdrawalValue() {
		return withdrawalValue;
	}

	public void setWithdrawalValue(double withdrawalValue) {
		this.withdrawalValue = withdrawalValue;
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

	public boolean isToCash() {
		return toCash;
	}

	public void setToCash(boolean toCash) {
		this.toCash = toCash;
	}

	public double getCurrentWithdrawalValue() {
		return currentWithdrawalValue;
	}

	public void setCurrentWithdrawalValue(double currentWithdrawalValue) {
		this.currentWithdrawalValue = currentWithdrawalValue;
	}

	public double getCurrentWithdrawalDifference() {
		return currentWithdrawalDifference;
	}

	public void setCurrentWithdrawalDifference(double currentWithdrawalDifference) {
		this.currentWithdrawalDifference = currentWithdrawalDifference;
	}
	
	
	

	
	
}
