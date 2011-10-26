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

	/**
	 * @return the activeThematicGrid
	 */
	public ThematicGrid getActiveThematicGrid()
	{
		return activeThematicGrid;
	}

	/**
	 * @param activeThematicGrid
	 *            the activeThematicGrid to set
	 */
	public void setActiveThematicGrid(ThematicGrid activeThematicGrid)
	{
		this.activeThematicGrid = activeThematicGrid;
	}

	/**
	 * 
	 * @return all available {@link ThematicRole}s.
	 */
	public List<ThematicRole> getRoles()
	{
		return roles;
	}

	/**
	 * 
	 * @param roles
	 *            All available {@link ThematicRole}s.
	 */
	public void setRoles(List<ThematicRole> roles)
	{
		this.roles = roles;
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
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result
				+ ((activeThematicGrid == null) ? 0 : activeThematicGrid.hashCode());
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
		EditThematicGridCompositeSelection other = (EditThematicGridCompositeSelection) obj;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
			return false;
		if (activeThematicGrid == null)
		{
			if (other.activeThematicGrid != null)
				return false;
		}
		else if (!activeThematicGrid.equals(other.activeThematicGrid))
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
		return "EditThematicGridCompositeSelection [thematicGrid=" + activeThematicGrid
				+ ", roles=" + roles + "]";
	}
}
