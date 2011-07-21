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
package de.akra.idocit.java.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jdt.core.dom.Javadoc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Scope;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.core.utils.ObjectStructureUtils;

/**
 * Parser for the HTML tables in the {@link Javadoc} comments.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class HTMLTableParser
{
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	private static final String XML_ROOT_START = "<javadoc>";
	private static final String XML_ROOT_END = "</javadoc>";

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(HTMLTableParser.class.getName());

	/**
	 * Parse the <code>html</code> String and converts each table into a
	 * {@link Documentation}.
	 * 
	 * @param html
	 *            HTML tables as string representation from a {@link Javadoc}.
	 * @return The list of converted {@link Documentation}s.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static List<Documentation> convertJavadocToDocumentations(String html)
			throws SAXException, IOException, ParserConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		html = XML_HEADER + XML_ROOT_START + html + XML_ROOT_END;
		HTMLTableHandler handler = new HTMLTableHandler();

		saxParser.parse(new ByteArrayInputStream(html.getBytes()), handler);

		return handler.getDocumentations();
	}

	/**
	 * The handler to parse the HTML table and convert it to {@link Documentation}s.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * @since 0.0.1
	 * @version 0.0.1
	 * 
	 */
	private static class HTMLTableHandler extends DefaultHandler2
	{
		private static final String HTML_TAG_TABLE = "table";
		private static final String HTML_TAG_TR = "tr";
		private static final String HTML_TAG_TD = "td";
		private static final String ELEMENT = "Element:";
		private static final String ROLE = "Role:";
		private static final String SCOPE = "Scope:";

		/**
		 * The list of converted {@link Documentation}s.
		 */
		private List<Documentation> documentations = Collections.emptyList();

		/**
		 * The current {@link Documentation}.
		 */
		private Documentation currentDoc;

		private byte currentColumn = 0;

		/**
		 * The type of the last found value. It determines the next step.
		 */
		private LAST_VALUE lastValue = LAST_VALUE.NONE;

		/**
		 * A found {@link Addressee} in table. In the next step the description for this
		 * addressee is read.
		 */
		private Addressee currentAddressee;

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException
		{
			if (qName.equals(HTML_TAG_TR))
			{
				currentColumn = 0;
			}
			else if (qName.equals(HTML_TAG_TABLE))
			{
				finishCurrentDocumentation();
			}
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String value = String.valueOf(ch, start, length);

			switch (lastValue)
			{
			case ELEMENT:
				currentDoc.setSignatureElementIdentifier(value);
				lastValue = LAST_VALUE.NONE;
				break;
			case ROLE:
				ThematicRole thematicRole = ObjectStructureUtils.findThematicRole(value);
				currentDoc.setThematicRole(thematicRole);
				lastValue = LAST_VALUE.NONE;
				break;
			case SCOPE:
				try
				{
					Scope scope = Scope.fromString(value);
					currentDoc.setScope(scope);
				}
				catch (IllegalArgumentException e)
				{
					logger.log(Level.WARNING, "Unknown scope \"" + value
							+ "\" use Scope.EXPLICIT as default.");
				}
				lastValue = LAST_VALUE.NONE;
				break;
			case ADDRESSEE:
				if (currentColumn == 2)
				{
					currentDoc.getAddresseeSequence().add(currentAddressee);
					currentDoc.getDocumentation().put(currentAddressee, value);
					currentAddressee = null;
					lastValue = LAST_VALUE.NONE;
				}
				break;
			case NONE:
				// ignore new lines etc.
				if (!value.matches("^\\s*$"))
				{
					if (value.equals(ELEMENT))
					{
						lastValue = LAST_VALUE.ELEMENT;
					}
					else if (value.equals(ROLE))
					{
						lastValue = LAST_VALUE.ROLE;
					}
					else if (value.equals(SCOPE))
					{
						lastValue = LAST_VALUE.SCOPE;
					}
					else
					{
						currentAddressee = ObjectStructureUtils.findAddressee(value);
						lastValue = LAST_VALUE.ADDRESSEE;
					}
				}
				break;
			default:
				logger.log(Level.SEVERE, "Unknown LAST_VALUE!");
				break;
			}
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException
		{
			if (qName.equals(HTML_TAG_TABLE))
			{
				currentDoc = new Documentation();
				currentDoc.setScope(Scope.EXPLICIT);
			}
			else if (qName.equals(HTML_TAG_TD))
			{
				currentColumn++;
			}
		}

		/**
		 * Adds the <code>currentDoc</code> to <code>documentations</code> and resets
		 * <code>currentDoc</code> to <code>null</code>.
		 */
		private void finishCurrentDocumentation()
		{
			if (currentDoc != null)
			{
				if (documentations == Collections.EMPTY_LIST)
				{
					documentations = new ArrayList<Documentation>();
				}
				documentations.add(currentDoc);
				currentDoc = null;
			}
		}

		/**
		 * @return the created {@link Documentation}s.
		 */
		public List<Documentation> getDocumentations()
		{
			return documentations;
		}

		/**
		 * The last found value determines what will come next and what should be done.
		 * 
		 * @author Dirk Meier-Eickhoff
		 * @since 0.0.1
		 * @version 0.0.1
		 * 
		 */
		private enum LAST_VALUE
		{
			ELEMENT, ROLE, ADDRESSEE, SCOPE, NONE;
		}
	}

}
