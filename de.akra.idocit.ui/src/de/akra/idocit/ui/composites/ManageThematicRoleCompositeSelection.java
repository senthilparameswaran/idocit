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

import de.akra.idocit.common.structure.ThematicRole;

/**
 * The selection / state for a {@link ManageThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageThematicRoleCompositeSelection implements ISelection
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
	 * The timestamp of the last save action. If the roles were not saved during this
	 * session the value is -1.
	 */
	private long lastSaveTimeThematicRoles = -1;

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

	/* (non-Javadoc)
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
				+ (int) (lastSaveTimeThematicRoles ^ (lastSaveTimeThematicRoles >>> 32));
		result = prime * result
				+ ((modifiedThematicRole == null) ? 0 : modifiedThematicRole.hashCode());
		result = prime * result + (nameExists ? 1231 : 1237);
		result = prime * result
				+ ((thematicRoles == null) ? 0 : thematicRoles.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		if (lastSaveTimeThematicRoles != other.lastSaveTimeThematicRoles)
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
		if (thematicRoles == null)
		{
			if (other.thematicRoles != null)
				return false;
		}
		else if (!thematicRoles.equals(other.thematicRoles))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ManageThematicRoleCompositeSelection [activeThematicRole=");
		builder.append(activeThematicRole);
		builder.append(", modifiedThematicRole=");
		builder.append(modifiedThematicRole);
		builder.append(", thematicRoles=");
		builder.append(thematicRoles);
		builder.append(", lastSaveTimeThematicRoles=");
		builder.append(lastSaveTimeThematicRoles);
		builder.append(", nameExists=");
		builder.append(nameExists);
		builder.append(", lastCurserPosition=");
		builder.append(lastCurserPosition);
		builder.append("]");
		return builder.toString();
	}

	public void setLastSaveTimeThematicRoles(long lastSaveTimeThematicRoles)
	{
		this.lastSaveTimeThematicRoles = lastSaveTimeThematicRoles;
	}

	public long getLastSaveTimeThematicRoles()
	{
		return lastSaveTimeThematicRoles;
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
}
