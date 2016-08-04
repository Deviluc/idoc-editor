package model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Idoc {

	private final String filename, lineEnding;
	private final List<IdocSection> sections;

	public Idoc(final String filename, final String lineEnding) {
		this.filename = filename;
		this.lineEnding = lineEnding;
		sections = new ArrayList<IdocSection>();
	}

	public String getFilename() {
		return filename;
	}
	
	public String getLineEnding() {
		return lineEnding;
	}

	public String generateIdocFile() {
		final StringWriter writer = new StringWriter();

		sections.forEach(sec -> {
			writer.append(sec.getControlSegment().getContent() + lineEnding);
			sec.getSegments().forEach(seg -> {
				final StringWriter segmentWriter = new StringWriter();
				
				segmentWriter.append(seg.getSegmentBase());
				
				seg.getFields().forEach(f -> {
					segmentWriter.append(f.getContent());
				});
				
				writer.append(segmentWriter.getBuffer().toString().trim() + lineEnding);
			});
		});

		return writer.toString();
	}
	
	public List<IdocSection> getSections() {
		return sections;
	}

	public void addSection(final IdocSection section) {
		sections.add(section);
	}

}
