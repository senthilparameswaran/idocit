package de.akra.idocit.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;

/**
 * The selection / state for a {@link ManageThematicGridsComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class ManageThematicGridsCompositeSelection implements ISelection
{
	private List<ThematicGrid> thematicGrids;

	private ThematicGrid activeThematicGrid;

	private List<ThematicRole> roles;

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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeThematicGrid == null) ? 0 : activeThematicGrid.hashCode());
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
		if (thematicGrids == null)
		{
			if (other.thematicGrids != null)
				return false;
		}
		else if (!thematicGrids.equals(other.thematicGrids))
			return false;
		return true;
	}
}
