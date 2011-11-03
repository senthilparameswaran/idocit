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
package de.akra.idocit.common.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.ParameterTest;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;

public class RuleServiceTest
{
	
	@Test
	public void testReduceGrid()
	{
		// Positive tests
		// ******************************************************************************
		{
			
		}
		
		// Negative tests
		// ******************************************************************************
		{
			
		}
	}

	@Test
	public void testDeriveRolesRecommendation()
	{
		// Positive tests
		// ******************************************************************************
		{
			
		}
		
		// Negative tests
		// ******************************************************************************
		{
			
		}
	}

	@Test
	public void testIsRuleValid()
	{
		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Check if a valid snippet of JS is regarded as valid rule.
		 * ****************************************************************************
		 */
		{
			assertTrue(RuleService.isRuleValid("println('Hello World!');"));
		}
		{
			final Parameter param = ParameterTest.createParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			final ThematicRole role = new ThematicRole("testRole", "This is a role for testing");
			RuleService.evaluateRule("info()", role, param);
		}
		
		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Check the validity of a random rule-expression.
		 * ****************************************************************************
		 */
		{
			assertFalse(RuleService.isRuleValid("foo bar baz"));
		}
	}
}
