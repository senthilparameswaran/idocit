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
package de.akra.idocit.ui.components;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.ManageThematicGridsComposite;
import de.akra.idocit.ui.composites.ManageThematicGridsCompositeSelection;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * A {@link PreferencePage} for {@link ThematicGrid}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ThematicGridPreferencePage
		extends
		AbsPreferencePage<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection>
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
		return !getSelection().isNameExists();
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
		try
		{
			List<ThematicGrid> grids = ServiceManager.getInstance()
					.getPersistenceService().loadThematicGrids();
			List<ThematicRole> roles = ServiceManager.getInstance()
					.getPersistenceService().loadThematicRoles();

			ManageThematicGridsCompositeSelection selection = new ManageThematicGridsCompositeSelection();

			if (!grids.isEmpty())
			{
				selection.setActiveThematicGrid(grids.get(0));
			}

			selection.setThematicGrids(grids);
			selection.setRoles(roles);
			selection.setLastSaveTimeThematicRoles(ServiceManager.getInstance()
					.getPersistenceService().getLastSaveTimeOfThematicRoles());

			setSelection(selection);
		}
		catch (UnitializedIDocItException unInitEx)
		{
			setErrorMessage("iDocIt! has not been initialized completly yet. Please close this preference page and try again in a few seconds.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		if (checkState())
		{
			performApply();
			boolean saveState = super.performOk();
			return saveState;
		}
		return false;
	}

	/**
	 * Checks if changes can be saved or not.
	 * 
	 * @return true, if changes can be saved (all names are unique).
	 */
	private boolean checkState()
	{
		boolean res = isValid();
		if (!res)
		{
			MessageBoxUtils.openErrorBox(getShell(),
					"Changes can not be saved. Names must be unique.");
		}
		return res;
	}

	@Override
	protected void performDefaults()
	{
		PlatformUI.getPreferenceStore().setValue(
				PreferenceStoreConstants.VERBCLASS_ROLE_MAPPING, "");

		loadPreferences();
	}

	@Override
	protected ManageThematicGridsComposite instanciateMask(Composite parent)
	{
		return new ManageThematicGridsComposite(parent,
				EmptyActionConfiguration.getInstance());
	}

	@Override
	protected void performApply()
	{
		if (checkState())
		{
			List<ThematicGrid> grids = getSelection().getThematicGrids();
			ServiceManager.getInstance().getPersistenceService()
					.persistThematicGrids(grids);

			List<ThematicRole> roles = getSelection().getRoles();
			ServiceManager.getInstance().getPersistenceService()
					.persistThematicRoles(roles);
		}
	}
}
