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
package de.akra.idocit.wsdl.services;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.WSDLException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.utils.TestUtils;
import de.akra.idocit.wsdl.Constants;

/**
 * Tests for {@link WSDLParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class WSDLParserTest
{
	private static Logger logger = Logger.getLogger(WSDLParserTest.class.getName());

	private static final String XML_SCHEMA_FILE_NAME = Constants.FOLDER_SOURCE
			+ "CompanySchema.xsd";
	private static final String TMP_XML_SCHEMA_FILE_NAME = Constants.FOLDER_OUT
			+ "CompanySchema.xsd";

	@BeforeClass
	public static void init() throws IOException
	{
		// copy the XSD to the out folder because the WSDL needs it there
		FileUtils.copyFile(new File(XML_SCHEMA_FILE_NAME), new File(
				TMP_XML_SCHEMA_FILE_NAME));
	}

	/**
	 * Clean up the out folder.
	 */
	@AfterClass
	public static void cleanUp()
	{
		final File out = new File(Constants.FOLDER_OUT);
		if (out.exists() && out.isDirectory())
		{
			for (final File file : out.listFiles())
			{
				file.delete();
			}
		}
	}

	/**
	 * Tests {@link WSDLParser#parse(org.eclipse.core.resources.IFile)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseWSDL() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Parse the PortType of the WSDL file {@link
		 * Constants#FOLDER_SOURCE}/CustomerService.xml". One PortType is expected.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact iStruct = null;
			WSDLParserMock parser = new WSDLParserMock();

			iStruct = parser.parse(new File(Constants.FOLDER_SOURCE
					+ "CustomerService.wsdl"));
			// iStruct = parser.parse(new File(Constants.FOLDER_SOURCE + "wsdl_46001"));

			assertEquals(1, iStruct.getInterfaces().size());

			StringBuffer parseResult = new StringBuffer();
			TestUtils.buildHierarchy(parseResult, iStruct, 0);

			logger.log(Level.INFO, parseResult.toString());

			/*
			 * write the result to a file
			 */
			PrintWriter writer = new PrintWriter(
					new BufferedWriter(new FileWriter(Constants.FOLDER_OUT
							+ "testParseWSDL_with_CustomerService.wsdl.out")));
			writer.write(parseResult.toString());
			writer.close();
			/*
			 * ***************************************************************************
			 * Test case #1.1: The generated structure of the prior parsed PortType of the
			 * WSDL file {@link Constants#FOLDER_SOURCE}/CustomerService.xml" is compared
			 * against an expected structure.
			 * ***************************************************************************
			 */

			// check result against an expectation
			String expectedFileContent = TestUtils.readFile(Constants.FOLDER_EXPECTED
					+ "testParseWSDL_with_CustomerService.xml");

			assertEquals(expectedFileContent.toString(), parseResult.toString());
		}

		/*
		 * ***************************************************************************
		 * Test case #1.2: The two operations in file wsdl_46001.wsdl are exspected to
		 * have thematic grids. The names of the grids are ascending integer numbers
		 * starting with 1.
		 * ***************************************************************************
		 */
		{
			WSDLParserMock parser = new WSDLParserMock();
			InterfaceArtifact iStruct = parser.parse(new File(Constants.FOLDER_SOURCE
					+ "wsdl_46001.wsdl"));
			int opNumber = 0;

			assertEquals(1, iStruct.getInterfaces().size());

			for (Interface interfac : iStruct.getInterfaces())
			{
				assertEquals(2, interfac.getOperations().size());

				for (Operation operation : interfac.getOperations())
				{
					assertEquals(String.valueOf(opNumber),
							operation.getThematicGridName());
					opNumber++;
				}
			}
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: three WSDL-operations should be classified correctly
		 * ****************************************************************************
		 * None
		 */
	}

	/**
	 * Tests {@link WSDLParser#parse(org.eclipse.core.resources.IFile)} with file
	 * "wsdl_100001.wsdl".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseWsdl_100001WSDL() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Parse the PortType of the WSDL file {@link
		 * Constants#FOLDER_SOURCE}/CustomerService.xml". One PortType is expected.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact iStruct = null;
			WSDLParserMock parser = new WSDLParserMock();

			iStruct = parser
					.parse(new File(Constants.FOLDER_SOURCE + "wsdl_100001.wsdl"));
			// iStruct = parser.parse(new File(Constants.FOLDER_SOURCE + "wsdl_46001"));

			assertEquals(1, iStruct.getInterfaces().size());

			StringBuffer parseResult = new StringBuffer();
			TestUtils.buildHierarchy(parseResult, iStruct, 0);

			// logger.log(Level.INFO, parseResult.toString());

			/*
			 * write the result to a file
			 */
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
					Constants.FOLDER_OUT + "wsdl_100001.wsdl.out")));
			writer.write(parseResult.toString());
			writer.close();
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: three WSDL-operations should be classified correctly
		 * ****************************************************************************
		 * None
		 */
	}

	/**
	 * Tests {@link WSDLParser#write(InterfaceArtifact, org.eclipse.core.resources.IFile)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWriteWSDL() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1.1: Parse the WSDL file {@link
		 * Constants#FOLDER_SOURCE}/CustomerService.xml, write it back to
		 * test/out/CustomerService.xml and compare the files.
		 * ******************************************************************************
		 */
		{
			final String srcFile = Constants.FOLDER_SOURCE + "CustomerService.wsdl";
			final String destFile = Constants.FOLDER_OUT + "CustomerService.wsdl";

			InterfaceArtifact sourceArtifact = null;
			WSDLParserMock parser = new WSDLParserMock();

			sourceArtifact = parser.parse(new File(srcFile));
			parser.write(sourceArtifact, new File(destFile));

			// TODO make file format the same and support all elements and
			// attributes (not
			// easy)
			// String expectedContent = readFile(srcFile);
			// String generatedContent = readFile(destFile);
			// assertEquals(expectedContent, generatedContent);

			/*
			 * **************************************************************************
			 * Test case #1.2: Parse the written test/out/CustomerService.xml and compare
			 * the size() (element cound).
			 * **************************************************************************
			 */

			InterfaceArtifact destArtifact = null;
			destArtifact = parser.parse(new File(destFile));

			logger.log(Level.FINE, sourceArtifact.toString());
			logger.log(Level.FINE, destArtifact.toString());

			// equals() could not be tested, because each SignatureElement has
			// an ID.
			assertEquals(sourceArtifact.size(), destArtifact.size());
		}

		/*
		 * Negative tests
		 */

		/*
		 * ***************************************************************************
		 * Test case #1: None
		 * ***************************************************************************
		 */
	}

	/**
	 * Parses the WSDL-file {@link Constants#FOLDER_SOURCE}/CustomerServiceErrorDocs.wsdl
	 * and tests if the documentation of the ACTION of the operation "find" contains error
	 * documentation. This is done by checking the flag
	 * {@link Documentation#isErrorCase()}.
	 * 
	 * @throws WSDLException
	 *             If the parsed WSDL-file contains a syntactical error
	 */
	@Test
	public void testParseDocumentationWithErrorFlag() throws WSDLException
	{
		WSDLParserMock parser = new WSDLParserMock();
		InterfaceArtifact customerServiceWsdl = parser.parse(new File(
				Constants.FOLDER_SOURCE + "CustomerServiceErrorDocs.wsdl"));

		Interface customerServiceIntf = customerServiceWsdl.getInterfaces().get(0);
		Operation findOperation = customerServiceIntf.getOperations().get(0);

		Documentation actionDocs = findOperation.getDocumentations().get(0);

		Assert.assertEquals("ACTION", actionDocs.getThematicRole().getName());
		Assert.assertTrue(actionDocs.isErrorCase());
	}
}
