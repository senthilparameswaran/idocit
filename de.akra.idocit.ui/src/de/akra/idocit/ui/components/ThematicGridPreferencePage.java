/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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

import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.ui.composites.ManageThematicGridsComposite;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeAC;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeSelection;

/**
 * A {@link PreferencePage} for {@link ThematicGrid}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
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
		performApply();
		boolean saveState = super.performOk();
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