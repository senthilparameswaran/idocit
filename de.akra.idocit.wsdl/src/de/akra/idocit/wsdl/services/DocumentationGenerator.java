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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Documentation;

/**
 * Generates documentation elements from <code>SignatureElements</code> and
 * <code>Parameter</code>.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DocumentationGenerator
{
	/*
	 * Name of the attribute for an element name.
	 */
	// private static final String ELEMENT_ATTRIBUTE_NAME = "name";

	private static final String SIGNATURE_ELEMENT_ATTRIBUTE_NAME = "signatureElement";
	private static final String ADDRESSEE_GROUP_ATTRIBUTE_NAME = "group";
	private static final String ADDRESSEE_ELEMENT_NAME = "addressee";
	private static final String THEMATIC_SCOPE_ATTRIBUTE_NAME = "scope";
	private static final String THEMATIC_ROLE_ATTRIBUTE_NAME = "role";

	private static final String XML_TAG_TAB = "tab";
	private static final String XML_TAG_BR = "br";

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(DocumentationParser.class.getName());

	/**
	 * A DOM Document object. It is needed to create new nodes for documentation elements.
	 */
	private static Document domDocument;

	/**
	 * Creates a documentation {@link Element} out of <code>docparts</code>. It sets the
	 * attributes and adds the addressee elements.
	 * 
	 * @param tagName
	 *            Qualified tag name for the documentation element.
	 * @param docparts
	 *            The documentation parts that should be written into a documentation
	 *            element.
	 * @return The generated documentation {@link Element} or <code>null</code> if
	 *         <code>docparts != null && !docparts.isEmpty()</code>.
	 */
	public static Element generateDocumentationElement(String tagName,
			List<Documentation> docparts)
	{
		Element docElem = null;
		if (docparts != null && !docparts.isEmpty())
		{
			try
			{
				if (domDocument == null)
				{
					// must be initialized here, because of error handling
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder;
					builder = factory.newDocumentBuilder();
					domDocument = builder.newDocument();
				}
				docElem = domDocument.createElement(tagName);

				for (Documentation doc : docparts)
				{
					docElem.appendChild(generateDocpartElement(doc));
				}
			}
			catch (ParserConfigurationException e)
			{
				logger.log(Level.SEVERE, "This error should not occur.", e);
			}
		}

		return docElem;
	}

	/**
	 * Wraps the addressees with their individual documentation into a docpart
	 * {@link Element}, sets the attributes of the {@link Element} and returns it.
	 * 
	 * @param documentation
	 *            {@link Documentation} whose addressees should be generated to Elements.
	 * @return The generated docpart {@link Element}, null if <code>domDocument</code> is
	 *         not initialized.
	 */
	private static Element generateDocpartElement(Documentation documentation)
	{
		Element docpart = null;
		// Without DOM Document you can not create new elements. It should be
		// initialized in generateDocElement().
		if (domDocument != null)
		{
			docpart = domDocument.createElement(DocumentationParser.DOCPART_TAG_NAME);

			// set attributes
			if (documentation.getScope() != null)
			{
				docpart.setAttribute(THEMATIC_SCOPE_ATTRIBUTE_NAME, documentation
						.getScope().toString());
			}

			if (documentation.getThematicRole() != null)
			{
				docpart.setAttribute(THEMATIC_ROLE_ATTRIBUTE_NAME, documentation
						.getThematicRole().getName());
			}

			if (documentation.getSignatureElementIdentifier() != null)
			{
				docpart.setAttribute(SIGNATURE_ELEMENT_ATTRIBUTE_NAME,
						documentation.getSignatureElementIdentifier());
			}

			// insert addressee elements
			Map<Addressee, String> addresseeDocs = documentation.getDocumentation();
			List<Addressee> addresseeSequence = documentation.getAddresseeSequence();
			for (Addressee addressee : addresseeSequence)
			{
				Element addrElem = domDocument.createElement(ADDRESSEE_ELEMENT_NAME);
				addrElem.setAttribute(ADDRESSEE_GROUP_ATTRIBUTE_NAME, addressee.getName());

				addTextAsNodes(addrElem, addresseeDocs.get(addressee));
				docpart.appendChild(addrElem);
			}
		}
		else
		{
			logger.log(Level.SEVERE, "domDocument is no initialized.");
		}
		return docpart;
	}

	/**
	 * Adds the <code>text</code> as {@link Node#TEXT_NODE}s to the <code>element</code>.
	 * Newline and tabulator characters are replaced with &lt;br/&gt; and &lt;tab/&gt;
	 * elements.
	 * 
	 * @param element
	 *            The {@link Element} to which the <code>text</code> should be added.
	 * @param text
	 *            The text to convert and add to the <code>element</code>.
	 * @return The <code>element</code>.
	 */
	private static Element addTextAsNodes(Element element, String text)
	{
		StringBuilder tmpText = new StringBuilder();

		for (int i = 0; i < text.length(); ++i)
		{
			switch (text.charAt(i))
			{
			case '\t':
				if (tmpText.length() > 0)
				{
					element.appendChild(domDocument.createTextNode(tmpText.toString()));
					tmpText = new StringBuilder();
				}
				element.appendChild(domDocument.createElement(XML_TAG_TAB));
				break;

			case '\r':
				if (tmpText.length() > 0)
				{
					element.appendChild(domDocument.createTextNode(tmpText.toString()));
					tmpText = new StringBuilder();
				}
				element.appendChild(domDocument.createElement(XML_TAG_BR));

				// if CR and LF are together, replace it only once
				if (text.charAt(i + 1) == '\n')
				{
					i++;
				}
				break;

			case '\n':
				if (tmpText.length() > 0)
				{
					element.appendChild(domDocument.createTextNode(tmpText.toString()));
					tmpText = new StringBuilder();
				}
				element.appendChild(domDocument.createElement(XML_TAG_BR));

				// if CR and LF are together, replace it only once
				if (text.charAt(i + 1) == '\r')
				{
					i++;
				}
				break;

			default:
				tmpText.append(text.charAt(i));
				break;
			}
		}

		// append pending text
		if (tmpText.length() > 0)
		{
			element.appendChild(domDocument.createTextNode(tmpText.toString()));
		}

		return element;
	}
}
