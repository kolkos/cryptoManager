package nl.kolkos.cryptoManager.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import nl.kolkos.cryptoManager.Deposit;
import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.repositories.DepositRepository;

@Service
public class DepositService {
	private final static int PAGESIZE = 25;
	
	@Autowired
	DepositRepository depositRepository;
	
	public void save(Deposit deposit) {
		depositRepository.save(deposit);
	}
	
	public Iterable<Deposit> findAll() {
		return depositRepository.findAll();
	}
	
	public Deposit findById(Long id) {
		return depositRepository.findById(id);
	}
	
	public Deposit findByDepositDateAndAmountAndPurchaseValue(Date depositDate, double amount, double purchaseVale) {
		return depositRepository.findByDepositDateAndAmountAndPurchaseValue(depositDate, amount, purchaseVale);
	}
	
	
	public List<Deposit> findByWallet(Wallet wallet){
		return depositRepository.findByWallet(wallet);
	}
	
	public double getSumOfAmountForWalletId(long walletId) {
		return depositRepository.getSumOfAmountForWalletId(walletId);
	}
	
	public double getSumOfPurchaseValueForWalletId(long walletId) {
		return depositRepository.getSumOfPurchaseValueForWalletId(walletId);
	}
	
	public List<Deposit> filterResults(String coinId, String walletId, String PortfolioId){
		return depositRepository.filterResults(coinId, walletId, PortfolioId);
	}
	
	public double getSumOfAmountForWalletIdAndBeforeDepositDate(long walletId, Date depositDate) {
		return depositRepository.getSumOfAmountForWalletIdAndBeforeDepositDate(walletId, depositDate);
	}
	
	public double getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(long walletId, Date depositDate) {
		return depositRepository.getSumOfPurchaseValueForWalletIdAndBeforeDepositDate(walletId, depositDate);
	}
	
	public int getNumberOfDeposits() {
		return depositRepository.getNumberOfDeposits();
	}
	
	public List<Deposit> findByWalletPortfolioUsersEmail(int pageNumber, String columnName, String direction, String email){
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		
		
		return depositRepository.findByWalletPortfolioUsersEmail(email, request).getContent();
	}
	
	public List<Deposit> getPage(int pageNumber, String columnName, String direction) {
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		return depositRepository.findAll(request).getContent();
		
	}
	
	public void createDeposit(Date depositDate, double amount, double purchaseValue, Wallet wallet, String transactionRemarks) {
		// create a new deposit
		Deposit deposit = new Deposit();
		deposit.setDepositDate(new java.sql.Date(depositDate.getTime()));
		deposit.setAmount(amount);
		deposit.setPurchaseValue(purchaseValue);
		deposit.setRemarks(transactionRemarks);
		deposit.setWallet(wallet);
		
		// save the deposit
		depositRepository.save(deposit);
	}
	
	public void deleteDeposit(Deposit deposit) {
		depositRepository.delete(deposit);
	}
	
	public void deleteDeposit(List<Deposit> deposits) {
		for(Deposit deposit : deposits) {
			this.deleteDeposit(deposit);
		}
	}
}
