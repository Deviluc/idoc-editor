package util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XpathUtil {

	public static XPathExpression createXpath(final String xpath) throws XPathExpressionException {
		final XPathFactory xPathfactory = XPathFactory.newInstance();
		final XPath x = xPathfactory.newXPath();
		final XPathExpression expr = x.compile(xpath);

		return expr;
	}

}
