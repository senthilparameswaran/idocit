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
package de.akra.idocit.common.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;

/**
 * Service to derive thematic grids for a verb out of an identifier.
 * 
 * @author Jan Christian Krause
 * 
 */
public class ThematicGridService
{

	/**
	 * Finds the thematic grids for the given <code>verb</code>.
	 * 
	 * @param verb
	 *            The verb for which should be find the thematic grids.
	 * @param definedGrids
	 *            The list of defined {@link ThematicGrid}s (SOURCE)
	 * 
	 * @return List of found {@link ThematicGrid}s.
	 */
	private static List<ThematicGrid> findMatchingGrids(String verb,
			List<ThematicGrid> definedGrids)
	{
		List<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();

		for (ThematicGrid definedGrid : definedGrids)
		{
			Set<String> verbs = definedGrid.getVerbs();

			if (verbs.contains(verb))
			{
				matchingGrids.add(definedGrid);
			}
		}

		return matchingGrids;
	}

	/**
	 * Finds the thematic grids for the verb out of the <code>identifier</code>.
	 * 
	 * @param identifier
	 *            The identifier from which the verb should be extracted and the thematic
	 *            grids should be derived.
	 * @param definedGrids
	 *            The list of defined {@link ThematicGrid}s (SOURCE)
	 * @return Map of thematic grid names linking to Set of {@link ThematicRole} s.
	 */
	public static Map<String, Map<ThematicRole, Boolean>> deriveThematicGrid(
			String identifier, List<ThematicGrid> definedGrids)
	{
		Map<String, Map<ThematicRole, Boolean>> matchingRoles = new HashMap<String, Map<ThematicRole, Boolean>>();

		String sentenceIdentifier = StringUtils.addBlanksToCamelSyntax(identifier);

		// Identify the verb.
		String[] words = sentenceIdentifier.split(" ");
		if (words.length > 0)
		{
			String verb = words[0].toLowerCase();

			// Classify the verb.
			List<ThematicGrid> matchingVerbClasses = findMatchingGrids(verb, definedGrids);

			if (!matchingVerbClasses.isEmpty())
			{
				// Lookup the recommended arguments and modificators.
				for (ThematicGrid verbClass : matchingVerbClasses)
				{
					matchingRoles.put(verbClass.getName(), verbClass.getRoles());
				}
			}
		}

		return matchingRoles;
	}

	/**
	 * Tests if the given {@link ThematicRole} is included in the given list of
	 * {@link ThematicRole}s.
	 * 
	 * Please note: two roles are treated as equal, if they have the same name.
	 * 
	 * @param roles
	 *            The list to look into
	 * @param role
	 *            The role to look for
	 * @return <code>true</code>, if the role is included in the list, else
	 *         <code>false</code>
	 */
	public static boolean containsRole(List<ThematicRole> roles, ThematicRole role)
	{
		for (ThematicRole referenceRole : roles)
		{
			if ((referenceRole.getName() != null)
					&& (referenceRole.getName().equals(role.getName())))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Collects the list of {@link ThematicRole}s from the given list of
	 * {@link ThematicGrid}s and returns them. The existing list of roles avoid double
	 * imported {@link ThematicRole}s.
	 * 
	 * Please note: each role existing in the list of exsting roles is inluded only one
	 * time in the resulting list. Role equality in this context is defined by name
	 * equality: two roles with the same name are treated as equal!
	 * 
	 * @param grids
	 *            The list of {@link ThematicGirds}s to get the roles from (SOURCE)
	 * @param existingRoles
	 *            The list of {@link ThematicRole}s which should not be imported again.
	 * @return The list of {@link ThematicRole}s from the given list of
	 *         {@link ThematicGrid}s
	 * 
	 */
	public static List<ThematicRole> collectThematicRoles(List<ThematicGrid> grids,
			List<ThematicRole> existingRoles)
	{
		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.addAll(existingRoles);

		for (ThematicGrid grid : grids)
		{
			for (ThematicRole role : grid.getRoles().keySet())
			{
				if (!containsRole(roles, role))
				{
					roles.add(role);
				}
			}
		}

		return roles;
	}
}