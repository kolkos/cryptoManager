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
	
	@ManyToMany(mappedBy = "portfolios")
    private Set<User> users;
	
	
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
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	


}
