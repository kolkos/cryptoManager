package nl.kolkos.cryptoManager;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/deposit") // This means URL's start with /demo (after Application path)
public class DepositController {
	@Autowired
	private DepositRepository depositRepository;
	
	@GetMapping("/")
    public String forwardDepositForm(Model model) {
        model.addAttribute("deposit", new Deposit());
        return "deposit_form";
    }
	
	@GetMapping("/add")
    public String depositForm(Model model) {
        model.addAttribute("deposit", new Deposit());
        return "deposit_form";
    }
	
	@PostMapping(path="/add") // Map ONLY GET Requests
	public @ResponseBody String addNewDeposit (
			@RequestParam Date depositDate,
			@RequestParam int walletId,
			@RequestParam double amount,
			@RequestParam double purchaseValue,
			@RequestParam String remarks) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		System.out.println(String.format("depositDate=%s, walletId=%d, amount=%f, purchaseValue=%f, remarks=%s", depositDate, walletId, amount, purchaseValue, remarks));
		
				
		Deposit deposit = new Deposit();
		deposit.setDepositDate(depositDate);
		deposit.setWalletId(walletId);
		deposit.setAmount(amount);
		deposit.setPurchaseValue(purchaseValue);
		deposit.setRemarks(remarks);
		
		depositRepository.save(deposit);
		
		
		return "Saved";
	}

	@GetMapping(path="/list")
	public @ResponseBody Iterable<Deposit> getAllDeposits() {
		// This returns a JSON or XML with the users
		return depositRepository.findAll();
	}
}
