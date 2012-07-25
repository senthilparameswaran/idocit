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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.services.HTMLTableParser;
import de.akra.idocit.java.services.ReflectionHelper;
import de.akra.idocit.java.structure.StringReplacement;

public class JavadocUtils
{
	private static final String REGEX_CR = "[\r]";
	public static final String NEW_LINE = System.getProperty("line.separator");

	public static final String XML_HEADER = "<?xml version=\"1.1\" encoding=\"UTF-8\" ?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	public static final String XML_ROOT_START = "<javadoc>";
	public static final String XML_ROOT_END = "</javadoc>";

	private static final Logger logger = Logger.getLogger(JavadocUtils.class.getName());

	private static class JavadocHtmlHandler extends DefaultHandler2
	{

		private List<StringReplacement> replacements = new ArrayList<StringReplacement>();

		private InputSource dtdInputSources;

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String origValue = String.valueOf(ch, start, length);
			String escapedValue = StringUtils.escapeHtml(origValue);

			StringReplacement replacement = new StringReplacement();
			replacement.setOriginalString(origValue);
			replacement.setEscapedString(escapedValue);

			replacements.add(replacement);
		}

		@Override
		public InputSource resolveEntity(String name, String publicId, String baseURI,
				String systemId) throws SAXException, IOException
		{
			return dtdInputSources;
		}

		public void setDtdInputSources(InputSource dtdInputSources)
		{
			this.dtdInputSources = dtdInputSources;
		}

		public List<StringReplacement> getReplacements()
		{
			return replacements;
		}
	}

	public static boolean isParamInfo(final String tagName)
	{
		return CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
	}

	public static boolean isThrowsInfo(final String tagName)
	{
		return CustomTaglets.THROWS_INFO.getTagName().equals(tagName);
	}

	public static boolean isReturnInfo(final String tagName)
	{
		return CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
	}

	public static boolean isSubParam(final String tagName)
	{
		return CustomTaglets.SUB_PARAM.getTagName().equals(tagName);
	}

	public static boolean isParam(final String tagName)
	{
		return TagElement.TAG_PARAM.equals(tagName)
				|| CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
	}

	public static boolean isThrows(final String tagName)
	{
		return TagElement.TAG_THROWS.equals(tagName)
				|| CustomTaglets.THROWS_INFO.getTagName().equals(tagName);
	}

	public static boolean isReturn(final String tagName)
	{
		return TagElement.TAG_RETURN.equals(tagName)
				|| CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
	}

	public static boolean isStandardJavadocTaglet(final String tagName)
	{
		boolean isReturn = isReturn(tagName);
		boolean isThrows = TagElement.TAG_THROWS.equals(tagName);

		return isParam(tagName) || isReturn || isThrows;
	}

	public static boolean isSubReturn(final String tagName)
	{
		return CustomTaglets.SUB_RETURN.getTagName().equals(tagName);
	}

	public static boolean isIdocitJavadocTaglet(final String tagName)
	{
		boolean isSubParam = isSubParam(tagName);
		boolean isSubReturn = isSubReturn(tagName);
		boolean isInfoTag = isIdocItInfoTag(tagName);

		return isSubParam || isSubReturn || isInfoTag;
	}

	public static boolean isIdocItInfoTag(final String tagName)
	{
		boolean paramInfo = CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
		boolean returnInfo = CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
		boolean throwsInfo = CustomTaglets.THROWS_INFO.getTagName().equals(tagName);

		return paramInfo || returnInfo || throwsInfo;
	}

	public static String readIdentifier(final TagElement tag)
	{
		String identifier = null;

		if ((JavadocUtils.isParam(tag.getTagName()) || JavadocUtils.isThrows(tag
				.getTagName())) && tag.fragments() != null)
		{
			final ASTNode paramName = (ASTNode) tag.fragments().get(0);
			if (ASTNode.SIMPLE_NAME == paramName.getNodeType())
			{
				final SimpleName name = (SimpleName) paramName;
				identifier = name.getIdentifier();
			}
		}

		return identifier;
	}

	/**
	 * Extracts the plain text from the <code>fragments</code>.
	 * 
	 * @param fragments
	 *            The fragments to read.
	 * @param offset
	 *            The index at which should be started to read. If the fragments are e.g.
	 *            from a "@param" tag, then it is followed by the the variable name which
	 *            should be skipped. Therefore the <code>offset</code> should be 1.
	 * @return The text from the <code>fragments</code>.
	 */
	@SuppressWarnings("unchecked")
	public static String readFragments(final List<ASTNode> fragments, final int offset)
	{
		final StringBuffer html = new StringBuffer();

		if (fragments != null && fragments.size() >= offset)
		{
			for (final ASTNode fragment : fragments.subList(offset, fragments.size()))
			{
				final StringBuffer tempText = new StringBuffer(fragment.getLength());

				switch (fragment.getNodeType())
				{
				case ASTNode.TEXT_ELEMENT:
				{
					final TextElement textElem = (TextElement) fragment;
					tempText.append(textElem.getText());
					break;
				}
				case ASTNode.SIMPLE_NAME:
				case ASTNode.QUALIFIED_NAME:
				{
					final Name name = (Name) fragment;
					tempText.append(name.getFullyQualifiedName());
					break;
				}
				case ASTNode.METHOD_REF:
				{
					final MethodRef mRef = (MethodRef) fragment;
					if (mRef.getQualifier() != null)
					{
						final Name qualifier = mRef.getQualifier();
						tempText.append(qualifier.getFullyQualifiedName());
					}

					tempText.append('#');
					tempText.append(mRef.getName().getIdentifier());
					tempText.append('(');

					// write parameter list
					final List<MethodRefParameter> mRefParameters = (List<MethodRefParameter>) mRef
							.parameters();
					for (final MethodRefParameter mRefParam : mRefParameters)
					{
						tempText.append(ReflectionHelper.extractIdentifierFrom(mRefParam
								.getType()));
						if (mRefParam.isVarargs())
						{
							tempText.append("...");
						}
						if (mRefParam.getName() != null)
						{
							tempText.append(' ');
							tempText.append(mRefParam.getName().getFullyQualifiedName());
						}
						tempText.append(',');
					}
					if (!mRefParameters.isEmpty())
					{
						// remove last comma
						tempText.deleteCharAt(tempText.length() - 1);
					}

					tempText.append(')');
					break;
				}
				case ASTNode.MEMBER_REF:
				{
					final MemberRef mRef = (MemberRef) fragment;
					if (mRef.getQualifier() != null)
					{
						final Name qualifier = mRef.getQualifier();
						tempText.append(qualifier.getFullyQualifiedName());
					}
					tempText.append('#');
					tempText.append(mRef.getName().getIdentifier());
					break;
				}
				case ASTNode.TAG_ELEMENT:
				{
					final TagElement tagElem = (TagElement) fragment;
					if (tagElem.isNested())
					{
						tempText.append('{');
					}

					tempText.append(tagElem.getTagName());
					tempText.append(' ');
					tempText.append(readFragments((List<ASTNode>) tagElem.fragments(), 0));

					if (tagElem.isNested())
					{
						tempText.append('}');
					}
					break;
				}
				}
				appendWithSpace(html, tempText);
			}
			// delete leading space, that was added by Javadoc to separate a tag
			// from the following text (e.g. '@param My documentation').
			if (html.length() > 0 && html.charAt(0) == ' ')
			{
				html.deleteCharAt(0);
			}
		}
		return html.toString();
	}

	/**
	 * Append the {@code appendage} string to the {@code text} string and be sure that a
	 * space ' ' is between the strings if {@code text} is not empty and does not end with
	 * a special character and {@code appendage} is not empty and does not start with a
	 * special character, then there will be added a space before the strings are
	 * concatenated.
	 * <p>
	 * This is needed because if there are line breaks in the documentation text of a tag
	 * it can happen that two fragments are concatenated without space.
	 * </p>
	 * 
	 * @param text
	 *            [DESTINATION] The {@code appendage} is added to this object.
	 * @param appendage
	 *            [OBJECT] The text to append.
	 * @see #needEndCharSpace(char)
	 * @see #needStartCharSpace(char)
	 */
	private static void appendWithSpace(final StringBuffer text,
			final StringBuffer appendage)
	{
		if (text.length() > 0 && appendage.length() > 0)
		{
			final char textLastChar = text.charAt(text.length() - 1);
			final char appendageFirstChar = appendage.charAt(0);
			if (needEndCharSpace(textLastChar) && needStartCharSpace(appendageFirstChar))
			{
				text.append(' ');
			}
		}
		text.append(appendage);
	}

	/**
	 * Check if the character {@code c} needs a space as following character. For some
	 * characters a space is not necessary.
	 * 
	 * @param c [OBJECT]
	 * @return [REPORT] {@code true} if the character needs following a space.
	 */
	private static boolean needEndCharSpace(final char c)
	{
		return c != ' ' && c != '\t' && c != '>' && c != '(' && c != '[' && c != '{';
	}

	/**
	 * Check if the character {@code c} needs a leading space. For some characters a space
	 * is not necessary.
	 * 
	 * @param c [OBJECT]
	 * @return [REPORT] {@code true} if the character needs a space in front of it.
	 */
	private static boolean needStartCharSpace(final char c)
	{
		return c != ' ' && c != '\t' && c != '<' && c != ')' && c != ']' && c != '}'
				&& c != '.' && c != ';' && c != ',' && c != ':' && c != '!' && c != '?';
	}

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

	public static boolean isInfoTagElement(String tagElementName)
	{
		return CustomTaglets.PARAM_INFO.getTagName().equals(tagElementName)
				|| CustomTaglets.SUB_PARAM_INFO.getTagName().equals(tagElementName)
				|| CustomTaglets.RETURN_INFO.getTagName().equals(tagElementName)
				|| CustomTaglets.SUB_RETURN_INFO.getTagName().equals(tagElementName)
				|| CustomTaglets.THROWS_INFO.getTagName().equals(tagElementName)
				|| CustomTaglets.SUB_THROWS_INFO.getTagName().equals(tagElementName);
	}

	public static String escapeHtml4(String javadocText)
			throws ParserConfigurationException, SAXException, IOException
	{
		// Changes due to Issue #105
		// delete all '\r' from original text, because the SAXParser will loose them
		// during
		// parsing process. Because of that string replacement would not work at the end.
		javadocText = javadocText.replaceAll(REGEX_CR,
				de.akra.idocit.common.utils.StringUtils.EMPTY);
		// Changes due to Issue #105

		final StringBuilder xml = new StringBuilder(XML_HEADER.length()
				+ XML_ROOT_START.length() + javadocText.length() + XML_ROOT_END.length());
		xml.append(XML_HEADER).append(XML_ROOT_START).append(javadocText)
				.append(XML_ROOT_END);

		logger.log(Level.INFO, "Parsing string with SAX Parser: {0}", xml.toString());

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser saxParser = factory.newSAXParser();

		final JavadocHtmlHandler handler = new JavadocHtmlHandler();
		handler.setDtdInputSources(readDTDs());
		saxParser.parse(
				new ByteArrayInputStream(xml.toString()
						.getBytes(Charset.forName("UTF-8"))), handler);
		final List<StringReplacement> replacements = handler.getReplacements();

		for (StringReplacement replacement : replacements)
		{
			javadocText = javadocText.replaceAll(
					Pattern.quote(replacement.getOriginalString()),
					replacement.getEscapedString());
		}

		return javadocText;
	}
}
