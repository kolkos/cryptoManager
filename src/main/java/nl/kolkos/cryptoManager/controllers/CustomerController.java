package nl.kolkos.cryptoManager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

import nl.kolkos.cryptoManager.Customer;
import nl.kolkos.cryptoManager.services.CustomerService;



@Controller 
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@RequestMapping("/save")
	public String process() {
		customerService.save(new Customer("Jack", "Smith"));
		customerService.save(new Customer("Adam", "Johnson"));
		customerService.save(new Customer("Kim", "Smith"));
		customerService.save(new Customer("David", "Williams"));
		customerService.save(new Customer("Peter", "Davis"));
		return "Done";
	}

	@RequestMapping("/findall")
	public String findAll() {
		String result = "<html>";

		for (Customer customer : customerService.findAllCustomers()) {
			result += customer.toString() + "<br/>";
		}

		return result + "</html>";
	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public String viewCustomers(
			@RequestParam(name = "page", defaultValue = "1") int pageNumber,
			@RequestParam(name = "sortBy", defaultValue = "id") String columnName,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction
			) {
		String result = "<html>";

		List<Customer> customers = customerService.getPage(pageNumber, columnName, direction);

		for (Customer customer : customers) {
			result += customer.toString() + "<br/>";
		}

		return result + "</html>";
	}
	
	@RequestMapping(value = "/customers2", method = RequestMethod.GET)
	public String viewCustomersTable(
			@RequestParam(name = "page", defaultValue = "1") int pageNumber,
			@RequestParam(name = "sortBy", defaultValue = "id") String columnName,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction,
			Model model
			) {
		

		List<Customer> customers = customerService.getPage(pageNumber, columnName, direction);

		model.addAttribute("customers", customers);
		
		model.addAttribute("page", pageNumber);
		model.addAttribute("sortBy", columnName);
		model.addAttribute("direction", direction);
		
		
		

		return "customer_table";
	}
}
