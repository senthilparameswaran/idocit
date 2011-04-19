package de.akra.idocit.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.composites.ManageThematicRoleComposite;
import de.akra.idocit.ui.composites.ManageThematicRoleCompositeSelection;

/**
 * A {@link PreferencePage} for {@link ThematicRole}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ThematicRolePreferencePage
		extends
		AbsPreferencePage<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection>
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
		List<ThematicRole> roles = PersistenceService.loadThematicRoles();

		ManageThematicRoleCompositeSelection selection = new ManageThematicRoleCompositeSelection();
		selection.setActiveThematicRole(!roles.isEmpty() ? roles.get(0) : null);
		selection.setThematicRoles(roles);

		setSelection(selection);
	}

	@Override
	protected void performDefaults()
	{
		loadPreferences();
	}

	@Override
	protected void performApply()
	{
		List<ThematicRole> roles = new ArrayList<ThematicRole>();

		for (ThematicRole role : getSelection().getThematicRoles())
		{
			roles.add(role);
		}

		PersistenceService.persistThematicRoles(roles);
	}

	/*
	 * (non-Javadoc)
	 * 
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
	protected AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection> instanciateMask(
			Composite comp)
	{
		return new ManageThematicRoleComposite(comp);
	}
}
