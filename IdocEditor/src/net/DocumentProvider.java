package net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

public class DocumentProvider {

	public static Document getCleanXmlDocument(final String url) throws ParserConfigurationException, MalformedURLException, IOException {
		CleanerProperties props = new CleanerProperties();
		 
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		 
		// do parsing
		TagNode tagNode = new HtmlCleaner(props).clean(new URL(url));
		 
		
		return new DomSerializer(props).createDOM(tagNode);
	}

}
