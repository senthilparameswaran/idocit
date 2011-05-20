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
