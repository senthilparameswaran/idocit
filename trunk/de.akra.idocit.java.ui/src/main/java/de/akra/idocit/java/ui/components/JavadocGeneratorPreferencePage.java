/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.java.ui.components;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.ui.composites.ManageJavadocGeneratorComposite;
import de.akra.idocit.java.ui.composites.ManageJavadocGeneratorCompositeSelection;

public class JavadocGeneratorPreferencePage
		extends
		AbsPreferencePage<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection>
{
	
	@Override
	public void init(IWorkbench workbench)
	{
		// Set the preference store for the preference page.
		setPreferenceStore(PlatformUI.getPreferenceStore());
		getPreferenceStore().setDefault(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE_COMPLEX);
		
		final List<ThematicRole> roles = ServiceManager.getInstance()
				.getPersistenceService().loadThematicRoles();
		loadPreferences(roles);
	}
	
	private void loadPreferences(final List<ThematicRole> roles)
	{
		final ManageJavadocGeneratorCompositeSelection selection = new ManageJavadocGeneratorCompositeSelection();
		selection.setThematicRoles(roles);
		selection.setSelectedJavadocType(getPreferenceStore().getString(
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE));
		setSelection(selection);
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
		getPreferenceStore().setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				getSelection().getSelectedJavadocType());
	}

	@Override
	protected AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection> instanciateMask(
			Composite parent)
	{
		return new ManageJavadocGeneratorComposite(parent);
	}

	@Override
	protected void addAllListener()
	{
		// Nothing to do!
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		// Nothing to do!
	}

	@Override
	protected void removeAllListener()
	{
		// Nothing to do!
	}
}
