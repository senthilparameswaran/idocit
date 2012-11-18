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
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.utils.DescribedItemUtils;
import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.utils.JavadocUtils;

public abstract class AbsJavadocParser
{
	/**
	 * Logger.
	 */
	static Logger log = Logger.getLogger(AbsJavadocParser.class.getName());

	static final String JAVADOC_TAG_PARAM = TagElement.TAG_PARAM + "\\s*";
	static final String JAVADOC_TAG_RETURN = TagElement.TAG_RETURN + "\\s*";
	static final String JAVADOC_TAG_THROWS = TagElement.TAG_THROWS + "\\s*";
	static final String JAVADOC_TAG_THEMATICGRID = CustomTaglets.THEMATIC_GRID
			.getTagName();
	static final String JAVADOC_TAG_THEMATICGRID_PATTERN = JAVADOC_TAG_THEMATICGRID;

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
	 * @param addressees
	 *            TODO
	 * @param thematicRoles
	 *            s TODO
	 * @return List of {@link Documentation}s. If <code>javadoc == null</code> then
	 *         {@link Collections#EMPTY_LIST} is returned.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @since 0.0.2
	 */
	public abstract List<Documentation> parseIDocItJavadoc(Javadoc javadoc,
			List<Addressee> addressees, List<ThematicRole> thematicRoles,
			JavaMethod method) throws SAXException, IOException,
			ParserConfigurationException, ParsingException;

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
	public String parseIDocItReferenceGrid(Javadoc javadoc)
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
						return JavadocUtils.readFragments(docElement.fragments(), 0)
								.trim();
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
	public List<Documentation> convertExistingJavadoc(Javadoc javadoc)
	{
		List<Documentation> foundDocs = Collections.emptyList();
		if (javadoc != null)
		{
			foundDocs = new ArrayList<Documentation>(javadoc.tags().size());
			// TODO make the used addressee adjustable via the Preference Pages
			final Addressee developer = DescribedItemUtils.findAddressee("Developer");

			for (final TagElement tag : (List<TagElement>) javadoc.tags())
			{
				final Documentation doc = new Documentation();
				String comment = null;
				if (tag.getTagName() == null)
				{
					comment = JavadocUtils.readFragments((List<ASTNode>) tag.fragments(),
							0);
				}
				else if (tag.getTagName().matches(JAVADOC_TAG_RETURN))
				{
					comment = JavadocUtils.readFragments((List<ASTNode>) tag.fragments(),
							0).trim();
					doc.setSignatureElementIdentifier(CONVERTED_JAVADOC_TAG_RETURN);
				}
				else if (tag.getTagName().matches(JAVADOC_TAG_PARAM)
						|| tag.getTagName().matches(JAVADOC_TAG_THROWS))
				{
					comment = JavadocUtils.readFragments((List<ASTNode>) tag.fragments(),
							1);
					final ASTNode paramName = (ASTNode) tag.fragments().get(0);
					if (ASTNode.SIMPLE_NAME == paramName.getNodeType())
					{
						final SimpleName name = (SimpleName) paramName;
						String identifier = null;
						if (tag.getTagName().matches(JAVADOC_TAG_PARAM))
						{
							identifier = CONVERTED_JAVADOC_TAG_PARAM
									+ JavaParser.delimiters.getPathDelimiter()
									+ name.getIdentifier();
						}
						else
						{
							identifier = CONVERTED_JAVADOC_TAG_THROWS
									+ JavaParser.delimiters.getPathDelimiter()
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
	public List<TagElement> findAdditionalTags(final Javadoc javadoc,
			final List<ThematicRole> knownRoles)
	{
		List<TagElement> tags = Collections.emptyList();
		if (javadoc != null)
		{
			tags = new ArrayList<TagElement>(javadoc.tags().size());
			for (TagElement tag : (List<TagElement>) javadoc.tags())
			{
				if (!(tag.getTagName() == null
						&& !JavadocUtils.isStandardJavadocTaglet(tag.getTagName()) && !JavadocUtils
							.isIdocitJavadocTaglet(tag.getTagName())))
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
	public String extractPlainText(Javadoc javadoc)
	{
		StringBuffer html = new StringBuffer();
		for (TagElement tag : (List<TagElement>) javadoc.tags())
		{
			if (tag.getTagName() == null || tag.getTagName().matches(JAVADOC_TAG_RETURN))
			{
				html.append(JavadocUtils.readFragments((List<ASTNode>) tag.fragments(), 0));
			}
			else if (tag.getTagName().matches(JAVADOC_TAG_PARAM)
					|| tag.getTagName().matches(JAVADOC_TAG_THROWS))
			{
				html.append(JavadocUtils.readFragments((List<ASTNode>) tag.fragments(), 1));
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

}