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
package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.utils.StringUtils;

/**
 * Tests for {@link Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaMethodTest
{
	/**
	 * Tests {@link Operation#copy(SignatureElement)}
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
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			boolean ret = sourceOperation.equals(copiedOperation);
			assertEquals(true, ret);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Operation, copy it, change identifier and make an
		 * equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedOperation.setIdentifier("other name");
			assertEquals(false, sourceOperation.equals(copiedOperation));
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Operation, copy it, change identifier from output
		 * message and make an equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedOperation.getOutputParameters().getParameters().get(0)
					.setIdentifier("other name");
			assertEquals(false, sourceOperation.equals(copiedOperation));
		}
	}

	private Operation createOperation(SignatureElement parent)
	{
		Operation op = new JavaMethod(parent, "Operation", "Searching Operations",
				Numerus.SINGULAR);
		op.setIdentifier("findSomething");

		/*
		 * Input message
		 */
		Parameters inputParameters = new JavaParameters(op, StringUtils.EMPTY,
				Numerus.SINGULAR, false);
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new JavaParameter(inputParameters, Numerus.SINGULAR, true);
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		paramCust.setQualifiedDataTypeName("my.package.Customer");
		paramCust.setSignatureElementPath("findIn.Cust(Customer)");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new JavaParameter(paramCust, Numerus.SINGULAR, false);
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramId.setSignatureElementPath("findIn.Cust(Customer).id(int)");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new JavaParameter(paramCust, Numerus.SINGULAR, false);
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramNameIn.setQualifiedDataTypeName("java.lang.String");
		paramNameIn.setSignatureElementPath("findIn.Cust(Customer).name(String)");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new JavaParameters(op, StringUtils.EMPTY,
				Numerus.SINGULAR, false);
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new JavaParameter(outputParameters, Numerus.SINGULAR,
				true);
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		paramCustOut.setQualifiedDataTypeName("my.package.Customer");
		paramCustOut.setSignatureElementPath("findOut.Cust(Customer)");
		outputParameters.addParameter(paramCustOut);

		Parameter paramIdOut = new JavaParameter(paramCustOut, Numerus.SINGULAR, false);
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setQualifiedDataTypeName("int");
		paramIdOut.setSignatureElementPath("findOut.Cust(Customer).id(int)");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new JavaParameter(paramCustOut, Numerus.SINGULAR, false);
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramNameOut.setQualifiedDataTypeName("java.lang.String");
		paramNameOut.setSignatureElementPath("findOut.Cust(Customer).name(String)");
		paramCustOut.addParameter(paramNameOut);

		Parameter test1 = new JavaParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT,
				Numerus.SINGULAR, false);
		test1.setIdentifier("id");
		test1.setDataTypeName("int");
		test1.setQualifiedDataTypeName("int");
		test1.setSignatureElementPath("findOut.Cust(Customer).id(int)");

		Parameter test2 = new JavaParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT,
				Numerus.SINGULAR, false);
		test2.setIdentifier("id");
		test2.setDataTypeName("int");
		test2.setQualifiedDataTypeName("int");
		test2.setSignatureElementPath("findOu2.Cust(Customer).id(int)");

		/*
		 * Exception messages
		 */
		List<Parameters> exceptions = new Vector<Parameters>();

		Parameters exception = new JavaParameters(op, "FaultMessage", Numerus.SINGULAR,
				false);
		exception.setIdentifier("fault");
		exceptions.add(exception);

		Parameter exParam = new JavaParameter(exception, Numerus.SINGULAR, false);
		exParam.setIdentifier("Exception");
		exParam.setDataTypeName("ExType");
		exParam.setSignatureElementPath("fault.Exception(ExType)");
		exception.addParameter(exParam);

		op.setExceptions(exceptions);

		return op;
	}

}
