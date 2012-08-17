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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.DisplayRecommendedRolesComposite;
import de.akra.idocit.ui.composites.DisplayRecommendedRolesCompositeRC;
import de.akra.idocit.ui.composites.DisplayRecommendedRolesCompositeSelection;

/**
 * Factory to create a {@link DisplayRecommendedRolesComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DisplayRecommendedRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, DisplayRecommendedRolesCompositeRC, DisplayRecommendedRolesCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, DisplayRecommendedRolesCompositeRC, DisplayRecommendedRolesCompositeSelection> createComposite(
			Composite pvParent, int style)
	{
		DisplayRecommendedRolesComposite recRolesComp = new DisplayRecommendedRolesComposite(
				pvParent, SWT.NONE, getResourceConfiguration());
		DisplayRecommendedRolesCompositeSelection selection = new DisplayRecommendedRolesCompositeSelection();

		List<ThematicRole> thematicRoles = new ArrayList<ThematicRole>();
		ThematicRole roleAgent = new ThematicRole("AGENT");
		ThematicRole roleAction = new ThematicRole("ACTION");
		thematicRoles.add(roleAgent);
		thematicRoles.add(roleAction);

		Set<ThematicRole> assignedThematicRoles = new TreeSet<ThematicRole>();
		assignedThematicRoles.add(thematicRoles.get(0));

		final Set<ThematicRole> assignedThematicRolesDocumentingErrors = new TreeSet<ThematicRole>();
		assignedThematicRolesDocumentingErrors.add(roleAction);

		final Map<String, ThematicGrid> recommendedGrids = new HashMap<String, ThematicGrid>();
		final Map<ThematicRole, Boolean> recRoles = new HashMap<ThematicRole, Boolean>();

		recRoles.put(thematicRoles.get(0), Boolean.TRUE);
		recRoles.put(thematicRoles.get(1), Boolean.FALSE);

		final ThematicGrid grid1 = new ThematicGrid();
		grid1.setName("Class 1");
		grid1.setRoles(recRoles);
		recommendedGrids.put(grid1.getName(), grid1);

		final ThematicGrid grid2 = new ThematicGrid();
		grid2.setName("Class 2");
		grid2.setRoles(recRoles);
		recommendedGrids.put(grid2.getName(), grid2);

		selection = selection.setRecommendedThematicGrids(recommendedGrids);
		selection = selection.setAssignedThematicRoles(assignedThematicRoles);
		selection = selection
				.setAssignedThematicRolesWithErrorDocumentation(assignedThematicRolesDocumentingErrors);

		recRolesComp.setSelection(selection);

		recRolesComp
				.addSelectionListener(new ISelectionListener<DisplayRecommendedRolesCompositeSelection>() {

					@Override
					public void selectionChanged(
							DisplayRecommendedRolesCompositeSelection arg0,
							PocUIComposite<DisplayRecommendedRolesCompositeSelection> arg1,
							Object sourceControl)
					{
						System.out.println(arg0);
					}
				});

		pvParent.pack();
		pvParent.layout();
		return recRolesComp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DisplayRecommendedRolesCompositeRC getResourceConfiguration()
	{
		DisplayRecommendedRolesCompositeRC resConf = new DisplayRecommendedRolesCompositeRC();
		resConf.setRoleWithoutErrorDocsWarningIcon(new Image(Display.getDefault(),
				"src/test/resources/de/akra/idocit/ui/composites/factories/int_obj.gif"));

		return resConf;
	}
}
