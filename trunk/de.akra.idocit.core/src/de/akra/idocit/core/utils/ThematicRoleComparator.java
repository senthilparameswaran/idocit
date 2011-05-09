package de.akra.idocit.core.utils;

import java.util.Comparator;

import de.akra.idocit.core.structure.ThematicRole;

/**
 * Comparator for {@link ThematicRole}s
 * 
 * @author Jan Christian Krause
 * 
 */
public class ThematicRoleComparator implements Comparator<ThematicRole> {

	private static ThematicRoleComparator instance;

	private ThematicRoleComparator() {

	}

	public static ThematicRoleComparator getInstance() {
		if (instance == null) {
			instance = new ThematicRoleComparator();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(ThematicRole role0, ThematicRole role1) {
		if ((role0.getName() != null) && (role1.getName() == null)) {
			return 1;
		} else if ((role0.getName() == null) && (role1.getName() != null)) {
			return -1;
		} else {
			return role0.getName().compareTo(role1.getName());
		}
	}

}
