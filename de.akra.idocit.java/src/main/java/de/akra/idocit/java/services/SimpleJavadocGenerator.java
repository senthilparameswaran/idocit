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
import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.utils.JavadocUtils;

public class SimpleJavadocGenerator implements IJavadocGenerator
{
	private static final String EMPTY_STR = "";

	private static final String NEW_LINE = System.getProperty("line.separator");

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
	private void checkInvariant(List<Documentation> documentations)
	{
		if (documentations != null)
		{
			for (Documentation documentation : documentations)
			{
				Map<Addressee, String> addresseeDocs = documentation.getDocumentation();

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
	private String findDocTextByAddresseeName(String addresseeName,
			Map<Addressee, String> docTexts)
	{
		Set<Entry<Addressee, String>> addressees = docTexts.entrySet();

		for (Entry<Addressee, String> addresseeEntry : addressees)
		{
			Addressee addressee = addresseeEntry.getKey();
			String name = addressee.getName();

			if (addresseeName.equals(name))
			{
				return addresseeEntry.getValue();
			}
		}

		return EMPTY_STR;
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
	private boolean isRole(Documentation documentation, String roleName)
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
	private String getDocText(Documentation documentation)
	{

		if (documentation.getDocumentation() != null)
		{
			String docText = findDocTextByAddresseeName(
					AddresseeConstants.MOST_IMPORTANT_ADDRESSEE,
					documentation.getDocumentation());
			return StringUtils.toString(docText).trim();
		}

		return EMPTY_STR;
	}

	/**
	 * @param documentation
	 *            [SOURCE]
	 * @return [OBJECT] If the thematic role or its name is <code>null</code>, an empty
	 *         string is returned.
	 * 
	 * @thematicgrid Getting Operations
	 */
	private String getThematicRoleName(Documentation documentation)
	{
		if (documentation.getThematicRole() != null)
		{
			String roleName = documentation.getThematicRole().getName();

			if (roleName != null)
			{
				return roleName;
			}
		}

		return EMPTY_STR;
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
	private boolean isRuleOfCheckingOperation(Documentation documentation,
			String referenceGridName)
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
	private TagElement createTagElement(Documentation documentation,
			String referenceGridName, AST jdocAST, JavaMethod method)
	{
		TagElement newTag = jdocAST.newTagElement();

		if (!isRole(documentation, ThematicRoleConstants.MANDATORY_ROLE_ACTION)
				&& !isRuleOfCheckingOperation(documentation, referenceGridName))
		{
			String roleName = getThematicRoleName(documentation);
			boolean isClassIntroduction = ThematicRoleConstants.MANDATORY_ROLE_NONE
					.equals(roleName) && (method == null);

			if ((roleName != null) && !EMPTY_STR.equals(roleName.trim())
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
	private boolean containsAction(List<Documentation> documentations)
	{
		if (documentations != null)
		{
			for (Documentation documentation : documentations)
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
	public boolean containsClassDescription(List<Documentation> documentations,
			JavaMethod method)
	{
		List<ThematicRole> roles = ServiceManager.getInstance().getPersistenceService()
				.loadThematicRoles();
		ThematicRole noneRole = ThematicRoleUtils.findRoleByName(
				ThematicRoleConstants.MANDATORY_ROLE_NONE, roles);
		boolean hasDocumentations = notNull(documentations) && !documentations.isEmpty();

		if (hasDocumentations)
		{
			ThematicRole currentRole = documentations.get(0).getThematicRole();
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
	private void addTaglessJavadocs(List<Documentation> documentations,
			String referenceGridName, Javadoc javadoc, JavaMethod method)
	{
		boolean hasIntroductionSentence = containsAction(documentations)
				|| containsClassDescription(documentations, method);

		@SuppressWarnings("unchecked")
		List<TagElement> tags = javadoc.tags();

		for (int i = 0; i < documentations.size(); i++)
		{
			Documentation documentation = documentations.get(i);

			AST jdocAST = javadoc.getAST();
			TagElement newTag = createTagElement(documentation, referenceGridName,
					jdocAST, method);

			@SuppressWarnings("unchecked")
			List<ASTNode> fragments = newTag.fragments();
			TextElement textElement = jdocAST.newTextElement();

			if (!hasIntroductionSentence)
			{
				String roleName = getThematicRoleName(documentation);
				String formattedRoleName = (roleName != null) ? roleName : EMPTY_STR;

				if (!EMPTY_STR.equals(formattedRoleName.trim()))
				{
					formattedRoleName = StringUtils.capitalizeFirstChar(roleName) + ':';
				}

				String docText = " " + getDocText(documentation).trim();

				if (newTag.getTagName() == null)
				{
					docText = formattedRoleName + docText;
				}

				textElement.setText(docText);
				hasIntroductionSentence = true;
			}
			else
			{
				textElement.setText(' ' + getDocText(documentation));
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
	private TagElement createaEmptyJavadocRow(Javadoc javadoc)
	{
		AST jdocAST = javadoc.getAST();
		TagElement newTag = jdocAST.newTagElement();

		@SuppressWarnings("unchecked")
		List<ASTNode> fragments = newTag.fragments();

		TextElement textElement = jdocAST.newTextElement();
		textElement.setText(EMPTY_STR);
		fragments.add(textElement);

		return newTag;
	}

	private String[] extractLastPath(String signatureElementPath, Delimiters delimiters)
	{
		if (signatureElementPath.lastIndexOf(delimiters.pathDelimiter) > -1)
		{
			String[] paths = signatureElementPath.split(delimiters
					.getQuotedPathDelimiter());

			return paths;
		}

		return new String[] { signatureElementPath };
	}

	private String extractIdentifier(Documentation documentation)
	{
		Delimiters delimiters = JavaParser.delimiters;
		String signatureElementPath = documentation.getSignatureElementIdentifier();

		if (signatureElementPath != null)
		{
			String[] path = extractLastPath(signatureElementPath, delimiters);
			StringBuffer buffer = new StringBuffer();

			if (path.length > 2)
			{
				for (int i = 1; i < path.length; i++)
				{
					String pathEntry = path[i];
					String identifer = extractLastIdentifier(delimiters, pathEntry);

					buffer.append(identifer);
					buffer.append('.');
				}
			}
			else
			{
				String identifer = extractLastIdentifier(delimiters,
						path[path.length - 1]);

				buffer.append(identifer);
				buffer.append('.');
			}

			// jakr: omit the last character ('.')
			return buffer.substring(0, buffer.toString().length() - 1);
		}

		return EMPTY_STR;
	}

	private String extractLastIdentifier(Delimiters delimiters, String pathEntry)
	{
		String[] types = pathEntry.split(delimiters.getQuotedTypeDelimiter());
		String identifers = types[0];

		String[] identiferPath = identifers.split(delimiters
				.getQuotedNamespaceDelimiter());

		return identiferPath[identiferPath.length - 1];
	}

	private String deriveTagElementName(String tagElementName, int docNumber)
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
	private void createParamJavadoc(List<Documentation> documentations, Javadoc javadoc,
			String tagElementName, String identifier)
	{
		@SuppressWarnings("unchecked")
		List<TagElement> tags = javadoc.tags();
		AST jdocAST = javadoc.getAST();

		if ((documentations != null) && !documentations.isEmpty())
		{
			for (int i = 0; i < documentations.size(); i++)
			{
				Documentation documentation = documentations.get(i);

				TagElement newTag = jdocAST.newTagElement();
				newTag.setTagName(deriveTagElementName(tagElementName, i));

				@SuppressWarnings("unchecked")
				List<ASTNode> fragments = newTag.fragments();

				ThematicRole role = documentation.getThematicRole();

				StringBuffer docText = new StringBuffer();

				if (!TagElement.TAG_RETURN.equals(tagElementName))
				{
					String paramIdentifier = extractIdentifier(documentation);
					docText.append(StringUtils.toString(paramIdentifier));
					docText.append(' ');
				}

				if (role != null)
				{
					docText.append(StringUtils.inBrackets(role.getName()));
					docText.append(' ');
				}

				docText.append(getDocText(documentation));

				TextElement identifierElement = jdocAST.newTextElement();
				identifierElement.setText(' ' + docText.toString().trim());

				fragments.add(identifierElement);

				tags.add(newTag);
			}
		}
		else if (JavadocUtils.isStandardJavadocTaglet(tagElementName))
		{
			// @param, @return and @throws should mentioned even if no documentation has
			// been created for the parameter, return-type or exception!
			TagElement newTag = jdocAST.newTagElement();
			newTag.setTagName(tagElementName);

			@SuppressWarnings("unchecked")
			List<ASTNode> fragments = newTag.fragments();

			TextElement identifierElement = jdocAST.newTextElement();
			identifierElement.setText(' ' + StringUtils.toString(identifier));

			fragments.add(identifierElement);
			tags.add(newTag);
		}
	}

	private boolean containsTagElementWithIdentifier(String tagName, String identifier,
			List<TagElement> additionalTagElements)
	{
		if (additionalTagElements != null)
		{
			for (TagElement element : additionalTagElements)
			{
				String currentTagName = element.getTagName();
				String currentIdentifier = JavadocUtils.readIdentifier(element);

				if ((currentTagName != null) && (currentTagName.equals(tagName)))
				{
					if (currentIdentifier == null)
					{
						String[] splittedDocText = JavadocUtils
								.readFragments(element.fragments(), 0).trim().split(" ");

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
	private void appendJavadoc(List<Documentation> documentations, String tagName,
			Javadoc javadoc, String identifier, List<TagElement> additionalTagElements)
	{
		if (!containsTagElementWithIdentifier(tagName, identifier, additionalTagElements))
		{
			createParamJavadoc(documentations, javadoc, tagName, identifier);
		}
	}

	private boolean isEmptyRow(TagElement current)
	{
		List<ASTNode> fragments = current.fragments();

		if (fragments.size() == 1)
		{
			ASTNode node = fragments.get(0);
			if (node.getNodeType() == ASTNode.TEXT_ELEMENT)
			{
				TextElement textElement = (TextElement) node;
				String text = textElement.getText();

				return (current.getTagName() == null)
						&& ((text == null) || EMPTY_STR.equals(text.trim()));
			}

			return false;
		}

		return false;
	}

	private boolean shouldInsertEmptyRow(TagElement first, TagElement second,
			boolean firstHasIndex0)
	{
		boolean introductionSentence = (first.getTagName() == null)
				&& (second.getTagName() != null);
		boolean beforeParam = TagElement.TAG_PARAM.equals(second.getTagName());
		boolean beforeReturn = TagElement.TAG_RETURN.equals(second.getTagName());
		boolean beforeThrows = TagElement.TAG_THROWS.equals(second.getTagName());

		return !isEmptyRow(first) && !isEmptyRow(second) && (introductionSentence || // betweenIntroductionSentenceAndAnyTag
																						// ||
				beforeParam || beforeReturn || beforeThrows);
	}

	private List<TagElement> insertEmptyRows(List<TagElement> tags, Javadoc javadoc)
	{
		if (tags != null)
		{
			List<TagElement> resultTags = new ArrayList<TagElement>();

			if (!tags.isEmpty())
			{
				if (tags.size() > 1)
				{
					int lastIndex = tags.size() - 1;

					// We start with i=1, because an empty row should never be added
					// before the first sentence (at index 0). Its the same with the last
					// tagElement. This is why our terminate-condition is "i < lastIndex"
					// and not "i <= lastIndex".
					for (int i = 0; i < lastIndex; i++)
					{
						TagElement current = tags.get(i);
						TagElement next = tags.get(i + 1);
						TagElement emptyRow = createaEmptyJavadocRow(javadoc);

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

	private boolean isEmptyElement(TagElement tagElement)
	{
		String text = JavadocUtils.readFragments(tagElement.fragments(), 0);
		String tagName = tagElement.getTagName();

		boolean textEmpty = (text == null) || text.trim().isEmpty();
		boolean nameEmpty = (tagName == null) || (tagName.trim().isEmpty());

		return textEmpty && nameEmpty;
	}

	private String readFragments(TagElement element)
	{
		String text = JavadocUtils.readFragments(element.fragments(), 0);
		return text.replaceAll(Pattern.quote(HTML_BR), NEW_LINE);
	}

	private List<TagElement> mergeTagElements(List<TagElement> unmergedTagElements)
	{
		List<TagElement> mergedTagElements = new ArrayList<TagElement>();

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
				lastElementText.append(readFragments(lastElement));

				for (int i = elementIndex + 1; i < unmergedTagElements.size(); i++)
				{
					currentElement = unmergedTagElements.get(i);

					if (!isEmptyElement(currentElement))
					{
						if (currentElement.getTagName() == null)
						{
							if (!lastElementText.toString().endsWith(NEW_LINE))
							{
								lastElementText.append(NEW_LINE);
							}

							lastElementText.append(' ');
							String curElemText = readFragments(currentElement);
							lastElementText.append(curElemText);
						}
						else
						{
							lastElement.fragments().clear();
							TextElement newFragment = lastElement.getAST()
									.newTextElement();
							newFragment.setText(lastElementText.toString());
							lastElement.fragments().add(newFragment);

							mergedTagElements.add(lastElement);

							lastElement = currentElement;
							lastElementText = new StringBuffer();
							lastElementText.append(readFragments(lastElement));
						}
					}
				}

				lastElement.fragments().clear();
				TextElement newFragment = lastElement.getAST().newTextElement();
				newFragment.setText(lastElementText.toString());
				lastElement.fragments().add(newFragment);
				mergedTagElements.add(lastElement);
			}
		}

		return mergedTagElements;
	}

	private void insertEmptyRows(Javadoc javadoc)
	{
		List<TagElement> tags = javadoc.tags();
		List<TagElement> tagsWithEmptyRows = insertEmptyRows(tags, javadoc);
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
	 * @param). This parameter is nullable.
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
	public void appendDocsToJavadoc(List<Documentation> documentations, String tagName,
			String paramName, String thematicGridName, Javadoc javadoc,
			List<TagElement> additionalTagElements, JavaMethod method)
			throws ParsingException
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

		List<TagElement> tags = javadoc.tags();
		List<TagElement> copiedTags = new ArrayList<TagElement>();
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

	private String[] appendBRTag(String[] lines) throws ParserConfigurationException,
			SAXException, IOException
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

	private List<TagElement> splitTextInToFragments(List<TagElement> tagElements, AST ast)
			throws ParserConfigurationException, SAXException, IOException
	{
		List<TagElement> result = new ArrayList<TagElement>();

		for (TagElement tagElement : tagElements)
		{
			final String unescapedDocText = JavadocUtils.readFragments(
					tagElement.fragments(), 0);
			final String tagIdentifier = tagElement.getTagName();

			if ((unescapedDocText != null) && !unescapedDocText.isEmpty())
			{
				final String escapedDocText = JavadocUtils.escapeHtml4(unescapedDocText);

				TagElement newTagElement = ast.newTagElement();
				newTagElement.setTagName(tagIdentifier);
				List<ASTNode> fragments = newTagElement.fragments();

				TextElement identifierElement = ast.newTextElement();

				if (escapedDocText.lastIndexOf(HTML_BR) > -1)
				{
					String[] splittedText = escapedDocText.split(Pattern.quote(HTML_BR));
					splittedText = appendBRTag(splittedText);

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
	public String generateJavadocOptions(List<ThematicRole> roles)
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
					sb.append(" ");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		LOGGER.log(Level.WARNING, "No role name to convert.");
		return "";
	}
}
