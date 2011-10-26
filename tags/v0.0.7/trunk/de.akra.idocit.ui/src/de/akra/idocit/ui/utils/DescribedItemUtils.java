/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
package de.akra.idocit.ui.utils;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.DescribedItem;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Useful methods for working with {@link DescribedItem}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public final class DescribedItemUtils
{
	private static final String NAME_NOT_DEFINED_YET = "<Not defined yet>";

	/**
	 * Checks if the <code>item</code> is in the List of {@link DescribedItem}s
	 * <code>items</code>.
	 * 
	 * @param item
	 *            The searched item.
	 * @param items
	 *            The List in which should be searched.
	 * @return true, {@link IFile} <code>item</code> was found in <code>items</code>.
	 */
	public static boolean containsName(DescribedItem item,
			java.util.List<? extends DescribedItem> items)
	{
		for (DescribedItem curItem : items)
		{
			if (curItem.getName().equals(item.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the DescribedItem with the given name.
	 * 
	 * @param name
	 *            The name to find.
	 * @param items
	 *            List of DescribedItems to search in.
	 * @return The found DescribedItem, otherwise <code>null</code>.
	 */
	public static DescribedItem findByName(String name,
			java.util.List<? extends DescribedItem> items)
	{
		for (DescribedItem curItem : items)
		{
			if (curItem.getName().equals(name))
			{
				return curItem;
			}
		}
		return null;
	}

	/**
	 * Find the index in the items list of the item with the given name.
	 * 
	 * @param name
	 *            The name to search for.
	 * @param items
	 *            The list of items in that should be searched.
	 * @return the 0 based index of the item in the items list, otherwise -1.
	 */
	public static int indexOfName(String name,
			java.util.List<? extends DescribedItem> items)
	{
		for (int i = 0; i < items.size(); ++i)
		{
			if (items.get(i).getName().equals(name))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Create a new {@link Addressee} with default values.
	 * 
	 * @return a new {@link Addressee}.
	 */
	public static Addressee createNewAddressee()
	{
		Addressee addressee = new Addressee(NAME_NOT_DEFINED_YET);
		addressee.setDescription("");
		return addressee;
	}

	/**
	 * Create a new {@link ThematicRole} with default values.
	 * 
	 * @return a new {@link ThematicRole}.
	 */
	public static ThematicRole createNewThematicRole()
	{
		ThematicRole role = new ThematicRole(NAME_NOT_DEFINED_YET);
		role.setDescription("");
		return role;
	}

	/**
	 * Create a new {@link ThematicGrid} with default values.
	 * 
	 * @return a new {@link ThematicGrid}.
	 */
	public static ThematicGrid createNewThematicGrid()
	{
		ThematicGrid grid = new ThematicGrid();
		grid.setName(NAME_NOT_DEFINED_YET);
		grid.setDescription("");
		grid.setVerbs(new HashSet<String>());
		grid.setRoles(new HashMap<ThematicRole, Boolean>());
		return grid;
	}
	
	/**
	 * Copies the oldRole and returns a new one.
	 * 
	 * @param oldRole
	 *            The role to copy.
	 * @return The new {@link ThematicRole}.
	 */
	public static ThematicRole copy(ThematicRole oldRole)
	{
		return new ThematicRole(oldRole.getName(), oldRole.getDescription());
	}

	/**
	 * Copies the oldAddressee and returns a new one.
	 * 
	 * @param oldAddressee
	 *            The Addressee to copy.
	 * @return The new {@link Addressee}.
	 */
	public static Addressee copy(Addressee oldAddressee)
	{
		Addressee newAddressee = new Addressee(oldAddressee.getName());
		newAddressee.setDescription(oldAddressee.getDescription());
		newAddressee.setDefault(oldAddressee.isDefault());
		return newAddressee;
	}
}
