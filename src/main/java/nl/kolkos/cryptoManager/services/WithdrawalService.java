package nl.kolkos.cryptoManager.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import nl.kolkos.cryptoManager.Wallet;
import nl.kolkos.cryptoManager.Withdrawal;
import nl.kolkos.cryptoManager.repositories.WithdrawalRepository;

@Service
public class WithdrawalService {
	private final static int PAGESIZE = 25;
	
	@Autowired
	WithdrawalRepository withdrawalRepository;
	
	public void save(Withdrawal withdrawal) {
		withdrawalRepository.save(withdrawal);
	}
	
	public Iterable<Withdrawal> findAll() {
		return withdrawalRepository.findAll();
	}
	
	public Withdrawal findById(Long id) {
		return withdrawalRepository.findById(id);
	}
	
	public List<Withdrawal> findByWallet(Wallet wallet){
		return withdrawalRepository.findByWallet(wallet);
	}
	
	public double getSumOfAmountForWalletId(long walletId) {
		return withdrawalRepository.getSumOfAmountForWalletId(walletId);
	}
	
	public double getSumOfWithdrawalsToCashForWalletId(long walletId) {
		return withdrawalRepository.getSumOfWithdrawalsToCashForWalletId(walletId);
	}
	
	public double getSumOfWithdrawalsForWalletId(long walletId) {
		return withdrawalRepository.getSumOfWithdrawalsForWalletId(walletId);
	}
	
	public double getSumOfAmountForWalletIdAndBeforeWithdrawalDate(long walletId, Date depositDate) {
		return withdrawalRepository.getSumOfAmountForWalletIdAndBeforeWithdrawalDate(walletId, depositDate);
	}
	
	public double getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(long walletId, Date depositDate) {
		return withdrawalRepository.getSumOfWithdrawalToCashValueForWalletIdAndBeforeWithdrawalDate(walletId, depositDate);
	}
	
	public int getNumberOfDeposits() {
		return withdrawalRepository.getNumberOfDeposits();
	}
	
	public List<Withdrawal> filterResults(String coinId, String walletId, String PortfolioId){
		return withdrawalRepository.filterResults(coinId, walletId, PortfolioId);
	}
	
	
	public List<Withdrawal> getPage(int pageNumber, String columnName, String direction) {
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		return withdrawalRepository.findAll(request).getContent();
		
	}
	
}
