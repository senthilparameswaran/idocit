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

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeRC;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;
import de.akra.idocit.ui.constants.ImageConstants;

/**
 * Eclipse {@link ViewPart} to show recommended {@link ThematicGrid}s for an identifier (
 * {@link RecommendRolesComposite}).
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendedGridsView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = RecommendedGridsView.class.getName();

	private RecommendRolesComposite recommendRolesComposite;

	/**
	 * The constructor.
	 */
	public RecommendedGridsView()
	{
		super();
	}

	/**
	 * Update the view with the new data.
	 * 
	 * @param viewSelection
	 *            [ATTRIBUTE] insert <code>null</code> to clear the view.
	 */
	public void setSelection(final RecommendedGridsViewSelection viewSelection)
	{
		RecommendRolesCompositeSelection selection = null;
		if (viewSelection != null)
		{
			// FIXME Do not init with new set here!
			selection = new RecommendRolesCompositeSelection(
					viewSelection.getOperationIdentifier(),
					viewSelection.getAssignedThematicRoles(),
					viewSelection.getReferenceThematicGridName(),
					new HashSet<ThematicRole>());
		}
		else
		{
			selection = new RecommendRolesCompositeSelection();
		}

		this.recommendRolesComposite.setSelection(selection);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		
		RecommendRolesCompositeRC resConf = new RecommendRolesCompositeRC();
		resConf.setRoleWithoutErrorDocsWarningIcon(ImageConstants.WARNING_ICON);
		
		this.recommendRolesComposite = new RecommendRolesComposite(parent, SWT.NONE, resConf);
		this.recommendRolesComposite.setSelection(new RecommendRolesCompositeSelection());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		this.recommendRolesComposite.setFocus();
	}
}