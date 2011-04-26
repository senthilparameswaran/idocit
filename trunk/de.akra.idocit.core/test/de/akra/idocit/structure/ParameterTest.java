package de.akra.idocit.structure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.akra.idocit.structure.impl.TestParameter;

/**
 * @author Dirk Meier-Eickhoff
 * 
 */
public class ParameterTest
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
		Parameter param = new TestParameter(parent, "");
		param.setDataTypeName("Customer");
		param.setIdentifier("cust");
		param.setSignatureElementPath("find.cust(Customer)");
		param.addDocpart(DocumentationTest.createDocumentation());

		Parameter innerParam = new TestParameter(param, "");
		innerParam.setDataTypeName("int");
		innerParam.setQualifiedDataTypeName("int");
		innerParam.setIdentifier("id");
		innerParam.setQualifiedIdentifier("id");
		innerParam.setSignatureElementPath("find.cust(Customer).id(int)");
		innerParam.addDocpart(DocumentationTest.createDocumentation());

		param.addParameter(innerParam);
		return param;
	}

}
