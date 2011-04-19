package de.akra.idocit.wsdl.services;

import java.io.BufferedWriter;
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

import de.akra.idocit.extensions.Parser;
import de.akra.idocit.structure.Delimiters;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.wsdl.structure.WSDLInterfaceArtifact;

/**
 * Parser implementation for WSDL interfaces.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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
	 *             If the instance creation fails.
	 * @see de.akra.idocit.extensions.Parser#parse(IFile)
	 */
	@Override
	public InterfaceArtifact parse(IFile iFile) throws WSDLException
	{
		if (wsdlFactory == null)
		{
			wsdlFactory = WSDLFactory.newInstance();
		}
		WSDLReader reader = wsdlFactory.newWSDLReader();

		logger.log(Level.FINE, "reader implementation = " + reader.toString());

		Definition wsdlDefinition = reader.readWSDL(iFile.getLocation().toFile()
				.getAbsolutePath());
		WSDLInterfaceParser iParser = new WSDLInterfaceParser(wsdlDefinition,
				iFile.getName(), delimiters);
		InterfaceArtifact artifact = iParser.parse();
		return artifact;
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
		Definition updatedDefinition = wsdlInterfaceGenerator
				.updateDocumentationInDefinition();

		// write to file
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(iFile.getLocation()
				.toFile()));
		WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
		wsdlWriter.writeWSDL(updatedDefinition, bufWriter);
		bufWriter.close();
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
