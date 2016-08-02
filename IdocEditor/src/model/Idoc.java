package model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Idoc {

	private final String filename;
	private final List<IdocSection> sections;

	public Idoc(final String filename) {
		this.filename = filename;
		sections = new ArrayList<IdocSection>();
	}

	public String getFilename() {
		return filename;
	}

	public String generateIdocFile() {
		final StringWriter writer = new StringWriter();

		sections.forEach(sec -> {
			writer.append(sec.getControlSegment().getContent() + "\n");
			sec.getSegments().forEach(seg -> {
				writer.append(seg.getSegmentBase());
				seg.getFields().forEach(f -> {
					writer.append(f.getContent());
				});

				writer.append('\n');
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
