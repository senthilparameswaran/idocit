package de.akra.idocit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.constants.PreferenceStoreConstants;
import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.jankrause.diss.wsdl.common.services.WSDLTaggingService;

/**
 * The {@link IStartup} of iDocIt!.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class IDocItCoreStartup implements IStartup
{

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

	/**
	 * Initialize the used services for deriving the thematic grids.
	 */
	public static void initializeIDocIt()
	{
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		System.setProperty("wordnet.database.dir",
				store.getString(PreferenceStoreConstants.WORDNET_PATH));
		try
		{
			WSDLTaggingService.init(store
					.getString(PreferenceStoreConstants.TAGGER_MODEL_FILE));
		}
		catch (IOException ioEx)
		{
			// TODO: Route exception to Eclipse Platform
			throw new RuntimeException(ioEx);
		}
	}
}
