/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * The selection / state for a {@link EditThematicGridComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class EditThematicGridCompositeSelection implements ISelection
{
	/**
	 * The selected {@link ThematicGrid} to edit.
	 */
	private ThematicGrid activeThematicGrid;

	/**
	 * All existing {@link ThematicRole}s that are defined in the preference page.
	 */
	private List<ThematicRole> roles = Collections.emptyList();

	private ThematicRole activeRole;

	public EditThematicGridCompositeSelection()
	{

	}

	public EditThematicGridCompositeSelection(ThematicGrid activeThematicGrid,
			List<ThematicRole> roles, ThematicRole activeRole)
	{
		if (activeThematicGrid != null)
		{
			this.activeThematicGrid = (ThematicGrid) activeThematicGrid.clone();
		}

		if (roles != null)
		{
			this.roles = cloneThematicRoles(roles);
		}

		if (activeRole != null)
		{
			this.activeRole = (ThematicRole) activeRole.clone();
		}
	}

	private List<ThematicRole> cloneThematicRoles(List<ThematicRole> rolesToClone)
	{
		List<ThematicRole> clonedRole = new ArrayList<ThematicRole>(rolesToClone.size());

		for (ThematicRole role : rolesToClone)
		{
			clonedRole.add((ThematicRole) role.clone());
		}

		return clonedRole;
	}

	public ThematicRole getActiveRole()
	{
		if (activeRole != null)
		{
			return activeRole.clone();
		}
		else
		{
			return null;
		}
	}

	public EditThematicGridCompositeSelection setActiveRole(ThematicRole activeRole)
	{
		return new EditThematicGridCompositeSelection(this.activeThematicGrid,
				this.roles, activeRole);
	}

	/**
	 * @return the activeThematicGrid
	 */
	public ThematicGrid getActiveThematicGrid()
	{
		if (activeThematicGrid != null)
		{
			return activeThematicGrid.clone();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @param activeThematicGrid
	 *            the activeThematicGrid to set
	 */
	public EditThematicGridCompositeSelection setActiveThematicGrid(
			ThematicGrid activeThematicGrid)
	{
		return new EditThematicGridCompositeSelection(activeThematicGrid, roles,
				activeRole);
	}

	/**
	 * 
	 * @return all available {@link ThematicRole}s.
	 */
	public List<ThematicRole> getRoles()
	{
		if (roles != null)
		{
			return cloneThematicRoles(roles);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 
	 * @param roles
	 *            All available {@link ThematicRole}s.
	 */
	public EditThematicGridCompositeSelection setRoles(List<ThematicRole> roles)
	{
		return new EditThematicGridCompositeSelection(activeThematicGrid, roles,
				activeRole);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeRole == null) ? 0 : activeRole.hashCode());
		result = prime * result
				+ ((activeThematicGrid == null) ? 0 : activeThematicGrid.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		EditThematicGridCompositeSelection other = (EditThematicGridCompositeSelection) obj;
		if (activeRole == null)
		{
			if (other.activeRole != null)
				return false;
		}
		else if (!activeRole.equals(other.activeRole))
			return false;
		if (activeThematicGrid == null)
		{
			if (other.activeThematicGrid != null)
				return false;
		}
		else if (!activeThematicGrid.equals(other.activeThematicGrid))
			return false;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditThematicGridCompositeSelection [activeThematicGrid=");
		builder.append(activeThematicGrid);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", activeRole=");
		builder.append(activeRole);
		builder.append("]");
		return builder.toString();
	}
}
