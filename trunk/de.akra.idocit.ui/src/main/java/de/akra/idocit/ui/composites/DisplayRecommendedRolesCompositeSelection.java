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
package de.akra.idocit.ui.composites;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;

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
	private Map<String, ThematicGrid> recommendedThematicGrids;

	private Set<ThematicRole> assignedThematicRoles;

	private String referenceThematicGridName;

	/**
	 * The names of the collapsed grids / verb classes.
	 */
	private Set<String> collapsedThematicGridNames = Collections.emptySet();

	/**
	 * The default constructor
	 */
	public DisplayRecommendedRolesCompositeSelection()
	{
		assignedThematicRoles = new HashSet<ThematicRole>();
		referenceThematicGridName = StringUtils.EMPTY;
		recommendedThematicGrids = new HashMap<String, ThematicGrid>();
		collapsedThematicGridNames = new HashSet<String>();
	}

	/**
	 * Complete Constructor
	 * 
	 * Intention: this constructor has been implemented for convenience within internal
	 * setters. But it could be used from outside as well.
	 * 
	 * @param recommendedThematicGrids
	 *            The recommended thematic grids (key: name of thematic grid, value: the
	 *            ThematicGrid))
	 * @param assignedThematicRoles
	 *            The set of documented roles
	 * @param referenceThematicGridName
	 *            The name of the reference thematic grid
	 * @param collapsedThematicGridNames
	 *            The set of names of collapsed thematic grids
	 */
	public DisplayRecommendedRolesCompositeSelection(
			Map<String, ThematicGrid> recommendedThematicGrids,
			Set<ThematicRole> assignedThematicRoles, String referenceThematicGridName,
			Set<String> collapsedThematicGridNames)
	{
		this.recommendedThematicGrids = new HashMap<String, ThematicGrid>(
				recommendedThematicGrids);
		this.assignedThematicRoles = new HashSet<ThematicRole>(assignedThematicRoles);
		this.referenceThematicGridName = referenceThematicGridName;
		this.collapsedThematicGridNames = new HashSet<String>(collapsedThematicGridNames);
	}

	/**
	 * @param assignedThematicRoles
	 *            the assignedThematicRoles to set
	 */
	public DisplayRecommendedRolesCompositeSelection setAssignedThematicRoles(
			final Set<ThematicRole> assignedThematicRoles)
	{
		return new DisplayRecommendedRolesCompositeSelection(recommendedThematicGrids,
				assignedThematicRoles, referenceThematicGridName,
				collapsedThematicGridNames);
	}

	/**
	 * @param recommendedThematicGrids
	 *            the recommendedThematicGrids to set
	 */
	public DisplayRecommendedRolesCompositeSelection setRecommendedThematicGrids(
			final Map<String, ThematicGrid> recommendedThematicGrids)
	{
		return new DisplayRecommendedRolesCompositeSelection(recommendedThematicGrids,
				assignedThematicRoles, referenceThematicGridName,
				collapsedThematicGridNames);
	}

	/**
	 * @return the assignedThematicRoles
	 */
	public Set<ThematicRole> getAssignedThematicRoles()
	{
		return new HashSet<ThematicRole>(assignedThematicRoles);
	}

	/**
	 * @return the recommendedThematicGrids
	 */
	public Map<String, ThematicGrid> getRecommendedThematicGrids()
	{
		return new HashMap<String, ThematicGrid>(recommendedThematicGrids);
	}

	/**
	 * @param collapsedThematicGridNames
	 *            the collapsedThematicGridNames to set
	 */
	public DisplayRecommendedRolesCompositeSelection setCollapsedThematicGridNames(
			final Set<String> collapsedThematicGridNames)
	{
		return new DisplayRecommendedRolesCompositeSelection(recommendedThematicGrids,
				assignedThematicRoles, referenceThematicGridName,
				collapsedThematicGridNames);
	}

	/**
	 * @return the collapsedThematicGridNames
	 */
	public Set<String> getCollapsedThematicGridNames()
	{
		return new HashSet<String>(collapsedThematicGridNames);
	}

	public String getReferenceThematicGridName()
	{
		return referenceThematicGridName;
	}

	public DisplayRecommendedRolesCompositeSelection setReferenceThematicGridName(
			final String referenceThematicGridName)
	{
		return new DisplayRecommendedRolesCompositeSelection(recommendedThematicGrids,
				assignedThematicRoles, referenceThematicGridName,
				collapsedThematicGridNames);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((assignedThematicRoles == null) ? 0 : assignedThematicRoles.hashCode());
		result = prime
				* result
				+ ((collapsedThematicGridNames == null) ? 0 : collapsedThematicGridNames
						.hashCode());
		result = prime
				* result
				+ ((recommendedThematicGrids == null) ? 0 : recommendedThematicGrids
						.hashCode());
		result = prime
				* result
				+ ((referenceThematicGridName == null) ? 0 : referenceThematicGridName
						.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisplayRecommendedRolesCompositeSelection other = (DisplayRecommendedRolesCompositeSelection) obj;
		if (assignedThematicRoles == null)
		{
			if (other.assignedThematicRoles != null)
				return false;
		}
		else if (!assignedThematicRoles.equals(other.assignedThematicRoles))
			return false;
		if (collapsedThematicGridNames == null)
		{
			if (other.collapsedThematicGridNames != null)
				return false;
		}
		else if (!collapsedThematicGridNames.equals(other.collapsedThematicGridNames))
			return false;
		if (recommendedThematicGrids == null)
		{
			if (other.recommendedThematicGrids != null)
				return false;
		}
		else if (!recommendedThematicGrids.equals(other.recommendedThematicGrids))
			return false;
		if (referenceThematicGridName == null)
		{
			if (other.referenceThematicGridName != null)
				return false;
		}
		else if (!referenceThematicGridName.equals(other.referenceThematicGridName))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "DisplayRecommendedRolesCompositeSelection [recommendedThematicRoles="
				+ recommendedThematicGrids + ", assignedThematicRoles="
				+ assignedThematicRoles + ", referenceThematicGridName="
				+ referenceThematicGridName + ", collapsedThematicGridNames="
				+ collapsedThematicGridNames + "]";
	}
}
