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
