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
package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaParameterTest
{
	/**
	 * Tests {@link Parameter#copy(SignatureElement)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create a Parameter, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			Parameter sourceParam = createParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Parameter copiedParam = (Parameter) sourceParam
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			boolean ret = sourceParam.equals(copiedParam);
			assertEquals(true, ret);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create a Parameter, copy it, change identifier and make an equals
		 * test that should return false.
		 * ****************************************************************************
		 */
		{
			Parameter sourceParam = createParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Parameter copiedParam = (Parameter) sourceParam
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedParam.setIdentifier("other name");
			assertEquals(false, sourceParam.equals(copiedParam));
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #2: Create a Parameter, copy it, change identifier from the
		 * containing Parameter and make an equals test that should return false.
		 * ****************************************************************************
		 * None
		 */
		{
			Parameter sourceParam = createParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Parameter copiedParam = (Parameter) sourceParam
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedParam.getComplexType().get(0).setIdentifier("other name");
			assertEquals(false, sourceParam.equals(copiedParam));
		}
	}

	/**
	 * Create a test Parameter.
	 * 
	 * @param parent
	 *            The parent SignatureElement.
	 * 
	 * @return a new Parameter with some constant values.
	 */
	public static Parameter createParameter(SignatureElement parent)
	{
		Parameter param = new JavaParameter(parent);
		param.setDataTypeName("Customer");
		param.setQualifiedDataTypeName("my.Customer");
		param.setIdentifier("cust");
		param.setSignatureElementPath("find.cust(Customer)");
		param.addDocpart(DocumentationTest.createDocumentation());

		Parameter innerParam = new JavaParameter(param);
		innerParam.setDataTypeName("int");
		innerParam.setIdentifier("id");
		innerParam.setQualifiedDataTypeName("int");
		innerParam.setSignatureElementPath("find.cust(Customer).id(int)");
		innerParam.addDocpart(DocumentationTest.createDocumentation());

		param.addParameter(innerParam);
		return param;
	}

}
