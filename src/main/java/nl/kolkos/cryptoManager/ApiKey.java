package nl.kolkos.cryptoManager;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.Set;
import java.util.UUID;

@Entity
public class ApiKey {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String apiKey;
	private String description;
	
	@ManyToMany(mappedBy = "apiKeys")
	private Set<Portfolio> portfolios;
	
	public String generateRandomKey() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
