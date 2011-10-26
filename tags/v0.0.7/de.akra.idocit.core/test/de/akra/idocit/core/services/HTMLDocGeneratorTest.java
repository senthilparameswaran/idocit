package de.akra.idocit.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.Scope;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.structure.impl.TestInterface;
import de.akra.idocit.common.structure.impl.TestInterfaceArtifact;
import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;
import de.akra.idocit.core.services.impl.HTMLDocGenerator;

/**
 * Tests for {@link HTMLDocGenerator}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class HTMLDocGeneratorTest
{
	/**
	 * The delimiters for WSDL.
	 */
	private static final Delimiters delimiters;

	/**
	 * Initialize <code>delimiters</code>.
	 */
	static
	{
		// delimiters for WSDL
		delimiters = new Delimiters();
		delimiters.pathDelimiter = ";";
		// TODO maybe '}' as namespace delimiter?
		delimiters.namespaceDelimiter = "#";
		delimiters.typeDelimiter = "+";
	}
	
	@Test
	public void testGenerateHTML()
	{
		InterfaceArtifact artifact= createInterfaceArtifact();
		HTMLDocGenerator docGen = new HTMLDocGenerator(artifact, delimiters);
		System.out.println(docGen.generateHTML());
	}

	
	/**
	 * Create a test InterfaceArtifact.
	 * 
	 * @return a new InterfaceArtifact with some constant values.
	 */
	private InterfaceArtifact createInterfaceArtifact()
	{
		InterfaceArtifact artifact = new TestInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact");
		artifact.setIdentifier("test.wsdl");

		List<Interface> interfaceList = new Vector<Interface>();
		Interface interf = new TestInterface(artifact, "PortType") {
		};
		interf.setIdentifier("CustomerService");
		interf.addDocpart(createDocumentation());
		interfaceList.add(interf);

		List<Operation> operations = new Vector<Operation>();
		Operation op = new TestOperation(interf, "Operation", "Searching Operations");
		op.setIdentifier("find");
		op.addDocpart(createDocumentation());
		operations.add(op);
		interf.setOperations(operations);

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage");
		inputParameters.setIdentifier("findIn");
		inputParameters.addDocpart(createDocumentation());
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part");
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "");
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "");
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
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
		outputParameters.addParameter(paramCust);

		Parameter paramIdOut = new TestParameter(paramCustOut, "");
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "");
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramCustOut.addParameter(paramNameOut);

		artifact.setInterfaces(interfaceList);

		return artifact;
	}
	
	/**
	 * Create a test Documentation.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	public static Documentation createDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(new ThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("test.wsdl.CustomerService.find.input(Customer).Customer");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = new Addressee("DEVELOPER");
		Addressee manager = new Addressee("MANAGER");

		docMap.put(developer, "Documenation for developers.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Documenation for managers.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}
}
