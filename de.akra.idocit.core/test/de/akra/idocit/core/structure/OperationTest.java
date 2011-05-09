package de.akra.idocit.core.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.structure.impl.TestOperation;
import de.akra.idocit.core.structure.impl.TestParameter;
import de.akra.idocit.core.structure.impl.TestParameters;

/**
 * Tests for {@link Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class OperationTest
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
		Operation op = new TestOperation(parent, "Operation");
		op.setIdentifier("findSomething");

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage");
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part");
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		paramCust.setSignatureElementPath("findIn.Cust(Customer)");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "");
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramId.setSignatureElementPath("findIn.Cust(Customer).id(int)");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "");
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramNameIn.setSignatureElementPath("findIn.Cust(Customer).name(String)");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new TestParameters(op, "OutputMessage");
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new TestParameter(outputParameters, "Part");
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		paramCustOut.setSignatureElementPath("findOut.Cust(Customer)");
		outputParameters.addParameter(paramCustOut);

		Parameter paramIdOut = new TestParameter(paramCustOut, "");
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setSignatureElementPath("findOut.Cust(Customer).id(int)");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "");
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramNameOut.setSignatureElementPath("findOut.Cust(Customer).name(String)");
		paramCustOut.addParameter(paramNameOut);

		Parameter test1 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "");
		test1.setIdentifier("id");
		test1.setDataTypeName("int");
		test1.setSignatureElementPath("findOut.Cust(Customer).id(int)");

		Parameter test2 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "");
		test2.setIdentifier("id");
		test2.setDataTypeName("int");
		test2.setSignatureElementPath("findOu2.Cust(Customer).id(int)");

		/*
		 * Exception messages
		 */
		List<Parameters> exceptions = new Vector<Parameters>();

		Parameters exception = new TestParameters(op, "FaultMessage");
		exception.setIdentifier("fault");
		exceptions.add(exception);

		Parameter exParam = new TestParameter(exception, "Part");
		exParam.setIdentifier("Exception");
		exParam.setDataTypeName("ExType");
		exParam.setSignatureElementPath("fault.Exception(ExType)");
		exception.addParameter(exParam);

		op.setExceptions(exceptions);

		return op;
	}

}
