/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
import java.util.Collection;
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
 * @since 0.0.1
 * @version 0.0.2
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
	private static List<ThematicGrid> findMatchingGrids(final String verb,
			final List<ThematicGrid> definedGrids)
	{
		final List<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();

		for (final ThematicGrid definedGrid : definedGrids)
		{
			final Set<String> verbs = definedGrid.getVerbs();

			if (verbs.contains(verb))
			{
				matchingGrids.add(definedGrid);
			}
		}

		return matchingGrids;
	}

	/**
	 * Extracts the verb from the given identifier. The identifier is assumed to be in
	 * camel-syntax.
	 * 
	 * @param identifier
	 *            The operation identifier to extract the verb from
	 * 
	 * @return Extracted verb (<code>null</code> if no verb could be identified)
	 */
	public static String extractVerb(final String identifier)
	{
		if ((identifier != null) && (!identifier.trim().isEmpty()))
		{
			final String sentenceIdentifier = StringUtils
					.addBlanksToCamelSyntax(identifier);

			// Identify the verb.
			final String[] words = sentenceIdentifier.split(StringUtils.SPACE);
			if (words.length > 0)
			{
				return words[0].toLowerCase();
			}
		}

		return null;
	}

	/**
	 * Finds the thematic grids for the verb out of the <code>identifier</code>. The
	 * identifier is assumed to be in camel-syntax. If no verb could be identified, the
	 * resulting map will be empty.
	 * 
	 * @param identifier
	 *            The identifier from which the verb should be extracted and the thematic
	 *            grids should be derived.
	 * @param definedGrids
	 *            The list of defined {@link ThematicGrid}s (SOURCE)
	 * @return Map of thematic grid names linking to the {@link ThematicGrid}.
	 */
	public static Map<String, ThematicGrid> deriveThematicGrid(final String identifier,
			final List<ThematicGrid> definedGrids)
	{
		final Map<String, ThematicGrid> matchingGrids = new HashMap<String, ThematicGrid>();

		final String verb = extractVerb(identifier);
		if (verb != null)
		{
			// Classify the verb.
			final List<ThematicGrid> matchingVerbClasses = findMatchingGrids(verb,
					definedGrids);
			if (!matchingVerbClasses.isEmpty())
			{
				for (ThematicGrid verbClass : matchingVerbClasses)
				{
					matchingGrids.put(verbClass.getName(), verbClass);
				}
			}
		}

		return matchingGrids;
	}

	/**
	 * Tests if the given {@link ThematicRole} is included in the given list of
	 * {@link ThematicRole}s.
	 * 
	 * Please note: two roles are treated as equal, if they have the same name.
	 * 
	 * @param roles
	 *            [SOURCE] The list to look into
	 * @param role
	 *            [COMPARISON] The role to look for
	 * @subparam name
	 *            [PRIMARY_KEY]
	 * @return [REPORT] <code>true</code>, if the role is included in the list, else
	 *         <code>false</code>
	 * @thematicgrid Checking Operations
	 */
	public static boolean containsRole(final Collection<ThematicRole> roles,
			final ThematicRole role)
	{
		for (final ThematicRole referenceRole : roles)
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
	public static List<ThematicRole> collectThematicRoles(final List<ThematicGrid> grids,
			final List<ThematicRole> existingRoles)
	{
		final List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.addAll(existingRoles);

		for (final ThematicGrid grid : grids)
		{
			for (final ThematicRole role : grid.getRoles().keySet())
			{
				if (!containsRole(roles, role))
				{
					roles.add(role);
				}
			}
		}

		return roles;
	}

	public static ThematicGrid findThematicGridByName(final String thematicGridName,
			final List<ThematicGrid> grids)
	{
		for (final ThematicGrid grid : grids)
		{
			if (grid.getName().equals(thematicGridName))
			{
				return grid;
			}
		}

		return null;
	}
}