package nl.kolkos.cryptoManager.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import nl.kolkos.cryptoManager.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

}
