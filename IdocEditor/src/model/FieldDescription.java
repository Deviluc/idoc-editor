package model;

import java.util.ArrayList;
import java.util.List;

public class FieldDescription {

	private final String name, internalName;
	private final int length, offset;
	private List<FieldValue> fieldValues;

	public FieldDescription(final String name,final String internalName, final int length, final int offset) {
		this.name = name;
		this.length = length;
		this.offset = offset;
		this.internalName = internalName;
	}

	public String getName() {
		return name;
	}
	
	public String getInternalName() {
		return internalName;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	public String getFormattedContent(final Field field) {
		return field.getContent().trim();
	}
	
	public boolean hasFixedValues() {
		return fieldValues != null && fieldValues.size() > 0;
	}
	
	public List<FieldValue> getFieldValues() {
		return fieldValues;
	}
	
	public void addFieldValue(final FieldValue qualifier) {
		if (fieldValues == null) {
			fieldValues = new ArrayList<FieldValue>();
		}
		
		fieldValues.add(qualifier);
	}
	
	

}
