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
package de.akra.idocit.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.constants.ThematicRoleConstants;

/**
 * Some useful methods for tests.
 * 
 * @author Dirk Meier-Eickhoff
 * @version 0.0.2
 */
public class TestUtils
{

	/**
	 * Creates an Eclipse project "External Files" and links the file
	 * <code>fileName</code> to the project. Then the file can be used as {@link IFile}.
	 * 
	 * @param fileName
	 *            The path to a file.
	 * @return {@link IFile} from the <code>fileName</code>.
	 * @throws CoreException
	 */
	public static IFile makeIFileFromFileName(String fileName) throws CoreException
	{
		File file = new File(fileName);

		// create project to link source file to it. Is needed to get an IFile.
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject project = ws.getRoot().getProject("External Files");
		if (!project.exists())
		{
			project.create(null);
		}
		if (!project.isOpen())
		{
			project.open(null);
		}

		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile iFile = project.getFile(location.lastSegment());
		Assert.assertNotNull(iFile);
		iFile.createLink(location, IResource.NONE, null);
		return iFile;
	}

	/**
	 * Reads the content of the file and returns it as String.
	 * 
	 * @param fileName
	 *            The name of the file to be read.
	 * @return The file content as String.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws FileNotFoundException,
			IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		StringBuffer fileContent = new StringBuffer();
		int bufferSize = 256;
		char[] charArray = new char[bufferSize];
		int read = 0;
		while ((read = reader.read(charArray, 0, bufferSize)) > -1)
		{
			fileContent.append(charArray, 0, read);
		}
		reader.close();
		return fileContent.toString();
	}

	/**
	 * Builds the hierarchy tree as string.
	 * 
	 * @param result
	 *            The tree as string.
	 * @param sigElem
	 *            The element that should be added to the tree.
	 * @param level
	 *            The tree level (indentation of the line).
	 */
	public static void buildHierarchy(StringBuffer result, SignatureElement sigElem,
			int level)
	{
		if (sigElem != null)
		{
			// write tabs to the beginning of the line
			for (int i = 0; i < level; i++)
			{
				result.append('\t');
			}
			result.append(sigElem.getDisplayName() + "\n");

			if (sigElem instanceof Parameter)
			{
				Parameter param = (Parameter) sigElem;
				if (param.getComplexType() != null)
				{
					for (Parameter p : param.getComplexType())
					{
						buildHierarchy(result, p, level + 1);
					}
				}
			}
			else if (sigElem instanceof Operation)
			{
				Operation op = (Operation) sigElem;
				buildHierarchy(result, op.getInputParameters(), level + 1);
				buildHierarchy(result, op.getOutputParameters(), level + 1);

				for (Parameters paramList : op.getExceptions())
				{
					buildHierarchy(result, paramList, level + 1);
				}
			}
			else if (sigElem instanceof Parameters)
			{
				Parameters paramList = (Parameters) sigElem;
				if (paramList.getParameters() != null)
				{
					for (Parameter p : paramList.getParameters())
					{
						buildHierarchy(result, p, level + 1);
					}
				}
			}
			else if (sigElem instanceof Interface)
			{
				Interface interf = (Interface) sigElem;

				if (interf.getOperations() != null)
				{
					for (Operation o : interf.getOperations())
					{
						buildHierarchy(result, o, level + 1);
					}
				}
				if (interf.getInnerInterfaces() != null)
				{
					for (Interface i : interf.getInnerInterfaces())
					{
						buildHierarchy(result, i, level + 1);
					}
				}
			}
			else if (sigElem instanceof InterfaceArtifact)
			{
				InterfaceArtifact iArtifact = (InterfaceArtifact) sigElem;
				if (iArtifact.getInterfaces() != null)
				{
					for (Interface i : iArtifact.getInterfaces())
					{
						buildHierarchy(result, i, level + 1);
					}
				}
			}
		}
	}

	/**
	 * A deep toString() implementation for implementations of {@link SignatureElement}s,
	 * only the <code>id</code> of the signature element is omitted. Use this to test
	 * equality for two {@link SignatureElement} object structures.
	 * 
	 * @param sigElem
	 *            The {@link SignatureElement} to make a String representation from.
	 * @return The {@link SignatureElement} as String.
	 */
	public static String toStringWithoutId(SignatureElement sigElem)
	{
		return doToStringWithoutId(sigElem).toString();
	}

	/**
	 * 
	 * @param sigElem
	 * @return
	 * @see #toStringWithoutId(SignatureElement)
	 */
	private static StringBuilder doToStringWithoutId(SignatureElement sigElem)
	{
		StringBuilder builder = new StringBuilder();

		if (sigElem != null)
		{
			builder.append(sigElem.getClass().getSimpleName());
			builder.append(" [documentations=");
			builder.append(sigElem.getDocumentations().toString());
			builder.append(", identifier=");
			builder.append(sigElem.getIdentifier());
			builder.append(", qualifiedIdentifier=");
			builder.append(sigElem.getQualifiedIdentifier());
			builder.append(", category=");
			builder.append(sigElem.getCategory());
			builder.append(", documentationAllowed=");
			builder.append(sigElem.isDocumentationAllowed());
			builder.append(", documentationChanged=");
			builder.append(sigElem.isDocumentationChanged());

			if (sigElem instanceof Parameter)
			{
				Parameter param = (Parameter) sigElem;
				builder.append(", dataTypeName=");
				builder.append(param.getDataTypeName());
				builder.append(", qualifiedDataTypeName=");
				builder.append(param.getQualifiedDataTypeName());
				builder.append(", signatureElementPath=");
				builder.append(param.getSignatureElementPath());

				builder.append(", complexType={");
				if (param.getComplexType() != null)
				{
					for (Parameter p : param.getComplexType())
					{
						builder.append(doToStringWithoutId(p));
					}
				}
				builder.append("}");
			}
			else if (sigElem instanceof Operation)
			{
				Operation op = (Operation) sigElem;
				builder.append(", inputParameters=");
				builder.append(doToStringWithoutId(op.getInputParameters()));
				builder.append(", outputParameters=");
				builder.append(doToStringWithoutId(op.getOutputParameters()));

				builder.append(", exceptions={");
				for (Parameters paramList : op.getExceptions())
				{
					builder.append(doToStringWithoutId(paramList));
				}
				builder.append("}");
			}
			else if (sigElem instanceof Parameters)
			{
				Parameters paramList = (Parameters) sigElem;
				builder.append(", parameters={");
				if (paramList.getParameters() != null)
				{
					for (Parameter p : paramList.getParameters())
					{
						builder.append(doToStringWithoutId(p));
					}
				}
				builder.append("}");
			}
			else if (sigElem instanceof Interface)
			{
				Interface interf = (Interface) sigElem;

				builder.append(", operations={");
				if (interf.getOperations() != null)
				{
					for (Operation o : interf.getOperations())
					{
						builder.append(doToStringWithoutId(o));
					}
				}
				builder.append("}");
				builder.append(", interfaces={");
				if (interf.getInnerInterfaces() != null)
				{
					for (Interface i : interf.getInnerInterfaces())
					{
						builder.append(doToStringWithoutId(i));
					}
				}
				builder.append("}");
			}
			else if (sigElem instanceof InterfaceArtifact)
			{
				InterfaceArtifact iArtifact = (InterfaceArtifact) sigElem;
				builder.append(", interfaces={");
				if (iArtifact.getInterfaces() != null)
				{
					for (Interface i : iArtifact.getInterfaces())
					{
						builder.append(doToStringWithoutId(i));
					}
				}
				builder.append("}");
			}
			builder.append("]");
		}
		return builder;
	}

	public static Addressee createDeveloper()
	{
		Addressee addresseeDeveloper = new Addressee("Developer");
		addresseeDeveloper.setDefault(true);
		// The developer implements software-systems.
		addresseeDeveloper.setDescription("");

		return addresseeDeveloper;
	}

	public static Addressee createTester()
	{
		Addressee addresseeTester = new Addressee("Tester");
		addresseeTester.setDefault(false);
		// "The tester tests the implemented systems and gives feedback on their functional- and not-functional quality."
		addresseeTester.setDescription(StringUtils.EMPTY);

		return addresseeTester;
	}

	public static Addressee createArchitect()
	{
		Addressee addresseeArchitect = new Addressee("Architect");
		addresseeArchitect.setDefault(false);
		// "The architect designs software-systems"
		addresseeArchitect.setDescription(StringUtils.EMPTY);

		return addresseeArchitect;
	}

	public static ThematicRole createObject()
	{
		ThematicRole action = new ThematicRole("OBJECT");
		// "What the operation does."
		action.setDescription(StringUtils.EMPTY);

		return action;
	}

	public static ThematicRole createRule()
	{
		ThematicRole action = new ThematicRole(ThematicRoleConstants.MANDATORY_ROLE_RULE);
		// "What the operation does."
		action.setDescription(StringUtils.EMPTY);

		return action;
	}

	public static ThematicRole createAction()
	{
		ThematicRole action = new ThematicRole(
				ThematicRoleConstants.MANDATORY_ROLE_ACTION);
		// "What the operation does."
		action.setDescription(StringUtils.EMPTY);

		return action;
	}

	public static ThematicRole createAttribute()
	{
		final ThematicRole action = new ThematicRole("ATTRIBUTE");
		// "What the operation does."
		action.setDescription(StringUtils.EMPTY);
		return action;
	}

	public static ThematicRole createComparison()
	{
		ThematicRole comparison = new ThematicRole("COMPARISON");
		// "What identifies the OBJECT(s)."
		comparison.setDescription(StringUtils.EMPTY);

		return comparison;
	}

	public static ThematicRole createSource()
	{
		ThematicRole source = new ThematicRole("SOURCE");
		// "Where the OBJECT(s) come from"
		source.setDescription(StringUtils.EMPTY);

		return source;
	}

	public static ThematicRole createOrdering()
	{
		ThematicRole ordering = new ThematicRole("ORDERING");
		// "Defines the sequence of OBJECTs"
		ordering.setDescription(StringUtils.EMPTY);

		return ordering;
	}

	/**
	 * Creates a list of three addressees: Developer, Tester and Architect.
	 * 
	 * @return [OBJECT]
	 * 
	 * @source The three addressees and their attributes are hard coded in this method.
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static List<Addressee> createReferenceAddressees()
	{
		List<Addressee> addressees = new ArrayList<Addressee>();

		addressees.add(createDeveloper());
		addressees.add(createTester());
		addressees.add(createArchitect());

		return addressees;
	}

	/**
	 * Creates a list of thematic roles with action, comparison, source and ordering.
	 * 
	 * @return [OBJECT]
	 * 
	 * @source The thematic roles are hard coded in this method.
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static List<ThematicRole> createReferenceThematicRoles()
	{
		final List<ThematicRole> result = new ArrayList<ThematicRole>();

		result.add(createAction());
		result.add(createComparison());
		result.add(createSource());
		result.add(createOrdering());
		result.add(createObject());
		result.add(createRule());
		result.add(createAttribute());

		return result;
	}

	public static List<Addressee> createDeveloperSequence()
	{
		final List<Addressee> result = new ArrayList<Addressee>();
		result.add(createDeveloper());
		return result;
	}

	/**
	 * Collect all {@link Documentation}s in the {@link Operation} structure.
	 * 
	 * @param operation
	 *            [SOURCE]
	 * @return [OBJECT] all found {@link Documentation}s for the {@code operation}.
	 */
	public static List<Documentation> collectAllDocumentations(final Operation operation)
	{
		final List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.addAll(operation.getDocumentations());

		final Parameters inputParams = operation.getInputParameters();
		if (inputParams != null)
		{
			collectParameterDocumentations(inputParams.getParameters(), documentations);
		}

		final Parameters returnType = operation.getOutputParameters();
		if (returnType != null)
		{
			collectParameterDocumentations(returnType.getParameters(), documentations);
		}

		for (final Parameters exception : operation.getExceptions())
		{
			if (exception != null)
			{
				collectParameterDocumentations(exception.getParameters(), documentations);
			}
		}

		return documentations;
	}

	/**
	 * Collect all {@link Documentation}s in the {@link Parameter} structure and adds it
	 * to the result list <code>documentations</code>.
	 * 
	 * @param parameters
	 *            [SOURCE] The {@link Parameter} that should be processed.
	 * @param documentations
	 *            [DESINTATION] the result list of found {@link Documentation}s.
	 */
	private static void collectParameterDocumentations(final List<Parameter> parameters,
			final List<Documentation> documentations)
	{
		if (parameters != null && !parameters.isEmpty())
		{
			for (final Parameter param : parameters)
			{
				documentations.addAll(param.getDocumentations());
				collectParameterDocumentations(param.getComplexType(), documentations);
			}
		}
	}
}
