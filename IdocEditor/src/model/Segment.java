package model;

import java.util.ArrayList;
import java.util.List;

public class Segment {

	private final SegmentDescription description;
	private String segmentBase;
	private final List<Field> fields;

	public Segment(final SegmentDescription description) {
		this.description = description;
		fields = new ArrayList<Field>();
	}
	
	public String getSegmentBase() {
		return segmentBase;
	}

	public SegmentDescription getSegmentDescription() {
		return description;
	}

	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Parses a line into fields, doesn't check if this line matches any requirements
	 *
	 * @param line
	 */
	public void parseLine(final String line) {
		segmentBase = line.substring(0, description.getFieldDescriptions().get(0).getOffset());
		description.getFieldDescriptions().stream().filter(f -> f.getOffset() < line.length() - 1).forEach(f -> {
			int end = f.getOffset() + f.getLength();
			
			if (end >= line.length()) {
				end = line.length();
			}
			
			final Field field = new Field(f);
			field.setContent(line.substring(f.getOffset(), end));
			fields.add(field);
		});
	}

}
