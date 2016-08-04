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
import model.IdocDescription;
import model.IdocSection;
import model.Segment;
import model.SegmentDescription;
import net.IdocInformationProvider;

import org.xml.sax.SAXException;

public class IdocParser {

	public static Idoc parse(final String filename, final IdocDescription idocDescription) throws IOException {
		BufferedReader reader = null;

		try {
			// Get line ending
			FileInputStream in = new FileInputStream(filename);
			String lineEnding = System.getProperty("line.separator");
			
			while (in.available() > 0) {
				char currentChar = (char) in.read();
				
				if (currentChar == '\r') {
					if ((char) in.read() == '\n') {
						lineEnding = "\r\n";
						break;
					} else {
						lineEnding = "\r";
						break;
					}
				} else if (currentChar == '\n') {
					if ((char) in.read() == '\r') {
						lineEnding = "\n\r";
					} else {
						lineEnding = "\n";
					}
				}
			}
			
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

			
			final Idoc result = new Idoc("test.idoc", lineEnding);
			
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
				idocDescription.forEach(s -> {
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
