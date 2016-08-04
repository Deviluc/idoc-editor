package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SegmentDescription {

	private final String name;
	private final int min, max;
	private final List<FieldDescription> fields;
	private final List<String> identifiers;
	
	private String internalName;

	public SegmentDescription(final String name, final int min, final int max) {
		this.name = name;
		this.min = min;
		this.max = max;

		fields = new ArrayList<FieldDescription>();
		identifiers = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}
	
	public String getInternalName() {
		return internalName;
	}

	public int getMinimumOccurences() {
		return min;
	}

	public int getMaximalOccurences() {
		return max;
	}

	public List<FieldDescription> getFieldDescriptions() {
		return fields;
	}

	public boolean isLineOfThisSegmentType(final String line) {
		final AtomicBoolean result = new AtomicBoolean(false);

		identifiers.forEach(i -> {
			if (line.startsWith(i)) {
				result.set(true);
			}
		});

		return result.get();
	}

	public void addFieldDescription(final FieldDescription field) {
		fields.add(field);
	}

	public void addIdentifier(final String identifier) {
		identifiers.add(identifier);
	}
	
	public void setInternalName(final String internalName) {
		this.internalName = internalName;
	}

}
