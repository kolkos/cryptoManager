package nl.kolkos.cryptoManager;

public class FormOption {
	private String value;
	private String text;
	
	public FormOption(String value, String text) {
		this.setValue(value);
		this.setText(text);
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
