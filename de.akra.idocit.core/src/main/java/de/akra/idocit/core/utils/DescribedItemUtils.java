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
package de.akra.idocit.core.utils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;

/**
 * 
 * Some useful methods, e.g. to traverse the object structure.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class DescribedItemUtils
{

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(DescribedItemUtils.class.getName());

	private static List<Addressee> supportedAddressees = ServiceManager.getInstance().getPersistenceService()
			.loadConfiguredAddressees();

	private static List<ThematicRole> supportedThematicRoles = ServiceManager.getInstance().getPersistenceService()
			.loadThematicRoles();

	// use this for testing with JUnit, because the workspace is not available
	// private static List<Addressee> supportedAddressees = Collections.emptyList();
	// private static List<ThematicRole> supportedThematicRoles = Collections.emptyList();

	/**
	 * Get the {@link ThematicRole} by name. The names are compared with case
	 * insensitivity. If no existing {@link ThematicRole} with the name is found, a new
	 * one is created and returned.
	 * 
	 * @param name
	 *            The name of the searched {@link ThematicRole}.
	 * @return a {@link ThematicRole} with the name <code>name</code>.
	 */
	public static ThematicRole findThematicRole(String name)
	{
		for (ThematicRole role : supportedThematicRoles)
		{
			if (role.getName().equalsIgnoreCase(name))
			{
				return role;
			}
		}
		logger.log(Level.INFO, "Unknown thematic role: " + name);
		return new ThematicRole(name);
	}

	/**
	 * Get the {@link Addressee} by name. The names are compared with case insensitivity.
	 * If no existing {@link Addressee} with the name is found, a new one is created and
	 * returned.
	 * 
	 * @param name
	 *            The name of the searched {@link Addressee}.
	 * @return a {@link Addressee} with the name <code>name</code>.
	 */
	public static Addressee findAddressee(String name)
	{
		for (Addressee a : supportedAddressees)
		{
			if (a.getName().equalsIgnoreCase(name))
			{
				return a;
			}
		}
		logger.log(Level.INFO, "Unknown addressee: " + name);
		return new Addressee(name);
	}

}
