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
