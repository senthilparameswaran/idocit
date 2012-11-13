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

import java.util.Collection;
import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * The selection / state for a {@link ManageThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageThematicRoleCompositeSelection implements ISelection, Cloneable
{
	/**
	 * The selected ThematicRole to edit.
	 */
	private ThematicRole activeThematicRole = null;

	/**
	 * The modified role (copy of the active role with changes).
	 */
	private ThematicRole modifiedThematicRole = null;

	/**
	 * All available ThematicRoles.
	 */
	private List<ThematicRole> thematicRoles = null;

	/**
	 * True, if a ThematicRole with same name exists. If same name exists, changes can not
	 * be applied.
	 */
	private boolean nameExists = false;

	/**
	 * The last cursor position in the name text field at the edit composite.
	 */
	private int lastCurserPosition = 0;

	/**
	 * All from the list removed {@link ThematicRole}s. When the changes shall be
	 * persisted, delete these roles from all configured {@link ThematicGrid}s.
	 */
	private Collection<ThematicRole> removedThematicRoles = null;

	/**
	 * 
	 * @return The active {@link ThematicRole}.
	 */
	public ThematicRole getActiveThematicRole()
	{
		return activeThematicRole;
	}

	/**
	 * 
	 * @param activeRole
	 *            The active {@link ThematicRole}.
	 */
	public void setActiveThematicRole(ThematicRole activeRole)
	{
		this.activeThematicRole = activeRole;
	}

	/**
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	public List<ThematicRole> getThematicRoles()
	{
		return thematicRoles;
	}

	/**
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s.
	 */
	public void setThematicRoles(List<ThematicRole> roles)
	{
		this.thematicRoles = roles;
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
		result = prime * result + lastCurserPosition;
		result = prime * result
				+ ((modifiedThematicRole == null) ? 0 : modifiedThematicRole.hashCode());
		result = prime * result + (nameExists ? 1231 : 1237);
		result = prime * result
				+ ((removedThematicRoles == null) ? 0 : removedThematicRoles.hashCode());
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
		ManageThematicRoleCompositeSelection other = (ManageThematicRoleCompositeSelection) obj;
		if (activeThematicRole == null)
		{
			if (other.activeThematicRole != null)
				return false;
		}
		else if (!activeThematicRole.equals(other.activeThematicRole))
			return false;
		if (lastCurserPosition != other.lastCurserPosition)
			return false;
		if (modifiedThematicRole == null)
		{
			if (other.modifiedThematicRole != null)
				return false;
		}
		else if (!modifiedThematicRole.equals(other.modifiedThematicRole))
			return false;
		if (nameExists != other.nameExists)
			return false;
		if (removedThematicRoles == null)
		{
			if (other.removedThematicRoles != null)
				return false;
		}
		else if (!removedThematicRoles.equals(other.removedThematicRoles))
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
		builder.append("ManageThematicRoleCompositeSelection [activeThematicRole=")
				.append(activeThematicRole).append(", modifiedThematicRole=")
				.append(modifiedThematicRole).append(", thematicRoles=")
				.append(thematicRoles).append(", nameExists=").append(nameExists)
				.append(", lastCurserPosition=").append(lastCurserPosition)
				.append(", removedThematicRoles=").append(removedThematicRoles)
				.append("]");
		return builder.toString();
	}

	public void setNameExists(boolean nameExists)
	{
		this.nameExists = nameExists;
	}

	public boolean isNameExists()
	{
		return nameExists;
	}

	public void setLastCurserPosition(int lastCurserPosition)
	{
		this.lastCurserPosition = lastCurserPosition;
	}

	public int getLastCurserPosition()
	{
		return lastCurserPosition;
	}

	public void setModifiedThematicRole(ThematicRole modifiedThematicRole)
	{
		this.modifiedThematicRole = modifiedThematicRole;
	}

	public ThematicRole getModifiedThematicRole()
	{
		return modifiedThematicRole;
	}

	/**
	 * Clone this instance of {@link ManageThematicRoleCompositeSelection}. Collections
	 * are not deep copied.
	 */
	@Override
	public ManageThematicRoleCompositeSelection clone()
	{
		final ManageThematicRoleCompositeSelection s = new ManageThematicRoleCompositeSelection();
		s.setActiveThematicRole(activeThematicRole);
		s.setLastCurserPosition(lastCurserPosition);
		s.setModifiedThematicRole(modifiedThematicRole);
		s.setNameExists(nameExists);
		s.setThematicRoles(thematicRoles);
		s.setRemovedThematicRoles(removedThematicRoles);
		return s;
	}

	public Collection<ThematicRole> getRemovedThematicRoles()
	{
		return removedThematicRoles;
	}

	public void setRemovedThematicRoles(Collection<ThematicRole> removedThematicRoles)
	{
		this.removedThematicRoles = removedThematicRoles;
	}
}
