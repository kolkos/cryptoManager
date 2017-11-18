package nl.kolkos.cryptoManager;

import java.util.Date;

public class WalletChartLine {
	private Date date;
	private double totalInvested;
	private double value;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public double getTotalInvested() {
		return totalInvested;
	}
	public void setTotalInvested(double totalInvested) {
		this.totalInvested = totalInvested;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	
	
	
}
