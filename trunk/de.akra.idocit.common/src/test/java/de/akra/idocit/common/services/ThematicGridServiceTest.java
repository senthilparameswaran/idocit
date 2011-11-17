package de.akra.idocit.common.services;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThematicGridServiceTest
{

	@Test
	public void testExtractVerb()
	{
		// Positive tests
		// ******************************************************************************
		{
			// Test case #1: the extracted verb is "remove" from the identifier
			// "removeName".
			assertEquals("remove", ThematicGridService.extractVerb("removeName"));

			// Test case #2: the extracted verb is "remove" from the identifier
			// "RemoveName".
			assertEquals("remove", ThematicGridService.extractVerb("RemoveName"));

			// Test case #3: the extracted verb is null for the identifier "".
			assertEquals(null, ThematicGridService.extractVerb(""));

			// Test case #4: the extracted verb is null for the identifier "   ".
			assertEquals(null, ThematicGridService.extractVerb("  "));

			// Test case #5: the extracted verb is null for null.
			assertEquals(null, null);
		}

		// Negative tests
		// ******************************************************************************
		{
			// None
		}
	}

}
