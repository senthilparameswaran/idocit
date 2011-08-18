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
package de.akra.idocit.core.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Some useful methods for tests.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestUtils
{
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
	public static void buildHierarchy(StringBuffer result, SignatureElement sigElem, int level)
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
}
