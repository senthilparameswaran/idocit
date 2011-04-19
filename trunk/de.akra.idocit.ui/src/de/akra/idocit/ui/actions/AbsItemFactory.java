package de.akra.idocit.ui.actions;

import de.akra.idocit.structure.DescribedItem;

/**
 * Abstract item factory for {@link DescribedItem}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public abstract class AbsItemFactory
{
	/**
	 * @return A new created {@link DescribedItem}.
	 */
	public abstract DescribedItem createNewItem();
}
