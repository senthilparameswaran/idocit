package de.akra.idocit.ui.actions;

import java.util.HashMap;
import java.util.HashSet;

import de.akra.idocit.structure.DescribedItem;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;

/**
 * Factory for {@link ThematicGrid}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class ThematicGridFactory extends AbsItemFactory
{

	@Override
	public DescribedItem createNewItem()
	{
		ThematicGrid grid = new ThematicGrid();

		grid.setName("<NOT DEFINED YET>");
		grid.setDescription("");
		grid.setVerbs(new HashSet<String>());
		grid.setRoles(new HashMap<ThematicRole, Boolean>());
		return grid;
	}
}
