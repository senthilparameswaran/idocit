package de.akra.idocit.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.listeners.IDocItInitializationListener;
import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;
import de.jankrause.diss.wsdl.common.services.WSDLTaggingService;

/**
 * The {@link IStartup} of iDocIt!.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class IDocItInitialitationManager implements IStartup
{
	private static final Logger logger = Logger.getLogger(IDocItInitialitationManager.class
			.getName());

	private static final Set<IDocItInitializationListener> CONFIGURATION_LISTENERS = new HashSet<IDocItInitializationListener>();

	private static boolean initializedAtStartup = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void earlyStartup()
	{
		// Initialize the Preference Store.
		if (!PersistenceService.areAddresseesInitialized())
		{
			List<Addressee> addressees = PersistenceService.readInitialAddressees();
			PersistenceService.persistAddressees(addressees);
		}

		if (!PersistenceService.areThematicRolesInitialized())
		{
			List<ThematicRole> roles = PersistenceService.readInitialThematicRoles();
			PersistenceService.persistThematicRoles(roles);
		}

		if (!PersistenceService.areThematicGridsInitialized())
		{
			List<ThematicGrid> grids = new ArrayList<ThematicGrid>();

			ThematicGrid grid = new ThematicGrid();
			grid.setDescription("Description");
			grid.setName("Grid 1");
			grid.setRoles(new HashMap<ThematicRole, Boolean>());
			grid.setVerbs(new HashSet<String>());

			grids.add(grid);
			// TODO: Init Thematic Grids with Property File
			PersistenceService.persistThematicGrids(grids);
		}

		initializeIDocIt();
	}

	public static void addConfigurationListener(IDocItInitializationListener listener)
	{
		CONFIGURATION_LISTENERS.add(listener);

		if (!initializedAtStartup)
		{
			listener.initializationStarted();
		}
		else
		{
			listener.initializationFinished();
		}
	}

	public static void removeConfigurationListener(IDocItInitializationListener listener)
	{
		CONFIGURATION_LISTENERS.add(listener);
	}

	private static void fireChangeEvent(boolean initializationStarted)
	{
		for (IDocItInitializationListener listener : CONFIGURATION_LISTENERS)
		{
			if (initializationStarted)
			{
				listener.initializationStarted();
			}
			else
			{
				listener.initializationFinished();
			}
		}
	}

	/**
	 * Initialize the used services for deriving the thematic grids.
	 */
	public static void initializeIDocIt()
	{
		Thread initializer = new Thread() {
			@Override
			public void run()
			{
				fireChangeEvent(true);

				IPreferenceStore store = PlatformUI.getPreferenceStore();
				System.setProperty("wordnet.database.dir",
						store.getString(PreferenceStoreConstants.WORDNET_PATH));
				try
				{
					WSDLTaggingService.init(store
							.getString(PreferenceStoreConstants.TAGGER_MODEL_FILE));
				}
				catch (FileNotFoundException e)
				{
					logger.log(Level.WARNING, e.getMessage(), e);
				}
				catch (IOException ioEx)
				{
					// TODO: Route exception to Eclipse Platform
					throw new RuntimeException(ioEx);
				}

				initializedAtStartup = true;

				fireChangeEvent(false);
			}
		};

		initializer.start();
	}
}
