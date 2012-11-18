/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
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
	private static final String REFERENCE_HTML = "<!DOCTYPE>\n<html>\n<head>\n<title>Documentation of test.wsdl [Artifact]</title>\n<meta name=\"author\" content=\"AKRA GmbH\"/>\n<meta name=\"generator\" content=\"iDocIt!\"/>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n<link rel=\"stylesheet\" media=\"screen\" href=\"stylesheet.css\"/>\n</head>\n<body>\n<div id=\"header\">\n<h1 id=\"docTitle\">test.wsdl [Artifact]</h1>\n<h2 id=\"interfaceTitle\">Interface CustomerService [PortType]</h2>\n</div>\n<div id=\"nav\">\n<ul id=\"navElements\">\n<label class=\"navTitle\" for=\"operations\">Operations</label>\n<li><a href=\"#find [Operation]\">find [Operation]</a></li>\n</ul>\n</div>\n<div id=\"content\">\n<ul class=\"interfaceList\">\n<li>\n<h3 class=\"interfaceTitle\">Interface CustomerService [PortType]</h3>\n<p>\nElement: test.wsdl.CustomerService.find.input(Customer).Customer<br />\n<label class=\"title\">Role: </label>OBJECT<br />\nDEVELOPER: Documenation for developers.<br />\nMANAGER: Documenation for managers.<br />\n</p>\n<ul class=\"operationList\">\n<li>\n<h4 id=\"find [Operation]\">find [Operation]</h4>\n<div class=\"opDescription\">\n<p>\nElement: test.wsdl.CustomerService.find.input(Customer).Customer<br />\n<label class=\"title\">Role: </label>OBJECT<br />\nDEVELOPER: Documenation for developers.<br />\nMANAGER: Documenation for managers.<br />\n</p>\n</div>\n<div class=\"input\">\n<h5>Input</h5>\n<p>\nElement: test.wsdl.CustomerService.find.input(Customer).Customer<br />\n<label class=\"title\">Role: </label>OBJECT<br />\nDEVELOPER: Documenation for developers.<br />\nMANAGER: Documenation for managers.<br />\n</p>\n<ul class=\"paramDescription\">\n<li>\n<p>\nCust (Type: Customer) [Part]<br />\n</p>\n</li>\n</ul>\n</div>\n<div class=\"output\">\n<h5>Output</h5>\n<ul class=\"paramDescription\">\n<li>\n<p>\nCust (Type: Customer) [Part]<br />\n</p>\n</li>\n</ul>\n</div>\n</li>\n</ul>\n</li>\n</ul>\n</div>\n</body>\n</html>\n";
	
	@Test
	public void testGenerateHTML()
	{
		final InterfaceArtifact artifact= createInterfaceArtifact();
		final HTMLDocGenerator docGen = new HTMLDocGenerator(artifact);
		final String actualHTML = docGen.generateHTML();
		
		Assert.assertEquals(REFERENCE_HTML, actualHTML);
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
		Interface interf = new TestInterface(artifact, "PortType", Numerus.SINGULAR) {
		};
		interf.setIdentifier("CustomerService");
		interf.addDocpart(createDocumentation());
		interfaceList.add(interf);

		List<Operation> operations = new Vector<Operation>();
		Operation op = new TestOperation(interf, "Operation", "Searching Operations", Numerus.SINGULAR);
		op.setIdentifier("find");
		op.addDocpart(createDocumentation());
		operations.add(op);
		interf.setOperations(operations);

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage", Numerus.SINGULAR);
		inputParameters.setIdentifier("findIn");
		inputParameters.addDocpart(createDocumentation());
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part", Numerus.SINGULAR, true);
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "", Numerus.SINGULAR, false);
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "", Numerus.SINGULAR, false);
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new TestParameters(op, "OutputMessage", Numerus.SINGULAR);
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new TestParameter(outputParameters, "Part", Numerus.SINGULAR, true);
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		outputParameters.addParameter(paramCust);

		Parameter paramIdOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR, false);
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR, false);
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
