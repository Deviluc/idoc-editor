package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import model.Idoc;
import parser.IdocDescriptionParser;
import parser.IdocParser;

public class TestIdocParsing {

	@Test
	public void testDescriptionParser() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		IdocDescriptionParser.parse();
	}

	@Test
	public void testIdocParser() throws IOException {
		Idoc idoc = IdocParser.parse("test.idoc");
		
		FileWriter writer = new FileWriter(new File(idoc.getFilename() + ".generated"));
		writer.write(idoc.generateIdocFile());
		writer.close();
		
	}

}
