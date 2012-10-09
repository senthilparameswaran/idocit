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
