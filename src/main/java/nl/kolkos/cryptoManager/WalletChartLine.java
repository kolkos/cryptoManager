package nl.kolkos.cryptoManager;

import java.util.Date;

public class WalletChartLine {
	private Date date;
	private double totalDepositValue;
	private double value;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getTotalDepositValue() {
		return totalDepositValue;
	}
	public void setTotalDepositValue(double totalDepositValue) {
		this.totalDepositValue = totalDepositValue;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	
	
	
}
