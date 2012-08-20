/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.common.utils;

import java.util.Collection;

import de.akra.idocit.common.constants.ThematicRoleConstants;
import de.akra.idocit.common.structure.ThematicRole;

public final class ThematicRoleUtils
{
	public static boolean containsRoleWithName(String name, Collection<ThematicRole> roles)
	{
		return findRoleByName(name, roles) != null;
	}

	public static ThematicRole findRoleByName(String name, Collection<ThematicRole> roles)
	{
		if (name != null)
		{
			for (ThematicRole role : roles)
			{
				if (role.getName().toLowerCase().equals(name.toLowerCase()))
				{
					return role;
				}
			}
		}

		return null;
	}

	/**
	 * Checks if it is recommended to document an error case for the given thematic role.
	 * 
	 * @rule Documenting an error case is recommended for every thematic role except those
	 *       defined in {@link ThematicRoleConstants#MANDARTORY_ROLES}.
	 * 
	 * @param role
	 *            Ê[OBJECT]
	 * @return [REPORT] True: error case documenation is recommended
	 * 
	 * @thematicgrid Checking Operations
	 */
	public static boolean isRoleFailable(ThematicRole role)
	{
		if (role != null)
		{
			for (String mandatoryRole : ThematicRoleConstants.MANDARTORY_ROLES)
			{
				if (mandatoryRole.equals(role.getName()))
				{
					return false;
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}
}
