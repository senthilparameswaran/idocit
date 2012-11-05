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
package de.akra.idocit.java.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.ui.composites.ManageJavadocGeneratorComposite;
import de.akra.idocit.java.ui.composites.ManageJavadocGeneratorCompositeSelection;
import de.akra.idocit.ui.services.CompositeTestPersistenceService;

/**
 * Factory for {@link ManageJavadocGeneratorComposite} for managing Javac Generator
 * settings.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ManageJavadocGeneratorCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection>
{

	@Override
	public ManageJavadocGeneratorComposite createComposite(Composite parent, int style)
	{
		ServiceManager.getInstance().setPersistenceService(
				new CompositeTestPersistenceService());

		final ManageJavadocGeneratorComposite composite = new ManageJavadocGeneratorComposite(
				parent);
		final ManageJavadocGeneratorCompositeSelection selection = new ManageJavadocGeneratorCompositeSelection();

		final List<ThematicRole> thematicRoles = new ArrayList<ThematicRole>();
		selection.setThematicRoles(thematicRoles);

		ThematicRole role = new ThematicRole("OBJECT");
		role.setDescription("Object...");
		thematicRoles.add(role);

		role = new ThematicRole("AGENT");
		role.setDescription("Agent...");
		thematicRoles.add(role);

		selection.setSelectedJavadocType(PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE);
		
		composite.setSelection(selection);
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
