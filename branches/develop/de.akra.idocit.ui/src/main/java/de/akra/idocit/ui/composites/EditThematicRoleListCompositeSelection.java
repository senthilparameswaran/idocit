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

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection / state for {@link EditThematicRoleListComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicRoleListCompositeSelection implements ISelection
{
	/**
	 * The active {@link ThematicRole}.
	 */
	private ThematicRole activeThematicRole = null;

	/**
	 * List of all configured {@link ThematicRole}s.
	 */
	private List<ThematicRole> thematicRoles = null;

	/**
	 * The minimum number of items in a list.
	 */
	private int minNumberOfItems = 0;

	/**
	 * A {@link ThematicRole} that is removed from the list. When the changes shall be
	 * persisted, delete this roles from all configured {@link ThematicGrid}s.
	 */
	private ThematicRole removedThematicRole = null;

	/**
	 * @return the removedThematicRole
	 */
	public ThematicRole getRemovedThematicRole()
	{
		return removedThematicRole;
	}

	/**
	 * @param removedThematicRole
	 *            the removedThematicRole to set
	 */
	public void setRemovedThematicRole(ThematicRole removedThematicRole)
	{
		this.removedThematicRole = removedThematicRole;
	}

	/**
	 * @return the activeThematicRole
	 */
	public ThematicRole getActiveThematicRole()
	{
		return activeThematicRole;
	}

	/**
	 * @param activeThematicRole
	 *            the activeThematicRole to set
	 */
	public void setActiveThematicRole(ThematicRole activeThematicRole)
	{
		this.activeThematicRole = activeThematicRole;
	}

	/**
	 * @return the list of all configured {@link ThematicRole}s
	 */
	public List<ThematicRole> getThematicRoles()
	{
		return thematicRoles;
	}

	/**
	 * @param thematicRoles
	 *            the list of all configured {@link ThematicRole}s
	 */
	public void setThematicRoles(List<ThematicRole> thematicRoles)
	{
		this.thematicRoles = thematicRoles;
	}

	/**
	 * 
	 * @return the minimum number of items in a list.
	 */
	public int getMinNumberOfItems()
	{
		return minNumberOfItems;
	}

	/**
	 * 
	 * @param minNumberOfItems
	 *            The minimum number of items in a list.
	 */
	public void setMinNumberOfItems(int minNumberOfItems)
	{
		this.minNumberOfItems = minNumberOfItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeThematicRole == null) ? 0 : activeThematicRole.hashCode());
		result = prime * result + minNumberOfItems;
		result = prime * result
				+ ((removedThematicRole == null) ? 0 : removedThematicRole.hashCode());
		result = prime * result
				+ ((thematicRoles == null) ? 0 : thematicRoles.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
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
		EditThematicRoleListCompositeSelection other = (EditThematicRoleListCompositeSelection) obj;
		if (activeThematicRole == null)
		{
			if (other.activeThematicRole != null)
				return false;
		}
		else if (!activeThematicRole.equals(other.activeThematicRole))
			return false;
		if (minNumberOfItems != other.minNumberOfItems)
			return false;
		if (removedThematicRole == null)
		{
			if (other.removedThematicRole != null)
				return false;
		}
		else if (!removedThematicRole.equals(other.removedThematicRole))
			return false;
		if (thematicRoles == null)
		{
			if (other.thematicRoles != null)
				return false;
		}
		else if (!thematicRoles.equals(other.thematicRoles))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditThematicRoleListCompositeSelection [activeThematicRole=")
				.append(activeThematicRole).append(", thematicRoles=")
				.append(thematicRoles).append(", minNumberOfItems=")
				.append(minNumberOfItems).append(", removedThematicRole=")
				.append(removedThematicRole).append("]");
		return builder.toString();
	}

}
