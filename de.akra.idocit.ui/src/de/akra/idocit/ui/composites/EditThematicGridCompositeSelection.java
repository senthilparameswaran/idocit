package de.akra.idocit.ui.composites;

import java.util.Collections;
import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;

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
	private ThematicGrid thematicGrid;

	/**
	 * All existing {@link ThematicRole}s that are defined in the preference page.
	 */
	private List<ThematicRole> roles = Collections.emptyList();

	/**
	 * 
	 * @return the {@link ThematicGrid} to edit.
	 */
	public ThematicGrid getThematicGrid()
	{
		return thematicGrid;
	}

	/**
	 * 
	 * @param thematicGrid
	 *            The {@link ThematicGrid} to edit.
	 */
	public void setThematicGrid(ThematicGrid thematicGrid)
	{
		this.thematicGrid = thematicGrid;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((thematicGrid == null) ? 0 : thematicGrid.hashCode());
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
		EditThematicGridCompositeSelection other = (EditThematicGridCompositeSelection) obj;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
			return false;
		if (thematicGrid == null)
		{
			if (other.thematicGrid != null)
				return false;
		}
		else if (!thematicGrid.equals(other.thematicGrid))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EditThematicGridCompositeSelection [thematicGrid=" + thematicGrid
				+ ", roles=" + roles + "]";
	}
}
