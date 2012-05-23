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
package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.ManageThematicRoleComposite;
import de.akra.idocit.ui.composites.ManageThematicRoleCompositeSelection;
import de.akra.idocit.ui.services.CompositeTestPersistenceService;

/**
 * Factory for {@link ManageThematicRoleComposite} for managing {@link Addressee}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ManageThematicRoleCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection>
{

	@Override
	public ManageThematicRoleComposite createComposite(Composite parent, int style)
	{
		ServiceManager.getInstance().setPersistenceService(
				new CompositeTestPersistenceService());

		ManageThematicRoleComposite composite = new ManageThematicRoleComposite(parent);

		ManageThematicRoleCompositeSelection selection = new ManageThematicRoleCompositeSelection();

		List<ThematicRole> addressees = new ArrayList<ThematicRole>();
		ThematicRole addressee1 = new ThematicRole("OBJECT");
		addressee1.setDescription("Object...");

		ThematicRole addressee2 = new ThematicRole("AGENT");
		addressee2.setDescription("Agent...");

		addressees.add(addressee1);
		addressees.add(addressee2);

		selection.setThematicRoles(addressees);
		selection.setActiveThematicRole(addressee1);

		composite.setSelection(selection);
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
