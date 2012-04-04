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
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.core.dom.Javadoc;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.utils.DescribedItemUtils;

/**
 * Parser for the HTML tables in the {@link Javadoc} comments.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class HTMLTableParser
{
	private static final String XML_HEADER = "<?xml version=\"1.1\" encoding=\"UTF-8\" ?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	private static final String XML_ROOT_START = "<javadoc>";
	private static final String XML_ROOT_END = "</javadoc>";

	public static final String XML_TAG_TAB = "tab";
	public static final String XML_TAG_BR = "br";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String CHAR_TAB = "\t";

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(HTMLTableParser.class.getName());
	
	private static final HTMLTableHandler handler = new HTMLTableHandler();
	
	/**
	 * Replaces HTML-entities of special characters and <br/>
	 * - and <tab/>-elements with their corresponding character (e.g. <br/>
	 * with \n).
	 * 
	 * @param escapedText
	 *            The escaped test
	 * @return The unescaped text
	 */
	private static String unescapeHtml(String escapedText)
	{
		String unescapedHtml = StringEscapeUtils.unescapeHtml4(escapedText);
		unescapedHtml = unescapedHtml.replaceAll(JavadocGenerator.XML_TAG_BR, NEW_LINE);
		unescapedHtml = unescapedHtml.replaceAll(JavadocGenerator.XML_TAG_TAB, CHAR_TAB);
		return unescapedHtml;
	}

	/**
	 * Parse the <code>html</code> String and converts each iDocIt! comment table into a
	 * {@link Documentation}.
	 * 
	 * @param html
	 *            HTML tables as string representation from a {@link Javadoc}. Only tables
	 *            with the attribute name={@link JavadocGenerator#IDOCIT_HTML_TABLE_NAME}
	 *            are considered.
	 * @return The list of converted {@link Documentation}s. If the list is empty, no
	 *         tables with name={@link JavadocGenerator#IDOCIT_HTML_TABLE_NAME} are found.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static List<Documentation> convertJavadocToDocumentations(String html)
			throws SAXException, IOException, ParserConfigurationException
	{
		StringBuilder xml = new StringBuilder(XML_HEADER.length()
				+ XML_ROOT_START.length() + html.length() + XML_ROOT_END.length());
		xml.append(XML_HEADER).append(XML_ROOT_START).append(html).append(XML_ROOT_END);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		handler.setDtdInputSources(readDTDs());
		saxParser.parse(
				new ByteArrayInputStream(xml.toString()
						.getBytes(Charset.forName("UTF-8"))), handler);

		List<Documentation> documentations = unescapeDocumentationTexts(handler.getDocumentations());
		
		handler.reset();
		
		return documentations;
	}

	/**
	 * Reads the XML DTDs and combines them to an InputSource. The DTDs must be read
	 * always before they are used, as the file handler closes after each parsing process.
	 * 
	 * @return The XML DTDs and combines them to an InputSource
	 */
	private static InputSource readDTDs()
	{
		InputStream xhtmlLat1 = HTMLTableParser.class
				.getResourceAsStream("xhtml-lat1.ent");
		InputStream xhtmlSpecial = HTMLTableParser.class
				.getResourceAsStream("xhtml-special.ent");
		InputStream xhtmlSymbol = HTMLTableParser.class
				.getResourceAsStream("xhtml-symbol.ent");

		SequenceInputStream sequence1 = new SequenceInputStream(xhtmlSpecial, xhtmlSymbol);
		SequenceInputStream sequence = new SequenceInputStream(xhtmlLat1, sequence1);
		return new InputSource(sequence);
	}

	/**
	 * Unescapes the documentation-texts for each addressee in the given list.
	 * 
	 * Please note: the given list is manipulated and returned for convenience.
	 * 
	 * @param escapedDocumentations
	 *            The list of {@link Documentation}s to be escaped (and modified!!!)
	 * 
	 * @return The given, escaped list of documentations
	 */
	private static List<Documentation> unescapeDocumentationTexts(
			List<Documentation> escapedDocumentations)
	{
		if (escapedDocumentations != null)
		{
			for (Documentation documentation : escapedDocumentations)
			{
				Map<Addressee, String> documentationTexts = documentation
						.getDocumentation();
				Map<Addressee, String> documentationUnescapedTexts = new HashMap<Addressee, String>();

				for (Entry<Addressee, String> documentedText : documentationTexts
						.entrySet())
				{
					String unescapedText = unescapeHtml(documentedText.getValue());

					documentationUnescapedTexts.put(documentedText.getKey(),
							unescapedText);
				}

				documentation.setDocumentation(documentationUnescapedTexts);
			}
		}
		return escapedDocumentations;
	}

	/**
	 * The handler to parse the HTML table and convert it to {@link Documentation}s.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * @since 0.0.1
	 * @version 0.0.2
	 * 
	 */
	private static class HTMLTableHandler extends DefaultHandler2
	{
		private static final String HTML_TAG_TABLE = "table";
		private static final String HTML_TAG_TR = "tr";
		private static final String HTML_TAG_TD = "td";
		private static final String HTML_ATTRIBUTE_NAME = "name";
		private static final String ELEMENT = "Element:";
		private static final String ROLE = "Role:";

		/**
		 * The list of converted {@link Documentation}s.
		 */
		private List<Documentation> documentations = Collections.emptyList();

		/**
		 * The current {@link Documentation}.
		 */
		private Documentation currentDoc;

		/**
		 * True, if a iDocIt! table should be parsed.
		 */
		private boolean startTableParsing = false;

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

		private InputSource dtdInputSources;

		public void setDtdInputSources(InputSource dtdInputSources)
		{
			this.dtdInputSources = dtdInputSources;
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException
		{
			if (HTML_TAG_TR.equals(qName))
			{
				currentColumn = 0;
				currentAddressee = null;
				lastValue = LAST_VALUE.NONE;
			}
			else if (HTML_TAG_TABLE.equals(qName ))
			{
				finishCurrentDocumentation();
				startTableParsing = false;
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
				ThematicRole thematicRole = DescribedItemUtils.findThematicRole(value);
				currentDoc.setThematicRole(thematicRole);
				lastValue = LAST_VALUE.NONE;
				break;
			case ADDRESSEE:
				appendValueToLastDocumentation(value);
				break;
			case NONE:
				// ignore new lines etc.
				if (startTableParsing && !value.matches("^\\s*$"))
				{
					if (value.equals(ELEMENT))
					{
						lastValue = LAST_VALUE.ELEMENT;
					}
					else if (value.equals(ROLE))
					{
						lastValue = LAST_VALUE.ROLE;
					}
					else
					{
						currentAddressee = DescribedItemUtils.findAddressee(value);
						lastValue = LAST_VALUE.ADDRESSEE;
					}
				}
				break;
			default:
				logger.log(Level.SEVERE, "Unknown LAST_VALUE! " + lastValue);
				break;
			}
		}

		/**
		 * Append the <code>value</code> to the documentation of the last found
		 * {@link Addressee}.
		 * 
		 * @param value
		 *            The documentation value to appen.
		 */
		private void appendValueToLastDocumentation(String value)
		{
			// append only if we are in the second column of the iDocIt! documentation
			// table.
			if (currentColumn == 2)
			{
				String startedDocumentation = currentDoc.getDocumentation().get(
						currentAddressee);
				if (startedDocumentation != null)
				{
					startedDocumentation += value;
				}
				else
				{
					currentDoc.getAddresseeSequence().add(currentAddressee);
					startedDocumentation = value;
				}
				currentDoc.getDocumentation().put(currentAddressee, startedDocumentation);
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
			if (HTML_TAG_TABLE.equals(qName))
			{
				String name = attributes.getValue(HTML_ATTRIBUTE_NAME);
				if (name != null && JavadocGenerator.IDOCIT_HTML_TABLE_NAME.equals(name))
				{
					startTableParsing = true;
					currentDoc = new Documentation();
				}
			}
			else if (HTML_TAG_TD.equals(qName))
			{
				currentColumn++;
			}
			else if (XML_TAG_BR.equals(qName))
			{
				appendValueToLastDocumentation(NEW_LINE);
			}
			else if (XML_TAG_TAB.equals(qName))
			{
				appendValueToLastDocumentation(CHAR_TAB);
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

		@Override
		public InputSource resolveEntity(String name, String publicId, String baseURI,
				String systemId) throws SAXException, IOException
		{
			return dtdInputSources;
		}
		
		/**
		 * Resets this parser.
		 */
		public void reset(){
			this.currentAddressee = null;
			this.currentDoc = null;
			this.currentColumn = 0;
			this.documentations = Collections.emptyList();
			this.lastValue = LAST_VALUE.NONE;
			this.startTableParsing = false;
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
