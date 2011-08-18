/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core.utils;

import java.util.Comparator;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.DescribedItem;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Name comparator for {@link DescribedItem}s.
 * 
 * @see Addressee
 * @see ThematicGrid
 * @see ThematicRole
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DescribedItemNameComparator implements Comparator<DescribedItem>
{
	private static DescribedItemNameComparator instance;

	private DescribedItemNameComparator()
	{}

	public static DescribedItemNameComparator getInstance()
	{
		if (instance == null)
		{
			instance = new DescribedItemNameComparator();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(DescribedItem item0, DescribedItem item1)
	{
		if ((item0.getName() != null) && (item1.getName() == null))
		{
			return 1;
		}
		else if ((item0.getName() == null) && (item1.getName() != null))
		{
			return -1;
		}
		else
		{
			return item0.getName().compareTo(item1.getName());
		}
	}
}