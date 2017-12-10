package nl.kolkos.cryptoManager;

import java.util.List;
import java.util.Set;

import javax.persistence.*;


@Entity // This tells Hibernate to make a table out of this class
public class Portfolio {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	private String name;
	private String description;
	
	//@ManyToMany(mappedBy = "portfolios")
	@ManyToMany(cascade = { 
	        CascadeType.PERSIST, 
	        CascadeType.MERGE
	    })
	@JoinTable(name = "user_portfolio", joinColumns = @JoinColumn(name = "portfolio_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;
	
	public Portfolio() {
		
	}
	
	public Portfolio(String name, String description, List<User> users) {
		this.name = name;
		this.description = description;
		this.users = users;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	


}
