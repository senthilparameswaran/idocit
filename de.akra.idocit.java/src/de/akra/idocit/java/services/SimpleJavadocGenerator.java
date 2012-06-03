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
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.SAXException;

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
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.utils.JavadocUtils;

public class SimpleJavadocGenerator implements IJavadocGenerator
{
	// TODO: Move this constant to a special constant-class.
	public static final String THEMATIC_GRID_CHECKING_OPERATIONS = "Checking Operations";

	public static final SimpleJavadocGenerator INSTANCE;

	private static final Logger LOGGER = Logger.getLogger(SimpleJavadocGenerator.class
			.getName());

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

		return "";
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

		return "";
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

		return "";
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
		return (isRole(documentation, ThematicRoleConstants.MANDATORY_ROLE_RULE) && THEMATIC_GRID_CHECKING_OPERATIONS
				.equalsIgnoreCase(referenceGridName));
	}

	/**
	 * Creates a {@link TagElement} for the thematic role of the documentation.
	 * 
	 * @param documentation
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

			if ((roleName != null) && !"".equals(roleName.trim()) && !isClassIntroduction)
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

	private boolean containsClassDescription(List<Documentation> documentations,
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
	 * 
	 * @thematicgrid Putting Operations
	 */
	private void addTaglessJavadocs(List<Documentation> documentations,
			String referenceGridName, Javadoc javadoc, JavaMethod method)
	{
		checkInvariant(documentations);
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
				String formattedRoleName = (roleName != null) ? roleName : "";

				if (!"".equals(formattedRoleName.trim()))
				{
					formattedRoleName = StringUtils.capitalizeFirstChar(roleName) + ':';
				}

				String docText = (formattedRoleName + " " + getDocText(documentation))
						.trim();
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
		@SuppressWarnings("unchecked")
		AST jdocAST = javadoc.getAST();
		TagElement newTag = jdocAST.newTagElement();

		@SuppressWarnings("unchecked")
		List<ASTNode> fragments = newTag.fragments();

		TextElement textElement = jdocAST.newTextElement();
		textElement.setText("");
		fragments.add(textElement);

		return newTag;
	}

	private String extractLastPath(String signatureElementPath, Delimiters delimiters)
	{
		if (signatureElementPath.lastIndexOf(delimiters.pathDelimiter) > -1)
		{
			String[] paths = signatureElementPath.split(delimiters
					.getQuotedPathDelimiter());

			return paths[paths.length - 1];
		}

		return signatureElementPath;
	}

	private String extractIdentifier(Documentation documentation)
	{
		Delimiters delimiters = JavaParser.delimiters;
		String signatureElementPath = documentation.getSignatureElementIdentifier();

		if (signatureElementPath != null)
		{
			String lastPath = extractLastPath(signatureElementPath, delimiters);

			String[] types = lastPath.split(delimiters.getQuotedTypeDelimiter());
			String identifers = types[0];

			String[] identiferPath = identifers.split(delimiters
					.getQuotedNamespaceDelimiter());

			return identiferPath[identiferPath.length - 1];
		}

		return "";
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
		checkInvariant(documentations);

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
			checkInvariant(documentations);

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
						&& ((text == null) || "".equals(text.trim()));
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

		return text.replaceAll(Pattern.quote("<br/>"), "\n");
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
							if (!lastElementText.toString().endsWith("\n"))
							{
								lastElementText.append('\n');
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
	 * @param documentations
	 *            [SOURCE]
	 * @param tagName
	 *            Name of the current Javadoc-section to create additional Javadocs for
	 * @param paramName
	 *            [ATTRIBUTE] Name of the corresponding Java-element
	 * @param referenceGridName
	 *            [ATTRIBUTE]
	 * @param javadoc
	 * 
	 *            [DESTINATION] This object is modified.
	 * 
	 * @format This implementation generates a simplified Javadoc which has a 100%
	 *         compliance with the Javadoc conventions.
	 * 
	 * @thematicgrid Putting Operations
	 */
	@Override
	public void appendDocsToJavadoc(List<Documentation> documentations, String tagName,
			String paramName, String thematicGridName, Javadoc javadoc,
			List<TagElement> additionalTagElements, JavaMethod method)
			throws ParsingException
	{
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
				lines[i] += "<br/>";
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
			String unescapedDocText = JavadocUtils.readFragments(tagElement.fragments(),
					0);
			String tagIdentifier = tagElement.getTagName();

			if ((unescapedDocText != null) && !unescapedDocText.isEmpty())
			{
				String escapedDocText = JavadocUtils.escapeHtml4(unescapedDocText);

				TagElement newTagElement = ast.newTagElement();
				newTagElement.setTagName(tagIdentifier);
				List<ASTNode> fragments = newTagElement.fragments();

				TextElement identifierElement = ast.newTextElement();

				if (escapedDocText.lastIndexOf("<br/>") > -1)
				{
					String[] splittedText = escapedDocText.split(Pattern.quote("<br/>"));
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
}
