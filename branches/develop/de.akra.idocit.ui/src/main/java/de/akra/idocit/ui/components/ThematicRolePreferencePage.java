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
	private IConfigurationChangeListener thematicRoleConfigChangeListener;

	private void setupListener() throws CompositeInitializationException
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
		ServiceManager.getInstance().getPersistenceService()
				.addThematicRoleChangeListener(thematicRoleConfigChangeListener);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		ServiceManager.getInstance().getPersistenceService()
				.removeThematicRoleChangeListener(thematicRoleConfigChangeListener);
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

		setupListener();
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
			setErrorMessage("iDocIt! has not been initialized completly yet. Please close this preference page and try again in a few seconds.");
		}
	}

	@Override
	protected void performApply()
	{
		final List<ThematicRole> roles = new ArrayList<ThematicRole>();

		for (final ThematicRole role : getSelection().getThematicRoles())
		{
			roles.add(role);
		}

		ServiceManager.getInstance().getPersistenceService().persistThematicRoles(roles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		final boolean saveState = super.performOk();
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
