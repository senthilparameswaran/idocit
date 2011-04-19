package de.akra.idocit.structure;

/**
 * A DescribedItem is an item that has a name and a description.
 * 
 * @author Dirk Meier-Eickchoff
 * 
 */
public interface DescribedItem
{
	/**
	 * 
	 * @return the description for this item.
	 */
	public String getDescription();

	/**
	 * 
	 * @param description
	 *            The description for this item.
	 */
	public void setDescription(String description);

	/**
	 * 
	 * @return the name for this item.
	 */
	public String getName();

	/**
	 * 
	 * @param name
	 *            The name for this item.
	 */
	public void setName(String name);
}
