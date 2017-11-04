package nl.kolkos.cryptoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/portfolio") // This means URL's start with /demo (after Application path)
public class PortfolioController {
	@Autowired
	private PortfolioRepository portfolioRepository;
	
	// forward to list
	@GetMapping("/")
    public String forwardRepositoryList(Model model) {
        return "redirect:/portfolio/list";
    }
	
	// send the form
	@GetMapping("/add")
    public String portfolioForm(Model model) {
		model.addAttribute("portfolio", new Portfolio());
        return "portfolio_form";
    }
	
	// handle the form
	@PostMapping(path="/add") // Map ONLY Post Requests
	public @ResponseBody String addNewPortfolio (
			@RequestParam String description,
			@RequestParam String name) {
				
		Portfolio portfolio = new Portfolio();
		portfolio.setDescription(description);
		portfolio.setName(name);
		portfolioRepository.save(portfolio);
				
		String message = String.format("Portfolio '%s' created", name);
		
		
		return message;
		
	}
	
	// list all 
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Portfolio> getAllPortfolios() {
		// This returns a JSON or XML with the users
		return portfolioRepository.findAll();
	}
	
}
