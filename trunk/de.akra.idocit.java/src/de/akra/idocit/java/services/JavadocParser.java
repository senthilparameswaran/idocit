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
import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Scope;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.core.utils.DescribedItemUtils;

/**
 * Parser for {@link Javadoc}. It converts a Javadoc to a list of {@link Documentation}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class JavadocParser
{
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(JavadocParser.class.getName());

	private static final String JAVADOC_TAG_PARAM = TagElement.TAG_PARAM + "\\s*";
	private static final String JAVADOC_TAG_RETURN = TagElement.TAG_RETURN + "\\s*";
	private static final String JAVADOC_TAG_THROWS = TagElement.TAG_THROWS + "\\s*";
	static final String JAVADOC_TAG_THEMATICGRID = "@thematicgrid";
	static final String JAVADOC_TAG_THEMATICGRID_PATTERN = JAVADOC_TAG_THEMATICGRID
			+ "\\s*";

	public static final String CONVERTED_JAVADOC_TAG_PARAM = "param";
	public static final String CONVERTED_JAVADOC_TAG_RETURN = "return";
	public static final String CONVERTED_JAVADOC_TAG_THROWS = "throws";

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
	 * @since 0.0.2
	 */
	public static List<Documentation> parseIDocItJavadoc(Javadoc javadoc)
			throws SAXException, IOException, ParserConfigurationException
	{
		if (javadoc == null)
		{
			return Collections.emptyList();
		}
		String html = extractPlainText(javadoc);
		return HTMLTableParser.convertJavadocToDocumentations(html);
	}

	/**
	 * Extracts the name of the reference thematic grid from the given JavaDoc and returns
	 * it.
	 * 
	 * Please note:
	 * <ul>
	 * <ol>
	 * It is assumed that the given javadoc belongs to a method and not to a class, etc.
	 * </ol>
	 * <ol>
	 * It is expected that the given JavaDoc contains a tag named "@thematicgrid" which is
	 * succeeded by the name of the thematic grid. This name will be returned.
	 * </ol>
	 * <ol>
	 * If no name is found, <code>null</code> will be returned.
	 * </ol>
	 * </ul>
	 * 
	 * @param javadoc
	 *            The JavaDoc to extract the name of the reference grid from (SOURCE)
	 * @return The name of the reference grid
	 */
	@SuppressWarnings("unchecked")
	public static String parseIDocItReferenceGrid(Javadoc javadoc)
	{
		if (javadoc != null)
		{
			List<TagElement> javadocComments = javadoc.tags();

			if (javadocComments != null)
			{
				// Iterate over the comments ...
				for (int i = 0; i < javadocComments.size(); i++)
				{
					TagElement docElement = (TagElement) javadocComments.get(i);

					if ((docElement.getTagName() != null)
							&& docElement.getTagName().matches(
									JAVADOC_TAG_THEMATICGRID_PATTERN))
					{
						return readFragments(docElement.fragments(), 0).trim();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Converts the Javdoc to a list of {@link Documentation}s. The
	 * {@link Documentation#getSignatureElementIdentifier()} is set to a simple path of
	 * the Javadoc tag, so that the {@link Documentation}s can be assigned to right
	 * {@link SignatureElement}s later.
	 * <p>
	 * Example:<br />
	 * <ul>
	 * <li>General description => <code>signatureElementIdentifier = null</code></li>
	 * <li>@param paramName =>
	 * <code>signatureElementIdentifier = {@link #CONVERTED_JAVADOC_TAG_PARAM} + {@link JavaParser#delimiters}.pathDelimiter + paramName</code>
	 * <li>@return =>
	 * <code>signatureElementIdentifier = {@link #CONVERTED_JAVADOC_TAG_RETURN}</code>
	 * <li>@throws exceptionName =>
	 * <code>signatureElementIdentifier = {@link #CONVERTED_JAVADOC_TAG_THROWS} + {@link JavaParser#delimiters}.pathDelimiter + exceptionName</code>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param javadoc
	 *            The Javadoc to convert to {@link Documentation}s.
	 * @return a list of {@link Documentation}s.
	 * @since 0.0.2
	 */
	@SuppressWarnings("unchecked")
	public static List<Documentation> convertExistingJavadoc(Javadoc javadoc)
	{
		List<Documentation> foundDocs = Collections.emptyList();
		if (javadoc != null)
		{
			foundDocs = new ArrayList<Documentation>(javadoc.tags().size());
			// TODO make the used addressee adjustable via the Preference Pages
			Addressee developer = DescribedItemUtils.findAddressee("Developer");

			for (TagElement tag : (List<TagElement>) javadoc.tags())
			{
				Documentation doc = new Documentation();
				doc.setScope(Scope.EXPLICIT);
				String comment = null;
				if (tag.getTagName() == null)
				{
					comment = readFragments((List<ASTNode>) tag.fragments(), 0);
				}
				else if (tag.getTagName().matches(JAVADOC_TAG_RETURN))
				{
					comment = readFragments((List<ASTNode>) tag.fragments(), 0).trim();
					doc.setSignatureElementIdentifier(CONVERTED_JAVADOC_TAG_RETURN);
				}
				else if (tag.getTagName().matches(JAVADOC_TAG_PARAM)
						|| tag.getTagName().matches(JAVADOC_TAG_THROWS))
				{
					comment = readFragments((List<ASTNode>) tag.fragments(), 1);
					ASTNode paramName = (ASTNode) tag.fragments().get(0);
					if (ASTNode.SIMPLE_NAME == paramName.getNodeType())
					{
						SimpleName name = (SimpleName) paramName;
						String identifier = null;
						if (tag.getTagName().matches(JAVADOC_TAG_PARAM))
						{
							identifier = CONVERTED_JAVADOC_TAG_PARAM
									+ JavaParser.delimiters.pathDelimiter
									+ name.getIdentifier();
						}
						else
						{
							identifier = CONVERTED_JAVADOC_TAG_THROWS
									+ JavaParser.delimiters.pathDelimiter
									+ name.getIdentifier();
						}
						doc.setSignatureElementIdentifier(identifier);
					}
					else
					{
						log.warning("Javadoc Parameter name is not available: "
								+ tag.toString());
					}
				}
				else
				{
					log.log(Level.FINE, "Javadoc tag is not converted: " + tag);
				}
				if (comment != null)
				{
					doc.getDocumentation().put(developer, comment);
					doc.getAddresseeSequence().add(developer);
					foundDocs.add(doc);
				}
			}
		}
		return foundDocs;
	}

	/**
	 * Collects and returns the additional tags in the Javadoc (all except the tags param,
	 * return, throws and the general description).
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to check for additional {@link TagElement} s.
	 * @return The list of additional tags. If there are no one,
	 *         {@link Collections#EMPTY_LIST} is returned.
	 */
	@SuppressWarnings("unchecked")
	public static List<TagElement> findAdditionalTags(Javadoc javadoc)
	{
		List<TagElement> tags = Collections.emptyList();
		if (javadoc != null)
		{
			tags = new ArrayList<TagElement>(javadoc.tags().size());
			for (TagElement tag : (List<TagElement>) javadoc.tags())
			{
				if (!(tag.getTagName() == null
						|| tag.getTagName().matches(JAVADOC_TAG_RETURN)
						|| tag.getTagName().matches(JAVADOC_TAG_PARAM) || tag
						.getTagName().matches(JAVADOC_TAG_THROWS)))
				{
					tags.add(tag);
					log.log(Level.FINEST, "Keep Javadoc tag: " + tag);
				}
			}
		}
		return tags;
	}

	/**
	 * Extracts the plain text out of the <code>javadoc</code> from all supported tags
	 * (param, return, throws and the general description), others are ignored. As the
	 * iDocIt! format is written in HTML tables, the method extracts these tables and
	 * summarizes it in one String.<br />
	 * If the Javadoc is not written in the iDocIt! format the result is simply the plain
	 * text of the supported tags, that cannot be further interpreted.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to read.
	 * @return The read text.
	 */
	@SuppressWarnings("unchecked")
	private static String extractPlainText(Javadoc javadoc)
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
				// html.append(System.getProperty("line.separator"));
				// html.append(tag.getTagName());
				// html.append(readFragments((List<ASTNode>) tag.fragments(),
				// 0));
				// log.log(Level.INFO, "Generic Javadoc tag appended: " + tag);
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
