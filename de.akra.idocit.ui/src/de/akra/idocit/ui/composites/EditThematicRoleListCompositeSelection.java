package de.akra.idocit.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.ThematicRole;

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
	 * List of active {@link ThematicRole}s.
	 */
	private List<ThematicRole> activeThematicRoles = null;

	/**
	 * List of all {@link ThematicRole}s.
	 */
	private List<ThematicRole> thematicRoles = null;

	/**
	 * The minimum number of items in a list.
	 */
	private int minNumberOfItems = 0;

	/**
	 * 
	 * @return the List of active {@link ThematicRole}s.
	 */
	public List<ThematicRole> getActiveItems()
	{
		return activeThematicRoles;
	}

	/**
	 * 
	 * @param activeItems
	 *            List of active {@link ThematicRole}s.
	 */
	public void setActiveItems(List<ThematicRole> activeItems)
	{
		this.activeThematicRoles = activeItems;
	}

	/**
	 * 
	 * @return the List of all {@link ThematicRole}s.
	 */
	public List<ThematicRole> getItems()
	{
		return thematicRoles;
	}

	/**
	 * 
	 * @param items
	 *            List of all {@link ThematicRole}s.
	 */
	public void setItems(List<ThematicRole> items)
	{
		this.thematicRoles = items;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeThematicRoles == null) ? 0 : activeThematicRoles.hashCode());
		result = prime * result
				+ ((thematicRoles == null) ? 0 : thematicRoles.hashCode());
		result = prime * result + minNumberOfItems;
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
		EditThematicRoleListCompositeSelection other = (EditThematicRoleListCompositeSelection) obj;
		if (activeThematicRoles == null)
		{
			if (other.activeThematicRoles != null)
				return false;
		}
		else if (!activeThematicRoles.equals(other.activeThematicRoles))
			return false;
		if (thematicRoles == null)
		{
			if (other.thematicRoles != null)
				return false;
		}
		else if (!thematicRoles.equals(other.thematicRoles))
			return false;
		if (minNumberOfItems != other.minNumberOfItems)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditItemListCompositeSelection [activeItems=");
		builder.append(activeThematicRoles);
		builder.append(", items=");
		builder.append(thematicRoles);
		builder.append(", minNumberOfItems=");
		builder.append(minNumberOfItems);
		builder.append("]");
		return builder.toString();
	}
}
