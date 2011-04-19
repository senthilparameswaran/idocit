package de.akra.idocit.wsdl.services;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.utils.TestUtils;

/**
 * Tests for {@link WSDLParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class WSDLParserTest
{
	private static Logger logger = Logger.getLogger(WSDLParserTest.class.getName());

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
		 * Test case #1: Parse the PortType of the WSDL file
		 * test/source/CustomerService.xml". One PortType is expected.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact iStruct = null;
			WSDLParserMock parser = new WSDLParserMock();

			iStruct = parser.parse(new File("test/source/CustomerService.wsdl"));
			// iStruct = parser.parse(new File("test/source/wsdl_46001"));

			assertEquals(1, iStruct.getInterfaces().size());

			StringBuffer parseResult = new StringBuffer();
			TestUtils.buildHierarchy(parseResult, iStruct, 0);

			logger.log(Level.INFO, parseResult.toString());

			/*
			 * write the result to a file
			 */
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
					"test/out/testParseWSDL_with_CustomerService.wsdl.out")));
			writer.write(parseResult.toString());
			writer.close();

			/*
			 * ***************************************************************************
			 * Test case #1.1: The generated structure of the prior parsed PortType of the
			 * WSDL file test/source/CustomerService.xml" is compared against an expected
			 * structure.
			 * ***************************************************************************
			 */

			// check result against an expectation
			String expectedFileContent = TestUtils
					.readFile("test/expected/testParseWSDL_with_CustomerService.xml");

			assertEquals(expectedFileContent.toString(), parseResult.toString());
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
	@Ignore
	public void testParseWsdl_100001WSDL() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Parse the PortType of the WSDL file
		 * test/source/CustomerService.xml". One PortType is expected.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact iStruct = null;
			WSDLParserMock parser = new WSDLParserMock();

			iStruct = parser.parse(new File("test/source/wsdl_100001.wsdl"));
			// iStruct = parser.parse(new File("test/source/wsdl_46001"));

			assertEquals(1, iStruct.getInterfaces().size());

			StringBuffer parseResult = new StringBuffer();
			TestUtils.buildHierarchy(parseResult, iStruct, 0);

			// logger.log(Level.INFO, parseResult.toString());

			/*
			 * write the result to a file
			 */
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
					"test/out/wsdl_100001.wsdl.out")));
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
		 * Test case #1.1: Parse the WSDL file test/source/CustomerService.xml, write it
		 * back to test/out/CustomerService.xml and compare the files.
		 * ******************************************************************************
		 */
		{
			final String srcFile = "test/source/CustomerService.wsdl";
			final String destFile = "test/out/CustomerService.wsdl";

			InterfaceArtifact sourceArtifact = null;
			WSDLParserMock parser = new WSDLParserMock();

			sourceArtifact = parser.parse(new File(srcFile));
			parser.write(sourceArtifact, new File(destFile));

			// TODO make file format the same and support all elements and attributes (not
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

			// equals() could not be tested, because each SignatureElement has an ID.
			assertEquals(sourceArtifact.size(), destArtifact.size());
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1:
		 * ****************************************************************************
		 * None
		 */
	}
}
