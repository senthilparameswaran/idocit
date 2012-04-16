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

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.easywsdl.wsdl.api.WSDLWriter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.wsdl.structure.WSDLInterfaceArtifact;

/**
 * Parser implementation for WSDL interfaces.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class WSDLParser implements Parser
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(WSDLParser.class.getName());

	/**
	 * The type of supported files.
	 */
	private static final String SUPPORTED_TYPE = "wsdl";

	/**
	 * The delimiters for WSDL.
	 */
	private static final Delimiters delimiters;

	/**
	 * Initialize <code>delimiters</code>.
	 */
	static
	{
		delimiters = new Delimiters();
		delimiters.pathDelimiter = ";";
		// TODO maybe '}' as namespace delimiter?
		delimiters.namespaceDelimiter = "#";
		delimiters.typeDelimiter = "+";
	}

	/**
	 * Factory for WSDL parser.
	 */
	private WSDLFactory wsdlFactory;

	/**
	 * Parses the WSDL file and returns the containing interface structure (services).
	 * 
	 * @return WSDL interface structure (services).
	 * @throws WSDLException
	 *             If the instance creation of the WSDLFactory fails.
	 * @see de.akra.idocit.extensions.Parser#parse(IFile)
	 */
	@Override
	public InterfaceArtifact parse(IFile iFile) throws Exception
	{
		if (wsdlFactory == null)
		{
			wsdlFactory = WSDLFactory.newInstance();
		}
		WSDLReader reader = wsdlFactory.newWSDLReader();
		logger.log(Level.FINE, "reader implementation = " + reader.toString());

		InputStream byteStream = new FileInputStream(iFile.getLocation().toFile());
		InputSource is = new InputSource(byteStream);
		Description wsdlDescription = reader.read(is);
		
		WSDLInterfaceParser iParser = new WSDLInterfaceParser(wsdlDescription,
				iFile.getName(), delimiters);
		InterfaceArtifact artifact = iParser.parse();
		return artifact;
				
//		WSDLReader reader = wsdlFactory.newWSDLReader();
//		Definition wsdlDefinition = reader.readWSDL(iFile.getLocation().toFile()
//				.getAbsolutePath());
//		WSDLInterfaceParser iParser = new WSDLInterfaceParser(wsdlDefinition,
//				iFile.getName(), delimiters);
//		InterfaceArtifact artifact = iParser.parse();
//		return artifact;
	}

	/**
	 * @throws WSDLException
	 *             If the instance creation fails.
	 * @throws IOException
	 *             If any problem with the <code>file</code> occurs.
	 * @see de.akra.idocit.extensions.Parser#write(InterfaceArtifact, IFile)
	 */
	@Override
	public void write(InterfaceArtifact interfaceStructure, IFile iFile)
			throws WSDLException, IOException
	{
		if (wsdlFactory == null)
		{
			wsdlFactory = WSDLFactory.newInstance();
		}

		// update WSDL Definition with new documentations
		WSDLInterfaceGenerator wsdlInterfaceGenerator = new WSDLInterfaceGenerator(
				(WSDLInterfaceArtifact) interfaceStructure);
		Description updatedDescription = wsdlInterfaceGenerator
				.updateDocumentationInDefinition();

		// write to file
		// TODO test how to write the file
		WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
		
		Document doc = wsdlWriter.getDocument(updatedDescription);
		logger.fine("Document:\n" + doc.toString());
		
		Writer writer = new FileWriter(iFile.getLocation().toFile());
		
		String res = wsdlWriter.writeWSDL(updatedDescription);
		logger.fine("Description:\n" + res);
		
		writer.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSupported(String type)
	{
		return type.equalsIgnoreCase(SUPPORTED_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSupportedType()
	{
		return SUPPORTED_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Delimiters getDelimiters()
	{
		return delimiters;
	}
}
