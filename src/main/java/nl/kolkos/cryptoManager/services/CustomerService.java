package nl.kolkos.cryptoManager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import nl.kolkos.cryptoManager.Customer;
import nl.kolkos.cryptoManager.repositories.CustomerRepository;

@Service
public class CustomerService {
	private final static int PAGESIZE = 25;
	
	@Autowired
	CustomerRepository repository;
	
	public void save(Customer customer) {
		repository.save(customer);
	}
	
	public Iterable<Customer> findAllCustomers() {
		return repository.findAll();
	}
	
	public List<Customer> getPage(int pageNumber, String columnName, String direction) {
		Sort.Direction sort = Sort.Direction.ASC;
		if(direction.equals("DESC")) {
			sort = Sort.Direction.DESC;
		}
		
		PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE, sort, columnName);
		
		return repository.findAll(request).getContent();
	}
}
