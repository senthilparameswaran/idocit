package de.akra.idocit.ui.actions;

import de.akra.idocit.core.structure.ThematicRole;

/**
 * Factory for {@link ThematicRole}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
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
