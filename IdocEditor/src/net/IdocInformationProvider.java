package net;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.FieldValue;
import model.SegmentDescription;
import util.XpathUtil;

public class IdocInformationProvider {

	public static void enrichSegmentInformations(final SegmentDescription segmentDescription) throws MalformedURLException, ParserConfigurationException, IOException, XPathExpressionException {
		String ident = segmentDescription.getInternalName();
		
		if (ident == null) {
			return;
		}
		
		if (ident.length() > 7) {
			ident = ident.substring(0, 7);
		}
		
		final String relativeUrl = (ident.charAt(0) + "").toLowerCase() + "/" + ident.substring(0, 4).toLowerCase() + "/" + ident.toLowerCase();
		
		String url = "http://www.se80.co.uk/saptables/" + relativeUrl + ".htm";
		
		Document doc = DocumentProvider.getCleanXmlDocument(url);
		
		NodeList fields = (NodeList) XpathUtil.createXpath("//table[@class='tableFields']//tr[@class='otherField']").evaluate(doc, XPathConstants.NODESET);
		
		for (int i = 0; i < fields.getLength(); i++) {
			Node field = fields.item(i);
			
			String internalName = ((Node) XpathUtil.createXpath("./td[1]").evaluate(field, XPathConstants.NODE)).getTextContent();
			
		}
		
		segmentDescription.getFieldDescriptions().forEach(f -> {
			String fieldUrl = "http://www.se80.co.uk/saptabfields/" + relativeUrl + "-" + f.getInternalName().toLowerCase() + ".htm";
			try {
				Document fieldDoc = DocumentProvider.getCleanXmlDocument(fieldUrl);
				
				boolean hasFixedValues = ((boolean) XpathUtil.createXpath("//table[@class='fixedvalues']").evaluate(fieldDoc, XPathConstants.BOOLEAN));
				
				if (hasFixedValues) {
					NodeList values = ((NodeList) XpathUtil.createXpath("//table[@class='fixedvalues']//tr").evaluate(fieldDoc, XPathConstants.NODESET));
					
					for (int i = 1; i < values.getLength(); i++) {
						Node value = values.item(i);
						
						String fieldValue = ((Node) XpathUtil.createXpath("./td[1]").evaluate(value, XPathConstants.NODE)).getTextContent().trim();
						String fieldValueDescription = ((Node) XpathUtil.createXpath("./td[3]").evaluate(value, XPathConstants.NODE)).getTextContent().trim();
						
						f.addFieldValue(new FieldValue(fieldValue, fieldValueDescription));
					}
					
					if (f.hasFixedValues()) {
						f.getFieldValues().forEach(field -> {
							System.out.println(field.getValue() + " - " + field.getDescription());
						});
					}
				}
				
			} catch (ParserConfigurationException | IOException | DOMException | XPathExpressionException e) {
				//e.printStackTrace();
			}
		});
	}

}
