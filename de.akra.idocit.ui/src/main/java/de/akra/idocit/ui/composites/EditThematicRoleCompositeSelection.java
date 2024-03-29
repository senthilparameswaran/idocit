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

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection / state for {@link EditThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicRoleCompositeSelection implements ISelection
{
	/**
	 * The original item.
	 */
	private ThematicRole item = null;

	/**
	 * The modified item.
	 */
	private ThematicRole modifiedItem = null;
	
	/**
	 * The last cursor position in the name text field.
	 */
	private int lastCurserPosition = 0;

	/**
	 * 
	 * @return the modified {@link ThematicRole}.
	 */
	public ThematicRole getModifiedItem()
	{
		return modifiedItem;
	}

	/**
	 * 
	 * @param modifiedItem
	 *            the modified {@link ThematicRole}.
	 */
	public void setModifiedItem(ThematicRole modifiedItem)
	{
		this.modifiedItem = modifiedItem;
	}

	/**
	 * 
	 * @return the original {@link ThematicRole}.
	 */
	public ThematicRole getItem()
	{
		return item;
	}

	/**
	 * 
	 * @param item
	 *            the original {@link ThematicRole}.
	 */
	public void setItem(ThematicRole item)
	{
		this.item = item;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + lastCurserPosition;
		result = prime * result + ((modifiedItem == null) ? 0 : modifiedItem.hashCode());
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
		EditThematicRoleCompositeSelection other = (EditThematicRoleCompositeSelection) obj;
		if (item == null)
		{
			if (other.item != null)
				return false;
		}
		else if (!item.equals(other.item))
			return false;
		if (lastCurserPosition != other.lastCurserPosition)
			return false;
		if (modifiedItem == null)
		{
			if (other.modifiedItem != null)
				return false;
		}
		else if (!modifiedItem.equals(other.modifiedItem))
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
		builder.append("EditThematicRoleCompositeSelection [item=");
		builder.append(item);
		builder.append(", modifiedItem=");
		builder.append(modifiedItem);
		builder.append(", lastCurserPosition=");
		builder.append(lastCurserPosition);
		builder.append("]");
		return builder.toString();
	}

	public void setLastCurserPosition(int lastCurserPosition)
	{
		this.lastCurserPosition = lastCurserPosition;
	}

	public int getLastCurserPosition()
	{
		return lastCurserPosition;
	}
}
