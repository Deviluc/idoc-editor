package model;

public class FieldDescription {

	private final String name;
	private final int length, offset;

	public FieldDescription(final String name, final int length, final int offset) {
		this.name = name;
		this.length = length;
		this.offset = offset;
	}

	public String getName() {
		return name;
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

}
