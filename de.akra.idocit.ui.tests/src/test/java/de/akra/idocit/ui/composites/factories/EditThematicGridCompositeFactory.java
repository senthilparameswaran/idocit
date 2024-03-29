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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.EditThematicGridComposite;
import de.akra.idocit.ui.composites.EditThematicGridCompositeSelection;

/**
 * Factory for {@link EditThematicGridComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicGridCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicGridCompositeSelection>
{

	@Override
	public EditThematicGridComposite createComposite(Composite parent, int style)
	{
		EditThematicGridComposite composite = new EditThematicGridComposite(parent);

		EditThematicGridCompositeSelection selection = new EditThematicGridCompositeSelection();
		ThematicGrid grid = new ThematicGrid();

		grid.setDescription("A very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long descriptionA very long description");
		grid.setName("Name");

		Set<String> verbs = new HashSet<String>();
		verbs.add("get");
		verbs.add("write");
		grid.setVerbs(verbs);

		grid.setRefernceVerb("write");

		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		ThematicRole roleAgent = new ThematicRole("AGENT");
		ThematicRole roleAction = new ThematicRole("ACTION");
		roles.add(roleAgent);
		roles.add(roleAction);
		selection = selection.setRoles(roles);
		selection = selection.setActiveRole(roleAction);

		Map<ThematicRole, Boolean> selectedRoles = new HashMap<ThematicRole, Boolean>();
		selectedRoles.put(roleAction, Boolean.FALSE);
		grid.setRoles(selectedRoles);

		Map<String, String> gridBasedRules = new HashMap<String, String>();
		gridBasedRules.put("AGENT", ThematicGridConstants.DEFAULT_RULE);
		gridBasedRules.put("ACTION", ThematicGridConstants.DEFAULT_RULE);

		grid.setGridBasedRules(gridBasedRules);

		selection = selection.setActiveThematicGrid(grid);

		composite.setSelection(selection);

		composite
				.addSelectionListener(new ISelectionListener<EditThematicGridCompositeSelection>() {

					@Override
					public void selectionChanged(EditThematicGridCompositeSelection arg0,
							PocUIComposite<EditThematicGridCompositeSelection> arg1,
							Object sourceControl)
					{
						System.out.println(arg0);

					}
				});

		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
