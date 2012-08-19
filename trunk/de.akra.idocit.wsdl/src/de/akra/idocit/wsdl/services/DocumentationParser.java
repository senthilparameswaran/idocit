/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.utils.DescribedItemUtils;

/**
 * The parser for WSDL documentation elements.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class DocumentationParser {
	private static final String HTML_TAG_TAB = "tab";

	private static final String HTML_TAG_BR = "br";

	/**
	 * The tag name for the thematic documentation parts.
	 */
	public static final String DOCPART_TAG_NAME = "docpart";

	public static final String THEMATIC_GRID_TAG_NAME = "thematicgrid";

	public static final String THEMATIC_GRID_ATTRIBUTE_NAME = "name";

	public static final String THEMATIC_GRID_ATTRIBUTE_NAME_GLOBAL = "global";

	/**
	 * The attribute name for the identifier of a signature element. The
	 * signature element is identified by the path of types (the structure) to
	 * the element.
	 */
	public static final String SIGNATURE_ELEMENT_ATTRIBUTE_NAME = "signatureElement";

	private static final String ADDRESSEE_GROUP_ATTRIBUTE_NAME = "group";
	private static final String ADDRESSEE_ELEMENT_NAME = "addressee";
	private static final String THEMATIC_ROLE_ATTRIBUTE_NAME = "role";
	private static final String ERROR_DOCUMENTATION_ATTRIBUTE_NAME = "errordoc";

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(DocumentationParser.class
			.getName());

	// // use this for testing
	// private static List<Addressee> supportedAddressees =
	// Collections.emptyList();
	//
	// // use this for testing
	// private static List<ThematicRole> supportedThematicRoles =
	// Collections.emptyList();

	/**
	 * Searches for the first documentation element in
	 * <code>documentationElements</code> containing docpart elements and parses
	 * the docpart elements.
	 * 
	 * @param documentationElements
	 *            List of documentation elements that should be parsed.
	 * @return {@link List} of {@link Documentation}s; one {@link Documentation}
	 *         represents one docpart element. If no documentation element with
	 *         docpart elements is found, {@link Collections#emptyList()} is
	 *         returned.
	 */
	public static List<Documentation> parseDocElements(
			List<Element> documentationElements) {
		List<Documentation> documentations = Collections.emptyList();
		if (documentationElements != null) {
			Element docElem = findDocElemWithDocpart(documentationElements);

			if (docElem != null) {
				NodeList docParts = docElem
						.getElementsByTagName(DocumentationParser.DOCPART_TAG_NAME);

				documentations = new ArrayList<Documentation>(
						docParts.getLength());

				for (int i = 0; i < docParts.getLength(); i++) {
					Node dPart = docParts.item(i);
					Documentation doc = DocumentationParser
							.parseDocPartElement(dPart);
					documentations.add(doc);
				}
			}
		}

		return documentations;
	}

	/**
	 * Searches for the first documentation element in <code>docElems</code>
	 * with docpart elements as children.
	 * 
	 * @param docElems
	 *            List of documentation {@link Element}s.
	 * @return The first documentation {@link Element} with docpart elements as
	 *         children. If no element was found, <code>null</code> is returned.
	 */
	public static Element findDocElemWithDocpart(List<Element> docElems) {
		return findElementWithName(docElems, DOCPART_TAG_NAME);
	}

	/**
	 * Searches for the first documentation element in <code>docElems</code>
	 * with thematic elements as children.
	 * 
	 * @param docElems
	 *            List of documentation {@link Element}s.
	 * @return The first documentation {@link Element} with thematicgrid
	 *         elements as children. If no element was found, <code>null</code>
	 *         is returned.
	 */
	public static Element findDocElemWithThematicGrid(List<Element> docElems) {
		return findElementWithName(docElems, THEMATIC_GRID_TAG_NAME);
	}

	/**
	 * Returns the name of the thematic grid in the given documentation-element.
	 * 
	 * Please note: This method returns the name of the first thematic grid
	 * found in the list of documentation's children.
	 * 
	 * @param docElemWithThematicGrid
	 *            The &lt;documentation&gt;-element containing the thematic grid.
	 * 
	 * @return The name of the first found thematic grid
	 */
	public static String readThematicGridName(Element docElemWithThematicGrid) {
		NodeList documentationChildren = docElemWithThematicGrid
				.getChildNodes();

		int children = documentationChildren.getLength();

		for (int i = 0; i < children; i++) {
			Node child = documentationChildren.item(i);

			if (child.getNodeName().equals(THEMATIC_GRID_TAG_NAME)) {
				NamedNodeMap attributes = child.getAttributes();

				if (attributes != null) {
					Node nameAttribute = attributes
							.getNamedItem(THEMATIC_GRID_ATTRIBUTE_NAME);

					if (nameAttribute != null) {
						return nameAttribute.getNodeValue();
					} else {
						// It seems that we have an incomplete
						// <thematicgrid>-element. This is why we don't continue
						// to search for a named one.
						return null;
					}
				} else {
					// It seems that we have an incomplete
					// <thematicgrid>-element. This is why we don't continue to
					// search for a named one.
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * Searches for the first documentation element in <code>docElems</code>
	 * with docpart elements as children.
	 * 
	 * @param docElems
	 *            List of documentation {@link Element}s.
	 * @return The first documentation {@link Element} with docpart elements as
	 *         children. If no element was found, <code>null</code> is returned.
	 */
	public static Element findElementWithName(List<Element> docElems,
			String elementName) {
		if (docElems != null && !docElems.isEmpty()) {
			// search for the first <documentation> element with <docpart>
			// elements
			boolean bFoundDocParts = false;
			Iterator<Element> it = docElems.iterator();
			while (it.hasNext() && !bFoundDocParts) {
				Element docElem = it.next();
				NodeList docParts = docElem.getElementsByTagName(elementName);

				if (docParts.getLength() > 0) {
					logger.log(Level.FINE, "Number of <docpart> elements = "
							+ docParts.getLength());

					return docElem;
				}
			}
		}
		return null;
	}

	/**
	 * Extracts from the docpart element the addressees with their individual
	 * documentations and the attributes
	 * {@link DocumentationParser#THEMATIC_ROLE_ATTRIBUTE_NAME},
	 * {@link DocumentationParser#THEMATIC_SCOPE_ATTRIBUTE_NAME} and
	 * {@link DocumentationParser#SIGNATURE_ELEMENT_ATTRIBUTE_NAME} of the
	 * docpart and returns a new {@link Documentation} with this information. If
	 * an attribute is not available or has an unknown value it is simply
	 * ignored and not set.
	 * 
	 * @param docpartElem
	 *            docpart element that should be parsed.
	 * @return A new {@link Documentation} with the parsed information.
	 */
	private static Documentation parseDocPartElement(Node docpartElem) {
		Documentation doc = parseAddressees(docpartElem);

		// read attributes
		NamedNodeMap attributes = docpartElem.getAttributes();
		Node attr;
		if ((attr = attributes.getNamedItem(THEMATIC_ROLE_ATTRIBUTE_NAME)) != null) {
			logger.log(Level.FINE,
					THEMATIC_ROLE_ATTRIBUTE_NAME + "=\"" + attr.getNodeValue()
							+ "\"");

			ThematicRole role = DescribedItemUtils.findThematicRole(attr
					.getNodeValue());
			doc.setThematicRole(role);
		}

		if ((attr = attributes
				.getNamedItem(DocumentationParser.SIGNATURE_ELEMENT_ATTRIBUTE_NAME)) != null) {
			logger.log(Level.FINE,
					DocumentationParser.SIGNATURE_ELEMENT_ATTRIBUTE_NAME
							+ "=\"" + attr.getNodeValue() + "\"");
			doc.setSignatureElementIdentifier(attr.getNodeValue());
		}
		
		if ((attr = attributes
				.getNamedItem(DocumentationParser.ERROR_DOCUMENTATION_ATTRIBUTE_NAME)) != null) {
			logger.log(Level.FINE,
					DocumentationParser.ERROR_DOCUMENTATION_ATTRIBUTE_NAME
							+ "=\"" + attr.getNodeValue() + "\"");
			doc.setErrorCase(Boolean.parseBoolean(attr.getNodeValue()));
		}

		return doc;
	}

	/**
	 * Extracts from the docpart element the addressees with their individual
	 * documentation.
	 * 
	 * @param docpartElem
	 *            The docpart element that should be parsed.
	 * @return A new {@link Documentation} with the documentations for the
	 *         addresses.
	 */
	private static Documentation parseAddressees(Node docpartElem) {
		Documentation doc = new Documentation();
		Map<Addressee, String> docMap = doc.getDocumentation();
		List<Addressee> addresseeSequence = doc.getAddresseeSequence();

		NodeList childList = docpartElem.getChildNodes();

		// iterate all child nodes to find addressee elements
		for (int i = 0; i < childList.getLength(); i++) {
			Node item = childList.item(i);

			// if found addressee element, add the documentation to the map
			// in general there should be only addressee elements
			if (item.getNodeName().equals(ADDRESSEE_ELEMENT_NAME)) {
				NamedNodeMap attributes = item.getAttributes();
				Node attribute = attributes
						.getNamedItem(ADDRESSEE_GROUP_ATTRIBUTE_NAME);

				Addressee addressee = DescribedItemUtils
						.findAddressee(attribute.getNodeValue());
				logger.log(Level.FINE, attribute.getNodeName() + " -> "
						+ attribute.getNodeValue() + " | Addressee="
						+ addressee.getName());

				String text = readTextFromAddresseElement(item);

				// add documentation for addressee
				docMap.put(addressee, text);

				// save sequence of adding addressees
				addresseeSequence.add(addressee);

			} else {
				logger.log(Level.FINE, "Expect <" + ADDRESSEE_ELEMENT_NAME
						+ ">, but found: name=" + item.getNodeName()
						+ ", type=" + item.getNodeType());
			}
		}

		return doc;
	}

	/**
	 * Reads the child nodes from the addressee element and converts the &lt;br
	 * /&gt; and &lt;tab /&gt; elements to the corresponding ASCII characters.
	 * Unnecessary newlines and tabulators are removed from the text. After the
	 * text is build {@link String#trim()} is run on it.
	 * 
	 * @param addresseeElement
	 *            The addressee element from which the text should be read.
	 * @return The normalized text.
	 */
	private static String readTextFromAddresseElement(Node addresseeElement) {
		StringBuilder text = new StringBuilder();
		NodeList nodes = addresseeElement.getChildNodes();

		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case Node.TEXT_NODE:
				String unescapedText = StringEscapeUtils.unescapeHtml4(node.getNodeValue());
				text.append(StringUtils.cleanFormatting(unescapedText));
				break;
			case Node.ELEMENT_NODE:
				if (node.getNodeName().equalsIgnoreCase(HTML_TAG_BR)) {
					text.append(System.getProperty("line.separator"));
				} else if (node.getNodeName().equalsIgnoreCase(HTML_TAG_TAB)) {
					text.append('\t');
				}
				break;
			}
		}
		return text.toString().trim();
	}
}
