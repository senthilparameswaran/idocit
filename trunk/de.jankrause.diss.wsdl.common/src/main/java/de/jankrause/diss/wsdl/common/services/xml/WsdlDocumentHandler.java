package de.jankrause.diss.wsdl.common.services.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WsdlDocumentHandler extends DefaultHandler {

	private List<String> operationLabels = new ArrayList<String>();
	private boolean interfaceActive = false;
	private List<String> portTypeLabels = new ArrayList<String>();

	private boolean isWsdlInterface(String tagName) {
		return tagName.toLowerCase().equals("interface")
				|| tagName.toLowerCase().equals("porttype");
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		int indexOfColon = name.toLowerCase().indexOf(':');
		String tagName = (indexOfColon >= 0) ? name.substring(indexOfColon + 1)
				: name;

		if (isWsdlInterface(tagName)) {
			portTypeLabels.add(atts.getValue("name"));
		}

		if (!interfaceActive && isWsdlInterface(tagName)) {
			interfaceActive = true;
		}

		if (tagName.toLowerCase().equals("operation") && interfaceActive) {
			operationLabels.add(atts.getValue("name"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		int indexOfColon = name.toLowerCase().indexOf(':');
		String tagName = (indexOfColon >= 0) ? name.substring(indexOfColon + 1)
				: name;

		if (interfaceActive && isWsdlInterface(tagName)) {
			interfaceActive = false;
		}
	}

	public List<String> getPortTypeLabels() {
		return portTypeLabels;
	}

	public List<String> getOperationLabels() {
		return operationLabels;
	}
}
