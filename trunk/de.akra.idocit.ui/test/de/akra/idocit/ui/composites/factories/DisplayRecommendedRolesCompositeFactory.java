package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.composites.DisplayRecommendedRolesComposite;
import de.akra.idocit.ui.composites.DisplayRecommendedRolesCompositeSelection;

/**
 * Factory to create a {@link DisplayRecommendedRolesComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DisplayRecommendedRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, DisplayRecommendedRolesCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DisplayRecommendedRolesCompositeSelection> createComposite(
			Composite pvParent)
	{
		DisplayRecommendedRolesComposite recRolesComp = new DisplayRecommendedRolesComposite(
				pvParent, SWT.NONE);
		DisplayRecommendedRolesCompositeSelection selection = new DisplayRecommendedRolesCompositeSelection();

		List<ThematicRole> thematicRoles = new ArrayList<ThematicRole>();
		ThematicRole roleAgent = new ThematicRole("AGENT");
		ThematicRole roleAction = new ThematicRole("ACTION");
		thematicRoles.add(roleAgent);
		thematicRoles.add(roleAction);

		Set<ThematicRole> assignedThematicRoles = new TreeSet<ThematicRole>();
		assignedThematicRoles.add((ThematicRole) (thematicRoles.toArray()[0]));

		Map<String, Map<ThematicRole, Boolean>> roles = new HashMap<String, Map<ThematicRole, Boolean>>();
		Map<ThematicRole,Boolean> recRoles = new HashMap<ThematicRole,Boolean>();
		
		recRoles.put(thematicRoles.get(0), Boolean.TRUE);
		recRoles.put(thematicRoles.get(1), Boolean.FALSE);
		
		roles.put("Class 1", recRoles);
		roles.put("Class 2", recRoles);

		selection.setRecommendedThematicRoles(roles);
		selection.setAssignedThematicRoles(assignedThematicRoles);

		recRolesComp.setSelection(selection);

		pvParent.pack();
		pvParent.layout();
		return recRolesComp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
