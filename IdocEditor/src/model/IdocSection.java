package model;

import java.util.ArrayList;
import java.util.List;

public class IdocSection {
	
	private final ControlSegment controlSegment;
	private final List<Segment> segments;
	
	public IdocSection(final ControlSegment controlSegment) {
		this.controlSegment = controlSegment;
		this.segments = new ArrayList<Segment>();
	}
	
	public ControlSegment getControlSegment() {
		return controlSegment;
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	public void addSegment(final Segment segment) {
		segments.add(segment);
	}

}
