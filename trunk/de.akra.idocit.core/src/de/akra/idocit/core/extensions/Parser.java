package de.akra.idocit.core.extensions;

import java.io.File;

import org.eclipse.core.resources.IFile;

import de.akra.idocit.core.structure.Delimiters;
import de.akra.idocit.core.structure.InterfaceArtifact;

/**
 * Interface for different parser implementations.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public interface Parser
{
	/**
	 * Parses the <code>file</code> and returns the interface structure of the file.
	 * 
	 * @param iFile
	 *            The file which should be parsed.
	 * @return The interface structure of the file.
	 * @throws Exception
	 *             If anything went wrong.
	 */
	public InterfaceArtifact parse(IFile iFile) throws Exception;

	/**
	 * Writes the {@link InterfaceArtifact} into the {@link File} <code>file</code>.
	 * 
	 * @param interfaceStructure
	 *            The {@link InterfaceArtifact} which should be stored.
	 * @param iFile
	 *            The {@link File} into which the interface should be stored.
	 * @throws Exception
	 *             If anything went wrong.
	 */
	public void write(InterfaceArtifact interfaceStructure, IFile iFile) throws Exception;

	/**
	 * Checks if the file type is supported from the {@link Parser}.
	 * 
	 * @param type
	 *            The file type.
	 * @return true, if supported.
	 */
	public boolean isSupported(String type);

	/**
	 * @return The the supported file type.
	 */
	public String getSupportedType();

	/**
	 * @return {@link Delimiters} for the used programming language.
	 */
	public Delimiters getDelimiters();
}
