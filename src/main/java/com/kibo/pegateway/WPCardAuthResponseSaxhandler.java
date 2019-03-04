package com.kibo.pegateway;

import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author ankit.gupta
 * XML parser to reader the specific xml tags(lastEvent, orderCode and paymentMethod)
 */
public class WPCardAuthResponseSaxhandler extends DefaultHandler {
	
	 private String response;
	 private String paymentMethod;
	 private String orderCode;
	 boolean hasResponse;
	 private boolean hasPaymentMethod;

	public String getResponse() {
		return this.response;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public boolean isHasResponse() {
		return hasResponse;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("lastEvent")) {
			hasResponse = true;
		}
		
		if (qName.equalsIgnoreCase("paymentMethod")) {
			hasPaymentMethod = true;
		}
		
		if (qName.equalsIgnoreCase("orderStatus")) {
			
			if(attributes != null && attributes.getLength() > 0) {
				int length = attributes.getLength();
				for (int i=0; i<length; i++) {
					String attributeName = attributes.getQName(i);
					String attributeValue = attributes.getValue(i);
					if(attributeName != null && attributeName.equalsIgnoreCase("orderCode")) {
						orderCode = attributeValue;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (hasResponse) {
			response = new String(ch, start, length).trim();
			hasResponse = false;
		}
		
		if (hasPaymentMethod) {
			paymentMethod = new String(ch, start, length).trim();
			hasPaymentMethod = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) {
	    if (systemId.endsWith(".dtd")) {
	      return new InputSource(new StringReader(" "));
	    } else {
	      return null;
	    }
	}

}
