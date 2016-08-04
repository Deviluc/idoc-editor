package model;

public class FieldValue {
	
	private final String value, description;

	public FieldValue(final String value, final String description) {
		this.value = value;
		this.description = description;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return value + " - " + description;
	}

}
