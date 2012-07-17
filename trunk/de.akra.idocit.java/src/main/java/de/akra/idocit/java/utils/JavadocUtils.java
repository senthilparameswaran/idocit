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

	public static boolean isParamInfo(String tagName)
	{
		return CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
	}

	public static boolean isThrowsInfo(String tagName)
	{
		return CustomTaglets.THROWS_INFO.getTagName().equals(tagName);
	}

	public static boolean isReturnInfo(String tagName)
	{
		return CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
	}

	public static boolean isSubParam(String tagName)
	{
		return CustomTaglets.SUB_PARAM.getTagName().equals(tagName);
	}

	public static boolean isParam(String tagName)
	{
		return TagElement.TAG_PARAM.equals(tagName)
				|| CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
	}

	public static boolean isThrows(String tagName)
	{
		return TagElement.TAG_THROWS.equals(tagName)
				|| CustomTaglets.THROWS_INFO.getTagName().equals(tagName);
	}

	public static boolean isReturn(String tagName)
	{
		return TagElement.TAG_RETURN.equals(tagName)
				|| CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
	}

	public static boolean isStandardJavadocTaglet(String tagName)
	{
		boolean isReturn = isReturn(tagName);
		boolean isThrows = TagElement.TAG_THROWS.equals(tagName);

		return isParam(tagName) || isReturn || isThrows;
	}

	public static boolean isSubReturn(String tagName)
	{
		return CustomTaglets.SUB_RETURN.getTagName().equals(tagName);
	}

	public static boolean isIdocitJavadocTaglet(String tagName)
	{
		boolean isSubParam = isSubParam(tagName);
		boolean isSubReturn = isSubReturn(tagName);
		boolean isInfoTag = isIdocItInfoTag(tagName);

		return isSubParam || isSubReturn || isInfoTag;
	}

	public static boolean isIdocItInfoTag(String tagName)
	{
		boolean paramInfo = CustomTaglets.PARAM_INFO.getTagName().equals(tagName);
		boolean returnInfo = CustomTaglets.RETURN_INFO.getTagName().equals(tagName);
		boolean throwsInfo = CustomTaglets.THROWS_INFO.getTagName().equals(tagName);

		return paramInfo || returnInfo || throwsInfo;
	}

	public static String readIdentifier(TagElement tag)
	{
		String identifier = null;
		ASTNode paramName = (ASTNode) tag.fragments().get(0);

		if (JavadocUtils.isParam(tag.getTagName())
				|| JavadocUtils.isThrows(tag.getTagName())
				|| JavadocUtils.isReturn(tag.getTagName()))
		{
			if (ASTNode.SIMPLE_NAME == paramName.getNodeType())
			{
				SimpleName name = (SimpleName) paramName;
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
	public static String readFragments(List<ASTNode> fragments, int offset)
	{
		StringBuffer html = new StringBuffer();

		for (ASTNode fragment : fragments.subList(offset, fragments.size()))
		{
			switch (fragment.getNodeType())
			{
			case ASTNode.TEXT_ELEMENT:
			{
				TextElement textElem = (TextElement) fragment;
				html.append(textElem.getText());
				break;
			}
			case ASTNode.SIMPLE_NAME:
			case ASTNode.QUALIFIED_NAME:
			{
				Name name = (Name) fragment;
				html.append(name.getFullyQualifiedName());
				break;
			}
			case ASTNode.METHOD_REF:
			{
				MethodRef mRef = (MethodRef) fragment;
				if (mRef.getQualifier() != null)
				{
					Name qualifier = mRef.getQualifier();
					html.append(qualifier.getFullyQualifiedName());
				}

				html.append('#');
				html.append(mRef.getName().getIdentifier());
				html.append('(');

				// write parameter list
				List<MethodRefParameter> mRefParameters = (List<MethodRefParameter>) mRef
						.parameters();
				for (MethodRefParameter mRefParam : mRefParameters)
				{
					html.append(ReflectionHelper.extractIdentifierFrom(mRefParam
							.getType()));
					if (mRefParam.isVarargs())
					{
						html.append("...");
					}
					if (mRefParam.getName() != null)
					{
						html.append(' ');
						html.append(mRefParam.getName().getFullyQualifiedName());
					}
					html.append(',');
				}
				if (!mRefParameters.isEmpty())
				{
					// remove last comma
					html.deleteCharAt(html.length() - 1);
				}

				html.append(')');
				break;
			}
			case ASTNode.MEMBER_REF:
			{
				MemberRef mRef = (MemberRef) fragment;
				if (mRef.getQualifier() != null)
				{
					Name qualifier = mRef.getQualifier();
					html.append(qualifier.getFullyQualifiedName());
				}
				html.append('#');
				html.append(mRef.getName().getIdentifier());
				break;
			}
			case ASTNode.TAG_ELEMENT:
			{
				TagElement tagElem = (TagElement) fragment;
				if (tagElem.isNested())
				{
					html.append('{');
				}

				html.append(tagElem.getTagName());
				html.append(' ');
				html.append(readFragments((List<ASTNode>) tagElem.fragments(), 0));

				if (tagElem.isNested())
				{
					html.append('}');
				}
				break;
			}
			}
		}
		return html.toString();
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