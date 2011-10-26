/*******************************************************************************
 * Copyright 2011 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.akra.idocit.wsdl.services;

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
