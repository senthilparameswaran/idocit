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

import de.akra.idocit.common.structure.Documentation;
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

	/**
	 * This set contains all thematic roles with an error documentation. An error
	 * documentation could be identified by checking{@link Documentation#isErrorCase()}.
	 */
	private Set<ThematicRole> assignedThematicRolesWithErrorDocumentation;

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
	 * @param assignedThematicRolesWithErrorDocumentation
	 *            The set of assigned thematic roles which have the error-flag (
	 *            {@link Documentation#isErrorCase()})
	 */
	public DisplayRecommendedRolesCompositeSelection(
			Map<String, ThematicGrid> recommendedThematicGrids,
			Set<ThematicRole> assignedThematicRoles, String referenceThematicGridName,
			Set<String> collapsedThematicGridNames,
			Set<ThematicRole> assignedThematicRolesWithErrorDocumentation)
	{
		if (recommendedThematicGrids != null)
		{
			this.recommendedThematicGrids = new HashMap<String, ThematicGrid>(
					recommendedThematicGrids);
		}
		else
		{
			this.recommendedThematicGrids = new HashMap<String, ThematicGrid>();
		}

		if (assignedThematicRoles != null)
		{
			this.assignedThematicRoles = new HashSet<ThematicRole>(assignedThematicRoles);
		}
		else
		{
			this.assignedThematicRoles = new HashSet<ThematicRole>();
		}

		this.referenceThematicGridName = referenceThematicGridName;

		if (collapsedThematicGridNames != null)
		{
			this.collapsedThematicGridNames = new HashSet<String>(
					collapsedThematicGridNames);
		}
		else
		{
			this.collapsedThematicGridNames = new HashSet<String>();
		}

		if (assignedThematicRolesWithErrorDocumentation != null)
		{
			this.assignedThematicRolesWithErrorDocumentation = new HashSet<ThematicRole>(
					assignedThematicRolesWithErrorDocumentation);
		}
		else
		{
			this.assignedThematicRolesWithErrorDocumentation = new HashSet<ThematicRole>();
		}
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
				collapsedThematicGridNames, assignedThematicRolesWithErrorDocumentation);
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
				collapsedThematicGridNames, assignedThematicRolesWithErrorDocumentation);
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
				collapsedThematicGridNames, assignedThematicRolesWithErrorDocumentation);
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
				collapsedThematicGridNames, assignedThematicRolesWithErrorDocumentation);
	}

	public Set<ThematicRole> getAssignedThematicRolesWithErrorDocumentation()
	{
		return assignedThematicRolesWithErrorDocumentation;
	}

	public DisplayRecommendedRolesCompositeSelection setAssignedThematicRolesWithErrorDocumentation(
			Set<ThematicRole> assignedThematicRolesWithErrorDocumentation)
	{
		return new DisplayRecommendedRolesCompositeSelection(recommendedThematicGrids,
				assignedThematicRoles, referenceThematicGridName,
				collapsedThematicGridNames, assignedThematicRolesWithErrorDocumentation);
	}

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
				+ ((assignedThematicRolesWithErrorDocumentation == null) ? 0
						: assignedThematicRolesWithErrorDocumentation.hashCode());
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
		if (assignedThematicRolesWithErrorDocumentation == null)
		{
			if (other.assignedThematicRolesWithErrorDocumentation != null)
				return false;
		}
		else if (!assignedThematicRolesWithErrorDocumentation
				.equals(other.assignedThematicRolesWithErrorDocumentation))
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

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DisplayRecommendedRolesCompositeSelection [recommendedThematicGrids=");
		builder.append(recommendedThematicGrids);
		builder.append(", assignedThematicRoles=");
		builder.append(assignedThematicRoles);
		builder.append(", assignedThematicRolesWithErrorDocumentation=");
		builder.append(assignedThematicRolesWithErrorDocumentation);
		builder.append(", referenceThematicGridName=");
		builder.append(referenceThematicGridName);
		builder.append(", collapsedThematicGridNames=");
		builder.append(collapsedThematicGridNames);
		builder.append("]");
		return builder.toString();
	}
}
