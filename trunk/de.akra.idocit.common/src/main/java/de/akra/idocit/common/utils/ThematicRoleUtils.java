package de.akra.idocit.common.utils;

import java.util.Collection;

import de.akra.idocit.common.structure.ThematicRole;

public final class ThematicRoleUtils
{
	public static boolean containsRoleWithName(String name, Collection<ThematicRole> roles)
	{
		return findRoleByName(name, roles) != null;
	}

	public static ThematicRole findRoleByName(String name, Collection<ThematicRole> roles)
	{
		for (ThematicRole role : roles)
		{
			if (role.getName().equals(name))
			{
				return role;
			}
		}

		return null;
	}
}
