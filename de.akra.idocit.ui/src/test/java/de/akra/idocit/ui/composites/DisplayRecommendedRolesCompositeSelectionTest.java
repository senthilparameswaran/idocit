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
					null, null, "", null);
			
			assertTrue(selection.getAssignedThematicRoles().isEmpty());
			assertTrue(selection.getCollapsedThematicGridNames().isEmpty());
			assertTrue(selection.getRecommendedThematicGrids().isEmpty());
		}

		// *********************************************************************
		// Positive tests
		// *********************************************************************
		{}
	}
}
