package de.akra.idocit.ui.actions;

import de.akra.idocit.structure.Addressee;

/**
 * Factory for {@link Addressee}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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
