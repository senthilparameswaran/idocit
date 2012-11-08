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
package de.akra.idocit.core.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.ValidationReport;

/**
 * Provides services to load and to write an {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public interface PersistenceService
{

	public abstract void init();

	/**
	 * Loads the interface from the {@link IFile} <code>iFile</code> and returns its
	 * structure.
	 * 
	 * @param iFile
	 *            The file to load.
	 * @return The interface structure represented in a {@link InterfaceArtifact}. If
	 *         <code>iFile == null</code> or the file type is not supported
	 *         {@link InterfaceArtifact#NOT_SUPPORTED_ARTIFACT} is returned.
	 * @throws Exception
	 */
	public abstract InterfaceArtifact loadInterface(IFile iFile) throws Exception;

	/**
	 * Writes the {@link InterfaceArtifact} <code>interfaceStructure</code> to the
	 * {@link File} <code>file</code>.
	 * 
	 * @param interfaceArtifact
	 *            The {@link InterfaceArtifact} that should be written into the iFile.
	 * @param iFile
	 *            The {@link IFile} into which the <code>interfaceArtifact</code> should
	 *            be written.
	 * @throws Exception
	 */
	public abstract void writeInterface(InterfaceArtifact interfaceArtifact, IFile iFile)
			throws Exception;

	/**
	 * Returns <code>true</code> if adressees are available in the storage of this
	 * service. The storage (SOURCE) depends on the implementation of this interface.
	 * 
	 * @return See above
	 */
	public abstract boolean areAddresseesInitialized();

	/**
	 * Returns <code>true</code> if thematic roles are available in the storage of this
	 * service. The storage (SOURCE) depends on the implementation of this interface.
	 * 
	 * @return See above
	 */
	public abstract boolean areThematicRolesInitialized();

	/**
	 * Stores the List of {@link Addressee}s into the storage of this service.
	 * 
	 * @param addressees
	 *            {@link Addressee}s to store.
	 */
	public abstract void persistAddressees(List<Addressee> addressees);

	/**
	 * Stores the List of {@link ThematicRole}s.
	 * 
	 * @param roles
	 *            {@link ThematicRole}s to store.
	 */
	public abstract void persistThematicRoles(List<ThematicRole> roles);

	/**
	 * Read the stored {@link ThematicRole}s.
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	public abstract List<ThematicRole> readInitialThematicRoles();

	/**
	 * Read the stored {@link Addressee}s.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	public abstract List<Addressee> readInitialAddressees();

	/**
	 * Returns the configured addressees.
	 * 
	 * @return See above
	 */
	public abstract List<Addressee> loadConfiguredAddressees();

	/**
	 * Returns the configured thematic roles.
	 * 
	 * @return See above
	 */
	public abstract List<ThematicRole> loadThematicRoles();

	/**
	 * Returns the configured thematic grids.
	 * 
	 * @return See above
	 * 
	 * @throws UnitializedIDocItException
	 *             If the configured grids are not available
	 */
	public abstract List<ThematicGrid> loadThematicGrids()
			throws UnitializedIDocItException;

	/**
	 * Stores the {@link ThematicGrid}s to the Eclipse {@link IPreferenceStore}.
	 * 
	 * @param verbClassRoleAssociations
	 *            {@link ThematicGrid}s to store.
	 */
	public abstract void persistThematicGrids(List<ThematicGrid> verbClassRoleAssociations);

	/**
	 * Exports the given list of {@link ThematicGrid}s into the file
	 * <code>destination</code> in XML-format.
	 * 
	 * @param destination
	 *            The file to store the generated XML in
	 * @param grids
	 *            The list of {@link ThematicGrid}s to export
	 * @throws IOException
	 *             In case of an error
	 */
	public abstract void exportThematicGridsAsXml(final File destination,
			List<ThematicGrid> grids) throws IOException;

	/**
	 * Imports the list of {@link ThematicGrid}s from the given file <code>source</code>
	 * in XML-format.
	 * 
	 * @param source
	 *            The file to import the XML from
	 * @throws IOException
	 *             In case of an error
	 */
	public abstract List<ThematicGrid> importThematicGrids(final File source)
			throws IOException;

	/**
	 * Exports the given list of {@link ThematicGrid}s as HTML-file.
	 * 
	 * @param destination
	 *            The {@link File} to store the exported HTML in
	 * @param grids
	 *            The list of {@link ThematicGrid}s to export
	 * @throws IOException
	 *             In case of an error
	 */
	public abstract void exportThematicGridsAsHtml(File destination,
			List<ThematicGrid> grids) throws IOException;

	/**
	 * @return the lAST_SAVE_TIME_OF_THEMATIC_GRIDS
	 * 
	 * @Deprecated
	 */
	public abstract long getLastSaveTimeOfThematicGrids();

	/**
	 * @return the lAST_SAVE_TIME_OF_THEMATIC_ROLES
	 * 
	 * @Deprecated
	 */
	public abstract long getLastSaveTimeOfThematicRoles();

	/**
	 * @return the lAST_SAVE_TIME_OF_ADDRESSEES
	 * 
	 * @Deprecated
	 */
	public abstract long getLastSaveTimeOfAddressees();

	public ValidationReport validateInterfaceArtifact(InterfaceArtifact artifact,
			IFile ifile) throws Exception;

}