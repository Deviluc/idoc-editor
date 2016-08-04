package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import model.ControlSegment;
import model.Idoc;
import model.IdocSection;
import model.Segment;
import model.SegmentDescription;
import net.IdocInformationProvider;

import org.xml.sax.SAXException;

public class IdocParser {

	public static Idoc parse(final String filename) throws IOException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

			List<SegmentDescription> segmentDescriptions = null;

			try {
				segmentDescriptions = IdocDescriptionParser.parse();
			} catch (XPathExpressionException | SAXException | ParserConfigurationException e) {
				throw new RuntimeException("Cannot parse idoc-description!", e);
			}
			
			segmentDescriptions.forEach(s -> {
				try {
					IdocInformationProvider.enrichSegmentInformations(s);
				} catch (XPathExpressionException | ParserConfigurationException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			
			final Idoc result = new Idoc("test.idoc");
			
			String line;
			IdocSection currentSection = null;

			while ((line = reader.readLine()) != null) {
				
				if (!line.startsWith("EDI_DC40")) {
					if (currentSection == null) {
						throw new RuntimeException("Empty file or not a PEXR2003 idoc!");
					}
				} else {
					if (currentSection != null) result.addSection(currentSection);
					
					currentSection = new IdocSection(new ControlSegment(line));
					continue;
				}
				
				
				final String finalLine = line;
				final AtomicBoolean matched = new AtomicBoolean(false);
				
				final IdocSection finalSec = currentSection;
				segmentDescriptions.forEach(s -> {
					if (s.isLineOfThisSegmentType(finalLine)) {
						final Segment segment = new Segment(s);
						segment.parseLine(finalLine);
						finalSec.addSegment(segment);
						matched.set(true);
					}
				});
				

				
				if (!matched.get()) {
					//System.out.println("Line skipped: " + line);
				}
			}
			
			if (currentSection != null) result.addSection(currentSection);

			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			reader.close();
		}
		
		return null;
	}

}
