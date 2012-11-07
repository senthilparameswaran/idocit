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
package de.akra.idocit.common.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import de.akra.idocit.common.constants.Misc;

/**
 * Some useful methods for working with strings.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 */
public class StringUtils
{
	private static final Logger LOGGER = Logger.getLogger(StringUtils.class.getName());
	
	/**
	 * An empty String ({@code ""}).
	 * 
	 * @since 0.0.2
	 */
	public static final String EMPTY = "";

	/**
	 * A space ({@code " "}).
	 * 
	 * @since 0.0.2
	 */
	public static final String SPACE = " ";

	/**
	 * The operating system's default line separator sequence.
	 * <p>
	 * {@code System.getProperty("line.separator")}
	 * </p>
	 * 
	 * @since 0.0.2
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\s*$");
	private static final String ASCII_TAB = "\t";
	private static final String ASCII_LF = "\n";
	private static final String ASCII_CR = "\r";

	/**
	 * <p>
	 * Checks if a CharSequence is whitespace, empty ("") or <code>null</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isBlank(<code>null</code>)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("abc")     = false
	 * StringUtils.isBlank("  abc  ") = false
	 * </pre>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be <code>null</code>
	 * @return <code>true</code> if the CharSequence is <code>null</code>, empty or
	 *         whitespace
	 * @since 0.0.2
	 */
	public static boolean isBlank(final CharSequence cs)
	{
		if (cs == null || cs.length() == 0)
		{
			return true;
		}
		return PATTERN_WHITESPACE.matcher(cs).matches();
	}

	/**
	 * 
	 * The <code>string</code> is split by the <code>splitChar</code> into words. After a
	 * word comes over to the <code>maxLineLength</code>, a new line sign is added. The
	 * new line sign is depending on the operating system.
	 * 
	 * @param string
	 *            String to be add new lines.
	 * @param maxLineLength
	 *            Maximum characters in a line.
	 * @param splitChar
	 *            Char to split the <code>string</code> into words.
	 * @return New String with new lines.
	 */
	public static String addLineBreaks(final String string, final int maxLineLength,
			final char splitChar)
	{
		final StringBuffer buffer = new StringBuffer();
		final String[] splitString = string.split(String.valueOf(splitChar));
		int currentLineLength = 0;

		for (final String token : splitString)
		{
			if (currentLineLength > maxLineLength)
			{
				buffer.append(NEW_LINE);
				currentLineLength = 0;
			}
			else if (currentLineLength > 0)
			{
				buffer.append(SPACE);
				currentLineLength++;
			}

			buffer.append(token);
			currentLineLength += token.length();
		}

		return buffer.toString();
	}

	/**
	 * The <code>string</code> is split by one space sign ' ' into words. After a word
	 * comes over to the <code>maxLineLength</code>, a new line sign is added. The new
	 * line sign is depending on the operating system. If the text contains already
	 * newline signs, additional ones are only added if one line exceeds the
	 * <code>maxLineLength</code>.
	 * 
	 * @param string
	 *            String to be add new lines.
	 * @param maxLineLength
	 *            Maximum characters in a line.
	 * @return New String with additional lines.
	 */
	public static String addAdditionalLineBreaks(final String string,
			final int maxLineLength)
	{
		final char splitChar = ' ';
		final StringBuffer buffer = new StringBuffer();
		final String[] splitString = string.split(String.valueOf(splitChar));
		int currentLineLength = 0;

		for (final String token : splitString)
		{
			if (currentLineLength > maxLineLength)
			{
				buffer.append(NEW_LINE);
				currentLineLength = 0;
			}
			else if (token.matches("[\r\n]+"))
			{
				// reset line length if text contains newlines
				currentLineLength = 0;
			}
			else if (currentLineLength > 0)
			{
				buffer.append(SPACE);
				currentLineLength++;
			}

			buffer.append(token);
			currentLineLength += token.length();
		}

		return buffer.toString();
	}

	/**
	 * All new line signs are removed from the <code>string</code>.
	 * 
	 * @param string
	 *            The string from which all new lines should be removed.
	 * @return String without new lines.
	 */
	public static String removeLineBreaks(final String string)
	{
		final StringBuffer buffer = new StringBuffer();
		final String[] splitString = string.split(NEW_LINE);

		for (int i = 0; i < splitString.length; i++)
		{
			final String token = splitString[i];
			buffer.append(token);

			if (i < (splitString.length - 1))
			{
				buffer.append(SPACE);
			}
		}

		return buffer.toString();
	}

	/**
	 * Converts the Collection of Strings into one comma separated string with the tokens.
	 * 
	 * @param tokens
	 *            Tokens to be converted.
	 * @return String with the comma separated tokens.
	 */
	public static String convertIntoCommaSeperatedTokens(final Collection<String> tokens)
	{
		final StringBuffer buffer = new StringBuffer();
		int tokenNo = 0;
		final int size = tokens.size();

		for (final String token : tokens)
		{
			buffer.append(token);

			if (tokenNo < (size - 1))
			{
				buffer.append(", ");
			}

			++tokenNo;
		}

		return buffer.toString();
	}

	/**
	 * Splits the <code>tokenSequence</code> with the <code>delimiter</code> and converts
	 * that to a Set of Strings.
	 * 
	 * @param tokenSequence
	 *            Sequence of tokens.
	 * @param delimiter
	 *            Delimiter used in the sequence.
	 * @return Set of tokens.
	 */
	public static Set<String> convertIntoTokenSet(String tokenSequence, String delimiter)
	{
		String[] splitTokens = tokenSequence.split(delimiter);
		Set<String> tokens = new HashSet<String>();

		for (String splitToken : splitTokens)
		{
			tokens.add(splitToken.trim());
		}

		return tokens;
	}

	/**
	 * Removes newline and tabulator characters from the text and each sequence of spaces
	 * is replaced with one single space.
	 * 
	 * @param text
	 *            Text to be cleaned up by formatting.
	 * @return cleaned string.
	 */
	public static String cleanFormatting(String text)
	{
		// Changes due to Issue #29
		return text.replaceAll("[" + ASCII_CR + ASCII_LF + ASCII_TAB + "]+", SPACE)
				.replaceAll("[ ]+", SPACE).trim();
		// End changes due to Issue #29
	}

	public static String concat(String... strings)
	{
		StringBuffer buffer = new StringBuffer();

		for (String string : strings)
		{
			buffer.append(string);
		}

		return buffer.toString();
	}

	public static List<String> replaceColons(List<String> unstructuredSentences)
	{
		List<String> colonFreeSentences = new ArrayList<String>();

		for (String unstructuredSentence : unstructuredSentences)
		{
			String colonFreeSentence = unstructuredSentence.replaceAll("\\.", SPACE);

			colonFreeSentences.add(colonFreeSentence);
		}

		return colonFreeSentences;
	}

	public static String addBlanksToCamelSyntax(String camelSyntaxLabel)
	{
		final StringBuffer labelWithBlanks = new StringBuffer();

		if (camelSyntaxLabel != null)
		{
			camelSyntaxLabel = camelSyntaxLabel.replace('_', ' ');

			for (int letter = 0; letter < camelSyntaxLabel.length(); letter++)
			{
				char currentLetter = camelSyntaxLabel.charAt(letter);

				if (isBigCharacter(currentLetter))
				{
					char prevChar = (letter > 0) ? camelSyntaxLabel.charAt(letter - 1)
							: 'a';

					if (!isBigCharacter(prevChar) || Character.isDigit(prevChar))
					{
						labelWithBlanks.append(SPACE);
					}
				}

				labelWithBlanks.append(currentLetter);
			}
		}

		return labelWithBlanks.toString().trim();
	}

	private static boolean isBigCharacter(char currentLetter)
	{
		return (currentLetter >= 65) && (currentLetter <= 90);
	}

	public static List<String> addBlanksToCamelSyntax(final List<String> camelSyntaxLabels)
	{
		final List<String> blankCamelSyntax = new ArrayList<String>();

		for (String camelLabel : camelSyntaxLabels)
		{
			blankCamelSyntax.add(addBlanksToCamelSyntax(camelLabel));
		}

		return blankCamelSyntax;
	}

	public static String toString(final InputStream stream)
	{
		InputStreamReader isr;
		if (stream instanceof BufferedInputStream)
		{
			isr = new InputStreamReader(stream, 
					Charset.forName(Misc.DEFAULT_CHARSET));
		}
		else
		{
			isr = new InputStreamReader(new BufferedInputStream(stream),
					Charset.forName(Misc.DEFAULT_CHARSET));
		}
		final StringWriter sw = new StringWriter();

		try
		{
			final char[] buffer = new char[256];
			int n = 0;
			while (-1 != (n = isr.read(buffer)))
			{
				sw.write(buffer, 0, n);
			}
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return sw.toString();
	}

	public static String capitalizeFirstChar(final String string)
	{
		final StringBuffer buffer = new StringBuffer();

		buffer.append(String.valueOf(string.charAt(0)).toUpperCase());
		buffer.append(string.substring(1).toLowerCase());

		return buffer.toString();
	}

	public static String toString(final String string)
	{
		if (string == null)
		{
			return EMPTY;
		}
		return string;
	}

	public static String inBrackets(final String string)
	{
		if (string == null)
		{
			return EMPTY;
		}
		else
		{
			return '[' + string + ']';
		}
	}

	public static String mergeSpaces(String stringWithSpaces)
	{
		if (stringWithSpaces != null)
		{
			StringBuffer mergedSpaces = new StringBuffer();
			char[] unmergedSpaces = stringWithSpaces.toCharArray();
			boolean lastCharacterWasSpace = false;

			for (int i = 0; i < unmergedSpaces.length; i++)
			{
				if (unmergedSpaces[i] == SPACE.charAt(0))
				{
					if (!lastCharacterWasSpace)
					{
						mergedSpaces.append(unmergedSpaces[i]);
						lastCharacterWasSpace = true;

					}
				}
				else
				{
					mergedSpaces.append(unmergedSpaces[i]);
					lastCharacterWasSpace = false;
				}
			}

			return mergedSpaces.toString();
		}
		else
		{
			return EMPTY;
		}
	}

}
