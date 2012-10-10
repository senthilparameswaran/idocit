package de.akra.idocit.common.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * Test-cases for {@link ThematicRoleUtils}
 * 
 * @author Jan Christian Krause
 * 
 */
public class ThematicRoleUtilsTest {

	@Test
	public void testIsRoleFailable() 
	{
		assertFalse(ThematicRoleUtils
				.isRoleFailable(new ThematicRole("ACTION")));
		assertFalse(ThematicRoleUtils.isRoleFailable(new ThematicRole("RULE")));
		assertFalse(ThematicRoleUtils.isRoleFailable(new ThematicRole("NONE")));
		assertFalse(ThematicRoleUtils.isRoleFailable(new ThematicRole("AGENT")));
		assertTrue(ThematicRoleUtils.isRoleFailable(new ThematicRole(null)));
		assertTrue(ThematicRoleUtils.isRoleFailable(new ThematicRole("SOURCE")));
	}
}
