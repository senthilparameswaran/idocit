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
package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;
import de.akra.idocit.ui.services.CompositeTestPersistenceService;

public class RecommendRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection> createComposite(
			Composite parent, int style)
	{
		ServiceManager.getInstance().setPersistenceService(
				new CompositeTestPersistenceService());

		final RecommendRolesComposite composite = new RecommendRolesComposite(parent,
				style);
		composite.setSelection(new RecommendRolesCompositeSelection());
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
