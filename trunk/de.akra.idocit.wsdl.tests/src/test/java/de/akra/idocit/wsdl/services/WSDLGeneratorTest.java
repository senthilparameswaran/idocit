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
package de.akra.idocit.wsdl.services;

import java.io.File;
import java.io.IOException;

import javax.wsdl.WSDLException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.wsdl.Constants;

public class WSDLGeneratorTest
{
	private static final String TMP_WSDL_FILE_NAME = Constants.FOLDER_OUT
			+ "CustomerServiceErrorDocs.wsdl";
	private static final String XML_SCHEMA_FILE_NAME = Constants.FOLDER_SOURCE
			+ "CompanySchema.xsd";
	private static final String TMP_XML_SCHEMA_FILE_NAME = Constants.FOLDER_OUT
			+ "CompanySchema.xsd";

	/**
	 * Clean up the out folder.
	 */
	@After
	public void cleanUp()
	{
		final File out = new File(Constants.FOLDER_OUT);
		if (out.exists() && out.isDirectory())
		{
			for (final File file : out.listFiles())
			{
				if (!file.delete())
				{
					throw new RuntimeException("Could not delete "
							+ file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Tests the error documentation-flag is written to an WSDL-file correctly:
	 * 
	 * <ol>
	 * <li>Parse the file {@link Constants#FOLDER_SOURCE}/CustomerServiceErrorDocs.wsdl
	 * <li>Check if the error documentation of ACTION is true.
	 * <li>Set it to false.
	 * <li>Write the changed interface to a file.
	 * <li>Parse the written file.
	 * <li>Check if the flag is set to false.
	 * <li>Set it to true.
	 * <li>Write it again into the file.
	 * <li>Parse it again and check if its still true.
	 * </ol>
	 * 
	 * @throws WSDLException
	 *             If the file CustomerServiceErrorDocs.wsdl contains a syntactical error
	 * @throws IOException
	 *             If the temporary files could not be read or written
	 */
	@Test
	public void testParseAndWriteWsdlWithErrorDocs() throws WSDLException, IOException
	{
		WSDLParserMock parser = new WSDLParserMock();

		InterfaceArtifact customerServiceWsdl = parser.parse(new File(
				Constants.FOLDER_SOURCE + "CustomerServiceErrorDocs.wsdl"));

		Documentation actionDocs = getFirstFindDocumentation(customerServiceWsdl);

		Assert.assertEquals("ACTION", actionDocs.getThematicRole().getName());
		Assert.assertTrue(actionDocs.isErrorCase());

		File tmpInterfaceFile = new File(TMP_WSDL_FILE_NAME);
		actionDocs.setErrorCase(false);
		parser.write(customerServiceWsdl, tmpInterfaceFile);

		File xmlSchema = new File(TMP_XML_SCHEMA_FILE_NAME);
		FileUtils.copyFile(new File(XML_SCHEMA_FILE_NAME), xmlSchema);
		customerServiceWsdl = parser.parse(tmpInterfaceFile);

		actionDocs = getFirstFindDocumentation(customerServiceWsdl);

		Assert.assertEquals("ACTION", actionDocs.getThematicRole().getName());
		Assert.assertFalse(actionDocs.isErrorCase());

		actionDocs.setErrorCase(true);
		parser.write(customerServiceWsdl, tmpInterfaceFile);

		customerServiceWsdl = parser.parse(tmpInterfaceFile);

		actionDocs = getFirstFindDocumentation(customerServiceWsdl);

		Assert.assertEquals("ACTION", actionDocs.getThematicRole().getName());
		Assert.assertTrue(actionDocs.isErrorCase());
	}

	private Documentation getFirstFindDocumentation(InterfaceArtifact customerServiceWsdl)
	{
		Interface customerServiceIntf = customerServiceWsdl.getInterfaces().get(0);
		Operation findOperation = customerServiceIntf.getOperations().get(0);

		Documentation actionDocs = findOperation.getDocumentations().get(0);
		return actionDocs;
	}
}
