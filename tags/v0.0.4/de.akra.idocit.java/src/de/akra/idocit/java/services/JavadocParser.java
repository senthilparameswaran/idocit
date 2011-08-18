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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Parser for {@link Javadoc}. It converts a Javadoc to a list of {@link Documentation}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavadocParser
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(JavadocParser.class.getName());

	private static final String JAVADOC_TAG_PARAM = TagElement.TAG_PARAM + "\\s*";
	private static final String JAVADOC_TAG_RETURN = TagElement.TAG_RETURN + "\\s*";
	private static final String JAVADOC_TAG_THROWS = TagElement.TAG_THROWS + "\\s*";

	/**
	 * Converts the {@link Javadoc} to a list of {@link Documentation}s. The generated
	 * Documentations can then be used to attach them to the corresponding
	 * {@link SignatureElement} in the object structure of the method parameters.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to parse.
	 * @return List of {@link Documentation}s. If <code>javadoc == null</code> then
	 *         {@link Collections#EMPTY_LIST} is returned.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static List<Documentation> parse(Javadoc javadoc) throws SAXException,
			IOException, ParserConfigurationException
	{
		if (javadoc == null)
		{
			return Collections.emptyList();
		}

		String html = extractHTMLTables(javadoc);
		return HTMLTableParser.convertJavadocToDocumentations(html);
	}

	/**
	 * Extracts the plain text out if the <code>javadoc</code> from all supported tags. As
	 * the iDocIt! format is written in HTML tables, the method extracts these tables and
	 * summarize it in a String.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to read.
	 * @return The read text.
	 */
	@SuppressWarnings("unchecked")
	private static String extractHTMLTables(Javadoc javadoc)
	{
		StringBuffer html = new StringBuffer();
		for (TagElement tag : (List<TagElement>) javadoc.tags())
		{
			if (tag.getTagName() == null || tag.getTagName().matches(JAVADOC_TAG_RETURN))
			{
				html.append(readFragments((List<ASTNode>) tag.fragments(), 0));
			}
			else if (tag.getTagName().matches(JAVADOC_TAG_PARAM)
					|| tag.getTagName().matches(JAVADOC_TAG_THROWS))
			{
				html.append(readFragments((List<ASTNode>) tag.fragments(), 1));
			}
			else
			{
				logger.log(Level.WARNING, "Unknown Javadoc tag: " + tag.getTagName());
			}
		}
		return html.toString();
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
	private static String readFragments(List<ASTNode> fragments, int offset)
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
					html.append(ReflectionHelper.getIdentifierFrom(mRefParam.getType()));
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
}
