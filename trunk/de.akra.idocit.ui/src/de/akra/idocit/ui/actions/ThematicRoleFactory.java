package de.akra.idocit.ui.actions;

import de.akra.idocit.structure.ThematicRole;

/**
 * Factory for {@link ThematicRole}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ThematicRoleFactory extends AbsItemFactory
{

	@Override
	public ThematicRole createNewItem()
	{
		ThematicRole role = new ThematicRole("<Not defined yet>");
		role.setDescription("");

		return role;
	}

}
