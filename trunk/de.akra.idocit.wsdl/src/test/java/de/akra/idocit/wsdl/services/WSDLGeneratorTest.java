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
	private final String TMP_WSDL_FILE_NAME = Constants.FOLDER_OUT
			+ "CustomerServiceErrorDocs.wsdl";
	private final String XML_SCHEMA_FILE_NAME = Constants.FOLDER_SOURCE
			+ "CompanySchema.xsd";
	private final String TMP_XML_SCHEMA_FILE_NAME = Constants.FOLDER_OUT
			+ "CompanySchema.xsd";

	/**
	 * Tests the error documentation-flag is written to an WSDL-file correctly:
	 * 
	 * 1) Parse the file test/source/CustomerServiceErrorDocs.wsdl 2) Check if the error
	 * documentation of ACTION is true. 3) Set it to false. 4) Write the changed interface
	 * to a file. 5) Parse the written file. 6) Check if the flag is set to false. 7) Set
	 * it to true. 8) Write it again into the file. 9) Parse it again and check if its
	 * still true.
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

	@After
	public void cleanUp()
	{
		File tmpWsdlFile = new File(TMP_WSDL_FILE_NAME);
		File tmpXmlSchemaFile = new File(TMP_XML_SCHEMA_FILE_NAME);

		if (tmpWsdlFile.exists())
		{
			tmpWsdlFile.delete();
		}

		if (tmpXmlSchemaFile.exists())
		{
			tmpXmlSchemaFile.delete();
		}
	}

	private Documentation getFirstFindDocumentation(InterfaceArtifact customerServiceWsdl)
	{
		Interface customerServiceIntf = customerServiceWsdl.getInterfaces().get(0);
		Operation findOperation = customerServiceIntf.getOperations().get(0);

		Documentation actionDocs = findOperation.getDocumentations().get(0);
		return actionDocs;
	}
}
