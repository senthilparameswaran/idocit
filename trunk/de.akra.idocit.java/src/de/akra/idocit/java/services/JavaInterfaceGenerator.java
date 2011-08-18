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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavadocTagElement;

/**
 * Updates the {@link Javadoc} comments in the {@link AST} of the source file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
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
	public static void updateJavadocInAST(JavaInterfaceArtifact artifact)
	{
		updateInterfaces((List<JavaInterface>) artifact.getInterfaces());
	}

	/**
	 * Apply the changes to the interfaces and inner structure.
	 * 
	 * @param interfaces
	 *            The {@link JavaInterface}s that should be processed.
	 */
	@SuppressWarnings("unchecked")
	private static void updateInterfaces(List<JavaInterface> interfaces)
	{
		for (JavaInterface jInterface : interfaces)
		{
			List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
			JavadocTagElement tagElem = new JavadocTagElement(
					jInterface.getDocumentations());
			jDocTags.add(tagElem);

			AbstractTypeDeclaration absTypeDeclaration = jInterface.getRefToASTNode();
			Javadoc javadoc = createOrUpdateJavadoc(jDocTags,
					absTypeDeclaration.getJavadoc(), absTypeDeclaration.getAST());

			//TODO keep existing Javadoc that is not from iDocIt! I have to check if the old Javadoc is from iDocIt! Is this behavior needed?
			// if a existing javadoc was updated it must not be set again!
			if ((absTypeDeclaration.getJavadoc() == null && javadoc != null)
					|| (absTypeDeclaration.getJavadoc() != null && javadoc == null))
			{
				absTypeDeclaration.setJavadoc(javadoc);
			}

			updateMethods((List<JavaMethod>) jInterface.getOperations());
			updateInterfaces((List<JavaInterface>) jInterface.getInnerInterfaces());
		}
	}

	/**
	 * Apply the changes to the methods with the documentations for the parameters.
	 * 
	 * @param methods
	 *            The {@link JavaMethod} that should be processed.
	 */
	private static void updateMethods(List<JavaMethod> methods)
	{
		for (JavaMethod method : methods)
		{
			List<JavadocTagElement> jDocTags = createMethodJavadocTagElements(method);

			MethodDeclaration methodDeclaration = method.getRefToASTNode();
			Javadoc javadoc = createOrUpdateJavadoc(jDocTags,
					methodDeclaration.getJavadoc(), methodDeclaration.getAST());

			//TODO keep existing Javadoc that is not from iDocIt! I have to check if the old Javadoc is from iDocIt! Is that needed?
			// if a existing javadoc was updated it must not be set again!
			if ((methodDeclaration.getJavadoc() == null && javadoc != null)
					|| (methodDeclaration.getJavadoc() != null && javadoc == null))
			{
				methodDeclaration.setJavadoc(javadoc);
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
		JavadocTagElement tagElem = new JavadocTagElement(method.getDocumentations());
		jDocTags.add(tagElem);

		Parameters inputParams = method.getInputParameters();
		collectParametersDocumentations(inputParams, TagElement.TAG_PARAM, jDocTags);

		Parameters returnType = method.getOutputParameters();
		collectParametersDocumentations(returnType, TagElement.TAG_RETURN, jDocTags);

		for (Parameters exception : method.getExceptions())
		{
			collectParametersDocumentations(exception, TagElement.TAG_THROWS, jDocTags);
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
	private static void collectParametersDocumentations(Parameters parameters,
			String tagName, List<JavadocTagElement> javadocTagElements)
	{
		if (parameters != null)
		{
			for (Parameter param : parameters.getParameters())
			{
				List<Documentation> documentations = new ArrayList<Documentation>();
				collectParameterDocumentations(param, documentations);

				if (!documentations.isEmpty())
				{
					String identifier = param.getIdentifier();
					if (tagName.equals(TagElement.TAG_RETURN))
					{
						// there is only a type, no identifier, for the return value
						identifier = null;
					}
					JavadocTagElement jDocTagElem = new JavadocTagElement(tagName,
							identifier, documentations);
					javadocTagElements.add(jDocTagElem);
				}
			}
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
	private static void collectParameterDocumentations(Parameter parameter,
			List<Documentation> documentations)
	{
		documentations.addAll(parameter.getDocumentations());
		for (Parameter param : parameter.getComplexType())
		{
			collectParameterDocumentations(param, documentations);
		}
	}

	/**
	 * //TODO an update of the javadoc must be implemented. Now it is always overwritten
	 * if there are changes.<br>
	 * Creates a new {@link Javadoc} comment if <code>javadoc == null</code> adds the
	 * information from the <code>javadocTagElements</code> to
	 * <code>javadoc == null</code> and returns it.
	 * 
	 * @param javadocTagElements
	 *            list of {@link JavadocTagElement}s to write into the {@link Javadoc}
	 *            comment.
	 * @param javadoc
	 *            The existing {@link Javadoc}.
	 * @param ast
	 *            The {@link AST} is needed as factory for new {@link Javadoc} elements.
	 * @return a new {@link Javadoc} comment if <code>javadoc == null</code>, otherwise
	 *         the updated <code>javadoc</code>. But if the comment would be empty always
	 *         <code>null</code> is returned.
	 */
	public static Javadoc createOrUpdateJavadoc(
			List<JavadocTagElement> javadocTagElements, Javadoc javadoc, AST ast)
	{
		// TODO an update of the javadoc must be implemented. Now it is always
		// overwritten and not merged.
		Javadoc newJavadoc = javadoc;
		if (newJavadoc == null)
		{
			newJavadoc = ast.newJavadoc();
		}
		else
		{
			newJavadoc.tags().clear();
		}

		for (JavadocTagElement jTagElem : javadocTagElements)
		{
			JavadocGenerator.appendDocsToJavadoc(jTagElem.getDocumentations(),
					jTagElem.getTagName(), jTagElem.getParameterName(), newJavadoc);
		}

		return newJavadoc.tags().size() == 0 ? null : newJavadoc;
	}
}
