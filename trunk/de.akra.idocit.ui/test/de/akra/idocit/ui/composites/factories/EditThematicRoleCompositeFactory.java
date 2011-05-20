/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.ui.composites.EditThematicRoleComposite;
import de.akra.idocit.ui.composites.EditThematicRoleCompositeSelection;

/**
 * Factory for {@link EditThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class EditThematicRoleCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicRoleCompositeSelection>
{

	@Override
	public EditThematicRoleComposite createComposite(
			Composite pvParent)
	{
		EditThematicRoleComposite composite = new EditThematicRoleComposite(pvParent);
		
		EditThematicRoleCompositeSelection selection = new EditThematicRoleCompositeSelection();
		
		ThematicRole itemOrig = new ThematicRole("My original name");
		itemOrig.setDescription("My original description");
		
		ThematicRole itemModify = new ThematicRole("My original name");
		itemModify.setDescription("My original description");
		
		selection.setItem(itemOrig);
		selection.setModifiedItem(itemModify);
		
		composite.setSelection(selection);
		
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
