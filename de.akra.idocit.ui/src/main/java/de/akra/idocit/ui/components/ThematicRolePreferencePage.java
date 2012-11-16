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
package de.akra.idocit.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.listeners.IConfigurationChangeListener;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.ManageThematicRoleComposite;
import de.akra.idocit.ui.composites.ManageThematicRoleCompositeSelection;

/**
 * A {@link PreferencePage} for {@link ThematicRole}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ThematicRolePreferencePage
		extends
		AbsPreferencePage<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection>
{
	private static final String ERR_MSG_UNINITIALIZED = "iDocIt! has not been initialized completly yet. Please close this preference page and try again in a few seconds.";

	private IConfigurationChangeListener thematicRoleConfigChangeListener;

	private void initConfigListener() throws CompositeInitializationException
	{
		this.thematicRoleConfigChangeListener = new IConfigurationChangeListener() {

			@Override
			public void configurationChange()
			{
				final ManageThematicRoleCompositeSelection newSelection = getSelection()
						.clone();
				newSelection.setThematicRoles(ServiceManager.getInstance()
						.getPersistenceService().loadThematicRoles());

				setSelection(newSelection);
			}
		};
	}

	private void addConfigListener()
	{
		ServiceManager.getInstance().getPersistenceService()
				.addThematicRoleChangeListener(thematicRoleConfigChangeListener);
	}

	private void removeConfigListener()
	{
		ServiceManager.getInstance().getPersistenceService()
				.removeThematicRoleChangeListener(thematicRoleConfigChangeListener);

	}

	@Override
	public void dispose()
	{
		super.dispose();
		removeConfigListener();
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		// nothing to do
	}

	@Override
	protected void addAllListener()
	{
		// nothing to do
	}

	@Override
	protected void removeAllListener()
	{
		// nothing to do
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
		if (getPreferenceStore() == null)
		{
			setPreferenceStore(PlatformUI.getPreferenceStore());
		}

		loadPreferences(ServiceManager.getInstance().getPersistenceService()
				.loadThematicRoles());

		initConfigListener();
		addConfigListener();
	}

	private void loadPreferences(List<ThematicRole> roles)
	{
		final ManageThematicRoleCompositeSelection selection = new ManageThematicRoleCompositeSelection();
		selection.setActiveThematicRole(!roles.isEmpty() ? roles.get(0) : null);
		selection.setThematicRoles(roles);

		setSelection(selection);
	}

	@Override
	protected void performDefaults()
	{
		try
		{
			IDocItActivator.initRoleBasedRules();

			final List<ThematicRole> initialRoles = ServiceManager.getInstance()
					.getPersistenceService().readInitialThematicRoles();

			loadPreferences(initialRoles);
		}
		catch (final UnitializedIDocItException unInitEx)
		{
			setErrorMessage(ERR_MSG_UNINITIALIZED);
		}
	}

	@Override
	public boolean performOk()
	{
		final boolean saveState = super.performOk();
		performApply();
		return saveState;
	}

	@Override
	protected void performApply()
	{
		final List<ThematicRole> roles = new ArrayList<ThematicRole>();

		final ManageThematicRoleCompositeSelection selection = getSelection();
		for (final ThematicRole role : selection.getThematicRoles())
		{
			roles.add(role);
		}

		// we need not to be informed about changes, because we have already the latest
		// state.
		removeConfigListener();
		try
		{
			ServiceManager.getInstance().getPersistenceService()
					.persistThematicRoles(roles);
			deleteThematicRolesFromGrids(selection.getRemovedThematicRoles());
			selection.setRemovedThematicRoles(null);
		}
		catch (final UnitializedIDocItException e)
		{
			setErrorMessage(ERR_MSG_UNINITIALIZED);
		}
		addConfigListener();
	}

	/**
	 * @source The global configured {@link ThematicGrid}s.
	 * @param rolesToDelete
	 *            [OBJECT]
	 * @throws UnitializedIDocItException
	 */
	private void deleteThematicRolesFromGrids(final Collection<ThematicRole> rolesToDelete)
			throws UnitializedIDocItException
	{
		if (rolesToDelete != null && !rolesToDelete.isEmpty())
		{
			final java.util.List<ThematicGrid> grids = ServiceManager.getInstance()
					.getPersistenceService().loadThematicGrids();

			for (final ThematicGrid grid : grids)
			{
				for (final ThematicRole role : rolesToDelete)
				{
					grid.getRoles().remove(role);
				}
			}

			ServiceManager.getInstance().getPersistenceService()
					.persistThematicGrids(grids);
		}
	}

	@Override
	protected AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection> instanciateMask(
			Composite comp)
	{
		return new ManageThematicRoleComposite(comp);
	}
}
