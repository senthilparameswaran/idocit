package de.akra.idocit.ui.composites;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.structure.ThematicRole;

/**
 * Selection / state for {@link EditThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((modifiedItem == null) ? 0 : modifiedItem.hashCode());
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
		EditThematicRoleCompositeSelection other = (EditThematicRoleCompositeSelection) obj;
		if (item == null)
		{
			if (other.item != null)
				return false;
		}
		else if (!item.equals(other.item))
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

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditDescribedItemCompositeSelection [item=");
		builder.append(item);
		builder.append(", modifiedItem=");
		builder.append(modifiedItem);
		builder.append("]");
		return builder.toString();
	}
}
