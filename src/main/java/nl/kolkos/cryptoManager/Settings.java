package nl.kolkos.cryptoManager;

import javax.persistence.*;
import org.hibernate.validator.constraints.NotBlank;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "settings")
public class Settings {
	@Id
	@Column(name = "option", nullable = false)
	private String option;
	
	@NotBlank
	private String value;
	
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	
}
