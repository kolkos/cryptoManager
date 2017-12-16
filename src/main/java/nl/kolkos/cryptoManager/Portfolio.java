package nl.kolkos.cryptoManager;

import java.util.Set;
import javax.persistence.*;


@Entity // This tells Hibernate to make a table out of this class
public class Portfolio {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	private String name;
	private String description;
	
	// user rights
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_portfolio", joinColumns = @JoinColumn(name = "portfolio_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;
	
	// user rights
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "apikey_portfolio", joinColumns = @JoinColumn(name = "portfolio_id"), inverseJoinColumns = @JoinColumn(name = "api_key_id"))
    private Set<ApiKey> apiKeys;
	
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
	
	public Set<ApiKey> getApiKeys() {
		return apiKeys;
	}
	public void setApiKeys(Set<ApiKey> apiKeys) {
		this.apiKeys = apiKeys;
	}
	


}
