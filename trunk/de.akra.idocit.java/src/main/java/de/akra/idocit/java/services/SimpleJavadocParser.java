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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.core.constants.AddresseeConstants;
import de.akra.idocit.core.constants.ThematicRoleConstants;
import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavaParameter;
import de.akra.idocit.java.utils.JavadocUtils;

public final class SimpleJavadocParser extends AbsJavadocParser
{
	private static final String ERR_MSG_NOT_EXISTING_RETURN_TYPE_IS_DOCUMENTED = "For method '%s' there is documented a return type although it does not exist. Please delete the '@return ...' tag from Javadoc comment and open the file again.";
	private static final String SUB_RETURN_PATTERN = CustomTaglets.SUB_RETURN
			.getTagName() + "\\s*";
	private static final String SUB_PARAM_PATTERN = CustomTaglets.SUB_PARAM.getTagName()
			+ "\\s*";
	private static final String THEMATIC_GRID_PATTERN = CustomTaglets.THEMATIC_GRID
			.getTagName() + "\\s*";

	private class StructuredJavaDoc
	{
		private String paramName;
		private String roleName;
		private String docText;

		public String getRoleName()
		{
			return roleName;
		}

		public void setRoleName(String roleName)
		{
			this.roleName = roleName;
		}

		public String getDocText()
		{
			return docText;
		}

		public void setDocText(String docText)
		{
			this.docText = docText;
		}

		public String getParamName()
		{
			return paramName;
		}

		public void setParamName(String paramName)
		{
			this.paramName = paramName;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((docText == null) ? 0 : docText.hashCode());
			result = prime * result + ((paramName == null) ? 0 : paramName.hashCode());
			result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StructuredJavaDoc other = (StructuredJavaDoc) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (docText == null)
			{
				if (other.docText != null)
					return false;
			}
			else if (!docText.equals(other.docText))
				return false;
			if (paramName == null)
			{
				if (other.paramName != null)
					return false;
			}
			else if (!paramName.equals(other.paramName))
				return false;
			if (roleName == null)
			{
				if (other.roleName != null)
					return false;
			}
			else if (!roleName.equals(other.roleName))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("StructuredJavaDoc [paramName=");
			builder.append(paramName);
			builder.append(", roleName=");
			builder.append(roleName);
			builder.append(", docText=");
			builder.append(docText);
			builder.append("]");
			return builder.toString();
		}

		private SimpleJavadocParser getOuterType()
		{
			return SimpleJavadocParser.this;
		}
	}

	private class AnnotatedDocumentation
	{
		private String docText;
		private String identifier;
		private ThematicRole thematicRole;

		public String getDocText()
		{
			return docText;
		}

		public void setDocText(String docText)
		{
			this.docText = docText;
		}

		public ThematicRole getThematicRole()
		{
			return thematicRole;
		}

		public void setThematicRole(ThematicRole thematicRole)
		{
			this.thematicRole = thematicRole;
		}

		public String getIdentifier()
		{
			return identifier;
		}

		public void setIdentifier(String identifier)
		{
			this.identifier = identifier;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((docText == null) ? 0 : docText.hashCode());
			result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
			result = prime * result
					+ ((thematicRole == null) ? 0 : thematicRole.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnnotatedDocumentation other = (AnnotatedDocumentation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (docText == null)
			{
				if (other.docText != null)
					return false;
			}
			else if (!docText.equals(other.docText))
				return false;
			if (identifier == null)
			{
				if (other.identifier != null)
					return false;
			}
			else if (!identifier.equals(other.identifier))
				return false;
			if (thematicRole == null)
			{
				if (other.thematicRole != null)
					return false;
			}
			else if (!thematicRole.equals(other.thematicRole))
				return false;
			return true;
		}

		private SimpleJavadocParser getOuterType()
		{
			return SimpleJavadocParser.this;
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("AnnotatedDocumentation [docText=");
			builder.append(docText);
			builder.append(", identifier=");
			builder.append(identifier);
			builder.append(", thematicRole=");
			builder.append(thematicRole);
			builder.append("]");
			return builder.toString();
		}
	}

	public static final SimpleJavadocParser INSTANCE;
	private static final Pattern SPLIT_JAVADOC_REGEXP = Pattern
			.compile("(.*?)\\[(.*?)\\](.*?)$");
	private static final Logger log = Logger.getLogger(SimpleJavadocParser.class
			.getName());

	static
	{
		INSTANCE = new SimpleJavadocParser();
	}

	/**
	 * Private default constructor due to Singleton Pattern.
	 */
	private SimpleJavadocParser()
	{

	}

	private StructuredJavaDoc splitJavadocText(String tagName, String docText,
			String referenceGridName)
	{
		docText = docText.trim();
		final StructuredJavaDoc structuredJavaDoc = new StructuredJavaDoc();

		final Matcher matcher = SPLIT_JAVADOC_REGEXP.matcher(docText);
		if (matcher.find())
		{
			structuredJavaDoc.setParamName(matcher.group(1).trim());
			structuredJavaDoc.setRoleName(matcher.group(2));
			structuredJavaDoc.setDocText(matcher.group(3).trim());
		}
		else
		{
			log.info("The Javadoc \""
					+ String.valueOf(docText)
					+ "\" seems not to contain a thematic role. I will return the whole docText as content and no thematic role name.");

			if (JavadocUtils.isParam(tagName))
			{
				structuredJavaDoc.setDocText(docText);
			}
			else
			{
				if (ThematicGridConstants.THEMATIC_GRID_CHECKING_OPERATIONS
						.equals(referenceGridName))
				{
					int startIndex = docText.toLowerCase().indexOf(
							ThematicRoleConstants.MANDATORY_ROLE_RULE.toLowerCase());

					if (startIndex >= 0)
					{
						// jakr: + 1 is needed because of the ':'
						startIndex += ThematicRoleConstants.MANDATORY_ROLE_RULE.length() + 1;
						docText = docText.substring(startIndex).trim();
						structuredJavaDoc
								.setRoleName(ThematicRoleConstants.MANDATORY_ROLE_RULE);
					}
					else
					{
						structuredJavaDoc.setRoleName(null);
					}
				}
				else
				{
					structuredJavaDoc.setRoleName(null);
				}

				structuredJavaDoc.setDocText(docText);
				structuredJavaDoc.setParamName(null);
			}
		}

		return structuredJavaDoc;
	}

	private StructuredJavaDoc readRoleName(final TagElement tagElement,
			final String referenceGridName, final String tagText, final JavaMethod method)
	{
		final String tagName = tagElement.getTagName();
		final StructuredJavaDoc structuredJavaDoc = splitJavadocText(tagName, tagText,
				referenceGridName);

		if (tagName != null && !JavadocUtils.isStandardJavadocTaglet(tagName)
				&& !JavadocUtils.isIdocitJavadocTaglet(tagName))
		{
			// When using tag names, remove the '@' at the beginning.
			structuredJavaDoc.setRoleName(tagName.substring(1));
		}
		else if (structuredJavaDoc.getRoleName() == null
				&& (JavadocUtils.isStandardJavadocTaglet(tagName) || JavadocUtils
						.isIdocitJavadocTaglet(tagName)))
		{
			// thematic role was not found and the taglet is also no thematic role
			// structuredJavaDoc.setRoleName(ThematicRoleConstants.MANDATORY_ROLE_NONE);
		}
		else if (tagName == null)
		{
			// Seems to be the general description about the method.
			// Must be ACTION or RULE (depends on the reference grid)
			if (ThematicGridConstants.THEMATIC_GRID_CHECKING_OPERATIONS
					.equals(referenceGridName))
			{
				structuredJavaDoc.setRoleName(ThematicRoleConstants.MANDATORY_ROLE_RULE);
			}
			else if (method != null)
			{
				structuredJavaDoc
						.setRoleName(ThematicRoleConstants.MANDATORY_ROLE_ACTION);
			}
			else
			{
				structuredJavaDoc.setRoleName(ThematicRoleConstants.MANDATORY_ROLE_NONE);
			}
		}

		return structuredJavaDoc;
	}

	/**
	 * Extracts the name of the thematic role and the documentation-text from the given
	 * tag element and searches for it in the given list of roles.
	 * 
	 * @param tagElement
	 *            [SOURCE] To derive the thematic role name
	 * @param thematicRoles
	 *            [SOURCE] To find the thematic role object
	 * @param referenceGridName
	 *            [ATTRIBUTE] To derive the thematic role name
	 * 
	 * @return [OBJECT]
	 */
	private AnnotatedDocumentation readDocumentationAndThematicRole(
			final TagElement tagElement, final List<ThematicRole> thematicRoles,
			final String referenceGridName, final JavaMethod method)
	{
		@SuppressWarnings("unchecked")
		final String origTagText = JavadocUtils.readFragments(tagElement.fragments(), 0);
		final StructuredJavaDoc structuredJavaDoc = readRoleName(tagElement,
				referenceGridName, origTagText, method);
		final String roleName = structuredJavaDoc.getRoleName();
		final ThematicRole role = ThematicRoleUtils.findRoleByName(roleName,
				thematicRoles);

		if (role == null)
		{
			log.info("No thematic role found for tag element: "
					+ String.valueOf(tagElement));
		}

		final AnnotatedDocumentation result = new AnnotatedDocumentation();
		result.setThematicRole(role);
		result.setDocText(structuredJavaDoc.getDocText());
		result.setIdentifier(structuredJavaDoc.getParamName());

		return result;
	}

	private String readParentParamterName(Javadoc javadoc, TagElement childTagElem,
			JavaMethod method)
	{
		List<?> tags = javadoc.tags();

		for (int i = 0; i < tags.size(); i++)
		{
			Object curTagElem = tags.get(i);
			if (curTagElem == childTagElem)
			{
				int prev = i - 1;

				while (prev >= 0)
				{
					final TagElement parentParamTag = (TagElement) tags.get(prev);

					// In a @return-tag no identifier is mentioned. So must not test
					// against @return here.
					if (TagElement.TAG_PARAM.equals(parentParamTag.getTagName())
							|| TagElement.TAG_THROWS.equals(parentParamTag.getTagName()))
					{
						@SuppressWarnings("unchecked")
						final String parentText = JavadocUtils.readFragments(
								parentParamTag.fragments(), 0);

						if (parentText.contains(StringUtils.SPACE))
						{
							return parentText.split(StringUtils.SPACE)[0];
						}

						return parentText;
					}
					else if (TagElement.TAG_RETURN.equals(parentParamTag.getTagName()))
					{
						final JavaParameter returnType = (JavaParameter) method
								.getOutputParameters().getParameters().get(0);

						return returnType.getIdentifier();
					}

					prev--;
				}
			}
		}

		return StringUtils.EMPTY;
	}

	private JavaParameter findParameterByName(List<? extends Parameter> parameters,
			String name)
	{
		if (parameters != null)
		{
			for (Parameter param : parameters)
			{
				if (name.equals(param.getIdentifier()))
				{
					return (JavaParameter) param;
				}
			}
		}

		return null;
	}

	private String[] extractIdentifierChain(String subParamText)
	{
		if (subParamText.contains("."))
		{
			String[] chain = subParamText.split("\\.");

			for (int i = 0; i < chain.length; i++)
			{
				chain[i] = chain[i].trim();
			}

			return chain;
		}
		else if (subParamText.contains(StringUtils.SPACE))
		{
			// It could be that there is only the identifier and a description, e.g.
			// "@param identifier description". In this case the result should only
			// contain "identifier".
			return new String[] { subParamText.split(StringUtils.SPACE)[0].trim() };
		}

		return new String[] { subParamText.trim() };
	}

	private String extractParentIdentifierPath(String identifier, TagElement tagElement,
			List<? extends Parameter> parameters, JavaMethod method)
	{
		Javadoc javadoc = (Javadoc) tagElement.getParent();
		String parentParamIdentifier = readParentParamterName(javadoc, tagElement, method);

		if (!parentParamIdentifier.isEmpty())
		{
			JavaParameter parentParam = findParameterByName(parameters,
					parentParamIdentifier);

			if (parentParam != null)
			{
				return parentParam.getSignatureElementPath();
			}
			else
			{
				log.info("No parent parameter found for tag element "
						+ String.valueOf(tagElement.toString()));
			}
		}
		else
		{
			// jakr: if no parent is found, leave the identifier initialized with
			// null. In this case there must be an invalid ordering of the Javadoc
			// tags (@subparam without @param above).
		}

		return StringUtils.EMPTY;
	}

	private String extractIdentifierPath(String identifier, TagElement tagElement,
			List<? extends Parameter> parameters, JavaMethod method)
			throws ParsingException
	{
		String[] parameterNames = extractIdentifierChain(identifier);
		Javadoc javadoc = (Javadoc) tagElement.getParent();
		String parentParamIdentifier = readParentParamterName(javadoc, tagElement, method);

		if (!parentParamIdentifier.isEmpty())
		{
			JavaParameter parentParam = findParameterByName(parameters,
					parentParamIdentifier);

			if (parentParam != null)
			{
				if (parameterNames.length >= 1)
				{
					JavaParameter childParameter = findParameterByName(
							parentParam.getComplexType(), parameterNames[0]);

					int i = 1;

					while (i < parameterNames.length)
					{
						if (childParameter != null)
						{
							childParameter = findParameterByName(
									childParameter.getComplexType(), parameterNames[i]);
						}
						else
						{
							throw new ParsingException(
									"No more subparameters to search in for the identifier "
											+ String.valueOf(parameterNames[i])
											+ " in method "
											+ String.valueOf(method.getIdentifier()));
						}

						i++;
					}

					if (childParameter != null)
					{
						return childParameter.getSignatureElementPath();
					}
					else
					{
						throw new ParsingException(
								"No more subparameters to search in for the identifier "
										+ String.valueOf(parameterNames[0])
										+ " in method "
										+ String.valueOf(method.getIdentifier()));
					}
				}
				else
				{
					throw new ParsingException("The docText "
							+ String.valueOf(identifier) + " could not be splitted.");
				}
			}
			else
			{
				log.info("No parent parameter found for tag element "
						+ String.valueOf(tagElement.toString()));
			}
		}
		else
		{
			// jakr: if no parent is found, leave the identifier initialized with
			// null. In this case there must be an invalid ordering of the Javadoc
			// tags (@subparam without @param above).
		}

		return StringUtils.EMPTY;
	}

	private JavaParameter findParameterByName(final JavaMethod method,
			final String identifier, final String tagName)
	{
		if (method != null)
		{
			if (JavadocUtils.isParam(tagName))
			{
				if (method.getInputParameters() != null)
				{
					return findParameterByName(method.getInputParameters()
							.getParameters(), identifier);
				}
			}
			else if (JavadocUtils.isReturn(tagName))
			{
				if (method.getOutputParameters() != null)
				{
					return findParameterByName(method.getOutputParameters()
							.getParameters(), identifier);
				}
			}
			else if (JavadocUtils.isThrows(tagName))
			{
				if ((method.getExceptions() != null)
						&& (!method.getExceptions().isEmpty())
						&& (method.getExceptions().get(0) != null))
				{
					return findParameterByName(method.getExceptions().get(0)
							.getParameters(), identifier);
				}
			}
			else
			{
				throw new RuntimeException("The tagname " + String.valueOf(tagName)
						+ " is unexpected.");
			}
		}

		return null;
	}

	private String extractDocumenationText(final String identifier, final String allTexts)
	{
		if ((identifier != null) && allTexts.trim().startsWith(identifier))
		{
			final int startIndex = allTexts.indexOf(identifier) + identifier.length();
			return allTexts.substring(startIndex).trim();
		}
		else if (identifier == null)
		{
			int startIndex = allTexts.toLowerCase().indexOf(
					ThematicRoleConstants.MANDATORY_ROLE_RULE.toLowerCase());

			if (startIndex >= 0)
			{
				// jakr: + 1 is needed because of the ':'
				startIndex += ThematicRoleConstants.MANDATORY_ROLE_RULE.length() + 1;
				return allTexts.substring(startIndex).trim();
			}
		}

		return allTexts;
	}

	private String formatText(final String text)
	{
		return StringEscapeUtils.unescapeHtml4(text.replaceAll("<br/>",
				StringUtils.NEW_LINE).replaceAll("<tab/>", "\t"));
	}

	/**
	 * Creates a {@link Documentation} for the given tagElement.
	 * 
	 * @param tagElement
	 *            [SOURCE]
	 * @param addressees
	 *            [ATTRIBUTE] Used to read the addressee with name "Developer" and its
	 *            descriptions
	 * @param thematicRoles
	 *            [ATTRIBUTE] Used to read the thematic roles and their descriptions
	 * @param referenceGridName
	 *            [ATTRIBUTE] Used to derive the thematic role name
	 * @param method
	 *            [DESINITATION] '@throws' tags can be added to the
	 *            {@link JavaMethod#getAdditionalTags()}. {@code method} may be
	 *            {@code null} if not a whole CompilationUnit were parsed.
	 * @paraminfo method [ATTRIBUTE]
	 * @return [OBJECT] <code>null</code> if the {@link Documentation} shall not be added
	 *         to the SignatureElement.
	 * @throws ParsingException
	 */
	private Documentation createDocumentation(final TagElement tagElement,
			final List<Addressee> addressees, final List<ThematicRole> thematicRoles,
			final String referenceGridName, final JavaMethod method)
			throws ParsingException
	{
		final AnnotatedDocumentation annotatedDoc = readDocumentationAndThematicRole(
				tagElement, thematicRoles, referenceGridName, method);

		Documentation documentation = new Documentation();

		final Addressee developer = AddresseeUtils.findByName(
				AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, addressees);

		final List<Addressee> addresseeOrdering = new ArrayList<Addressee>();
		addresseeOrdering.add(developer);
		documentation.setAddresseeSequence(addresseeOrdering);

		final Map<Addressee, String> documentations = new HashMap<Addressee, String>();

		String identifier = JavadocUtils.readIdentifier(tagElement);
		final String unformattedDocText = extractDocumenationText(identifier,
				annotatedDoc.getDocText());

		if (annotatedDoc.getThematicRole() == null
				&& StringUtils.isBlank(unformattedDocText))
		{
			// no useful documentation found
			return null;
		}

		final String docText = formatText(unformattedDocText);
		documentations.put(developer, docText);
		documentation.setDocumentation(documentations);

		if (identifier == null)
		{
			if (JavadocUtils.isSubParam(tagElement.getTagName()) && (method != null)
					&& (method.getInputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				identifier = extractIdentifierPath(annotatedDoc.getIdentifier(),
						tagElement, method.getInputParameters().getParameters(), method);
			}
			else if (JavadocUtils.isSubReturn(tagElement.getTagName())
					&& (method != null) && (method.getOutputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				identifier = extractIdentifierPath(annotatedDoc.getIdentifier(),
						tagElement, method.getOutputParameters().getParameters(), method);
			}
			else if (JavadocUtils.isReturn(tagElement.getTagName()) && (method != null))
			{
				if (method.getOutputParameters() == null)
				{
					throw new ParsingException(String.format(
							ERR_MSG_NOT_EXISTING_RETURN_TYPE_IS_DOCUMENTED,
							method.getIdentifier()));
				}
				// In Java a method can have only one return type (with several
				// attributes).This is why we reference the first element here.
				final JavaParameter returnType = (JavaParameter) method
						.getOutputParameters().getParameters().get(0);

				identifier = returnType.getSignatureElementPath();
			}
			else if (JavadocUtils.isParamInfo(tagElement.getTagName())
					&& (method != null) && (method.getInputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				identifier = extractParentIdentifierPath(annotatedDoc.getIdentifier(),
						tagElement, method.getInputParameters().getParameters(), method);
			}
			else if (JavadocUtils.isReturnInfo(tagElement.getTagName())
					&& (method != null) && (method.getOutputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				identifier = extractParentIdentifierPath(annotatedDoc.getIdentifier(),
						tagElement, method.getOutputParameters().getParameters(), method);
			}
			else if (JavadocUtils.isThrowsInfo(tagElement.getTagName())
					&& (method != null) && (method.getExceptions() != null)
					&& (method.getExceptions().get(0) != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				identifier = extractParentIdentifierPath(annotatedDoc.getIdentifier(),
						tagElement, method.getExceptions().get(0).getParameters(), method);
			}
			else if (tagElement.getTagName() != null)
			{
				identifier = annotatedDoc.getIdentifier();
			}
			else
			{
				// identifier remains null
			}
		}
		else
		{
			final JavaParameter parentParam = findParameterByName(method, identifier,
					tagElement.getTagName());

			if (parentParam != null)
			{
				identifier = parentParam.getSignatureElementPath();
			}
			else if (JavadocUtils.isThrows(tagElement.getTagName()))
			{
				if (method != null)
				{
					// add throws tag, that can not be assigned to an SignatureElement to
					// the additionalTags of the method.
					List<TagElement> additionalTags = method.getAdditionalTags();
					if (additionalTags == null
							|| additionalTags == Collections.EMPTY_LIST)
					{
						additionalTags = new ArrayList<TagElement>(
								SignatureElement.DEFAULT_ARRAY_SIZE);
						method.setAdditionalTags(additionalTags);
					}
					// set it at the beginning, so it will be written in front of the
					// other
					// additional tags
					additionalTags.add(0, tagElement);
				}

				// Because the tag can not be assigned to a SignatureElement, it can not
				// be documented with iDocIt! UI.
				documentation = null;
			}
			else if (method != null)
			{
				final StringBuffer buffer = new StringBuffer("The Javadoc of method \"");
				buffer.append(method.getIdentifier());
				buffer.append("\" could not be parsed:\n\nThe documented parameter \"");
				buffer.append(identifier);
				buffer.append("\" could not be found in the method's signature.");

				throw new ParsingException(buffer.toString());
			}
		}

		if (documentation != null)
		{
			documentation.setSignatureElementIdentifier(identifier);
			documentation.setThematicRole(annotatedDoc.getThematicRole());
		}

		return documentation;
	}

	@Override
	public List<Documentation> parseIDocItJavadoc(Javadoc javadoc,
			List<Addressee> addressees, List<ThematicRole> thematicRoles,
			JavaMethod method) throws SAXException, IOException,
			ParserConfigurationException, ParsingException
	{
		final List<Documentation> documentations = new ArrayList<Documentation>();

		if (javadoc != null)
		{
			@SuppressWarnings("unchecked")
			final List<TagElement> tags = (List<TagElement>) javadoc.tags();
			final String referenceGridName = parseIDocItReferenceGrid(javadoc);

			// Parse Rule (Checking Operations) or Action (else)
			for (final TagElement tag : tags)
			{
				if (!CustomTaglets.THEMATIC_GRID.getTagName().equals(tag.getTagName())
						&& !isAdditionalTag(tag.getTagName(), thematicRoles))
				{
					final Documentation documentation = createDocumentation(tag,
							addressees, thematicRoles, referenceGridName, method);

					if (documentation != null)
					{
						documentations.add(documentation);
					}
				}
			}
		}

		return documentations;
	}

	private boolean isKnownThematicRole(final String rolename,
			final List<ThematicRole> knownRoles)
	{
		for (final ThematicRole role : knownRoles)
		{
			if (rolename.toLowerCase().equals(role.getName().toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isAdditionalTag(final String tagName,
			final List<ThematicRole> knownThematicRoles)
	{
		if (StringUtils.isBlank(tagName))
		{
			return false;
		}

		final boolean isParam = tagName.matches(JAVADOC_TAG_PARAM);
		final boolean isSubParam = tagName.matches(SUB_PARAM_PATTERN);
		final boolean isThrows = tagName.matches(JAVADOC_TAG_THROWS);
		final boolean isReturn = tagName.matches(JAVADOC_TAG_RETURN);
		final boolean isSubReturn = tagName.matches(SUB_RETURN_PATTERN);
		final boolean isThematicGrid = tagName.matches(THEMATIC_GRID_PATTERN);
		// When passing the rolename, remove the '@' at the beginning of the tagname!
		final boolean isKnownThematicRole = isKnownThematicRole(tagName.substring(1),
				knownThematicRoles);
		final boolean isInfoParam = JavadocUtils.isIdocItInfoTag(tagName);

		return !(isParam || isSubParam || isThrows || isReturn || isSubReturn
				|| isThematicGrid || isKnownThematicRole || isInfoParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TagElement> findAdditionalTags(final Javadoc javadoc,
			final List<ThematicRole> knownRoles)
	{
		List<TagElement> tags = Collections.emptyList();
		if (javadoc != null)
		{
			tags = new ArrayList<TagElement>(javadoc.tags().size());
			for (TagElement tag : (List<TagElement>) javadoc.tags())
			{
				if ((tag.getTagName() != null)
						&& isAdditionalTag(tag.getTagName(), knownRoles))
				{
					tags.add(tag);
					log.log(Level.FINEST, "Keep Javadoc tag: " + tag);
				}
			}
		}
		return tags;
	}

}
