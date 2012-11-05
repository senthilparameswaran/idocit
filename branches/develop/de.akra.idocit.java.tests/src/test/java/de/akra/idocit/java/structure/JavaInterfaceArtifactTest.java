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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.TagElement;
import org.junit.Test;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.utils.StringUtils;

/**
 * Tests for {@link InterfaceArtifact}.
 */
public class JavaInterfaceArtifactTest
{
	private static Logger logger = Logger.getLogger(JavaInterfaceArtifactTest.class
			.getName());

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

			logger.log(Level.INFO, sourceArtifact.toString());
			logger.log(Level.INFO, copiedArtifact.toString());

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
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact", null,
				Numerus.SINGULAR);
		artifact.setIdentifier("test.java");

		List<Interface> interfaceList = new ArrayList<Interface>();
		Interface interf = new JavaInterface(artifact, "Class", Numerus.SINGULAR);
		interf.setIdentifier("CustomerService");
		interfaceList.add(interf);
		interf.setDocumentationChanged(true);

		AST ast = AST.newAST(AST.JLS3);
		TagElement tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_AUTHOR);

		List<TagElement> additionalTags = new ArrayList<TagElement>();
		additionalTags.add(tag);

		List<JavaMethod> operations = new ArrayList<JavaMethod>();
		JavaMethod op = new JavaMethod(interf, "Method", "Searching Operations",
				Numerus.SINGULAR);
		op.setIdentifier("find");
		op.setAdditionalTags(additionalTags);
		operations.add(op);
		interf.setOperations(operations);

		/*
		 * Input message
		 */
		Parameters inputParameters = new JavaParameters(op, StringUtils.EMPTY,
				Numerus.SINGULAR, false);
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new JavaParameter(inputParameters, Numerus.SINGULAR, true);
		paramCust.setDocumentationChanged(true);
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
		List<Parameters> exceptions = new ArrayList<Parameters>();

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

		artifact.setInterfaces(interfaceList);

		return artifact;
	}
}
