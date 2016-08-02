package model;

public class Field {

	private final FieldDescription description;
	private String content;

	public Field(final FieldDescription description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public FieldDescription getFieldDescription() {
		return description;
	}

	public void setContent(final String content) {
		if (content.length() > description.getLength()) {
			throw new IllegalArgumentException("Cannot set content to '" + content + "' because the length is bigger then " + description.getLength());
		} else if (content.length() == description.getLength()) {
			this.content = content;
		} else {
			final String spaceString = new String(new char[description.getLength() - content.length()]).replace('\0', ' ');
			this.content = content + spaceString;
		}
	}

}
