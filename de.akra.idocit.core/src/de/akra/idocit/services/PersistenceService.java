package de.akra.idocit.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

import de.akra.idocit.IDocItActivator;
import de.akra.idocit.constants.PreferenceStoreConstants;
import de.akra.idocit.extensions.Parser;
import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.utils.StringUtils;
import de.akra.idocit.utils.ThematicRoleComparator;

/**
 * Provides services to load and to write an {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class PersistenceService {
	/*
	 * Constants
	 */
	private static final int MAX_CHARACTERS_PER_DESCRIPTION_LINE = 80;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(PersistenceService.class.getName());

	/**
	 * Loads the interface from the {@link IFile} <code>iFile</code> and returns
	 * its structure.
	 * 
	 * @param iFile
	 *            The file to load.
	 * @return The interface structure represented in a
	 *         {@link InterfaceArtifact}. If <code>iFile == null</code> or the
	 *         file type is not supported
	 *         {@link InterfaceArtifact#NOT_SUPPORTED_ARTIFACT} is returned.
	 * @throws Exception
	 */
	public static InterfaceArtifact loadInterface(IFile iFile) throws Exception {
		// there must be a file extension to determine the type
		if (iFile == null || iFile.getFileExtension().isEmpty()) {
			logger.log(Level.SEVERE, "iFile is not initialized or has no extension."
					+ (iFile != null ? " iFile=" + iFile.getFullPath().toOSString() : ""));
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		// get Parser depending on the file extension
		Parser parser = ParsingService.getParser(iFile.getFileExtension());

		if (parser == null) {
			logger.log(Level.INFO, "Not supported type: " + iFile.getFileExtension());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		return parser.parse(iFile);
	}

	/**
	 * Writes the {@link InterfaceArtifact} <code>interfaceStructure</code> to
	 * the {@link File} <code>file</code>.
	 * 
	 * @param interfaceArtifact
	 *            The {@link InterfaceArtifact} that should be written into the
	 *            iFile.
	 * @param iFile
	 *            The {@link IFile} into which the
	 *            <code>interfaceArtifact</code> should be written.
	 * @throws Exception
	 */
	public static void writeInterface(InterfaceArtifact interfaceArtifact, IFile iFile) throws Exception {
		if (interfaceArtifact != null && iFile != null) {
			// get Parser depending on the file extension
			Parser parser = ParsingService.getParser(iFile.getFileExtension());
			if (parser != null) {
				parser.write(interfaceArtifact, iFile);
			} else {
				logger.log(Level.SEVERE, "Try to write into a not supported file.");
			}
		} else {
			logger.log(Level.SEVERE, "The input parameters must be initalized. interfaceStructure=" + interfaceArtifact + "; iFile=" + iFile);
			throw new IllegalArgumentException("The input parameters must be initalized.");
		}
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it
	 *         contains a value for {@link PreferenceStoreConstants#ADDRESSEES}.
	 */
	public static boolean areAddresseesInitialized() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.ADDRESSEES);
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it
	 *         contains a value for
	 *         {@link PreferenceStoreConstants#THEMATIC_ROLES}.
	 */
	public static boolean areThematicRolesInitialized() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.THEMATIC_ROLES);
	}

	private static XStream configureXStreamForThematicGrid() {
		XStream stream = new XStream();

		return stream;
	}

	/**
	 * 
	 * @return true, if the {@link IPreferenceStore} of Eclipse is loaded and it
	 *         contains a value for
	 *         {@link PreferenceStoreConstants#VERBCLASS_ROLE_MAPPING}.
	 */
	public static boolean areThematicGridsInitialized() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		return prefStore.contains(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING);
	}

	/**
	 * Converts a {@link PropertyResourceBundle} to a Map.
	 * 
	 * @param resBundle
	 *            The {@link PropertyResourceBundle} to convert.
	 * @return Map with item name mapping to it's description.
	 */
	private static Map<String, String> convertResourceBundleToMap(PropertyResourceBundle resBundle) {
		Map<String, String> roles = new HashMap<String, String>();
		Enumeration<String> keys = resBundle.getKeys();

		while (keys.hasMoreElements()) {
			String role = keys.nextElement();
			String description = resBundle.getString(role);
			if (description != null) {
				description = StringUtils.addLineBreaks(description, MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' ');
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
	private static Map<String, String> getInitialThematicRoles() {
		return convertResourceBundleToMap(IDocItActivator.getDefault().getThematicRoleResourceBundle());
	}

	/**
	 * Reads the in the plugin stored addressees.
	 * 
	 * @return Map of thematic addressee names mapping to their description.
	 */
	private static Map<String, String> getInitialAddressees() {
		return convertResourceBundleToMap(IDocItActivator.getDefault().getAddresseeResourceBundle());
	}

	/**
	 * Stores the List of {@link Addressee}s into the {@link IPreferenceStore}.
	 * 
	 * @param addressees
	 *            {@link Addressee}s to store.
	 */
	public static void persistAddressees(List<Addressee> addressees) {
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = new XStream();

		for (Addressee a : addressees) {
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
	public static void persistThematicRoles(List<ThematicRole> roles) {
		// TODO delete old entries from preference store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = new XStream();
		Collections.sort(roles, ThematicRoleComparator.getInstance());

		for (ThematicRole role : roles) {
			if (role.getDescription() != null) {
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
	public static List<ThematicRole> readInitialThematicRoles() {
		List<ThematicRole> addressees = new ArrayList<ThematicRole>();
		Map<String, String> initialRoles = getInitialThematicRoles();

		for (Entry<String, String> entry : initialRoles.entrySet()) {
			ThematicRole addressee = new ThematicRole(entry.getKey());
			addressee.setDescription(entry.getValue());

			addressees.add(addressee);
		}

		return addressees;
	}

	/**
	 * Read the stored {@link Addressee}s.
	 * 
	 * @return List of {@link Addressee}s.
	 */
	public static List<Addressee> readInitialAddressees() {
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
	@SuppressWarnings("unchecked")
	public static List<Addressee> loadConfiguredAddressees() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = new XStream();
		List<Addressee> addressees = new ArrayList<Addressee>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.ADDRESSEES);
		if (!prefVal.isEmpty()) {
			try {
				addressees = (List<Addressee>) stream.fromXML(prefVal);

				for (Addressee a : addressees) {
					if (a.getDescription() != null) {
						a.setDescription(StringUtils.addLineBreaks(a.getDescription(), MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' '));
					}
				}
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
	@SuppressWarnings("unchecked")
	public static List<ThematicRole> loadThematicRoles() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		XStream stream = new XStream();
		List<ThematicRole> roles = new ArrayList<ThematicRole>();

		String prefVal = prefStore.getString(PreferenceStoreConstants.THEMATIC_ROLES);
		if (!prefVal.isEmpty()) {
			try {
				roles = (List<ThematicRole>) stream.fromXML(prefVal);

				for (ThematicRole role : roles) {
					if (role.getDescription() != null) {
						role.setDescription(StringUtils.addLineBreaks(role.getDescription(), MAX_CHARACTERS_PER_DESCRIPTION_LINE, ' '));
					}
				}
			} catch (XStreamException e) {
				logger.log(Level.WARNING, "No thematic role were loaded.", e);
			}
		}

		Collections.sort(roles, ThematicRoleComparator.getInstance());

		return roles;
	}

	private static XStream configureXStreamForThematicRoles() {
		XStream stream = new XStream();

		stream.alias("role", ThematicRole.class);

		return stream;
	}

	/**
	 * Load the {@link ThematicGrid}s that are configured in the Eclipse
	 * preference pages for iDocIt!.
	 * 
	 * @return List of {@link ThematicGrid}s.
	 */
	@SuppressWarnings("unchecked")
	public static List<ThematicGrid> loadThematicGrids() {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = prefStore.getString(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING);

		if (verbClassRoleAssocsXML != null) {
			try {
				return (List<ThematicGrid>) configureXStreamForThematicRoles().fromXML(verbClassRoleAssocsXML);
			} catch (XStreamException e) {
				// TODO maybe display user message to inform, that the grids
				// could not be
				// read and iDocIt! uses no one, now.
				logger.log(Level.WARNING, e.getMessage());
			}
		}

		// init one default grid
		logger.log(Level.INFO, "Create one default thematic grid.");
		List<ThematicGrid> grids = new ArrayList<ThematicGrid>();
		ThematicGrid grid = new ThematicGrid();
		grid.setDescription("Description");
		grid.setName("Grid 1");
		grid.setRoles(new HashMap<ThematicRole, Boolean>());
		grid.setVerbs(new HashSet<String>());
		grids.add(grid);

		return grids;
	}

	/**
	 * Stores the {@link ThematicGrid}s to the Eclipse {@link IPreferenceStore}.
	 * 
	 * @param verbClassRoleAssociations
	 *            {@link ThematicGrid}s to store.
	 */
	public static void persistThematicGrids(List<ThematicGrid> verbClassRoleAssociations) {
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
		String verbClassRoleAssocsXML = configureXStreamForThematicRoles().toXML(verbClassRoleAssociations);

		prefStore.putValue(PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING, verbClassRoleAssocsXML);
	}

	public static void exportThematicGrids(final File destination, List<ThematicGrid> grids) throws IOException {
		XStream lvXStream = configureXStreamForThematicGrid();
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

	@SuppressWarnings("unchecked")
	public static List<ThematicGrid> importThematicGrids(final File source) throws IOException {
		Reader reader = null;
		List<ThematicGrid> grids = null;

		try {
			XStream xmlStream = configureXStreamForThematicGrid();
			reader = new BufferedReader(new FileReader(source));
			grids = (List<ThematicGrid>) xmlStream.fromXML(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return grids;
	}
}
