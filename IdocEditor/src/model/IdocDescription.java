package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.XmlUtil;
import util.XpathUtil;

public class IdocDescription extends ArrayList<SegmentDescription>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IdocDescription() {
		
	}
	
	public IdocDescription(final String filePath) {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final Document doc = factory.newDocumentBuilder().parse(filePath);
			
			List<Node> segs = XmlUtil.asList(doc.getElementsByTagName("IdocPexr2003").item(0).getChildNodes());
			
			segs.forEach(d -> {
				NamedNodeMap segMap = d.getAttributes();
				SegmentDescription segDes = new SegmentDescription(segMap.getNamedItem("name").getTextContent(), Integer.parseInt(segMap.getNamedItem("min").getTextContent()), Integer.parseInt(segMap.getNamedItem("max").getTextContent()));
				segDes.setInternalName(segMap.getNamedItem("internal-name").getTextContent());
				
				List<Node> fields = XmlUtil.asList(d.getChildNodes());
				
				fields.stream().filter(f -> f.getNodeName().equals("Field")).forEach(f -> {
					NamedNodeMap fieldMap = f.getAttributes();
					FieldDescription fieldDes = new FieldDescription(fieldMap.getNamedItem("name").getTextContent(), fieldMap.getNamedItem("internal-name").getTextContent(), Integer.parseInt(fieldMap.getNamedItem("length").getTextContent()), Integer.parseInt(fieldMap.getNamedItem("offset").getTextContent()));
					boolean hasFixedValues = Boolean.parseBoolean(fieldMap.getNamedItem("fixed-value").getTextContent());
					
					if (hasFixedValues) {
						List<Node> values;
						try {
							values = XmlUtil.asList((NodeList) XpathUtil.createXpath("//Segment[@internal-name='" + segDes.getInternalName() + "']/Field[@internal-name='" + fieldDes.getInternalName() + "']/Values/PossibleValue").evaluate(doc, XPathConstants.NODESET));
							values.forEach(v -> {
								FieldValue value = new FieldValue(v.getFirstChild().getFirstChild().getNodeValue(), v.getLastChild().getFirstChild().getNodeValue());
								fieldDes.addFieldValue(value);
							});
						} catch (XPathExpressionException e) {
							throw new RuntimeException("Cannot read idoc-description from file: " + filePath, e);
						}
						
						
					}
					
					segDes.addFieldDescription(fieldDes);
				});
				
				fields.stream().filter(f -> f.getNodeName().equals("Identifier")).forEach(f -> {
					segDes.addIdentifier(f.getTextContent());
				});
				
				add(segDes);
				
			});
			
			
		} catch (Exception e) {
			throw new RuntimeException("Cannot read idoc-description from file: " + filePath, e);
		}
		
	}
	
	
	public void writeToFile(final String filePath) throws RuntimeException {
		DOMSource xmlSource = new DOMSource(generateXmlDocument());
		StreamResult outputTarget = new StreamResult(new File(filePath));
		
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			throw new RuntimeException("Cannot write idoc-description to file!", e);
		}
	}
	
	
	private Document generateXmlDocument() throws RuntimeException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			final Element root = doc.createElement("IdocPexr2003");
			doc.appendChild(root);
			
			forEach(d -> {
				final Element segDes = doc.createElement("Segment");
				root.appendChild(segDes);
				
				segDes.setAttribute("name", d.getName());
				segDes.setAttribute("internal-name", d.getInternalName());
				segDes.setAttribute("min", d.getMinimumOccurences() + "");
				segDes.setAttribute("max", d.getMaximalOccurences() + "");
				
				d.getIdentifiers().forEach(i -> {
					Element identifier = doc.createElement("Identifier");
					identifier.appendChild(doc.createTextNode(i));
					segDes.appendChild(identifier);
				});
				
				d.getFieldDescriptions().forEach(f -> {
					final Element fieldDes = doc.createElement("Field");
					segDes.appendChild(fieldDes);
					
					fieldDes.setAttribute("name", f.getName());
					fieldDes.setAttribute("internal-name", f.getInternalName());
					fieldDes.setAttribute("offset", f.getOffset() + "");
					fieldDes.setAttribute("length", f.getLength() + "");
					fieldDes.setAttribute("fixed-value", f.hasFixedValues() + "");
					
					
					if (f.hasFixedValues()) {
						final Element fieldValues = doc.createElement("Values");
						fieldDes.appendChild(fieldValues);
						
						f.getFieldValues().forEach(v -> {
							Element possibleValue = doc.createElement("PossibleValue");
							fieldValues.appendChild(possibleValue);
							
							Element value = doc.createElement("Value");
							value.appendChild(doc.createTextNode(v.getValue()));
							possibleValue.appendChild(value);
							
							Element description = doc.createElement("Description");
							description.appendChild(doc.createTextNode(v.getDescription()));
							possibleValue.appendChild(description);
						});
					}
					
				});
				
			});
			
			
			return doc;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Cannot generate idoc-description document!", e);
		}
	}

}
