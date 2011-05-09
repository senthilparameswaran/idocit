package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.ui.composites.ManageThematicRoleComposite;
import de.akra.idocit.ui.composites.ManageThematicRoleCompositeSelection;

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
	public ManageThematicRoleComposite createComposite(Composite parent)
	{
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
