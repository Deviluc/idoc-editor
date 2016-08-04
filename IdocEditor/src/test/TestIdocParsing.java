package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import model.Idoc;
import model.IdocDescription;
import net.IdocInformationProvider;
import parser.IdocDescriptionParser;
import parser.IdocParser;

public class TestIdocParsing {

	@Test
	public void testDescriptionParser() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		IdocDescription des = IdocDescriptionParser.parse();
		
		des.forEach(t -> {
			try {
				IdocInformationProvider.enrichSegmentInformations(t);
			} catch (XPathExpressionException | ParserConfigurationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		des.writeToFile("resources/idoc-description.xml");
	}

	@Test
	public void testIdocParser() throws IOException {
		Idoc idoc = IdocParser.parse("test.idoc", new IdocDescription("resources/idoc-description.xml"));
		
		FileWriter writer = new FileWriter(new File(idoc.getFilename() + ".generated"));
		writer.write(idoc.generateIdocFile());
		writer.close();
		
	}

}
