package nl.kolkos.cryptoManager;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Transaction {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	// general fields (for Deposit and Withdrawal)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date transactionDate;
	private double amount;
	private double value;
	private String remarks;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "transaction_type_id")
	private TransactionType transactionType;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "currency_id")
	private Currency currency;
		
	// these fields don't need to be in the database since there are calculated
	@Transient
	private double currentValue;
	
	@Transient
	private double currentDifference;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
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

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public double getCurrentDifference() {
		return currentDifference;
	}

	public void setCurrentDifference(double currentDifference) {
		this.currentDifference = currentDifference;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
}
