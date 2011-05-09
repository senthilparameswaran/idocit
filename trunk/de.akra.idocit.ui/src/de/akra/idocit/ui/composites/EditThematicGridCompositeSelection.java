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
	private ThematicGrid thematicGrid;

	private List<ThematicRole> roles = Collections.emptyList();

	private List<ThematicGrid> existingGrids = Collections.emptyList();

	/**
	 * 
	 * @return The configured {@link ThematicGrid}s.
	 */
	public List<ThematicGrid> getExistingGrids()
	{
		return existingGrids;
	}

	/**
	 * 
	 * @param existingGrids
	 *            The existing {@link ThematicGrid}s.
	 */
	public void setExistingGrids(List<ThematicGrid> existingGrids)
	{
		this.existingGrids = existingGrids;
	}

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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((existingGrids == null) ? 0 : existingGrids.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((thematicGrid == null) ? 0 : thematicGrid.hashCode());
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
		if (existingGrids == null)
		{
			if (other.existingGrids != null)
				return false;
		}
		else if (!existingGrids.equals(other.existingGrids))
			return false;
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

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditThematicGridCompositeSelection [thematicGrid=");
		builder.append(thematicGrid);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", existingGrids=");
		builder.append(existingGrids);
		builder.append("]");
		return builder.toString();
	}
}
