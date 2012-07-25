/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.java.utils;

import java.util.List;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;

public final class JavaInterfaceArtifactComparatorUtils
{
	public static boolean equalsDocumentations(final List<Documentation> documentations1,
			final List<Documentation> documentations2)
	{
		if (documentations1 == documentations2)
		{
			return true;
		}
		if ((documentations1 != null) && (documentations2 == null))
		{
			return false;
		}
		if ((documentations1 == null) && (documentations2 != null))
		{
			return false;
		}
		if (documentations1.size() != documentations2.size())
		{
			return false;
		}

		for (int i = 0; i < documentations1.size(); i++)
		{
			final Documentation doc1 = documentations1.get(i);
			final Documentation doc2 = documentations2.get(i);

			if ((doc1 != null) && (doc2 == null))
			{
				return false;
			}
			if ((doc1 == null) && (doc2 != null))
			{
				return false;
			}
			if ((doc1 != null) && !doc1.equals(doc2))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean equalsSignatureElement(final SignatureElement element1,
			final SignatureElement element2)
	{
		if (element1 == element2)
			return true;
		if (element1.getCategory() == null)
		{
			if (element2.getCategory() != null)
				return false;
		}
		else if (!element1.getCategory().equals(element2.getCategory()))
			return false;
		if (element1.getDocumentations() == null)
		{
			if (element2.getDocumentations() != null)
				return false;
		}
		else if (!equalsDocumentations(element1.getDocumentations(),
				element2.getDocumentations()))
			return false;
		if (element1.hasPublicAccessibleAttributes() != element2
				.hasPublicAccessibleAttributes())
			return false;
		if (element1.getIdentifier() == null)
		{
			if (element2.getIdentifier() != null)
				return false;
		}
		else if (!element1.getIdentifier().equals(element2.getIdentifier()))
			return false;
		if (element1.getNumerus() != element2.getNumerus())
			return false;
		if (element1.getQualifiedIdentifier() == null)
		{
			if (element2.getQualifiedIdentifier() != null)
				return false;
		}
		else if (!element1.getQualifiedIdentifier().equals(
				element2.getQualifiedIdentifier()))
			return false;

		return true;
	}

	public static boolean equalsParameter(final Parameter parameter1,
			final Parameter parameter2)
	{
		if (parameter1 == parameter2)
		{
			return true;
		}

		if ((parameter1 != null) && (parameter2 == null))
		{
			return false;
		}
		if ((parameter1 == null) && (parameter2 != null))
		{
			return false;
		}

		if (!equalsSignatureElement(parameter1, parameter2))
		{
			return false;
		}

		if (parameter1.getComplexType() == null)
		{
			if (parameter2.getComplexType() != null)
				return false;
		}
		else if (!equalsParameters(parameter1.getComplexType(),
				(parameter2.getComplexType())))
			return false;
		if (parameter1.getDataTypeName() == null)
		{
			if (parameter2.getDataTypeName() != null)
				return false;
		}
		else if (!parameter1.getDataTypeName().equals(parameter2.getDataTypeName()))
			return false;
		if (parameter1.getQualifiedDataTypeName() == null)
		{
			if (parameter2.getQualifiedDataTypeName() != null)
				return false;
		}
		else if (!parameter1.getQualifiedDataTypeName().equals(
				parameter2.getQualifiedDataTypeName()))
			return false;
		if (parameter1.getSignatureElementPath() == null)
		{
			if (parameter2.getSignatureElementPath() != null)
				return false;
		}
		else if (!parameter1.getSignatureElementPath().equals(
				parameter2.getSignatureElementPath()))
			return false;

		return true;
	}

	public static boolean equalsParameters(final List<? extends Parameter> parameters1,
			final List<? extends Parameter> parameters2)
	{
		if (parameters1 == parameters2)
		{
			return true;
		}
		if ((parameters1 != null) && (parameters2 == null))
		{
			return false;
		}
		if ((parameters1 == null) && (parameters2 != null))
		{
			return false;
		}
		if (parameters1.size() != parameters2.size())
		{
			return false;
		}

		for (int i = 0; i < parameters1.size(); i++)
		{
			final Parameter param1 = parameters1.get(i);
			final Parameter param2 = parameters2.get(i);

			if ((param1 != null) && (param2 == null))
			{
				return false;
			}
			if ((param1 == null) && (param2 != null))
			{
				return false;
			}

			if (!equalsSignatureElement(param1, param2))
			{
				return false;
			}

			if (!equalsParameter(param1, param2))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean equalsParameters(final Parameters parameters1,
			final Parameters parameters2)
	{
		if (parameters1 == parameters2)
		{
			return true;
		}

		if ((parameters1 != null) && (parameters2 == null))
		{
			return false;
		}
		if ((parameters1 == null) && (parameters2 != null))
		{
			return false;
		}

		if (!equalsSignatureElement(parameters1, parameters2))
		{
			return false;
		}

		if (!equalsParameters(parameters1.getParameters(), parameters2.getParameters()))
		{
			return false;
		}

		return true;
	}

	public static boolean equalsExceptions(
			final List<? extends Parameters> parameterList1,
			final List<? extends Parameters> parameterList2)
	{
		if (parameterList1 == parameterList2)
		{
			return true;
		}
		if ((parameterList1 != null) && (parameterList2 == null))
		{
			return false;
		}
		if ((parameterList1 == null) && (parameterList2 != null))
		{
			return false;
		}

		if (parameterList1.size() != parameterList2.size())
		{
			return false;
		}

		for (int i = 0; i < parameterList1.size(); i++)
		{
			final Parameters param1 = parameterList1.get(i);
			final Parameters param2 = parameterList2.get(i);

			if ((param1 != null) && (param2 == null))
			{
				return false;
			}
			if ((param1 == null) && (param2 != null))
			{
				return false;
			}
			if (!equalsParameters(param1, param2))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean equalsMethods(final JavaMethod method1, final JavaMethod method2)
	{
		if (method1 == method2)
		{
			return true;
		}
		if ((method1 != null) && (method2 == null))
		{
			return false;
		}
		if ((method1 == null) && (method2 != null))
		{
			return false;
		}

		if (!equalsSignatureElement(method1, method2))
		{
			return false;
		}

		if (method1.getExceptions() == null)
		{
			if (method2.getExceptions() != null)
				return false;
		}
		else if (!equalsExceptions(method1.getExceptions(), method2.getExceptions()))
			return false;
		if (method1.getInputParameters() == null)
		{
			if (method2.getInputParameters() != null)
				return false;
		}
		else if (!equalsParameters(method1.getInputParameters(),
				method2.getInputParameters()))
			return false;
		if (method1.getOutputParameters() == null)
		{
			if (method1.getOutputParameters() != null)
				return false;
		}
		else if (!equalsParameters(method1.getOutputParameters(),
				method2.getOutputParameters()))
			return false;
		if (method1.getThematicGridName() == null)
		{
			if (method2.getThematicGridName() != null)
				return false;
		}
		else if (!method1.getThematicGridName().equals(method2.getThematicGridName()))
			return false;

		return true;
	}

	public static boolean equalsInterfaces(final List<? extends Interface> interfaces1,
			final List<? extends Interface> interfaces2)
	{
		if (interfaces1 == interfaces2)
		{
			return true;
		}

		if ((interfaces1 != null) && (interfaces2 == null))
		{
			return false;
		}
		if ((interfaces1 == null) && (interfaces2 != null))
		{
			return false;
		}

		if (interfaces1.size() != interfaces2.size())
		{
			return false;
		}

		for (int i = 0; i < interfaces1.size(); i++)
		{
			final JavaInterface interface1 = (JavaInterface) interfaces1.get(i);
			final JavaInterface interface2 = (JavaInterface) interfaces2.get(i);

			if ((interface1 != null) && (interface2 == null))
			{
				return false;
			}
			if ((interface1 == null) && (interface2 != null))
			{
				return false;
			}
			if (!equalsInterfaces(interface1, interface2))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean equalsInterfaces(final JavaInterface interface1,
			final JavaInterface interface2)
	{
		if (interface1 == interface2)
		{
			return true;
		}
		if ((interface1 != null) && (interface2 == null))
		{
			return false;
		}
		if ((interface1 == null) && (interface2 != null))
		{
			return false;
		}

		if (!equalsSignatureElement(interface1, interface2))
		{
			return false;
		}

		if (!equalsInterfaces(interface1.getInnerInterfaces(),
				interface2.getInnerInterfaces()))
		{
			return false;
		}

		if (interface1.getAdditionalTags() == null)
		{
			if (interface2.getAdditionalTags() != null)
				return false;
		}
		else if (!interface1.getAdditionalTags().equals(interface2.getAdditionalTags()))
			return false;

		if ((interface1.getOperations() != null) && (interface2.getOperations() == null))
		{
			return false;
		}

		if ((interface1.getOperations() == null) && (interface2.getOperations() != null))
		{
			return false;
		}

		if (interface1.getOperations().size() != interface2.getOperations().size())
		{
			return false;
		}

		for (int i = 0; i < interface1.getOperations().size(); i++)
		{
			final JavaMethod operation1 = (JavaMethod) interface1.getOperations().get(i);
			final JavaMethod operation2 = (JavaMethod) interface2.getOperations().get(i);

			if (!equalsMethods(operation1, operation2))
			{
				return false;
			}

		}

		return true;
	}

	private static boolean equalsReference(final Object obj1, final Object obj2)
	{
		if (obj1 == obj2)
		{
			return true;
		}
		if ((obj1 != null) && (obj2 == null))
		{
			return false;
		}
		if ((obj1 == null) && (obj2 != null))
		{
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean equalsInterfaceArtifacts(final JavaInterfaceArtifact artifact1,
			final JavaInterfaceArtifact artifact2)
	{
		if (!equalsReference(artifact1, artifact2))
		{
			return false;
		}

		if (!equalsSignatureElement(artifact1, artifact2))
		{
			return false;
		}

		if (!equalsReference(artifact1.getInterfaces(), artifact2.getInterfaces()))
		{
			return false;
		}

		final List<JavaInterface> interfaces1 = (List<JavaInterface>) artifact1
				.getInterfaces();
		final List<JavaInterface> interfaces2 = (List<JavaInterface>) artifact2
				.getInterfaces();

		if (interfaces1.size() != interfaces2.size())
		{
			return false;
		}

		for (int i = 0; i < interfaces1.size(); i++)
		{
			final JavaInterface interface1 = interfaces1.get(i);
			final JavaInterface interface2 = interfaces2.get(i);

			if (!equalsInterfaces(interface1, interface2))
			{
				return false;
			}
		}

		return true;
	}
}
