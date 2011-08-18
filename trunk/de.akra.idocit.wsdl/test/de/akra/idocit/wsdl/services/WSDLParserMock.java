/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

import org.eclipse.core.resources.IFile;

import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.wsdl.structure.WSDLInterfaceArtifact;

/**
 * Implementation for tests.
 * 
 * @author Dirk Meier/Eickhoff
 * 
 */
public class WSDLParserMock extends WSDLParser
{
	private static Logger logger = Logger.getLogger(WSDLParserMock.class.getName());

	/**
	 * Factory for WSDL parser.
	 */
	private WSDLFactory wsdlFactory;

	/**
	 * Parses the WSDL file and returns the containing interface structure (services).
	 * 
	 * @param file
	 *            the file to parse
	 * @return a new {@link InterfaceArtifact}.
	 * @throws WSDLException
	 * 
	 * @see de.akra.idocit.wsdl.services.WSDLParser#parse(IFile)
	 */
	public InterfaceArtifact parse(File file) throws WSDLException
	{
		if (wsdlFactory == null)
		{
			wsdlFactory = WSDLFactory.newInstance();
		}
		WSDLReader reader = wsdlFactory.newWSDLReader();

		logger.log(Level.INFO, "reader implementation = " + reader.toString());

		Definition wsdlDefinition = reader.readWSDL(file.getAbsolutePath());
		WSDLInterfaceParser iParser = new WSDLInterfaceParser(wsdlDefinition,
				file.getName(), getDelimiters());
		InterfaceArtifact iStruct = iParser.parse();

		return iStruct;
	}
	
	/**
	 * @param interfaceStructure
	 * @param file
	 * 
	 * @throws WSDLException
	 *             If the instance creation fails.
	 * @throws IOException
	 *             If any problem with the <code>file</code> occurs.
	 * @see de.akra.idocit.extensions.Parser#write(InterfaceArtifact, IFile)
	 */
	public void write(InterfaceArtifact interfaceStructure, File file)
			throws WSDLException, IOException
	{
		if (wsdlFactory == null)
		{
			wsdlFactory = WSDLFactory.newInstance();
		}

		// update WSDL Definition with new documentations
		WSDLInterfaceGenerator wsdlInterfaceGenerator = new WSDLInterfaceGenerator(
				(WSDLInterfaceArtifact) interfaceStructure);
		Definition updatedDefinition = wsdlInterfaceGenerator
				.updateDocumentationInDefinition();

		// write to file
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
		WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
		wsdlWriter.writeWSDL(updatedDefinition, bufWriter);
		bufWriter.close();
	}
}
