package interfaces;

public class ConfigurationException extends Exception {

	public ConfigurationException() {}
	
	public ConfigurationException(Throwable cause) {
		super(cause);
	}
	
	public ConfigurationException(String message) {
		super(message);
	}
	
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = -3580595687258491537L;

}
