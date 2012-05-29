package de.akra.idocit.java.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import de.akra.idocit.java.services.HTMLTableParser;

public class StringUtils
{
	
	public static final String XML_TAG_TAB = '<' + HTMLTableParser.XML_TAG_TAB + "/>";
	public static final String XML_TAG_BR = '<' + HTMLTableParser.XML_TAG_BR + "/>";

	public static String escapeHtml(String unescapedText)
	{
		String escapedText = StringEscapeUtils.escapeHtml4(unescapedText);

		StringBuilder tmpText = new StringBuilder();

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
}
