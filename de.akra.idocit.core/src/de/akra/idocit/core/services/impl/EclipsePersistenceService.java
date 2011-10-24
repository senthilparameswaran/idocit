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
package de.akra.idocit.core.services.impl;

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

import de.akra.idocit.common.factories.XStreamFactory;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.utils.DescribedItemNameComparator;

/**
 * Provides services to load and to write an {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class EclipsePersistenceService implements PersistenceService {
	/*
	 * Constants
	 */
	private static final String XML_ALIAS_ADDRESSEE = "addressee";

	private static long LAST_SAVE_TIME_OF_THEMATIC_GRIDS = -1;
	private static long LAST_SAVE_TIME_OF_THEMATIC_ROLES = -1;
	private static long LAST_SAVE_TIME_OF_ADDRESSEES = -1;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(EclipsePersistenceService.class
			.getName());

	private InputStream defaultThematicGrids = null;

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#init(java.io.InputStream)
	 */
	@Override
	public void init(InputStream defaultGrids) {
		defaultThematicGrids = defaultGrids;
		logger.info("The PersistenceService is now initialized with an input-stream to the default thematic grids.");
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#loadInterface(org.eclipse.core.resources.IFile)
	 */
	@Override
	public InterfaceArtifact loadInterface(IFile iFile) throws Exception {
		// there must be a file extension to determine the type
		if (iFile == null || iFile.getFileExtension().isEmpty()) {
			logger.log(Level.SEVERE,
					"iFile is not initialized or has no extension."
							+ (iFile != null ? " iFile="
									+ iFile.getFullPath().toOSString() : ""));
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		// get Parser depending on the file extension
		Parser parser = ServiceManager.getInstance().getParsingService()
				.getParser(iFile.getFileExtension());

		if (parser == null) {
			logger.log(Level.INFO,
					"Not supported type: " + iFile.getFileExtension());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		return parser.parse(iFile);
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#writeInterface(de.akra.idocit.common.structure.InterfaceArtifact, org.eclipse.core.resources.IFile)
	 */
	@Override
	public void writeInterface(InterfaceArtifact interfaceArtifact,
			IFile iFile) throws Exception {
		if (interfaceArtifact != null && iFile != null) {
			// get Parser depending on the file extension
			Parser parser = ServiceManager.getInstance().getParsingService()
					.getParser(iFile.getFileExtension());
			if (parser != null) {
				parser.write(interfaceArtifact, iFile);
			} else {
				logger.log(Level.SEVERE,
						"Try to write into a not supported file.");
			}
		} else {
			logger.log(Level.SEVERE,
					"The input parameters must be initalized. interfaceStructure="
							+ interfaceArtifact + "; iFile=" + iFile);
			throw new IllegalArgumentException(
					"The input parameters must be initalized.");
		}
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it
	 *         contains a value for {@link PreferenceStoreConstants#ADDRESSEES}.
	 */
	@Override
	public boolean areAddresseesInitialized() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.ADDRESSEES)
				&& !"".equals(prefStore
						.getString(PreferenceStoreConstants.ADDRESSEES));
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it
	 *         contains a value for
	 *         {@link PreferenceStoreConstants#THEMATIC_ROLES}.
	 */
	@Override
	public boolean areThematicRolesInitialized() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.THEMATIC_ROLES)
				&& !"".equals(prefStore
						.getString(PreferenceStoreConstants.THEMATIC_ROLES));
	}

	private XStream configureXStreamForAddressee() {
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
	private Map<String, String> convertResourceBundleToMap(
			PropertyResourceBundle resBundle) {
		Map<String, String> roles = new HashMap<String, String>();
		Enumeration<String> keys = resBundle.getKeys();

		while (keys.hasMoreElements()) {
			String role = keys.nextElement();
			String description = resBundle.getString(role);

			roles.put(role, description);
		}

		return roles;
	}

	/**
	 * Reads the in the plugin stored thematic roles.
	 * 
	 * @return Map of thematic role names mapping to their description.
	 */
	private Map<String, String> getInitialThematicRoles() {
		return convertResourceBundleToMap(IDocItActivator.getDefault()
				.getThematicRoleResourceBundle());
	}

	/**
	 * Reads the in the plugin stored addressees.
	 * 
	 * @return Map of thematic addressee names mapping to their description.
	 */
	private Map<String, String> getInitialAddressees() {
		return convertResourceBundleToMap(IDocItActivator.getDefault()
				.getAddresseeResourceBundle());
	}

	/**
	 * Stores the List of {@link Addressee}s into the {@link IPreferenceStore}.
	 * 
	 * @param addressees
	 *            {@link Addressee}s to store.
	 */
	@Override
	public void persistAddressees(List<Addressee> addressees) {
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForAddressee();

		for (Addressee a : addressees) {
			a.setDescription(StringUtils.removeLineBreaks(a.getDescription()));
		}

		String addresseeXML = stream.toXML(addressees);
		prefStore.putValue(PreferenceStoreConstants.ADDRESSEES, addresseeXML);
		LAST_SAVE_TIME_OF_ADDRESSEES = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#persistThematicRoles(java.util.List)
	 */
	@Override
	public void persistThematicRoles(List<ThematicRole> roles) {
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = XStreamFactory.configureXStreamForThematicRoles();
		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		for (ThematicRole role : roles) {
			if (role.getDescription() != null) {
				role.setDescription(StringUtils.removeLineBreaks(role
						.getDescription()));
			}
		}

		String rolesXML = stream.toXML(roles);
		prefStore.putValue(PreferenceStoreConstants.THEMATIC_ROLES, rolesXML);
		LAST_SAVE_TIME_OF_THEMATIC_ROLES = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#readInitialThematicRoles()
	 */
	@Override
	public List<ThematicRole> readInitialThematicRoles() {
		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		Map<String, String> initialRoles = getInitialThematicRoles();

		for (Entry<String, String> entry : initialRoles.entrySet()) {
			ThematicRole role = new ThematicRole(entry.getKey());
			role.setDescription(entry.getValue());

			roles.add(role);
		}

		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		return roles;
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#readInitialAddressees()
	 */
	@Override
	public List<Addressee> readInitialAddressees() {
		List<Addressee> addressees = new ArrayList<Addressee>();
		Map<String, String> initialAddressees = getInitialAddressees();

		for (Entry<String, String> entry : initialAddressees.entrySet()) {
			Addressee addressee = new Addressee(entry.getKey());
			addressee.setDescription(entry.getValue());

			addressees.add(addressee);
		}

		return addressees;
	}

	/**
	 * Load the {@link Addressee}s that are configured in the Eclipse preference
	 * pages for iDocIt!.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Addressee> loadConfiguredAddressees() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = configureXStreamForAddressee();
		List<Addressee> addressees = new ArrayList<Addressee>();

		String prefVal = prefStore
				.getString(PreferenceStoreConstants.ADDRESSEES);
		if (!prefVal.isEmpty()) {
			try {
				addressees = (List<Addressee>) stream.fromXML(prefVal);
			} catch (XStreamException e) {
				logger.log(Level.WARNING, "No addressees were loaded.", e);
			}
		}

		return addressees;
	}

	/**
	 * Load the {@link ThematicRole}s that are configured in the Eclipse
	 * preference pages for iDocIt!.
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicRole> loadThematicRoles() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = XStreamFactory.configureXStreamForThematicRoles();
		List<ThematicRole> roles = new ArrayList<ThematicRole>();

		String prefVal = prefStore
				.getString(PreferenceStoreConstants.THEMATIC_ROLES);
		if (!prefVal.isEmpty()) {
			try {
				roles = (List<ThematicRole>) stream.fromXML(prefVal);
			} catch (XStreamException e) {
				logger.log(Level.WARNING, "No thematic role were loaded.", e);
			}
		}

		Collections.sort(roles, DescribedItemNameComparator.getInstance());

		return roles;
	}

	/**
	 * Load the {@link ThematicGrid}s that are configured in the Eclipse
	 * preference pages for iDocIt!.
	 * 
	 * @return List of {@link ThematicGrid}s.
	 * 
	 * @throws UnitializedIDocItException
	 *             If the default grids should be loaded, but their input-stream
	 *             has not been initialized yet via
	 *             {@link PersistenceService#init(InputStream)}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicGrid> loadThematicGrids()
			throws UnitializedIDocItException {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = prefStore
				.getString(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING);

		if ((verbClassRoleAssocsXML != null)
				&& (!"".equals(verbClassRoleAssocsXML.trim()))) {
			try {
				return (List<ThematicGrid>) XStreamFactory
						.configureXStreamForThematicGrid().fromXML(
								verbClassRoleAssocsXML);
			} catch (XStreamException e) {
				logger.log(Level.SEVERE, e.getMessage());

				throw new RuntimeException(
						"The preference store of this workspace contains no configured thematic grids, but it is expected to do so! It is recommended to setup a new, consistent workspace.");
			}
		} else {
			if (defaultThematicGrids != null) {
				List<ThematicGrid> defaultGrids = (List<ThematicGrid>) XStreamFactory
						.configureXStreamForThematicGrid().fromXML(
								defaultThematicGrids);

				// Keep the default grids in preference store for the next time.
				persistThematicGrids(defaultGrids);

				return defaultGrids;
			} else {
				throw new UnitializedIDocItException(
						"The PersistenceService has not been initialized yet. The required input-stream to the default thematic roles is still null.");
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#persistThematicGrids(java.util.List)
	 */
	@Override
	public void persistThematicGrids(
			List<ThematicGrid> verbClassRoleAssociations) {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = XStreamFactory
				.configureXStreamForThematicGrid().toXML(
						verbClassRoleAssociations);

		prefStore.putValue(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING,
				verbClassRoleAssocsXML);
		LAST_SAVE_TIME_OF_THEMATIC_GRIDS = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#exportThematicGridsAsXml(java.io.File, java.util.List)
	 */
	@Override
	public void exportThematicGridsAsXml(final File destination,
			List<ThematicGrid> grids) throws IOException {
		XStream lvXStream = XStreamFactory.configureXStreamForThematicGrid();
		Writer lvWriter = null;

		try {
			lvWriter = new BufferedWriter(new FileWriter(destination));
			lvXStream.toXML(grids, lvWriter);
		} finally {
			if (lvWriter != null) {
				lvWriter.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#importThematicGrids(java.io.File)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ThematicGrid> importThematicGrids(final File source)
			throws IOException {
		Reader reader = null;
		List<ThematicGrid> grids = null;

		try {
			XStream xmlStream = XStreamFactory
					.configureXStreamForThematicGrid();
			reader = new BufferedReader(new FileReader(source));
			grids = (List<ThematicGrid>) xmlStream.fromXML(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return grids;
	}

	/**
	 * Converts the given verbs into an unordered list (HTML). Before generating
	 * the HTML-list, the verbs are sorted alphabetically.
	 * 
	 * @param verbs
	 *            The verbs to export
	 * @return The HTML-representation of the given verbs
	 */
	private String convertVerbListIntoHtml(Set<String> verbs) {
		List<String> sortedVerbs = new ArrayList<String>();
		sortedVerbs.addAll(verbs);
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (String verb : sortedVerbs) {
			buffer.append("\n\t\t\t\t\t\t<li>" + verb + "</li>");
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

	/**
	 * Converts the given map of {@link ThematicRole}s to booleans into an
	 * unordered list (HTML). The booleans indicate whether the role is
	 * mandatory or optional. Mandatory are printed in bold-font.
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s to export
	 * @return The HTML-representation of the given roles
	 */
	private String convertRoleMapIntoHtml(
			Map<ThematicRole, Boolean> roles) {
		List<ThematicRole> sortedVerbs = new ArrayList<ThematicRole>();
		sortedVerbs.addAll(roles.keySet());
		Collections.sort(sortedVerbs);

		StringBuffer buffer = new StringBuffer("\n\t\t\t\t\t<ul>");

		for (ThematicRole role : sortedVerbs) {
			if (roles.get(role).booleanValue()) {
				buffer.append("\n\t\t\t\t\t\t<li><b>" + role.getName()
						+ "</b></li>");
			} else {
				buffer.append("\n\t\t\t\t\t\t<li>" + role.getName() + "</li>");
			}
		}

		buffer.append("\n\t\t\t\t\t</ul>");

		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#exportThematicGridsAsHtml(java.io.File, java.util.List)
	 */
	@Override
	public void exportThematicGridsAsHtml(File destination,
			List<ThematicGrid> grids) throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(destination));
			writer.write("<html>\n\t<head/>\n\t<body>");

			for (ThematicGrid grid : grids) {
				writer.write("\n\t\t<table>");
				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td colspan=\"2\" valign=\"top\"><h3>"
						+ grid.getName() + "</h3></td>\n\t\t\t</tr>");

				writer.write("\n\t\t\t<tr>\n\t\t\t\t<td valign=\"top\"><u>Included verbs</u>"
						+ convertVerbListIntoHtml(grid.getVerbs())
						+ "\n\t\t\t\t</td>");
				writer.write("\n\t\t\t\t<td valign=\"top\"><u>Associated Thematic Roles</u>"
						+ convertRoleMapIntoHtml(grid.getRoles())
						+ "\n\t\t\t\t</td>\n\t\t\t</tr>");

				writer.write("\n\t\t</table>");
			}

			writer.write("\n\t<body>\n</html>");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#getLastSaveTimeOfThematicGrids()
	 */
	@Override
	public long getLastSaveTimeOfThematicGrids() {
		return LAST_SAVE_TIME_OF_THEMATIC_GRIDS;
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#getLastSaveTimeOfThematicRoles()
	 */
	@Override
	public long getLastSaveTimeOfThematicRoles() {
		return LAST_SAVE_TIME_OF_THEMATIC_ROLES;
	}

	/* (non-Javadoc)
	 * @see de.akra.idocit.core.services.impl.PersistenceService#getLastSaveTimeOfAddressees()
	 */
	@Override
	public long getLastSaveTimeOfAddressees() {
		return LAST_SAVE_TIME_OF_ADDRESSEES;
	}
}
