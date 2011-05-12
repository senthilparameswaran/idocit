package de.akra.idocit.core.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Some useful methods for working with strings.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class StringUtils
{
	private static final String ASCII_TAB = "\t";
	private static final String ASCII_LF = "\n";
	private static final String ASCII_CR = "\r";

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
	public static String addLineBreaks(String string, int maxLineLength, char splitChar)
	{
		StringBuffer buffer = new StringBuffer();
		String[] splitString = string.split(String.valueOf(splitChar));
		int currentLineLength = 0;

		for (String token : splitString)
		{
			if (currentLineLength > maxLineLength)
			{
				buffer.append(System.getProperty("line.separator"));
				currentLineLength = 0;
			}
			else if (currentLineLength > 0)
			{
				buffer.append(' ');
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
	public static String addAdditionalLineBreaks(String string, int maxLineLength)
	{
		char splitChar = ' ';
		StringBuffer buffer = new StringBuffer();
		String[] splitString = string.split(String.valueOf(splitChar));
		int currentLineLength = 0;

		for (String token : splitString)
		{
			if (currentLineLength > maxLineLength)
			{
				buffer.append(System.getProperty("line.separator"));
				currentLineLength = 0;
			}
			else if (token.matches("[\r\n]+"))
			{
				// reset line length if text contains newlines
				currentLineLength = 0;
			}
			else if (currentLineLength > 0)
			{
				buffer.append(' ');
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
	public static String removeLineBreaks(String string)
	{
		StringBuffer buffer = new StringBuffer();
		String[] splitString = string.split(System.getProperty("line.separator"));

		for (int i = 0; i < splitString.length; i++)
		{
			String token = splitString[i];

			buffer.append(token);

			if (i < (splitString.length - 1))
			{
				buffer.append(' ');
			}
		}

		return buffer.toString();
	}

	/**
	 * Converts the Set of Strings into one comma separated string with the tokens.
	 * 
	 * @param tokens
	 *            Tokens to be converted.
	 * @return String with the comma separated tokens.
	 */
	public static String convertIntoCommaSeperatedTokens(Set<String> tokens)
	{
		StringBuffer buffer = new StringBuffer();
		int tokenNo = 0;
		int size = tokens.size();

		for (String token : tokens)
		{
			buffer.append(token);

			if (tokenNo < (size - 1))
			{
				buffer.append(", ");
			}

			tokenNo++;
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
		return text.replaceAll("[" + ASCII_CR + ASCII_LF + ASCII_TAB + "]+", " ")
				.replaceAll("[ ]+", " ");
		// End changes due to Issue #29
	}
}
