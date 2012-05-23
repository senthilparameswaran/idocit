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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.utils.Preconditions;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;

public class AddresseeUtils
{
	public static boolean containsAddresseeName(String addresseeName,
			Map<Addressee, String> docTexts)
	{
		Set<Entry<Addressee, String>> addressees = docTexts.entrySet();

		for (Entry<Addressee, String> addresseeEntry : addressees)
		{
			Addressee addressee = addresseeEntry.getKey();
			String name = addressee.getName();

			if (addresseeName.equals(name))
			{
				return true;
			}
		}

		return false;
	}

	private static boolean checkSignatureElement(SignatureElement sigElement,
			String addresseeName)
	{
		if (sigElement.getDocumentations() != null)
		{
			for (Documentation documentation : sigElement.getDocumentations())
			{
				Map<Addressee, String> addresseeDocs = documentation.getDocumentation();

				if (addresseeDocs != null)
				{
					if ((addresseeDocs.size() > 1)
							|| ((addresseeDocs.size() == 1) && !AddresseeUtils
									.containsAddresseeName(addresseeName, addresseeDocs)))
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	private static boolean checkParamsIfOnlyForDeveloper(List<Parameter> parameters,
			String addresseeName)
	{
		if (parameters != null)
		{
			for (Parameter param : parameters)
			{
				if (!checkSignatureElement(param, addresseeName)
						|| !checkParamsIfOnlyForDeveloper(param.getComplexType(),
								addresseeName))
				{
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkExceptionsIfOnlyForDeveloper(
			List<? extends Parameters> exceptions, String addresseeName)
	{
		if (exceptions != null)
		{
			for (Parameters exception : exceptions)
			{
				if (!checkOpsIfOnlyForDeveloper(exception, addresseeName))
				{
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkOpsIfOnlyForDeveloper(Parameters parameters,
			String addresseeName)
	{
		return checkSignatureElement(parameters, addresseeName)
				&& checkParamsIfOnlyForDeveloper(parameters.getParameters(),
						addresseeName);
	}

	private static boolean checkOpsIfOnlyForDeveloper(
			List<? extends Operation> operations, String addresseeName)
	{
		for (Operation operation : operations)
		{
			if (!checkSignatureElement(operation, addresseeName)
					|| !checkOpsIfOnlyForDeveloper(operation.getInputParameters(),
							addresseeName)
					|| !checkOpsIfOnlyForDeveloper(operation.getOutputParameters(),
							addresseeName)
					|| !checkExceptionsIfOnlyForDeveloper(operation.getExceptions(),
							addresseeName))
			{
				return false;
			}
		}

		return true;
	}

	private static boolean checkIfOnlyForDeveloper(List<? extends Interface> interfaces,
			String addresseeName)
	{
		for (Interface iface : interfaces)
		{
			if (!checkSignatureElement(iface, addresseeName)
					|| !checkIfOnlyForDeveloper(iface.getInnerInterfaces(), addresseeName)
					|| !checkOpsIfOnlyForDeveloper(iface.getOperations(), addresseeName))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean containsOnlyOneAddressee(JavaInterfaceArtifact artifact,
			String addresseeName)
	{
		Preconditions.checkNotNull(artifact, "The artifact must not be null");
		Preconditions.checkNotNull(addresseeName, "The addressee name must not be null.");

		return checkSignatureElement(artifact, addresseeName)
				&& checkIfOnlyForDeveloper(artifact.getInterfaces(), addresseeName);
	}

	public static Addressee findByName(String name, List<Addressee> addressees)
	{
		for (Addressee addressee : addressees)
		{
			if (name.equalsIgnoreCase(addressee.getName()))
			{
				return addressee;
			}
		}

		return null;
	}
}
