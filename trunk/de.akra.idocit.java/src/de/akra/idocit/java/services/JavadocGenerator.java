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

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Scope;
import de.akra.idocit.java.structure.JavadocTagElement;

/**
 * Generator for {@link Javadoc} comments.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class JavadocGenerator
{
	/**
	 * The value for attribute "name" for the iDocIt! HTML tables in Javadoc.
	 * 
	 * @since 0.0.2
	 */
	public static final String IDOCIT_HTML_TABLE_NAME = "idocit";

/**
	 * HTML Quote for "<"
	 * 
	 * @since 0.0.2
	 */
	public static final String LESS_THAN_CODE = "&lt;";

	/**
	 * HTML Quote for ">"
	 * 
	 * @since 0.0.2
	 */
	public static final String GREATER_THAN_CODE = "&gt;";

	/**
	 * Generates from the {@link JavadocTagElement}s one {@link Javadoc} comment.
	 * 
	 * @param javadocTagElements
	 *            The {@link JavadocTagElement}s from which the Javadoc should be created.
	 * @param javadoc
	 *            A {@link Javadoc} comment to which the information from the
	 *            <code>javadocTagElements</code> should be added. The
	 *            <code>javadoc</code> is either a new one created from a {@link AST} or
	 *            an existing one. If it is an existing one, it should be cleared before
	 *            adding information.
	 */
	public void generateJavadoc(List<JavadocTagElement> javadocTagElements,
			Javadoc javadoc)
	{
		if (javadocTagElements != null && !javadocTagElements.isEmpty())
		{
			for (JavadocTagElement tagElement : javadocTagElements)
			{
				if (!tagElement.isEmpty())
				{
					appendDocsToJavadoc(tagElement.getDocumentations(),
							tagElement.getTagName(), tagElement.getParameterName(),
							javadoc);
				}
			}
		}
	}

	public static String quoteGenericsInIdentifier(String identifier)
	{
		int startIndex = identifier.indexOf('<');

		// return identifier.replaceAll("<", LESS_THAN_CODE).replaceAll(">",
		// GREATER_THAN_CODE);

		if (startIndex >= 0)
		{
			return identifier.substring(0, startIndex);
		}
		else
		{
			return identifier;
		}
	}

	/**
	 * Append the information out of <code>documentations</code> to the {@link Javadoc}
	 * block comment. If <code>tagName != null</code> the documentations are added to a
	 * new {@link TagElement} with that name. Add first the general description text with
	 * <code>tagName == null</code>. After the wished tags.
	 * 
	 * @param documentations
	 *            The list of {@link Documentation}s which should be converted to
	 *            {@link Javadoc}.
	 * @param tagName
	 *            The name of the tag element, or <code>null</code> (@see
	 *            {@link TagElement#setTagName(String)} ).
	 * @param paramName
	 *            The name of the described parameter, or <code>null</code> if no name is
	 *            needed.
	 * @param javadoc
	 *            The {@link Javadoc} reference to which the {@link TagElement}s should be
	 *            added.
	 */
	public static void appendDocsToJavadoc(List<Documentation> documentations,
			String tagName, String paramName, Javadoc javadoc)
	{
		@SuppressWarnings("unchecked")
		List<TagElement> tags = javadoc.tags();
		AST jdocAST = javadoc.getAST();

		boolean tagChanged = false;

		TagElement newTag = jdocAST.newTagElement();

		if (tagName != null)
		{
			tagChanged = true;
			newTag.setTagName(tagName + "");
		}

		@SuppressWarnings("unchecked")
		List<ASTNode> fragments = newTag.fragments();

		if (paramName != null)
		{
			tagChanged = true;
			TextElement paramNameElement = jdocAST.newTextElement();
			paramNameElement.setText(paramName + "");
			fragments.add(paramNameElement);
		}

		final String tableStartTag = "<table name=\"" + IDOCIT_HTML_TABLE_NAME
				+ "\" border=\"1\" cellspacing=\"0\">\n";

		for (Documentation doc : documentations)
		{
			// write only if there is something to write
			if (doc.getScope() != null || doc.getThematicRole() != null
					|| !doc.getDocumentation().isEmpty())
			{
				tagChanged = true;
				StringBuffer textElem = new StringBuffer();
				if (fragments.size() >= 1)
				{
					textElem.append("\n<br />");
				}
				textElem.append(tableStartTag);

				if (doc.getSignatureElementIdentifier() != null)
				{
					textElem.append("<tr><td>Element:</td><td>");
					textElem.append(quoteGenericsInIdentifier(doc
							.getSignatureElementIdentifier()));
					textElem.append("</td></tr>\n");
				}

				if (doc.getThematicRole() != null)
				{
					textElem.append("<tr><td>Role:</td><td>");
					textElem.append(doc.getThematicRole().getName());
					textElem.append("</td></tr>\n");
				}

				if (doc.getScope() == Scope.IMPLICIT)
				{
					textElem.append("<tr><td>Scope:</td><td>implicit</td></tr>\n");
				}

				Map<Addressee, String> docMap = doc.getDocumentation();
				for (Addressee addressee : doc.getAddresseeSequence())
				{
					String text = docMap.get(addressee);
					if (!text.isEmpty())
					{
						textElem.append("<tr><td><b>");
						textElem.append(addressee.getName());
						textElem.append("</b>:</td><td>");
						textElem.append(docMap.get(addressee));
						textElem.append("</td></tr>\n");
					}
				}
				textElem.append("</table>");

				TextElement textElement = jdocAST.newTextElement();
				textElement.setText(textElem.toString());
				fragments.add(textElement);
			}
		}
		if (tagChanged)
		{
			tags.add(newTag);
		}
	}

	/**
	 * <table border ="1">
	 * <tr>
	 * <td><b>Manager</b>:</td>
	 * <td>sdfgsgjse gjgijwejj</td>
	 * </tr>
	 * </table>
	 * 
	 * @param filter
	 *            <table border="1">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>filter.id</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>Criterion</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>My extisting documentation: this is a really good filter ;)</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Manager</b>:</td>
	 *            <td>sdfgsgjse gjgijwejj</td>
	 *            </tr>
	 *            </table>
	 * <br />
	 *            <table border="1">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>filter.name</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>Criterion</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>My extisting documentation: this is a really good filter ;)</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Manager</b>:</td>
	 *            <td>sdfgsgjse gjgijwejj</td>
	 *            </tr>
	 *            </table>
	 * 
	 * @implicitParam <table border="1">
	 *                <tr>
	 *                <td>Role:</td>
	 *                <td>Location</td>
	 *                </tr>
	 *                <tr>
	 *                <td><b>Developer</b>:</td>
	 *                <td>My extisting documentation: this is a really good filter ;)</td>
	 *                </tr>
	 *                <tr>
	 *                <td><b>Manager</b>:</td>
	 *                <td>sdfgsgjse gjgijwejj</td>
	 *                </tr>
	 *                </table>
	 * @return
	 * @throws IllegalArgumentException
	 */
	// private void test()
	// {
	//
	// }
}
