package de.akra.idocit.ui.components;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.composites.ManageThematicGridsComposite;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeAC;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeSelection;

/**
 * A {@link PreferencePage} for {@link ThematicGrid}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ThematicGridPreferencePage
		extends
		AbsPreferencePage<ManageThematicGridsCompositeAC, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection>
{

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		// Nothing to do!
	}

	@Override
	protected void addAllListener()
	{
		// Nothing to do!
	}

	@Override
	protected void removeAllListener()
	{
		// Nothing to do!
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		IPreferenceStore prefStore = getPreferenceStore();

		if (prefStore == null)
		{
			setPreferenceStore(PlatformUI.getPreferenceStore());
			prefStore = getPreferenceStore();
		}

		loadPreferences();
	}

	private void loadPreferences()
	{
		List<ThematicGrid> grids = PersistenceService.loadThematicGrids();
		List<ThematicRole> roles = PersistenceService.loadThematicRoles();

		ManageThematicGridsCompositeSelection selection = new ManageThematicGridsCompositeSelection();

		if (!grids.isEmpty())
		{
			selection.setActiveThematicGrid(grids.get(0));
		}

		selection.setThematicGrids(grids);
		selection.setRoles(roles);

		setSelection(selection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		boolean saveState = super.performOk();
		performApply();
		return saveState;
	}

	@Override
	protected void performDefaults()
	{
		loadPreferences();
	}

	@Override
	protected ManageThematicGridsComposite instanciateMask(Composite parent)
	{
		return new ManageThematicGridsComposite(parent,
				new ManageThematicGridsCompositeAC());
	}

	@Override
	protected void performApply()
	{
		List<ThematicGrid> grids = getSelection().getThematicGrids();
		PersistenceService.persistThematicGrids(grids);
		
		List<ThematicRole> roles = getSelection().getRoles();
		PersistenceService.persistThematicRoles(roles);
	}
}
