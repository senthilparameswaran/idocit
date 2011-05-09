package de.akra.idocit.ui.actions;

import de.akra.idocit.core.structure.DescribedItem;

/**
 * Abstract item factory for {@link DescribedItem}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class AbsItemFactory
{
	/**
	 * @return A new created {@link DescribedItem}.
	 */
	public abstract DescribedItem createNewItem();
}
