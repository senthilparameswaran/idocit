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
 * The selection / state for a {@link ManageThematicGridsComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageThematicGridsCompositeSelection implements ISelection
{
	/**
	 * All available thematic grids.
	 */
	private List<ThematicGrid> thematicGrids;

	/**
	 * Copy of the selected thematic grid.
	 */
	private ThematicGrid activeThematicGrid;

	/**
	 * Index of the selected thematic grid in the global grid list (
	 * <code>thematicGrids</code>).
	 */
	private int indexOfActiveThematicGrid;

	/**
	 * All available thematic roles.
	 */
	private List<ThematicRole> roles;

	/**
	 * True, if a ThematicGrid with same name exists. If same name exists, changes can not
	 * be applied.
	 */
	private boolean nameExists = false;

	/**
	 * The timestamp of the last save action. If the roles were not saved during this
	 * session the value is -1.
	 */
	private long lastSaveTimeThematicRoles = -1;

	/**
	 * The selected thematic role in the list of roles.
	 */
	private ThematicRole activeRole;

	public ThematicRole getActiveRole()
	{
		return activeRole;
	}

	public void setActiveRole(ThematicRole activeRole)
	{
		this.activeRole = activeRole;
	}

	/**
	 * 
	 * @return the available {@link ThematicRole}s.
	 */
	public List<ThematicRole> getRoles()
	{
		return roles;
	}

	/**
	 * 
	 * @param roles
	 *            The available {@link ThematicRole}s that can be used in the
	 *            {@link ThematicGrid}s.
	 */
	public void setRoles(List<ThematicRole> roles)
	{
		this.roles = roles;
	}

	/**
	 * 
	 * @return The configured {@link ThematicGrid}s.
	 */
	public List<ThematicGrid> getThematicGrids()
	{
		return thematicGrids;
	}

	/**
	 * 
	 * @param thematicGrids
	 *            The configured {@link ThematicGrid}s to display.
	 */
	public void setThematicGrids(List<ThematicGrid> thematicGrids)
	{
		this.thematicGrids = thematicGrids;
	}

	/**
	 * 
	 * @return The active {@link ThematicGrid}.
	 */
	public ThematicGrid getActiveThematicGrid()
	{
		return activeThematicGrid;
	}

	/**
	 * 
	 * @param activeThematicGrid
	 *            The active {@link ThematicGrid}.
	 */
	public void setActiveThematicGrid(ThematicGrid activeThematicGrid)
	{
		this.activeThematicGrid = activeThematicGrid;
	}

	/**
	 * @param nameExists
	 *            the nameExists to set
	 */
	public void setNameExists(boolean nameExists)
	{
		this.nameExists = nameExists;
	}

	/**
	 * @return the nameExists
	 */
	public boolean isNameExists()
	{
		return nameExists;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeRole == null) ? 0 : activeRole.hashCode());
		result = prime * result
				+ ((activeThematicGrid == null) ? 0 : activeThematicGrid.hashCode());
		result = prime * result + indexOfActiveThematicGrid;
		result = prime * result
				+ (int) (lastSaveTimeThematicRoles ^ (lastSaveTimeThematicRoles >>> 32));
		result = prime * result + (nameExists ? 1231 : 1237);
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result
				+ ((thematicGrids == null) ? 0 : thematicGrids.hashCode());
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
		ManageThematicGridsCompositeSelection other = (ManageThematicGridsCompositeSelection) obj;
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
		if (indexOfActiveThematicGrid != other.indexOfActiveThematicGrid)
			return false;
		if (lastSaveTimeThematicRoles != other.lastSaveTimeThematicRoles)
			return false;
		if (nameExists != other.nameExists)
			return false;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
			return false;
		if (thematicGrids == null)
		{
			if (other.thematicGrids != null)
				return false;
		}
		else if (!thematicGrids.equals(other.thematicGrids))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ManageThematicGridsCompositeSelection [thematicGrids=");
		builder.append(thematicGrids);
		builder.append(", activeThematicGrid=");
		builder.append(activeThematicGrid);
		builder.append(", indexOfActiveThematicGrid=");
		builder.append(indexOfActiveThematicGrid);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", nameExists=");
		builder.append(nameExists);
		builder.append(", lastSaveTimeThematicRoles=");
		builder.append(lastSaveTimeThematicRoles);
		builder.append(", activeRole=");
		builder.append(activeRole);
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

	public void setIndexOfActiveThematicGrid(int indexOfActiveThematicGrid)
	{
		this.indexOfActiveThematicGrid = indexOfActiveThematicGrid;
	}

	public int getIndexOfActiveThematicGrid()
	{
		return indexOfActiveThematicGrid;
	}
}
