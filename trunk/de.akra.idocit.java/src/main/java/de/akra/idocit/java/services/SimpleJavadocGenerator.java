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

import static de.akra.idocit.common.utils.ObjectUtils.notNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.Preconditions;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.core.constants.AddresseeConstants;
import de.akra.idocit.core.constants.ThematicRoleConstants;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.java.constants.Constants;
import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.utils.JavadocUtils;

public class SimpleJavadocGenerator implements IJavadocGenerator
{
	private static final String HTML_BR = "<br/>";

	private static final String JAVADOC_OPTION_TAG_HEAD = ":\" ";

	private static final String JAVADOC_OPTION_TAG_PLACEMENT = ":tcm:\"";

	private static final String JAVADOC_OPTION_TAG = "-tag ";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SimpleJavadocGenerator.class
			.getName());

	/**
	 * Singleton Instance
	 */
	public static final SimpleJavadocGenerator INSTANCE;

	static
	{
		INSTANCE = new SimpleJavadocGenerator();
	}

	/**
	 * Declare default constructor as private due to the Singleton-Pattern.
	 */
	private SimpleJavadocGenerator()
	{

	}

	/**
	 * The invariant is violated if one of the given {@link Documentation}s contains
	 * documentations for other addressees as developers.
	 * 
	 * @param documentations
	 *            [OBJECT]
	 * 
	 * @throws IllegalArgumentException
	 *             [REPORT] If the invariant is violated
	 * 
	 * @thematicgrid Checking Operations
	 */
	private void checkInvariant(final List<Documentation> documentations)
	{
		if (documentations != null)
		{
			for (final Documentation documentation : documentations)
			{
				final Map<Addressee, String> addresseeDocs = documentation
						.getDocumentation();

				if ((addresseeDocs != null) && !addresseeDocs.isEmpty())
				{
					Preconditions
							.checkTrue(addresseeDocs.size() == 1,
									"Simplified Javadoc could not be generated for more than one addressee");

					Preconditions.checkTrue(AddresseeUtils.containsAddresseeName(
							AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, addresseeDocs),
							"Simplified Javadoc could only be generated for the "
									+ AddresseeConstants.MOST_IMPORTANT_ADDRESSEE + ".");
				}
			}
		}
	}

	/**
	 * @param addresseeName
	 *            [COMPARISON]
	 * @param docTexts
	 *            [SOURCE]
	 * @return [OBJECT] If no addressee is found, an empty string is returned.
	 * 
	 * @thematicgrid Searching Operations
	 */
	private String findDocTextByAddresseeName(final String addresseeName,
			final Map<Addressee, String> docTexts)
	{
		final Set<Entry<Addressee, String>> addressees = docTexts.entrySet();

		for (final Entry<Addressee, String> addresseeEntry : addressees)
		{
			final Addressee addressee = addresseeEntry.getKey();
			final String name = addressee.getName();

			if (addresseeName.equals(name))
			{
				if (addresseeEntry.getValue() != null)
				{
					return addresseeEntry.getValue().trim();
				}
				else
				{
					return StringUtils.EMPTY;
				}
			}
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Rule: the documentation has thematic role with given role-name.
	 * 
	 * @param documentation
	 *            [OBJECT]
	 * @param roleName
	 *            [COMPARISON]
	 * @return [REPORT] <code>true</code> if rule is fulfilled
	 * 
	 * @thematicgrid Checking Operations
	 */
	private boolean isRole(final Documentation documentation, final String roleName)
	{
		return notNull(documentation) && notNull(documentation.getThematicRole())
				&& notNull(documentation.getThematicRole().getName())
				&& roleName.equals(documentation.getThematicRole().getName());
	}

	/**
	 * Returns the documented text for the addressee "Developer".
	 * 
	 * @param documentation
	 *            [SOURCE]
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Getting Operations
	 */
	private String getDocText(final Documentation documentation)
	{
		if (documentation.getDocumentation() != null)
		{

			final StringBuffer docText = new StringBuffer();
			docText.append(findDocTextByAddresseeName(
					AddresseeConstants.MOST_IMPORTANT_ADDRESSEE,
					documentation.getDocumentation()));

			if (documentation.isErrorCase())
			{
				docText.append(" (" + Constants.ERROR_CASE_DOCUMENTATION_TEXT + ") ");
			}

			return StringUtils.SPACE + StringUtils.toString(docText.toString()).trim();
		}

		return StringUtils.EMPTY;
	}

	/**
	 * @param documentation
	 *            [SOURCE]
	 * @return [OBJECT] If the thematic role or its name is <code>null</code>, an empty
	 *         string is returned.
	 * 
	 * @thematicgrid Getting Operations
	 */
	private String getThematicRoleName(final Documentation documentation)
	{
		if (documentation.getThematicRole() != null)
		{
			final String roleName = documentation.getThematicRole().getName();
			if (roleName != null)
			{
				return roleName;
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Rule: the thematic role of the documentation is "RULE" and the thematic grid is
	 * "Checking Operations".
	 * 
	 * @param documentation
	 *            [SOURCE]
	 * @param referenceGridName
	 *            [COMPARISON]
	 * @return [REPORT]
	 * 
	 * @thematicgrid Checking Operations
	 */
	private boolean isRuleOfCheckingOperation(final Documentation documentation,
			final String referenceGridName)
	{
		return (isRole(documentation, ThematicRoleConstants.MANDATORY_ROLE_RULE) && ThematicGridConstants.THEMATIC_GRID_CHECKING_OPERATIONS
				.equalsIgnoreCase(referenceGridName));
	}

	/**
	 * Creates a {@link TagElement} for the thematic role of the documentation.
	 * 
	 * @param documentation
	 *            Not nullable.
	 * @subparam thematicRole.name [SOURCE] Used as taglet-name
	 * @param referenceGridName
	 *            [ATTRIBUTE] Used to determine the format of the {@link TagElement}
	 * @param jdocAST
	 *            [FACTORY] Used to instanciate the {@link TagElement}
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	private TagElement createTagElement(final Documentation documentation,
			final String referenceGridName, final AST jdocAST, final JavaMethod method)
	{
		final TagElement newTag = jdocAST.newTagElement();

		if (!isRole(documentation, ThematicRoleConstants.MANDATORY_ROLE_ACTION)
				&& !isRuleOfCheckingOperation(documentation, referenceGridName))
		{
			final String roleName = getThematicRoleName(documentation);
			final boolean isClassIntroduction = ThematicRoleConstants.MANDATORY_ROLE_NONE
					.equals(roleName) && (method == null);

			if ((roleName != null) && !StringUtils.EMPTY.equals(roleName.trim())
					&& !isClassIntroduction)
			{
				newTag.setTagName('@' + getThematicRoleName(documentation).toLowerCase());
			}
			else
			{
				newTag.setTagName(null);
			}
		}

		return newTag;
	}

	/**
	 * Rule: One documentation has the thematic role "ACTION".
	 * 
	 * @param documentations
	 *            [SOURCE]
	 * @return [REPORT] <code>true</code> if the rule fulfilled
	 * 
	 * @thematicgrid Checking Operations
	 */
	private boolean containsAction(final List<Documentation> documentations)
	{
		if (documentations != null)
		{
			for (final Documentation documentation : documentations)
			{
				if (isRole(documentation, ThematicRoleConstants.MANDATORY_ROLE_ACTION))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks for class-introduction sentence. An introduction sentence could only be in
	 * the first {@link Documentation} of the given list. The thematic role of this
	 * sentence is <code>null</code> or "NONE".
	 * 
	 * The introduction sentence is considered as class-introduction if the given method
	 * is <code>null</code>.
	 * 
	 * @param documentations
	 *             [OBJECT]
	 * @param method
	 *            [OBJECT] If this method is <code>null</code>, the documentations are
	 *            considered as clas-documentations.
	 * 
	 * @return [REPORT]
	 * 
	 * @thematicgrid Checking Operations
	 */
	public boolean containsClassDescription(final List<Documentation> documentations,
			final JavaMethod method)
	{
		final List<ThematicRole> roles = ServiceManager.getInstance()
				.getPersistenceService().loadThematicRoles();
		final ThematicRole noneRole = ThematicRoleUtils.findRoleByName(
				ThematicRoleConstants.MANDATORY_ROLE_NONE, roles);
		final boolean hasDocumentations = notNull(documentations)
				&& !documentations.isEmpty();

		if (hasDocumentations)
		{
			final ThematicRole currentRole = documentations.get(0).getThematicRole();
			boolean withoutThematicRole = (currentRole == null)
					|| noneRole.equals(currentRole);
			return (method == null) && withoutThematicRole;
		}

		return false;
	}

	/**
	 * Creates a Javadoc {@link TagElement} for each given documentation and adds it to
	 * the given <code>javadoc</code>.
	 * 
	 * @param documentations
	 *            [SOURCE]
	 * @param referenceGridName
	 *            [ATTRIBUTE] Used to determine the format of the {@link TagElement}
	 * @param javadoc
	 *            [DESTINATION]
	 * @param method
	 *            [ATTRIBUTE] Used to determine whether the new Javadoc-tags are attached
	 *            to a method or a class.
	 * 
	 * @thematicgrid Putting Operations
	 */
	private void addTaglessJavadocs(final List<Documentation> documentations,
			final String referenceGridName, final Javadoc javadoc, final JavaMethod method)
	{
		boolean hasIntroductionSentence = containsAction(documentations)
				|| containsClassDescription(documentations, method);

		@SuppressWarnings("unchecked")
		final List<TagElement> tags = javadoc.tags();

		for (int i = 0; i < documentations.size(); i++)
		{
			final Documentation documentation = documentations.get(i);

			final AST jdocAST = javadoc.getAST();
			final TagElement newTag = createTagElement(documentation, referenceGridName,
					jdocAST, method);

			@SuppressWarnings("unchecked")
			final List<ASTNode> fragments = newTag.fragments();
			final TextElement textElement = jdocAST.newTextElement();

			if (!hasIntroductionSentence)
			{
				String docText = getDocText(documentation).trim();

				if (newTag.getTagName() == null)
				{
					final String roleName = getThematicRoleName(documentation);
					String formattedRoleName = (roleName != null) ? roleName
							: StringUtils.EMPTY;

					if (!StringUtils.EMPTY.equals(formattedRoleName.trim()))
					{
						formattedRoleName = StringUtils.capitalizeFirstChar(roleName) + ':';
					}
					docText = formattedRoleName + StringUtils.SPACE + docText.trim();
				}

				textElement.setText(StringUtils.SPACE + docText.trim());
				hasIntroductionSentence = true;
			}
			else
			{
				textElement.setText(StringUtils.SPACE + getDocText(documentation).trim());
			}

			fragments.add(textElement);
			tags.add(newTag);
		}
	}

	/**
	 * @param javadoc
	 *            [FACTORY]
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	private TagElement createaEmptyJavadocRow(final Javadoc javadoc)
	{
		final AST jdocAST = javadoc.getAST();
		final TagElement newTag = jdocAST.newTagElement();

		@SuppressWarnings("unchecked")
		final List<ASTNode> fragments = newTag.fragments();

		final TextElement textElement = jdocAST.newTextElement();
		textElement.setText(StringUtils.EMPTY);
		fragments.add(textElement);

		return newTag;
	}

	private String[] extractLastPath(final String signatureElementPath,
			final Delimiters delimiters)
	{
		if (signatureElementPath.lastIndexOf(delimiters.pathDelimiter) > -1)
		{
			final String[] paths = signatureElementPath.split(delimiters
					.getQuotedPathDelimiter());
			return paths;
		}

		return new String[] { signatureElementPath };
	}

	private String extractIdentifier(final Documentation documentation)
	{
		final Delimiters delimiters = JavaParser.delimiters;
		final String signatureElementPath = documentation.getSignatureElementIdentifier();

		if (signatureElementPath != null)
		{
			final String[] path = extractLastPath(signatureElementPath, delimiters);
			final StringBuffer buffer = new StringBuffer();

			if (path.length > 2)
			{
				for (int i = 1; i < path.length; i++)
				{
					final String pathEntry = path[i];
					final String identifer = extractLastIdentifier(delimiters, pathEntry);

					buffer.append(identifer);
					buffer.append('.');
				}
			}
			else
			{
				final String identifer = extractLastIdentifier(delimiters,
						path[path.length - 1]);

				buffer.append(identifer);
				buffer.append('.');
			}

			// jakr: omit the last character ('.')
			return buffer.substring(0, buffer.toString().length() - 1).trim();
		}

		return StringUtils.EMPTY;
	}

	private String extractLastIdentifier(final Delimiters delimiters,
			final String pathEntry)
	{
		final String[] types = pathEntry.split(delimiters.getQuotedTypeDelimiter());
		final String identifers = types[0];

		final String[] identiferPath = identifers.split(delimiters
				.getQuotedNamespaceDelimiter());

		return identiferPath[identiferPath.length - 1];
	}

	private String deriveTagElementName(final String tagElementName, final int docNumber)
	{
		if (TagElement.TAG_RETURN.equals(tagElementName)
				|| TagElement.TAG_PARAM.equals(tagElementName)
				|| TagElement.TAG_THROWS.equals(tagElementName))
		{
			if (docNumber == 0)
			{
				return tagElementName;
			}
			else
			{
				return tagElementName + "info";
			}
		}
		else
		{
			return tagElementName;
		}
	}

	/**
	 * Creates Javadoc for the given parameter and its children.
	 * 
	 * @param parameter
	 *            [SOURCE]
	 * @param javadoc
	 *             [DESTINATION] This object is modified!
	 * @param level
	 *            Recursion level (starts with 0)
	 * @param tagElementName
	 *            [FORMAT] Name of the taglet ({@link TagElement#TAG_RETURN},
	 *            {@link TagElement#TAG_PARAM} or {@link TagElement#TAG_THROWS})
	 * @param subTagElementName
	 *            [FORMAT] Name of the sub-taglet (subreturn, subparam or subexception)
	 * 
	 * @thematicgrid Creating Operations
	 */
	private void createParamJavadoc(final List<Documentation> documentations,
			final Javadoc javadoc, final String tagElementName, final String identifier)
	{
		@SuppressWarnings("unchecked")
		final List<TagElement> tags = javadoc.tags();
		final AST jdocAST = javadoc.getAST();

		if ((documentations != null) && !documentations.isEmpty())
		{
			for (int i = 0; i < documentations.size(); i++)
			{
				final Documentation documentation = documentations.get(i);

				final TagElement newTag = jdocAST.newTagElement();
				newTag.setTagName(deriveTagElementName(tagElementName, i));

				@SuppressWarnings("unchecked")
				final List<ASTNode> fragments = newTag.fragments();
				final StringBuffer docText = new StringBuffer();

				if (!TagElement.TAG_RETURN.equals(tagElementName))
				{
					final String paramIdentifier = extractIdentifier(documentation);
					docText.append(StringUtils.toString(paramIdentifier).trim());
					docText.append(StringUtils.SPACE);
				}

				final ThematicRole role = documentation.getThematicRole();
				if (role != null)
				{
					docText.append(StringUtils.inBrackets(role.getName()));
				}

				docText.append(getDocText(documentation));

				final TextElement identifierElement = jdocAST.newTextElement();
				identifierElement.setText(StringUtils.SPACE + docText.toString().trim());

				fragments.add(identifierElement);
				tags.add(newTag);
			}
		}
		else if (JavadocUtils.isStandardJavadocTaglet(tagElementName))
		{
			// @param, @return and @throws should mentioned even if no documentation has
			// been created for the parameter, return-type or exception!
			final TagElement newTag = jdocAST.newTagElement();
			newTag.setTagName(tagElementName);

			@SuppressWarnings("unchecked")
			final List<ASTNode> fragments = newTag.fragments();

			final TextElement identifierElement = jdocAST.newTextElement();
			identifierElement.setText(StringUtils.SPACE
					+ StringUtils.toString(identifier));

			fragments.add(identifierElement);
			tags.add(newTag);
		}
	}

	private boolean containsTagElementWithIdentifier(final String tagName,
			final String identifier, final List<TagElement> additionalTagElements)
	{
		if (additionalTagElements != null)
		{
			for (final TagElement element : additionalTagElements)
			{
				final String currentTagName = element.getTagName();
				String currentIdentifier = JavadocUtils.readIdentifier(element);

				if ((currentTagName != null) && (currentTagName.equals(tagName)))
				{
					if (currentIdentifier == null)
					{
						@SuppressWarnings("unchecked")
						final String[] splittedDocText = JavadocUtils
								.readFragments(element.fragments(), 0).trim()
								.split(StringUtils.SPACE);

						currentIdentifier = splittedDocText[0];
					}

					if ((currentIdentifier != null)
							&& (currentIdentifier.equals(identifier)))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Creates a Javadoc taglet for the given SignatureElement.
	 * 
	 * @param documentations
	 *            [SOURCE]
	 * @param tagName
	 *            [FORMAT] Name of the taglet
	 * @param subTagName
	 *            [FORMAT]
	 * @param javadoc
	 *            [DESTINATION]
	 * 
	 * @thematicgrid Putting Operations
	 */
	private void appendJavadoc(final List<Documentation> documentations,
			final String tagName, final Javadoc javadoc, final String identifier,
			final List<TagElement> additionalTagElements)
	{
		if (!containsTagElementWithIdentifier(tagName, identifier, additionalTagElements))
		{
			createParamJavadoc(documentations, javadoc, tagName, identifier);
		}
	}

	private boolean isEmptyRow(final TagElement current)
	{
		@SuppressWarnings("unchecked")
		final List<ASTNode> fragments = current.fragments();

		if (fragments.size() == 1)
		{
			final ASTNode node = fragments.get(0);
			if (node.getNodeType() == ASTNode.TEXT_ELEMENT)
			{
				final TextElement textElement = (TextElement) node;
				final String text = textElement.getText();

				return (current.getTagName() == null)
						&& ((text == null) || StringUtils.EMPTY.equals(text.trim()));
			}
		}
		return false;
	}

	private boolean shouldInsertEmptyRow(final TagElement first, final TagElement second,
			final boolean firstHasIndex0)
	{
		final boolean introductionSentence = (first.getTagName() == null)
				&& (second.getTagName() != null);
		final boolean beforeParam = TagElement.TAG_PARAM.equals(second.getTagName());
		final boolean beforeReturn = TagElement.TAG_RETURN.equals(second.getTagName());
		final boolean beforeThrows = TagElement.TAG_THROWS.equals(second.getTagName());

		return !isEmptyRow(first) && !isEmptyRow(second)
				&& (introductionSentence || beforeParam || beforeReturn || beforeThrows);
	}

	private List<TagElement> insertEmptyRows(final List<TagElement> tags,
			final Javadoc javadoc)
	{
		if (tags != null)
		{
			final List<TagElement> resultTags = new ArrayList<TagElement>();

			if (!tags.isEmpty())
			{
				if (tags.size() > 1)
				{
					final int lastIndex = tags.size() - 1;

					// We start with i=1, because an empty row should never be added
					// before the first sentence (at index 0). Its the same with the last
					// tagElement. This is why our terminate-condition is "i < lastIndex"
					// and not "i <= lastIndex".
					for (int i = 0; i < lastIndex; i++)
					{
						final TagElement current = tags.get(i);
						final TagElement next = tags.get(i + 1);
						final TagElement emptyRow = createaEmptyJavadocRow(javadoc);

						resultTags.add(current);

						if (shouldInsertEmptyRow(current, next, i == 0))
						{
							resultTags.add(emptyRow);
						}
					}

					resultTags.add(tags.get(lastIndex));
				}
				else if (!isEmptyRow(tags.get(0)))
				{
					resultTags.add(tags.get(0));
					resultTags.add(createaEmptyJavadocRow(javadoc));
				}
				else
				{
					resultTags.add(tags.get(0));
				}
			}

			return resultTags;
		}

		return null;
	}

	private boolean isEmptyElement(final TagElement tagElement)
	{
		@SuppressWarnings("unchecked")
		final String text = JavadocUtils.readFragments(tagElement.fragments(), 0);
		final String tagName = tagElement.getTagName();

		final boolean textEmpty = (text == null) || text.trim().isEmpty();
		final boolean nameEmpty = (tagName == null) || (tagName.trim().isEmpty());

		return textEmpty && nameEmpty;
	}

	private String readFragments(final TagElement element)
	{
		@SuppressWarnings("unchecked")
		final String text = JavadocUtils.readFragments(element.fragments(), 0);
		return text.replaceAll(Pattern.quote(HTML_BR), JavadocUtils.NEW_LINE);
	}

	@SuppressWarnings("unchecked")
	private List<TagElement> mergeTagElements(final List<TagElement> unmergedTagElements)
	{
		final List<TagElement> mergedTagElements = new ArrayList<TagElement>();

		if ((unmergedTagElements != null) && !unmergedTagElements.isEmpty())
		{
			int elementIndex = 0;
			TagElement currentElement = unmergedTagElements.get(elementIndex);

			// Look for the first "not empty"-element.
			while (isEmptyElement(currentElement)
					&& (elementIndex < unmergedTagElements.size()))
			{
				elementIndex++;
			}

			// Was our search for a "not empty" element successful?
			if (!isEmptyElement(currentElement))
			{
				TagElement lastElement = currentElement;
				StringBuffer lastElementText = new StringBuffer();
				lastElementText.append(StringUtils.SPACE);
				lastElementText.append(readFragments(lastElement).trim());

				for (int i = elementIndex + 1; i < unmergedTagElements.size(); i++)
				{
					currentElement = unmergedTagElements.get(i);

					if (!isEmptyElement(currentElement))
					{
						if (currentElement.getTagName() == null)
						{
							if (!lastElementText.toString().endsWith(
									JavadocUtils.NEW_LINE))
							{
								lastElementText.append(JavadocUtils.NEW_LINE);
							}

							if (!lastElementText.toString().endsWith(StringUtils.SPACE))
							{
								lastElementText.append(StringUtils.SPACE);
							}
							final String curElemText = readFragments(currentElement)
									.trim();
							lastElementText.append(curElemText);
						}
						else
						{
							lastElement.fragments().clear();
							final TextElement newFragment = lastElement.getAST()
									.newTextElement();
							newFragment.setText(StringUtils.SPACE
									+ lastElementText.toString().trim());
							lastElement.fragments().add(newFragment);

							mergedTagElements.add(lastElement);

							lastElement = currentElement;
							lastElementText = new StringBuffer();
							lastElementText.append(readFragments(lastElement).trim());
						}
					}
				}

				lastElement.fragments().clear();
				final TextElement newFragment = lastElement.getAST().newTextElement();
				newFragment
						.setText(StringUtils.SPACE + lastElementText.toString().trim());
				lastElement.fragments().add(newFragment);
				mergedTagElements.add(lastElement);
			}
		}

		return mergedTagElements;
	}

	private void insertEmptyRows(final Javadoc javadoc)
	{
		@SuppressWarnings("unchecked")
		final List<TagElement> tags = javadoc.tags();
		final List<TagElement> tagsWithEmptyRows = insertEmptyRows(tags, javadoc);
		tags.clear();
		tags.addAll(tagsWithEmptyRows);
	}

	/**
	 * Creates Javadoc-representation for all {@link Documentation}s in
	 * <code>signatureElement</code> and it's children.
	 * 
	 * @format This implementation generates a simplified Javadoc which has a
	 *         100%compliance with the Javadoc conventions.
	 * @instrument To parse the Java and Javadoc code, the parser provided by the <a
	 *             href="http://www.eclipse.org/jdt/">Eclipse Java Development Tools</a>
	 *             is used.
	 * 
	 * @param documentations
	 *            [OBJECT]
	 * 
	 * @param tagName
	 *            [ATTRIBUTE] The of the Javadoc-tag of the given documentations (e.g.
	 *            '@param'). This parameter is nullable.
	 * 
	 * @param paramName
	 *            [ATTRIBUTE] Name of the corresponding Java-element. This parameter is
	 *            nullable.
	 * 
	 * @param thematicGridName
	 *            [ATTRIBUTE] Reference-grid of the given Java method. This parameter is
	 *            nullable.
	 * 
	 * @param javadoc
	 *            [DESTINATION]
	 * 
	 * @param additionalTagElements
	 *            [ATTRIBUTE] Existing Javadoc tags which are kept and not changed by
	 *            iDocIt!.
	 * 
	 * @param method
	 *            [DESTINATION] The given Javadoc belongs to this method.
	 * 
	 * @throws ParsingException
	 *             [NONE]
	 * @thematicgrid Putting Operations
	 */
	@Override
	public void appendDocsToJavadoc(final List<Documentation> documentations,
			final String tagName, final String paramName, final String thematicGridName,
			final Javadoc javadoc, final List<TagElement> additionalTagElements,
			final JavaMethod method) throws ParsingException
	{
		checkInvariant(documentations);

		if (tagName == null)
		{ // ACTION or new tags for specific thematic roles.
			addTaglessJavadocs(documentations, thematicGridName, javadoc, method);
		}
		else
		{
			appendJavadoc(documentations, tagName, javadoc, paramName,
					additionalTagElements);
		}

		@SuppressWarnings("unchecked")
		final List<TagElement> tags = javadoc.tags();
		final List<TagElement> copiedTags = new ArrayList<TagElement>();
		copiedTags.addAll(tags);
		tags.clear();

		List<TagElement> mergedTags = mergeTagElements(copiedTags);
		try
		{
			tags.addAll(splitTextInToFragments(mergedTags, javadoc.getAST()));
			insertEmptyRows(javadoc);
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.log(
					Level.WARNING,
					"ParserConfigurationException occured with "
							+ de.akra.idocit.java.utils.StringUtils.asStringSequence(
									documentations, tagName, paramName, thematicGridName,
									javadoc, additionalTagElements, method), e);

			throw new ParsingException(
					"The internal XML-parser could not be inialized. It said: "
							+ e.getLocalizedMessage());
		}
		catch (SAXException e)
		{
			LOGGER.log(
					Level.WARNING,
					"SAXException occured with "
							+ de.akra.idocit.java.utils.StringUtils.asStringSequence(
									documentations, tagName, paramName, thematicGridName,
									javadoc, additionalTagElements, method), e);

			throw new ParsingException(
					"The internal XML-parser caused an error. Maybe it is not able to parse your documentation texts. It said: "
							+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			LOGGER.log(
					Level.WARNING,
					"IOException occured with "
							+ de.akra.idocit.java.utils.StringUtils.asStringSequence(
									documentations, tagName, paramName, thematicGridName,
									javadoc, additionalTagElements, method), e);

			throw new ParsingException(
					"The internal XML-parser could not read your Java-file. It said: "
							+ e.getLocalizedMessage());
		}
	}

	private String[] appendBRTag(final String[] lines)
			throws ParserConfigurationException, SAXException, IOException
	{
		// A <br/>-tag at the end of a single line is not necessary, because there
		// couldn't be any formatting with linebreaks, which should be kept.
		if (lines.length >= 2)
		{
			// Don't look at the last element, because we want no <br>-tag there! The
			// reason is the same as above ;).
			for (int i = 0; i < lines.length - 1; i++)
			{
				lines[i] += HTML_BR;
			}
		}

		return lines;
	}

	@SuppressWarnings("unchecked")
	private List<TagElement> splitTextInToFragments(final List<TagElement> tagElements,
			final AST ast) throws ParserConfigurationException, SAXException, IOException
	{
		final List<TagElement> result = new ArrayList<TagElement>();

		for (final TagElement tagElement : tagElements)
		{
			final String unescapedDocText = JavadocUtils.readFragments(
					tagElement.fragments(), 0);
			final String tagIdentifier = tagElement.getTagName();

			if ((unescapedDocText != null) && !unescapedDocText.isEmpty())
			{
				String escapedDocText = JavadocUtils.escapeHtml4(unescapedDocText);

				TagElement newTagElement = ast.newTagElement();
				newTagElement.setTagName(tagIdentifier);
				List<ASTNode> fragments = newTagElement.fragments();

				TextElement identifierElement = ast.newTextElement();

				if (escapedDocText.lastIndexOf(HTML_BR) > -1)
				{
					String[] splittedText = escapedDocText.split(Pattern.quote(HTML_BR));
					splittedText = appendBRTag(splittedText);

					if (!splittedText[0].startsWith(StringUtils.SPACE))
					{
						splittedText[0] = StringUtils.SPACE + splittedText[0];
					}

					identifierElement.setText(splittedText[0]);
					fragments.add(identifierElement);
					result.add(newTagElement);

					if (splittedText.length > 1)
					{
						for (int i = 1; i < splittedText.length; i++)
						{
							newTagElement = ast.newTagElement();
							fragments = newTagElement.fragments();
							identifierElement = ast.newTextElement();
							identifierElement.setText(splittedText[i]);
							fragments.add(identifierElement);

							result.add(newTagElement);
						}
					}
				}
				else
				{
					if (!escapedDocText.startsWith(StringUtils.SPACE))
					{
						escapedDocText = StringUtils.SPACE + escapedDocText;
					}

					identifierElement.setText(escapedDocText);
					fragments.add(identifierElement);

					result.add(tagElement);
				}
			}
			else
			{
				result.add(tagElement);
			}
		}

		return result;
	}

	/**
	 * Generate the command line options for the Javadoc Generator from the given
	 * <code>roles</code> and from all {@link CustomTaglets}.
	 * 
	 * @param roles
	 *            [OBJECT] The {@link ThematicRole}s that shall be used to create the
	 *            command line options for the Javadoc Generator.
	 * @return the command line options.
	 * 
	 * @thematicgrid Creating Operations
	 */
	public String generateJavadocOptions(final List<ThematicRole> roles)
	{
		final StringBuilder sb = new StringBuilder(1000);

		for (final CustomTaglets ct : CustomTaglets.values())
		{
			sb.append(JAVADOC_OPTION_TAG);
			sb.append(ct.getName());
			sb.append(JAVADOC_OPTION_TAG_PLACEMENT);
			sb.append(ct.getHeader());
			sb.append(JAVADOC_OPTION_TAG_HEAD);
		}

		for (final ThematicRole role : roles)
		{
			sb.append(JAVADOC_OPTION_TAG);
			sb.append(role.getName().toLowerCase(Locale.ENGLISH));
			sb.append(JAVADOC_OPTION_TAG_PLACEMENT);
			sb.append(convertRoleName(role.getName()));
			sb.append(JAVADOC_OPTION_TAG_HEAD);
		}

		if (sb.length() > 0)
		{
			// delete last space
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Convert the <code>roleName</code> to an appropriate name for the Javadoc export.
	 * Role names are split by '_'. Each resulting word is formatted, that the first
	 * character is upper case and all other characters in the word are lower case. Then
	 * the words are concatenated by a space.
	 * <p>
	 * Example: <code>"TIME_TO_LIVE" => "Time To Live"</code>
	 * </p>
	 * 
	 * @param roleName
	 *            [OBJECT]
	 * @return appropriate name for the Javadoc header. If <code>roleName</code> is
	 *         <code>null</code> or empty an empty string is returned.
	 */
	private String convertRoleName(final String roleName)
	{
		if (roleName != null && roleName.length() > 0)
		{
			final StringBuilder sb = new StringBuilder(20);
			final String[] words = roleName.split("_");
			for (final String word : words)
			{
				if (word.length() > 0)
				{
					sb.append(word.substring(0, 1).toUpperCase(Locale.ENGLISH));
					sb.append(word.substring(1).toLowerCase(Locale.ENGLISH));
					sb.append(StringUtils.SPACE);
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		LOGGER.log(Level.WARNING, "No role name to convert.");
		return StringUtils.EMPTY;
	}
}
