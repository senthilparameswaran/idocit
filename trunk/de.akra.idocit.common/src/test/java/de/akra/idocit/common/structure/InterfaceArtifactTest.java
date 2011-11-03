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
package de.akra.idocit.common.structure;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.akra.idocit.common.structure.impl.TestInterface;
import de.akra.idocit.common.structure.impl.TestInterfaceArtifact;
import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;

/**
 * Tests for {@link InterfaceArtifact}.
 */
public class InterfaceArtifactTest
{
	private static Logger logger = Logger
			.getLogger(InterfaceArtifactTest.class.getName());

	/**
	 * Tests {@link InterfaceArtifact#copy(SignatureElement)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create test InterfaceArtifact, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact sourceArtifact = createInterfaceArtifact();

			InterfaceArtifact copiedArtifact = (InterfaceArtifact) sourceArtifact
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			logger.log(Level.FINE, sourceArtifact.toString());
			logger.log(Level.FINE, copiedArtifact.toString());

			boolean res = sourceArtifact.equals(copiedArtifact);
			assertEquals(true, res);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create test InterfaceArtifact, copy it, change something in one
		 * structure and make an equals test.
		 * ****************************************************************************
		 */
		{
			InterfaceArtifact sourceArtifact = createInterfaceArtifact();

			InterfaceArtifact copiedArtifact = (InterfaceArtifact) sourceArtifact
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			assertEquals(true, sourceArtifact.equals(copiedArtifact));

			sourceArtifact.getInterfaces().get(0).setIdentifier("other");
			logger.log(Level.FINE, sourceArtifact.toString());
			logger.log(Level.FINE, copiedArtifact.toString());

			boolean res = sourceArtifact.equals(copiedArtifact);
			assertEquals(false, res);
		}
	}

	/**
	 * Create a test InterfaceArtifact.
	 * 
	 * @return a new InterfaceArtifact with some constant values.
	 */
	private InterfaceArtifact createInterfaceArtifact()
	{
		InterfaceArtifact artifact = new TestInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact", Numerus.SINGULAR);
		artifact.setIdentifier("test.wsdl");

		List<Interface> interfaceList = new Vector<Interface>();
		Interface interf = new TestInterface(artifact, "PortType", Numerus.SINGULAR);
		interf.setIdentifier("CustomerService");
		interfaceList.add(interf);

		List<Operation> operations = new Vector<Operation>();
		Operation op = new TestOperation(interf, "Operation", "Searching Operations", Numerus.SINGULAR);
		op.setIdentifier("find");
		op.setDocumentationChanged(true);
		operations.add(op);
		interf.setOperations(operations);

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage", Numerus.SINGULAR);
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part", Numerus.SINGULAR);
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "", Numerus.SINGULAR);
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "", Numerus.SINGULAR);
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new TestParameters(op, "OutputMessage", Numerus.SINGULAR);
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new TestParameter(outputParameters, "Part", Numerus.SINGULAR);
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		outputParameters.addParameter(paramCust);

		Parameter paramIdOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR);
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setDocumentationChanged(true);
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR);
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramCustOut.addParameter(paramNameOut);

		artifact.setInterfaces(interfaceList);

		return artifact;
	}
}
