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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeRC;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;
import de.akra.idocit.ui.services.CompositeTestPersistenceService;

public class RecommendRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, RecommendRolesCompositeRC, RecommendRolesCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, RecommendRolesCompositeRC, RecommendRolesCompositeSelection> createComposite(
			Composite parent, int style)
	{
		ServiceManager.getInstance().setPersistenceService(
				new CompositeTestPersistenceService());

		final RecommendRolesComposite composite = new RecommendRolesComposite(parent,
				style, getResourceConfiguration());
		final RecommendRolesCompositeSelection selection = new RecommendRolesCompositeSelection();

		List<ThematicRole> thematicRoles = new ArrayList<ThematicRole>();
		ThematicRole roleAgent = new ThematicRole("AGENT");
		ThematicRole roleAction = new ThematicRole("ACTION");
		thematicRoles.add(roleAgent);
		thematicRoles.add(roleAction);

		Set<ThematicRole> assignedThematicRoles = new TreeSet<ThematicRole>();
		assignedThematicRoles.add(thematicRoles.get(0));

		final Set<ThematicRole> assignedThematicRolesDocumentingErrors = new TreeSet<ThematicRole>();
		assignedThematicRolesDocumentingErrors.add(roleAction);

		selection.setOperationIdentifier("findCustomerById");
		selection.setAssignedThematicRoles(assignedThematicRoles);
		selection
				.setAssignedThematicRolesWithErrorDocs(assignedThematicRolesDocumentingErrors);
		selection.setReferenceThematicGridName("Searching Operations");
		
		composite.setSelection(selection);
		return composite;
	}

	@Override
	public RecommendRolesCompositeRC getResourceConfiguration()
	{
		RecommendRolesCompositeRC resConf = new RecommendRolesCompositeRC();
		resConf.setRoleWithoutErrorDocsWarningIcon(new Image(Display.getDefault(),
				"src/test/resources/de/akra/idocit/ui/composites/factories/int_obj.gif"));

		return resConf;
	}
}
