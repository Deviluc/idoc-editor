package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import model.FieldDescription;
import model.SegmentDescription;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IdocDescriptionParser {

	public static List<SegmentDescription> parse() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final Document doc = factory.newDocumentBuilder().parse("PEXR2003_d.xml");

		//final NodeList segHeads = (NodeList) createXpath("(//ul)[1]/li").evaluate(doc, XPathConstants.NODESET);
		final NodeList segHeads = (NodeList) createXpath("//li/b/parent::li[contains(.,'Status')]").evaluate(doc, XPathConstants.NODESET);
		
		final Pattern minMaxPattern = Pattern.compile("min\\.\\sAnzahl\\s:[\\s]+([0-9]+)[\\s]+,\\smax.\\sAnzahl\\s:[\\s]+([0-9]+)");
		final Pattern offsetLengthPattern = Pattern.compile("Offset\\s:\\s([0-9]+)\\.\\sexterne\\sL�nge\\s:\\s([0-9]+)");
		final Pattern fieldIdentifierPattern = Pattern.compile("Segmentdefinition\\s([^\\n\\r]*)");

		final List<SegmentDescription> segments = new ArrayList<SegmentDescription>();

		for (int i = 0; i < segHeads.getLength(); i++) {
			final Node segHead = segHeads.item(i);
			final String segName = ((Node) createXpath(".//b").evaluate(segHead, XPathConstants.NODE)).getTextContent();
			final String[] identNameList = segName.split(":");

			final String segBasicInfo = ((Node) createXpath(".//p[contains(., 'Status')]").evaluate(segHead, XPathConstants.NODE)).getTextContent();

			final Matcher match = minMaxPattern.matcher(segBasicInfo);

			if (match.find()) {
				final int min = Integer.parseInt(match.group(1));
				final int max = Integer.parseInt(match.group(2));
				
				final String name = segName.substring(segName.indexOf(":") + 1).trim();

				final SegmentDescription segment = new SegmentDescription(name, min, max);
				segment.addIdentifier(identNameList[0].trim());
				
				if (identNameList[0].trim().equals("E1IDB02")) {
					segment.addIdentifier("E2IDB02003");
				}

				final Node fieldLink = ((Node) createXpath(".//a").evaluate(segHead, XPathConstants.NODE));
				final String fieldIdentifier = fieldLink.getAttributes().getNamedItem("href").getTextContent();
				final NodeList fields = (NodeList) createXpath("//li/h2/a[@name='" + fieldIdentifier.substring(1) + "']/ancestor::li[1]//li").evaluate(doc, XPathConstants.NODESET);
				
				final String fieldIdentifier2 = ((Node) createXpath("./ancestor::li[1]/p[1]").evaluate(fields.item(0), XPathConstants.NODE)).getTextContent();
				
				Matcher matcher = fieldIdentifierPattern.matcher(fieldIdentifier2);
				
				if (matcher.find()) {
					System.out.println("Found Segment: " + matcher.group(1).trim());
					segment.addIdentifier(matcher.group(1).trim());
				} else {
					throw new RuntimeException("Couldn't parse Segment: " + segment.getName() + "\tReason: No identifier found!");
				}
				
				for (int n = 0; n < fields.getLength(); n++) {
					final Node field = fields.item(n);
					final String fieldName = ((Node) createXpath("./b").evaluate(field, XPathConstants.NODE)).getTextContent();
					
					
					final String fieldInfo = ((Node) createXpath("./p[2]").evaluate(field, XPathConstants.NODE)).getTextContent();

					matcher = offsetLengthPattern.matcher(fieldInfo);

					if (matcher.find()) {
						final int offset = Integer.parseInt(matcher.group(1));
						final int length = Integer.parseInt(matcher.group(2));

						segment.addFieldDescription(new FieldDescription(fieldName, length, offset));
					} else {
						throw new RuntimeException("Couldn't parse field: " + fieldName);
					}

				}

				segments.add(segment);
			} else {
				throw new RuntimeException("Couldn't find segment info in: " + segBasicInfo);
			}
		}

		return segments;
	}

	private static XPathExpression createXpath(final String xpath) throws XPathExpressionException {
		final XPathFactory xPathfactory = XPathFactory.newInstance();
		final XPath x = xPathfactory.newXPath();
		final XPathExpression expr = x.compile(xpath);

		return expr;
	}

}
