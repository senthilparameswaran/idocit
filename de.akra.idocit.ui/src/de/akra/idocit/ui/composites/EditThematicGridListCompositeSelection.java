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

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.ThematicGrid;

/**
 * Selection / state for {@link EditThematicGridListComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
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
