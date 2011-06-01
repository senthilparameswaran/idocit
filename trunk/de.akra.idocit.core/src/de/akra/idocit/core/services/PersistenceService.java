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
package de.akra.idocit.core.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.core.utils.StringUtils;
import de.akra.idocit.core.utils.ThematicRoleComparator;

/**
 * Provides services to load and to write an {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class PersistenceService
{
	private static final String XML_ALIAS_THEMATIC_GRID = "thematicGrid";

	private static final String XML_ALIAS_THEMATIC_ROLE = "thematicRole";

	private static final String XML_ALIAS_ADDRESSEE = "addressee";

	/*
	 * Constants
	 */
	private static final int MAX_CHARACTERS_PER_DESCRIPTION_LINE = 80;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(PersistenceService.class.getName());

	private static InputStream defaultThematicGrids = null;

	public static void init(InputStream defaultGrids)
	{
		defaultThematicGrids = defaultGrids;
	}

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
	public static InterfaceArtifact loadInterface(IFile iFile) throws Exception
	{
		// there must be a file extension to determine the type
		if (iFile == null || iFile.getFileExtension().isEmpty())
		{
			logger.log(Level.SEVERE, "iFile is not initialized or has no extension."
					+ (iFile != null ? " iFile=" + iFile.getFullPath().toOSString() : ""));
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		// get Parser depending on the file extension
		Parser parser = ParsingService.getParser(iFile.getFileExtension());

		if (parser == null)
		{
			logger.log(Level.INFO, "Not supported type: " + iFile.getFileExtension());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		return parser.parse(iFile);
	}

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
	public static void writeInterface(InterfaceArtifact interfaceArtifact, IFile iFile)
			throws Exception
	{
		if (interfaceArtifact != null && iFile != null)
		{
			// get Parser depending on the file extension
			Parser parser = ParsingService.getParser(iFile.getFileExtension());
			if (parser != null)
			{
				parser.write(interfaceArtifact, iFile);
			}
			else
			{
				logger.log(Level.SEVERE, "Try to write into a not supported file.");
			}
		}
		else
		{
			logger.log(Level.SEVERE,
					"The input parameters must be initalized. interfaceStructure="
							+ interfaceArtifact + "; iFile=" + iFile);
			throw new IllegalArgumentException("The input parameters must be initalized.");
		}
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it contains
	 *         a value for {@link PreferenceStoreConstants#ADDRESSEES}.
	 */
	public static boolean areAddresseesInitialized()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.ADDRESSEES)
				&& !"".equals(prefStore.getString(PreferenceStoreConstants.ADDRESSEES));
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it contains
	 *         a value for {@link PreferenceStoreConstants#THEMATIC_ROLES}.
	 */
	public static boolean areThematicRolesInitialized()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.THEMATIC_ROLES)
				&& !"".equals(prefStore
						.getString(PreferenceStoreConstants.THEMATIC_ROLES));
	}

	private static XStream configureXStreamForThematicGrid()
	{
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_THEMATIC_GRID, ThematicGrid.class);
		stream.alias(XML_ALIAS_THEMATIC_ROLE, ThematicRole.class);
		return stream;
	}

	private static XStream configureXStreamForThematicRoles()
	{
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_THEMATIC_ROLE, ThematicRole.class);
		return stream;
	}

	private static XStream configureXStreamForAddressee()
	{
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_ADDRESSEE, Addressee.class);
		return stream;
	}

	/**
	 * Converts a {@link PropertyResourceBundle} to a Map.
	 * 
	 * @param resBundle
	 *            The {@link PropertyResourceBundle} to convert.
	 * @return Map with item name mapping to it's description.
	 */
	private static Map<String, String> convertResourceBundleToMap(
			PropertyResourceBundle resBundle)
	{
		Map<String, String> roles = new HashMap<String, String>();
		Enumeration<String> keys = resBundle.getKeys();

		while (keys.hasMoreElements())
		{
			String role = keys.nextElement();
			String description = resBundle.getString(role);
			if (description != null)
			{
				description = StringUtils.addLineBreaks(description,
						MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' ');
			}

			roles.put(role, description);
		}

		return roles;
	}

	/**
	 * Reads the in the plugin stored thematic roles.
	 * 
	 * @return Map of thematic role names mapping to their description.
	 */
	private static Map<String, String> getInitialThematicRoles()
	{
		return convertResourceBundleToMap(IDocItActivator.getDefault()
				.getThematicRoleResourceBundle());
	}

	/**
	 * Reads the in the plugin stored addressees.
	 * 
	 * @return Map of thematic addressee names mapping to their description.
	 */
	private static Map<String, String> getInitialAddressees()
	{
		return convertResourceBundleToMap(IDocItActivator.getDefault()
				.getAddresseeResourceBundle());
	}

	/**
	 * Stores the List of {@link Addressee}s into the {@link IPreferenceStore}.
	 * 
	 * @param addressees
	 *            {@link Addressee}s to store.
	 */
	public static void persistAddressees(List<Addressee> addressees)
	{
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForAddressee();

		for (Addressee a : addressees)
		{
			a.setDescription(StringUtils.removeLineBreaks(a.getDescription()));
		}

		String addresseeXML = stream.toXML(addressees);
		prefStore.putValue(PreferenceStoreConstants.ADDRESSEES, addresseeXML);
	}

	/**
	 * Stores the List of {@link ThematicRole}s.
	 * 
	 * @param roles
	 *            {@link ThematicRole}s to store.
	 */
	public static void persistThematicRoles(List<ThematicRole> roles)
	{
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForThematicRoles();
		Collections.sort(roles, ThematicRoleComparator.getInstance());

		for (ThematicRole role : roles)
		{
			if (role.getDescription() != null)
			{
				role.setDescription(StringUtils.removeLineBreaks(role.getDescription()));
			}
		}

		String rolesXML = stream.toXML(roles);
		prefStore.putValue(PreferenceStoreConstants.THEMATIC_ROLES, rolesXML);
	}

	/**
	 * Read the stored {@link ThematicRole}s.
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	public static List<ThematicRole> readInitialThematicRoles()
	{
		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		Map<String, String> initialRoles = getInitialThematicRoles();

		for (Entry<String, String> entry : initialRoles.entrySet())
		{
			ThematicRole addressee = new ThematicRole(entry.getKey());
			addressee.setDescription(entry.getValue());

			roles.add(addressee);
		}

		Collections.sort(roles, ThematicRoleComparator.getInstance());
		
		return roles;
	}

	/**
	 * Read the stored {@link Addressee}s.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	public static List<Addressee> readInitialAddressees()
	{
		List<Addressee> addressees = new ArrayList<Addressee>();
		Map<String, String> initialAddressees = getInitialAddressees();

		for (Entry<String, String> entry : initialAddressees.entrySet())
		{
			Addressee addressee = new Addressee(entry.getKey());
			addressee.setDescription(entry.getValue());

			addressees.add(addressee);
		}

		return addressees;
	}

	/**
	 * Load the {@link Addressee}s that are configured in the Eclipse preference pages for
	 * iDocIt!.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	@SuppressWarnings("unchecked")
	public static List<Addressee> loadConfiguredAddressees()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForAddressee();
		List<Addressee> addressees = new ArrayList<Addressee>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.ADDRESSEES);
		if (!prefVal.isEmpty())
		{
			try
			{
				addressees = (List<Addressee>) stream.fromXML(prefVal);

				for (Addressee a : addressees)
				{
					if (a.getDescription() != null)
					{
						a.setDescription(StringUtils.addLineBreaks(a.getDescription(),
								MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' '));
					}
				}
			}
			catch (XStreamException e)
			{
				logger.log(Level.WARNING, "No addressees were loaded.", e);
			}
		}

		return addressees;
	}

	/**
	 * Load the {@link ThematicRole}s that are configured in the Eclipse preference pages
	 * for iDocIt!.
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	@SuppressWarnings("unchecked")
	public static List<ThematicRole> loadThematicRoles()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForThematicRoles();
		List<ThematicRole> roles = new ArrayList<ThematicRole>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.THEMATIC_ROLES);
		if (!prefVal.isEmpty())
		{
			try
			{
				roles = (List<ThematicRole>) stream.fromXML(prefVal);

				for (ThematicRole role : roles)
				{
					if (role.getDescription() != null)
					{
						role.setDescription(StringUtils.addLineBreaks(
								role.getDescription(),
								MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' '));
					}
				}
			}
			catch (XStreamException e)
			{
				logger.log(Level.WARNING, "No thematic role were loaded.", e);
			}
		}

		Collections.sort(roles, ThematicRoleComparator.getInstance());

		return roles;
	}

	/**
	 * Load the {@link ThematicGrid}s that are configured in the Eclipse preference pages
	 * for iDocIt!.
	 * 
	 * @return List of {@link ThematicGrid}s.
	 */
	@SuppressWarnings("unchecked")
	public static List<ThematicGrid> loadThematicGrids()
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = prefStore
				.getString(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING);

		if ((verbClassRoleAssocsXML != null)
				&& (!"".equals(verbClassRoleAssocsXML.trim())))
		{
			try
			{
				return (List<ThematicGrid>) configureXStreamForThematicGrid().fromXML(
						verbClassRoleAssocsXML);
			}
			catch (XStreamException e)
			{
				logger.log(Level.SEVERE, e.getMessage());

				throw new RuntimeException(
						"The preference store of this workspace contains no configured thematic grids, but it is expected to do so! It is recommended to setup a new, consistent workspace.");
			}
		}
		else
		{
			return (List<ThematicGrid>) configureXStreamForThematicGrid().fromXML(
					defaultThematicGrids);
		}
	}

	/**
	 * Stores the {@link ThematicGrid}s to the Eclipse {@link IPreferenceStore}.
	 * 
	 * @param verbClassRoleAssociations
	 *            {@link ThematicGrid}s to store.
	 */
	public static void persistThematicGrids(List<ThematicGrid> verbClassRoleAssociations)
	{
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = configureXStreamForThematicGrid().toXML(
				verbClassRoleAssociations);

		prefStore.putValue(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING,
				verbClassRoleAssocsXML);
	}

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
	public static void exportThematicGridsAsXml(final File destination,
			List<ThematicGrid> grids) throws IOException
	{
		XStream lvXStream = configureXStreamForThematicGrid();
		Writer lvWriter = null;

		try
		{
			lvWriter = new BufferedWriter(new FileWriter(destination));
			lvXStream.toXML(grids, lvWriter);
		}
		finally
		{
			if (lvWriter != null)
			{
				lvWriter.close();
			}
		}
	}

	/**
	 * Imports the list of {@link ThematicGrid}s from the given file <code>source</code>
	 * in XML-format.
	 * 
	 * @param source
	 *            The file to import the XML from
	 * @throws IOException
	 *             In case of an error
	 */
	@SuppressWarnings("unchecked")
	public static List<ThematicGrid> importThematicGrids(final File source)
			throws IOException
	{
		Reader reader = null;
		List<ThematicGrid> grids = null;

		try
		{
			XStream xmlStream = configureXStreamForThematicGrid();
			reader = new BufferedReader(new FileReader(source));
			grids = (List<ThematicGrid>) xmlStream.fromXML(reader);
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		return grids;
	}

	/**
	 * Converts the given verbs into an unordered list (HTML). Before generating the
	 * HTML-list, the verbs are sorted alphabetically.
	 * 
	 * @param verbs
	 *            The verbs to export
	 * @return The HTML-representation of the given verbs
	 */
	private static String convertVerbListIntoHtml(Set<String> verbs)
	{
		List<String> sortedVerbs = new ArrayList<String>();
		sortedVerbs.addAll(verbs);
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (String verb : sortedVerbs)
		{
			buffer.append("\n\t\t\t\t\t\t<li>" + verb + "</li>");
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

	/**
	 * Converts the given map of {@link ThematicRole}s to booleans into an unordered list
	 * (HTML). The booleans indicate whether the role is mandatory or optional. Mandatory
	 * are printed in bold-font.
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s to export
	 * @return The HTML-representation of the given roles
	 */
	private static String convertRoleMapIntoHtml(Map<ThematicRole, Boolean> roles)
	{
		List<ThematicRole> sortedVerbs = new ArrayList<ThematicRole>();
		sortedVerbs.addAll(roles.keySet());
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (ThematicRole role : sortedVerbs)
		{
			if (roles.get(role).booleanValue())
			{
				buffer.append("\n\t\t\t\t\t\t<li><b>" + role.getName() + "</b></li>");
			}
			else
			{
				buffer.append("\n\t\t\t\t\t\t<li>" + role.getName() + "</li>");
			}
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

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
	public static void exportThematicGridsAsHtml(File destination,
			List<ThematicGrid> grids) throws IOException
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(destination));
			writer.write("<html>\n\t<head/>\n\t<body>");

			for (ThematicGrid grid : grids)
			{
				writer.write("\n\t\t<table>");
				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td colspan=\"2\" valign=\"top\"><h3>"
						+ grid.getName() + "</h3></td>\n\t\t\t</tr>");

				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td valign=\"top\"><u>Included verbs</u>"
						+ convertVerbListIntoHtml(grid.getVerbs()) + "\n\t\t\t\t</td>");
				writer.write("\n\t\t\t\t<td valign=\"top\"><u>Associated Thematic Roles</u>"
						+ convertRoleMapIntoHtml(grid.getRoles())
						+ "\n\t\t\t\t</td>\n\t\t\t</tr>");

				writer.write("\n\t\t</table>");
			}

			writer.write("\n\t<body>\n</html>");
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}
}
