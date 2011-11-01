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

import java.util.Collection;

import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicGrid;

/**
 * <table name="idocit" border="1" cellspacing="0">
<tr><td>Role:</td><td>AGENT</td></tr>
<tr><td><b>Developer</b>:</td><td>Service to execute role-based and grid-based rules. Rules are used to reduce the gridto the minimum set of roles required to be documented.</td></tr>
</table>
 * @author Jan Christian Krause
 * 
 */
public final class RuleService
{

	public static ThematicGrid reduceGrid(ThematicGrid gridToReduce,
			SignatureElement selectedSignatureElement)
	{
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <table name="idocit" border="1" cellspacing="0">
	<tr><td>Role:</td><td>ACTION</td></tr>
	</table>
	 * @thematicgrid  None
	 */
	public static RolesRecommendations deriveRolesRecommendation(
			Collection<ThematicGrid> matchingGrids,
			SignatureElement selectedSignatureElement)
	{
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <table name="idocit" border="1" cellspacing="0">
	<tr><td>Role:</td><td>ACTION</td></tr>
	<tr><td><b>Developer</b>:</td><td>Tests whether the given rule expression has a valid syntax or not. </td></tr>
	</table> 
	<br /><table name="idocit" border="1" cellspacing="0">
	<tr><td>Role:</td><td>COMPARISON</td></tr>
	<tr><td><b>Developer</b>:</td><td>The rule expression is tested against the grammar of the  iDocIt! Rule Language.</td></tr>
	</table>
	 * @param ruleExpression 
	<br /><table name="idocit" border="1" cellspacing="0">
	<tr><td>Element:</td><td>ruleExpression:java.lang.String</td></tr>
	<tr><td>Role:</td><td>OBJECT</td></tr>
	</table>
	 * @return <table name="idocit" border="1" cellspacing="0">
	<tr><td>Element:</td><td>boolean:boolean</td></tr>
	<tr><td>Role:</td><td>REPORT</td></tr>
	</table>
	 * @thematicgrid  None
	 */
	public static boolean isRuleValid(String ruleExpression)
	{
		throw new RuntimeException("Not yet implemented");
	}
}
