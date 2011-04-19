package de.akra.idocit.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.structure.ThematicGrid;

/**
 * Selection / state for {@link EditThematicGridListComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class EditThematicGridListCompositeSelection implements ISelection
{
	/**
	 * List of active {@link ThematicGrid}s.
	 */
	private List<ThematicGrid> activeThematicGrids = null;

	/**
	 * List of all {@link ThematicGrid}s.
	 */
	private List<ThematicGrid> thematicGrids = null;

	/**
	 * The minimum number of items in a list.
	 */
	private int minNumberOfItems = 0;

	/**
	 * 
	 * @return the List of active {@link ThematicGrid}s.
	 */
	public List<ThematicGrid> getActiveItems()
	{
		return activeThematicGrids;
	}

	/**
	 * 
	 * @param activeItems
	 *            List of active {@link ThematicGrid}s.
	 */
	public void setActiveItems(List<ThematicGrid> activeItems)
	{
		this.activeThematicGrids = activeItems;
	}

	/**
	 * 
	 * @return the List of all {@link ThematicGrid}s.
	 */
	public List<ThematicGrid> getItems()
	{
		return thematicGrids;
	}

	/**
	 * 
	 * @param items
	 *            List of all {@link ThematicGrid}s.
	 */
	public void setItems(List<ThematicGrid> items)
	{
		this.thematicGrids = items;
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
				+ ((activeThematicGrids == null) ? 0 : activeThematicGrids.hashCode());
		result = prime * result
				+ ((thematicGrids == null) ? 0 : thematicGrids.hashCode());
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
		EditThematicGridListCompositeSelection other = (EditThematicGridListCompositeSelection) obj;
		if (activeThematicGrids == null)
		{
			if (other.activeThematicGrids != null)
				return false;
		}
		else if (!activeThematicGrids.equals(other.activeThematicGrids))
			return false;
		if (thematicGrids == null)
		{
			if (other.thematicGrids != null)
				return false;
		}
		else if (!thematicGrids.equals(other.thematicGrids))
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
		builder.append(activeThematicGrids);
		builder.append(", items=");
		builder.append(thematicGrids);
		builder.append(", minNumberOfItems=");
		builder.append(minNumberOfItems);
		builder.append("]");
		return builder.toString();
	}
}
