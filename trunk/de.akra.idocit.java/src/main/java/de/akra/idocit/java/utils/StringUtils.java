/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.java.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import de.akra.idocit.java.services.HTMLTableParser;

public class StringUtils
{

	public static final String XML_TAG_TAB = '<' + HTMLTableParser.XML_TAG_TAB + "/>";
	public static final String XML_TAG_BR = '<' + HTMLTableParser.XML_TAG_BR + "/>";

	/**
	 * Escapes the special characters in the given string with HTML entities. Tabs and
	 * line breaks are replaced with their corresponding HTML entities as well.
	 * 
	 * @param unescapedText
	 *            [SOURCE] The text to escape
	 * 
	 * @return [OBJECT] The escaped text
	 */
	public static String escapeHtml(final String unescapedText)
	{
		final String escapedText = StringEscapeUtils.escapeHtml4(unescapedText);
		final StringBuilder tmpText = new StringBuilder();

		for (int i = 0; i < escapedText.length(); ++i)
		{
			switch (escapedText.charAt(i))
			{
			case '\t':
				tmpText.append(XML_TAG_TAB);
				break;

			case '\r':
				tmpText.append(XML_TAG_BR);

				// if CR and LF are together, replace it only once
				// Changes due to Issue #2
				if ((escapedText.length() > i + 1) && escapedText.charAt(i + 1) == '\n')
				// End changes due to Issue #2
				{
					i++;
				}
				break;

			case '\n':
				tmpText.append(XML_TAG_BR);

				// if CR and LF are together, replace it only once
				// Changes due to Issue #2
				if ((escapedText.length() > i + 1) && escapedText.charAt(i + 1) == '\r')
				// End changes due to Issue #2
				{
					i++;
				}
				break;

			default:
				tmpText.append(escapedText.charAt(i));
				break;
			}
		}

		return tmpText.toString();
	}

	public static String asStringSequence(Object... objects)
	{
		StringBuffer buffer = new StringBuffer();

		for (Object object : objects)
		{
			buffer.append(String.valueOf(object));
			buffer.append(',');
		}

		String objectSequence = buffer.toString();

		// Cut the last character, its a comma ;).
		return objectSequence.substring(0, objectSequence.length() - 1);
	}
}
