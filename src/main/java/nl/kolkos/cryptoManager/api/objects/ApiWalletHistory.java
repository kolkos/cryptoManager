package nl.kolkos.cryptoManager.api.objects;

import java.util.Date;

import nl.kolkos.cryptoManager.Wallet;

public class ApiWalletHistory {
	public Date date;
	public Wallet wallet;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Wallet getWallet() {
		return wallet;
	}
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
	
	
}
