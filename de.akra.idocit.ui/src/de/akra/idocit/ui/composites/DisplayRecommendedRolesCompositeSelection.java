package de.akra.idocit.ui.composites;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.ThematicRole;

/**
 * Selection for {@link DisplayRecommendedRolesComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DisplayRecommendedRolesCompositeSelection implements ISelection
{
	private Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles;

	private Set<ThematicRole> assignedThematicRoles;
	
	/**
	 * The names of the collapsed grids / verb classes.
	 */
	private Set<String> collapsedThematicGridNames = Collections.emptySet();

	/**
	 * @return the assignedThematicRoles
	 */
	public Set<ThematicRole> getAssignedThematicRoles()
	{
		return assignedThematicRoles;
	}

	/**
	 * @param assignedThematicRoles
	 *            the assignedThematicRoles to set
	 */
	public void setAssignedThematicRoles(Set<ThematicRole> assignedThematicRoles)
	{
		this.assignedThematicRoles = assignedThematicRoles;
	}

	/**
	 * @param recommendedThematicRoles
	 *            the recommendedThematicRoles to set
	 */
	public void setRecommendedThematicRoles(
			Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles)
	{
		this.recommendedThematicRoles = recommendedThematicRoles;
	}

	/**
	 * @return the recommendedThematicRoles
	 */
	public Map<String, Map<ThematicRole, Boolean>> getRecommendedThematicRoles()
	{
		return recommendedThematicRoles;
	}

	/**
	 * @param collapsedThematicGridNames the collapsedThematicGridNames to set
	 */
	public void setCollapsedThematicGridNames(Set<String> collapsedThematicGridNames)
	{
		this.collapsedThematicGridNames = collapsedThematicGridNames;
	}

	/**
	 * @return the collapsedThematicGridNames
	 */
	public Set<String> getCollapsedThematicGridNames()
	{
		return collapsedThematicGridNames;
	}

}
