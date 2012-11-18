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
package de.akra.idocit.ui.composites;

import static org.junit.Assert.*;

import org.junit.Test;

public class DisplayRecommendedRolesCompositeSelectionTest
{

	@Test
	public void testDisplayRecommendedRolesCompositeSelectionMapOfStringThematicGridSetOfThematicRoleStringSetOfString()
	{
		// *********************************************************************
		// Positive tests
		// *********************************************************************
		{
			// *****************************************************************
			// Test case #1: if the constructor is invoked with null-parameters for maps
			// or sets, the selection is initialized with empty maps or sets.
			// *****************************************************************
			DisplayRecommendedRolesCompositeSelection selection = new DisplayRecommendedRolesCompositeSelection(
					null, null, "", null, null);
			
			assertTrue(selection.getAssignedThematicRoles().isEmpty());
			assertTrue(selection.getCollapsedThematicGridNames().isEmpty());
			assertTrue(selection.getRecommendedThematicGrids().isEmpty());
			assertTrue(selection.getAssignedThematicRolesWithErrorDocumentation().isEmpty());
		}

		// *********************************************************************
		// Positive tests
		// *********************************************************************
		{}
	}
}
