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
import de.akra.idocit.common.structure.ThematicRole;
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

	private StructuredJavaDoc splitJavadocText(String tagName, String docText)
	{
		Matcher matcher = SPLIT_JAVADOC_REGEXP.matcher(docText);
		StructuredJavaDoc structuredJavaDoc = new StructuredJavaDoc();

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
				structuredJavaDoc.setParamName(docText.trim());
				structuredJavaDoc.setDocText("");
			}
			else
			{
				structuredJavaDoc.setDocText(docText.trim());
				structuredJavaDoc.setParamName(null);
			}

			structuredJavaDoc.setRoleName(null);
		}

		return structuredJavaDoc;
	}

	private StructuredJavaDoc readRoleName(TagElement tagElement,
			String referenceGridName, String tagText, JavaMethod method)
	{
		String tagName = tagElement.getTagName();
		StructuredJavaDoc structuredJavaDoc = splitJavadocText(tagName, tagText);

		if ((tagName != null) && !JavadocUtils.isStandardJavadocTaglet(tagName)
				&& !JavadocUtils.isIdocitJavadocTaglet(tagName))
		{
			// When using tag names, remove the '@' at the beginning.
			structuredJavaDoc.setRoleName(tagName.substring(1));
		}
		// TODO: Could this condition be removed?
		else if (!JavadocUtils.isStandardJavadocTaglet(tagName)
				&& !JavadocUtils.isIdocitJavadocTaglet(tagName))
		{ // Must be ACTION or RULE (depends on the reference grid)
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
			TagElement tagElement, List<ThematicRole> thematicRoles,
			String referenceGridName, JavaMethod method)
	{
		String origTagText = JavadocUtils.readFragments(tagElement.fragments(), 0);
		StructuredJavaDoc structuredJavaDoc = readRoleName(tagElement, referenceGridName,
				origTagText, method);
		String roleName = structuredJavaDoc.getRoleName();
		String tagText = structuredJavaDoc.getDocText();
		ThematicRole role = ThematicRoleUtils.findRoleByName(roleName, thematicRoles);

		if (role == null)
		{
			log.info("No thematic role found for tag element: "
					+ String.valueOf(tagElement));
		}

		AnnotatedDocumentation result = new AnnotatedDocumentation();
		result.setThematicRole(role);
		result.setDocText(tagText);
		result.setIdentifier(structuredJavaDoc.getParamName());

		return result;
	}

	private String readParentParamterName(Javadoc javadoc, TagElement childTagElem,
			JavaMethod method)
	{
		List tags = javadoc.tags();

		for (int i = 0; i < tags.size(); i++)
		{
			Object curTagElem = tags.get(i);
			if (curTagElem == childTagElem)
			{
				int prev = i - 1;

				while (prev >= 0)
				{
					TagElement parentParamTag = (TagElement) tags.get(prev);

					// In a @return-tag no identifier is mentioned. So must not test
					// against @return here.
					if (TagElement.TAG_PARAM.equals(parentParamTag.getTagName())
							|| TagElement.TAG_THROWS.equals(parentParamTag.getTagName()))
					{
						String parentText = JavadocUtils.readFragments(
								parentParamTag.fragments(), 0);

						if (parentText.contains(" "))
						{
							return parentText.split(" ")[0];
						}

						return parentText;
					}
					else if (TagElement.TAG_RETURN.equals(parentParamTag.getTagName()))
					{
						JavaParameter returnType = (JavaParameter) method
								.getOutputParameters().getParameters().get(0);

						return returnType.getIdentifier();
					}

					prev--;
				}
			}
		}

		return "";
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
		else if (subParamText.contains(" "))
		{
			// It could be that there is only the identifier and a description, e.g.
			// "@param identifier description". In this case the result should only
			// contain "identifier".
			return new String[] { subParamText.split(" ")[0].trim() };
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

		return "";
	}

	private String extractIdentifierPath(String identifier, TagElement tagElement,
			List<? extends Parameter> parameters, JavaMethod method)
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
							throw new RuntimeException(
									"No more subparameters to search in for the identifier "
											+ String.valueOf(parameterNames[i]));
						}

						i++;
					}

					return childParameter.getSignatureElementPath();
				}
				else
				{
					throw new RuntimeException("The docText "
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

		return "";
	}

	private JavaParameter findParameterByName(JavaMethod method, String identifier,
			String tagName)
	{
		if (JavadocUtils.isParam(tagName))
		{
			if(method.getInputParameters() != null)
			{
				return findParameterByName(method.getInputParameters().getParameters(),
					identifier);
			}
		}
		else if (JavadocUtils.isReturn(tagName))
		{
			if(method.getOutputParameters() != null)
			{
				return findParameterByName(method.getOutputParameters().getParameters(),
					identifier);
			}	
		}
		else if (JavadocUtils.isThrows(tagName))
		{
			if((method.getExceptions() != null) && (method.getExceptions().get(0) != null))
			{
				return findParameterByName(method.getExceptions().get(0).getParameters(),
					identifier);
			}
		}
		else
		{
			throw new RuntimeException("The tagname " + String.valueOf(tagName)
					+ " is unexpected.");
		}
		
		return null;
	}

	private String extractDocumenationText(String identifier, String allTexts)
	{
		if ((identifier != null) && allTexts.trim().startsWith(identifier))
		{
			int startIndex = allTexts.indexOf(identifier) + identifier.length();

			return allTexts.substring(startIndex).trim();
		}

		return allTexts;
	}

	private String formatText(String text)
	{
		String newLine = System.getProperty("line.separator");
		return StringEscapeUtils.unescapeHtml4(text.replaceAll("<br/>", newLine)
				.replaceAll("<tab/>", "\t"));
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
	 * @return [OBJECT]
	 * @throws ParsingException
	 */
	private Documentation createDocumentation(TagElement tagElement,
			List<Addressee> addressees, List<ThematicRole> thematicRoles,
			String referenceGridName, JavaMethod method) throws ParsingException
	{
		AnnotatedDocumentation annotatedDoc = readDocumentationAndThematicRole(
				tagElement, thematicRoles, referenceGridName, method);
		Documentation documentation = new Documentation();

		Addressee developer = AddresseeUtils.findByName(
				AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, addressees);

		List<Addressee> addresseeOrdering = new ArrayList<Addressee>();
		addresseeOrdering.add(developer);
		documentation.setAddresseeSequence(addresseeOrdering);

		Map<Addressee, String> documentations = new HashMap<Addressee, String>();

		String identifier = JavadocUtils.readIdentifier(tagElement);
		String unformattedDocText = extractDocumenationText(identifier,
				annotatedDoc.getDocText());
		String docText = formatText(unformattedDocText);
		documentations.put(developer, docText);
		documentation.setDocumentation(documentations);

		if (identifier == null)
		{
			if (JavadocUtils.isSubParam(tagElement.getTagName()) && (method != null)
					&& (method.getInputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				documentation.setSignatureElementIdentifier(extractIdentifierPath(
						annotatedDoc.getIdentifier(), tagElement, method
								.getInputParameters().getParameters(), method));
			}
			else if (JavadocUtils.isSubReturn(tagElement.getTagName())
					&& (method != null) && (method.getOutputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				documentation.setSignatureElementIdentifier(extractIdentifierPath(
						annotatedDoc.getIdentifier(), tagElement, method
								.getOutputParameters().getParameters(), method));
			}
			else if (JavadocUtils.isReturn(tagElement.getTagName()))
			{
				// In Java a method can have only one return type (with several
				// attributes).This is why we reference the first element here.
				JavaParameter returnType = (JavaParameter) method.getOutputParameters()
						.getParameters().get(0);

				identifier = returnType.getSignatureElementPath();
				documentation.setSignatureElementIdentifier(identifier);
			}
			else if (JavadocUtils.isParamInfo(tagElement.getTagName())
					&& (method != null) && (method.getInputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				documentation.setSignatureElementIdentifier(extractParentIdentifierPath(
						annotatedDoc.getIdentifier(), tagElement, method
								.getInputParameters().getParameters(), method));
			}
			else if (JavadocUtils.isReturnInfo(tagElement.getTagName())
					&& (method != null) && (method.getOutputParameters() != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				documentation.setSignatureElementIdentifier(extractParentIdentifierPath(
						annotatedDoc.getIdentifier(), tagElement, method
								.getOutputParameters().getParameters(), method));
			}
			else if (JavadocUtils.isThrowsInfo(tagElement.getTagName())
					&& (method != null) && (method.getExceptions() != null)
					&& (method.getExceptions().get(0) != null)
					&& (annotatedDoc.getIdentifier() != null))
			{
				documentation.setSignatureElementIdentifier(extractParentIdentifierPath(
						annotatedDoc.getIdentifier(), tagElement, method.getExceptions()
								.get(0).getParameters(), method));
			}
			else if (tagElement.getTagName() == null)
			{
				documentation.setSignatureElementIdentifier(null);
			}
			else
			{
				identifier = annotatedDoc.getIdentifier();
				documentation.setSignatureElementIdentifier(identifier);
			}
		}
		else
		{
			JavaParameter parentParam = findParameterByName(method, identifier,
					tagElement.getTagName());

			if (parentParam != null)
			{
				documentation.setSignatureElementIdentifier(parentParam
						.getSignatureElementPath());
			}
			else
			{
				StringBuffer buffer = new StringBuffer("The Javadoc of method ");
				buffer.append(method.getIdentifier());
				buffer.append(" could not be parsed:\n\nThe documented parameter ");
				buffer.append(identifier);
				buffer.append(" could not be found in the method's signature.");

				throw new ParsingException(buffer.toString());
			}
		}

		documentation.setThematicRole(annotatedDoc.getThematicRole());

		return documentation;
	}

	@Override
	public List<Documentation> parseIDocItJavadoc(Javadoc javadoc,
			List<Addressee> addressees, List<ThematicRole> thematicRoles,
			JavaMethod method) throws SAXException, IOException,
			ParserConfigurationException, ParsingException
	{
		List<Documentation> documentations = new ArrayList<Documentation>();

		if (javadoc != null)
		{
			List<TagElement> tags = (List<TagElement>) javadoc.tags();
			String referenceGridName = parseIDocItReferenceGrid(javadoc);

			// Parse Rule (Checking Operations) or Action (else)
			for (TagElement tag : tags)
			{
				if (!CustomTaglets.THEMATIC_GRID.equals(tag.getTagName()))
				{
					Documentation documentation = createDocumentation(tag, addressees,
							thematicRoles, referenceGridName, method);
					ThematicRole role = documentation.getThematicRole();

					if (role != null)
					{
						documentations.add(documentation);
					}
				}
			}
		}

		return documentations;
	}

	private boolean isKnownThematicRole(String rolename, List<ThematicRole> knownRoles)
	{
		for (ThematicRole role : knownRoles)
		{
			if (rolename.toLowerCase().equals(role.getName().toLowerCase()))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isAdditionalTag(String tagName, List<ThematicRole> knownThematicRoles)
	{
		boolean isParam = tagName.matches(JAVADOC_TAG_PARAM);
		boolean isSubParam = tagName.matches(CustomTaglets.SUB_PARAM_PATTERN);
		boolean isThrows = tagName.matches(JAVADOC_TAG_THROWS);
		boolean isReturn = tagName.matches(JAVADOC_TAG_RETURN);
		boolean isSubReturn = tagName.matches(CustomTaglets.SUB_RETURN_PATTERN);
		boolean isThematicGrid = tagName.matches(CustomTaglets.THEMATIC_GRID_PATTERN);
		// When passing the rolename, remove the '@' at the beginning of the tagname!
		boolean isKnownThematicRole = isKnownThematicRole(tagName.substring(1),
				knownThematicRoles);
		boolean isInfoParam = JavadocUtils.isIdocItInfoTag(tagName);

		return !(isParam || isSubParam || isThrows || isReturn || isSubReturn
				|| isThematicGrid || isKnownThematicRole || isInfoParam);
	}

	@Override
	public List<TagElement> findAdditionalTags(Javadoc javadoc,
			List<ThematicRole> knownRoles)
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
