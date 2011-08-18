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
package de.akra.idocit.ui.composites.factories;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.impl.TestInterface;
import de.akra.idocit.common.structure.impl.TestInterfaceArtifact;
import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;

/**
 * Helper factory to create a test {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
@Deprecated
public class InterfaceArtifactTestFactory
{
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger
			.getLogger(InterfaceArtifactTestFactory.class.getName());

	/**
	 * Create a small structure of an {@link InterfaceArtifact} for testing.
	 * 
	 * @return the created {@link InterfaceArtifact}.
	 */
	public static InterfaceArtifact createTestStructure()
	{
		InterfaceArtifact artifact = new TestInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact");

		artifact.setIdentifier("test.file");

		Interface iFace = new TestInterface(artifact, "PortType");
		iFace.setIdentifier("TestServices");

		List<Operation> opList = new Vector<Operation>();

		// 1. Operation
		Operation operation = new TestOperation(iFace, "Operation");
		operation.setIdentifier("TestOperation_1");

		Parameters paramList = createParameters(operation, "InputMessage", "TestOp_1_IN",
				2, 3);
		operation.setInputParameters(paramList);

		paramList = createParameters(operation, "OutputMessage", "TestOp_1_OUT", 3, 4);
		operation.setOutputParameters(paramList);

		opList.add(operation);

		// 2. Operation
		operation = new TestOperation(iFace, "Operation");
		operation.setIdentifier("TestOperation_2");

		paramList = createParameters(operation, "InputMessage", "TestOp_2_IN", 5, 7);
		operation.setInputParameters(paramList);

		paramList = createParameters(operation, "OutputMessage", "TestOp_2_OUT", 1, 0);
		operation.setOutputParameters(paramList);

		opList.add(operation);

		iFace.setOperations(opList);
		artifact.addInterface(iFace);

		return artifact;
	}

	/**
	 * Creates a {@link Parameters} with <code>paramCount</code> sub {@link Parameter}s in
	 * complexity of <code>complexIndex</code>.
	 * 
	 * @param paramCount
	 *            Number of the {@link Parameter}s which should be added to the new
	 *            {@link Parameters}.
	 * @param category
	 *            The category name for the {@link Parameters}.
	 * @param identifier
	 *            The identifier for the {@link Parameters}.
	 * @param complexIndex
	 *            The depth of complexity for the {@link Parameter}s.
	 * @return The new {@link Parameters}.
	 */
	private static Parameters createParameters(SignatureElement parent, String category,
			String identifier, int paramCount, int complexIndex)
	{
		Parameters paramList = new TestParameters(parent, category);
		paramList.setIdentifier(identifier);

		for (int i = 0; i < paramCount; i++)
		{
			Parameter param = createParameter(parent, complexIndex);
			setSignatureElementPaths(identifier, param);
			paramList.addParameter(param);
		}
		return paramList;
	}

	/**
	 * Creates a {@link Parameter} structure with a depth of <code>complexIndex</code>.
	 * 
	 * @param complexIndex
	 * @return new {@link Parameter} structure.
	 */
	private static Parameter createParameter(SignatureElement parent, int complexIndex)
	{
		// exit recursion
		if (complexIndex == 0)
		{
			return null;
		}

		Parameter param = new TestParameter(parent, "");
		param.setDataTypeName("TestType");

		for (int i = 0; i < complexIndex; i++)
		{
			param.setIdentifier("item" + complexIndex);
			param.setDataTypeName("int");
			param.addParameter(createParameter(param, complexIndex - 1));
		}

		return param;
	}

	/**
	 * Sets to all {@link Parameter}s in the structure the attribute
	 * {@link Parameter#setSignatureElementPath(String)}. 
	 * 
	 * @param path
	 *            The parent's path.
	 * @param parameter
	 *            The {@link Parameter} to which the
	 *            <code>path + IDENTIFIER + "(" + DATATYPE + ")"</code> should be set.
	 */
	private static void setSignatureElementPaths(String path, Parameter parameter)
	{
		if (parameter != null)
		{
			path += "." + parameter.getIdentifier() + "(" + parameter.getDataTypeName()
					+ ")";
			parameter.setSignatureElementPath(path);

			for (Parameter param : parameter.getComplexType())
			{
				setSignatureElementPaths(path, param);
			}
		}
		else
		{
			logger.log(Level.WARNING, path + "  --  null");
		}
	}

}
