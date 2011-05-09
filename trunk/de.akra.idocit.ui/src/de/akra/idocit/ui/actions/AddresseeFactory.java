package de.akra.idocit.ui.actions;

import de.akra.idocit.core.structure.Addressee;

/**
 * Factory for {@link Addressee}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class AddresseeFactory extends AbsItemFactory
{

	@Override
	public Addressee createNewItem()
	{
		Addressee addressee = new Addressee("<Not defined yet>");
		addressee.setDescription("");
		return addressee;
	}
}
