package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.akra.idocit.structure.Interface;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.structure.Operation;
import de.akra.idocit.structure.Parameter;
import de.akra.idocit.structure.Parameters;
import de.akra.idocit.structure.SignatureElement;

/**
 * Tests for {@link InterfaceArtifact}.
 */
public class JavaInterfaceArtifactTest
{
	private static Logger logger = Logger
			.getLogger(JavaInterfaceArtifactTest.class.getName());

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
		InterfaceArtifact artifact = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact", null);
		artifact.setIdentifier("test.java");

		List<Interface> interfaceList = new Vector<Interface>();
		Interface interf = new JavaInterface(artifact, "Class") {
		};
		interf.setIdentifier("CustomerService");
		interfaceList.add(interf);

		List<Operation> operations = new Vector<Operation>();
		Operation op = new JavaMethod(interf, "Method");
		op.setIdentifier("find");
		operations.add(op);
		interf.setOperations(operations);

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

		artifact.setInterfaces(interfaceList);

		return artifact;
	}
}
