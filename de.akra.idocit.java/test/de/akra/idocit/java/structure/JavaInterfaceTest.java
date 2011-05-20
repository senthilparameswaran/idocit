/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Tests for {@link Interface}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaInterfaceTest
{
	/**
	 * Tests {@link Interface#copy(SignatureElement)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create an Operation, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			Interface sourceInterface = createInterface(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Interface copiedInterface = (Interface) sourceInterface
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			boolean ret = sourceInterface.equals(copiedInterface);
			assertEquals(true, ret);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Interface, copy it, change identifier and make an
		 * equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Interface sourceInterface = createInterface(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Interface copiedInterface = (Interface) sourceInterface
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedInterface.setIdentifier("other name");
			assertEquals(false, sourceInterface.equals(copiedInterface));
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Interface, copy it, change identifier from operation
		 * and make an equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Interface sourceInterface = createInterface(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Interface copiedInterface = (Interface) sourceInterface
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedInterface.getOperations().get(0).setIdentifier("other name");
			assertEquals(false, sourceInterface.equals(copiedInterface));
		}
	}

	private Interface createInterface(SignatureElement parent)
	{
		Interface newInterface = new JavaInterface(parent, "Interface");
		newInterface.setIdentifier("CustomerService");
		List<Operation> operations = new Vector<Operation>();
		newInterface.setOperations(operations);

		Operation op = new JavaMethod(parent, "Operation");
		op.setIdentifier("findSomething");
		operations.add(op);

		/*
		 * Input message
		 */
		Parameters inputParameters = new JavaParameters(op, "");
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new JavaParameter(inputParameters);
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		paramCust.setQualifiedDataTypeName("my.package.Customer");
		paramCust.setSignatureElementPath("findIn.Cust(Customer)");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new JavaParameter(paramCust);
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramId.setSignatureElementPath("findIn.Cust(Customer).id(int)");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new JavaParameter(paramCust);
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramNameIn.setQualifiedDataTypeName("java.lang.String");
		paramNameIn.setSignatureElementPath("findIn.Cust(Customer).name(String)");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new JavaParameters(op, "");
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new JavaParameter(outputParameters);
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		paramCustOut.setQualifiedDataTypeName("my.package.Customer");
		paramCustOut.setSignatureElementPath("findOut.Cust(Customer)");
		outputParameters.addParameter(paramCustOut);

		Parameter paramIdOut = new JavaParameter(paramCustOut);
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setQualifiedDataTypeName("int");
		paramIdOut.setSignatureElementPath("findOut.Cust(Customer).id(int)");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new JavaParameter(paramCustOut);
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramNameOut.setQualifiedDataTypeName("java.lang.String");
		paramNameOut.setSignatureElementPath("findOut.Cust(Customer).name(String)");
		paramCustOut.addParameter(paramNameOut);

		Parameter test1 = new JavaParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
		test1.setIdentifier("id");
		test1.setDataTypeName("int");
		test1.setQualifiedDataTypeName("int");
		test1.setSignatureElementPath("findOut.Cust(Customer).id(int)");

		Parameter test2 = new JavaParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
		test2.setIdentifier("id");
		test2.setDataTypeName("int");
		test2.setQualifiedDataTypeName("int");
		test2.setSignatureElementPath("findOu2.Cust(Customer).id(int)");

		/*
		 * Exception messages
		 */
		List<Parameters> exceptions = new Vector<Parameters>();

		Parameters exception = new JavaParameters(op, "FaultMessage");
		exception.setIdentifier("fault");
		exceptions.add(exception);

		Parameter exParam = new JavaParameter(exception);
		exParam.setIdentifier("Exception");
		exParam.setDataTypeName("ExType");
		exParam.setSignatureElementPath("fault.Exception(ExType)");
		exception.addParameter(exParam);

		op.setExceptions(exceptions);

		return newInterface;
	}
}
