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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.utils.SignatureElementUtils;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.java.constants.CustomTaglets;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavadocTagElement;
import de.akra.idocit.java.utils.JavadocUtils;

/**
 * Updates the {@link Javadoc} comments in the {@link AST} of the source file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class JavaInterfaceGenerator
{

	/**
	 * Apply the changes in the {@link JavaInterfaceArtifact} object structure and
	 * transfer it to the {@link AST}. The {@link Javadoc} comments are updated, created
	 * or removed.
	 * 
	 * @param artifact
	 *            The {@link JavaInterfaceArtifact} with the references to the {@link AST}
	 *            that should be updated.
	 */
	@SuppressWarnings("unchecked")
	public static void updateJavadocInAST(JavaInterfaceArtifact artifact,
			IJavadocGenerator javadocGenerator) throws ParsingException
	{
		updateInterfaces((List<JavaInterface>) artifact.getInterfaces(), javadocGenerator);
	}

	/**
	 * Apply the changes to the interfaces and inner structure.
	 * 
	 * @param interfaces
	 *            The {@link JavaInterface}s that should be processed. @
	 */
	@SuppressWarnings("unchecked")
	private static void updateInterfaces(List<JavaInterface> interfaces,
			IJavadocGenerator javadocGenerator) throws ParsingException
	{
		for (JavaInterface jInterface : interfaces)
		{
			if (jInterface.isDocumentationChanged())
			{
				List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
				JavadocTagElement tagElem = new JavadocTagElement(
						jInterface.getDocumentations(), jInterface);
				jDocTags.add(tagElem);

				AbstractTypeDeclaration absTypeDeclaration = jInterface.getRefToASTNode();
				Javadoc javadoc = createOrUpdateJavadoc(jDocTags,
						jInterface.getAdditionalTags(), absTypeDeclaration.getJavadoc(),
						absTypeDeclaration.getAST(), null, javadocGenerator, null);

				// if an existing javadoc was updated it must not be set again!
				if ((absTypeDeclaration.getJavadoc() == null && javadoc != null)
						|| (absTypeDeclaration.getJavadoc() != null && javadoc == null))
				{
					absTypeDeclaration.setJavadoc(javadoc);
				}
			}

			updateMethods((List<JavaMethod>) jInterface.getOperations(), javadocGenerator);
			updateInterfaces((List<JavaInterface>) jInterface.getInnerInterfaces(),
					javadocGenerator);
		}
	}

	/**
	 * Apply the changes to the methods with the documentations for the parameters.
	 * 
	 * @param methods
	 *            The {@link JavaMethod} that should be processed.
	 */
	private static void updateMethods(List<JavaMethod> methods,
			IJavadocGenerator javadocGenerator) throws ParsingException
	{
		for (JavaMethod method : methods)
		{
			if (SignatureElementUtils.isOperationsDocChanged(method))
			{
				List<JavadocTagElement> jDocTags = createMethodJavadocTagElements(method);

				MethodDeclaration methodDeclaration = method.getRefToASTNode();
				Javadoc javadoc = createOrUpdateJavadoc(jDocTags,
						method.getAdditionalTags(), methodDeclaration.getJavadoc(),
						methodDeclaration.getAST(), method.getThematicGridName(),
						javadocGenerator, method);

				// if an existing Javadoc was updated it must not be set again!
				if ((methodDeclaration.getJavadoc() == null && javadoc != null)
						|| (methodDeclaration.getJavadoc() != null && javadoc == null))
				{
					methodDeclaration.setJavadoc(javadoc);
				}
			}
		}
	}

	/**
	 * Create a list of {@link JavadocTagElement}s to convert them to a {@link Javadoc}
	 * comment.
	 * 
	 * @param method
	 *            The {@link JavaMethod} that should be processed to extract the needed
	 *            information to generate a Javadoc.
	 * @return List of {@link JavadocTagElement}s.
	 */
	private static List<JavadocTagElement> createMethodJavadocTagElements(
			JavaMethod method)
	{
		List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
		JavadocTagElement tagElem = new JavadocTagElement(method.getDocumentations(),
				method);
		jDocTags.add(tagElem);

		Parameters inputParams = method.getInputParameters();
		if (inputParams != null)
		{
			collectParametersDocumentations(inputParams.getParameters(),
					TagElement.TAG_PARAM, jDocTags, CustomTaglets.SUB_PARAM.getTagName());
		}

		Parameters returnType = method.getOutputParameters();

		if (returnType != null)
		{
			collectParametersDocumentations(returnType.getParameters(),
					TagElement.TAG_RETURN, jDocTags,
					CustomTaglets.SUB_RETURN.getTagName());
		}

		for (Parameters exception : method.getExceptions())
		{
			if (exception != null)
			{
				collectParametersDocumentations(exception.getParameters(),
						TagElement.TAG_THROWS, jDocTags, null);
			}
		}

		return jDocTags;
	}

	/**
	 * For each {@link Parameter} in {@link Parameters} it creates a
	 * {@link JavadocTagElement} and adds all {@link Documentation}s for that parameter to
	 * it and sets the necessary attributes for writing a {@link Javadoc}. After that it
	 * is added to the result list <code>javadocTagElements</code>.
	 * 
	 * @param parameters
	 *            The {@link Parameters} that should be converted to
	 *            {@link JavadocTagElement}s.
	 * @param tagName
	 *            The tag name for all {@link JavadocTagElement}, that are created out of
	 *            <code>parameters</code>. E.g. {@link TagElement#TAG_PARAM},
	 *            {@link TagElement#TAG_RETURN} or {@link TagElement#TAG_THROWS}
	 * @param javadocTagElements
	 *            The created {@link JavadocTagElement}s.
	 * @see TagElement
	 */
	private static void collectParametersDocumentations(List<Parameter> parameters,
			String tagName, List<JavadocTagElement> javadocTagElements, String subTagName)
	{
		if (parameters != null)
		{
			for (Parameter param : parameters)
			{
				List<Documentation> paramDocumentations = param.getDocumentations();
				addJavadocTagElement(tagName, javadocTagElements, param,
						paramDocumentations);

				collectParameterDocumentations(subTagName, param.getComplexType(),
						javadocTagElements);
			}
		}
	}

	private static void addJavadocTagElement(String tagName,
			List<JavadocTagElement> javadocTagElements, Parameter param,
			List<Documentation> paramDocumentations)
	{
		if ((paramDocumentations != null) && !paramDocumentations.isEmpty())
		{
			String identifier = param.getIdentifier();
			if (TagElement.TAG_RETURN.equals(tagName)
					|| CustomTaglets.SUB_RETURN.getTagName().equals(tagName))
			{
				// there is only a type, no identifier, for the return
				// value
				identifier = null;
			}
			JavadocTagElement jDocTagElem = new JavadocTagElement(tagName, identifier,
					paramDocumentations, param);
			javadocTagElements.add(jDocTagElem);
		}
		else if (JavadocUtils.isStandardJavadocTaglet(tagName))
		{
			// @param, @return and @throws should mentioned even if no documentation has
			// been created for the parameter, return-type or exception!
			JavadocTagElement jDocTagElem = new JavadocTagElement(tagName,
					param.getIdentifier(), new ArrayList<Documentation>(), param);
			javadocTagElements.add(jDocTagElem);
		}
	}

	/**
	 * Collect all {@link Documentation}s in the {@link Parameter} structure and adds it
	 * to the result list <code>documentations</code>.
	 * 
	 * @param parameter
	 *            The {@link Parameter} that should be processed.
	 * @param documentations
	 *            the result list of found {@link Documentation}s.
	 */
	private static void collectParameterDocumentations(String tagName,
			List<Parameter> parameters, List<JavadocTagElement> javadocTagElements)
	{
		for (Parameter param : parameters)
		{
			addJavadocTagElement(tagName, javadocTagElements, param,
					param.getDocumentations());

			collectParameterDocumentations(tagName, param.getComplexType(),
					javadocTagElements);
		}
	}

	private static TagElement findTagElementByName(String name, List<TagElement> elements)
	{
		for (final TagElement tagElement : elements)
		{
			if ((name != null) && name.equals(tagElement.getTagName()))
			{
				return tagElement;
			}
		}

		return null;
	}

	private static TextElement findFirstTextElement(List<ASTNode> nodes)
	{
		for (final ASTNode node : nodes)
		{
			if (node instanceof TextElement)
			{
				return (TextElement) node;
			}
		}

		return null;
	}

	/**
	 * Creates a new {@link Javadoc} comment if <code>javadoc == null</code> or clears an
	 * existing and then adds the information from the <code>javadocTagElements</code> and
	 * returns it.
	 * <p>
	 * <b>Hint:</b> The scope is set to friendly for tests.
	 * </p>
	 * 
	 * @param javadocTagElements
	 *            list of {@link JavadocTagElement}s to write into the {@link Javadoc}
	 *            comment.
	 * @param additionalTags
	 *            List of additional {@link TagElement}s to append to the Javadoc. It must
	 *            not be <code>null</code>!
	 * @param javadoc
	 *            The existing {@link Javadoc}.
	 * @param ast
	 *            The {@link AST} is needed as factory for new {@link Javadoc} elements.
	 * @param thematicGridName
	 *            The name of the thematic grid
	 * 
	 * @return a new {@link Javadoc} comment if <code>javadoc == null</code>, otherwise
	 *         the updated <code>javadoc</code>. But if the comment would be empty always
	 *         <code>null</code> is returned.
	 * @since 0.0.2
	 */
	// Changes due to Issue #62
	@SuppressWarnings("unchecked")
	static Javadoc createOrUpdateJavadoc(List<JavadocTagElement> javadocTagElements,
			List<TagElement> additionalTags, Javadoc javadoc, AST ast,
			String thematicGridName, IJavadocGenerator javadocGenerator, JavaMethod method)
			throws ParsingException
	{
		Javadoc newJavadoc = javadoc;
		if (newJavadoc == null)
		{
			newJavadoc = ast.newJavadoc();
		}
		else
		{
			newJavadoc.tags().clear();
		}

		for (JavadocTagElement tagElement : javadocTagElements)
		{
			javadocGenerator.appendDocsToJavadoc(tagElement.getDocumentations(),
					tagElement.getTagName(), tagElement.getParameterName(),
					thematicGridName, newJavadoc, additionalTags, method);
		}

		newJavadoc.tags().addAll(additionalTags);

		addThematicGridTag(ast, thematicGridName, newJavadoc);

		return newJavadoc.tags().size() == 0 ? null : newJavadoc;
	}

	// End changes due to Issue #62

	@SuppressWarnings("unchecked")
	private static void addThematicGridTag(AST ast, String thematicGridName,
			Javadoc newJavadoc)
	{
		TagElement thematicGridElement = findTagElementByName(
				JavadocParser.JAVADOC_TAG_THEMATICGRID, newJavadoc.tags());

		if (thematicGridName != null)
		{
			if (thematicGridElement == null)
			{
				thematicGridElement = ast.newTagElement();
				thematicGridElement.setTagName(JavadocParser.JAVADOC_TAG_THEMATICGRID);

				newJavadoc.tags().add(thematicGridElement);
			}

			final List<ASTNode> fragments = (List<ASTNode>) thematicGridElement.fragments();
			TextElement textElement = findFirstTextElement(fragments);

			if (textElement == null)
			{
				textElement = ast.newTextElement();
				fragments.add(textElement);
			}

			textElement.setText(StringUtils.SPACE + thematicGridName);
		}
		else
		{
			newJavadoc.tags().remove(thematicGridElement);
		}
	}
}
