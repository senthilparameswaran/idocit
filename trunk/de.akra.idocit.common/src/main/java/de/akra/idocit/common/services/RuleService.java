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
 * <tr>
 * <td>Role:</td>
 * <td>AGENT</td>
 * </tr>
 * <tr>
 * <td><b>Developer</b>:</td>
 * <td>Service to execute role-based and grid-based rules. Rules are used to reduce the
 * gridto the minimum set of roles required to be documented.</td>
 * </tr>
 * </table>
 * 
 * @author Jan Christian Krause
 * @author Florian Stumpf
 * 
 */
public final class RuleService
{

	/**
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ACTION</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Executes all grid-based roles on the given thematic grid. It removes all
	 * thematic roles which lead to a "false"-evaluation of a rule.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param gridToReduce
	 * <br />
	 *            <table name="idocit" border="1" cellspacing="0">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>gridToReduce:de.akra.idocit.common.structure.ThematicGrid</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>SOURCE</td>
	 *            </tr>
	 *            </table>
	 * @param selectedSignatureElement
	 * <br />
	 *            <table name="idocit" border="1" cellspacing="0">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>
	 *            selectedSignatureElement:de.akra.idocit.common.structure.SignatureElement
	 *            </td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>SOURCE</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>Represents the execution context of the grid-based roles.</td>
	 *            </tr>
	 *            </table>
	 * @return <table name="idocit" border="1" cellspacing="0">
	 *         <tr>
	 *         <td>Element:</td>
	 *         <td>
	 *         de.akra.idocit.common.structure.ThematicGrid:de.akra.idocit.common.structure
	 *         .ThematicGrid</td>
	 *         </tr>
	 *         <tr>
	 *         <td>Role:</td>
	 *         <td>OBJECT</td>
	 *         </tr>
	 *         <tr>
	 *         <td><b>Developer</b>:</td>
	 *         <td>The reduced grid</td>
	 *         </tr>
	 *         </table>
	 * @thematicgrid None
	 */
	public static ThematicGrid reduceGrid(ThematicGrid gridToReduce,
			SignatureElement selectedSignatureElement)
	{
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ACTION</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Splits the defined thematic roles into two groups:1st level: these roles are
	 * recommended to document 2nd level: these roles could bedocumented, but they do not
	 * need to</td>
	 * </tr>
	 * </table>
	 * <br />
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ALGORITHM</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>The derivation algorithm in pseudo code:For each defined thematic role {Execute
	 * role-based ruleif(role-rule == true) {add role to 1st level list} else {add role to
	 * 2nd level list} }Get the Operation which the given signature element belongs to.If
	 * a reference grid exists or the given collection contains only 1 grid {for each role
	 * in grid {if role exists in 1st level list {Execute grid-based ruleif(grid-rule ==
	 * false){Remove rule from first level list}}}}</td>
	 * </tr>
	 * </table>
	 * <br />
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>SOURCE</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>The recommendation bases on all locally defined roles which are retrieved from
	 * the {@link PersistenceService}.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param matchingGrids
	 * <br />
	 *            <table name="idocit" border="1" cellspacing="0">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>matchingGrids:java.util.Collection</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>SOURCE</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>After executing the role-based rules the two recommendation lists
	 *            will have an intermediate state. Afterwards all roles of the first list
	 *            list will be checked against the roles of these grids (are they allowed
	 *            due to the grid-rules).</td>
	 *            </tr>
	 *            </table>
	 * @param selectedSignatureElement
	 * <br />
	 *            <table name="idocit" border="1" cellspacing="0">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>
	 *            selectedSignatureElement:de.akra.idocit.common.structure.SignatureElement
	 *            </td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>OWNER</td>
	 *            </tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>If this signature element is an operation, the recommendation holds
	 *            for it. If it is a parameter, the recommendation holds for the operation
	 *            the parameter belongs to.</td>
	 *            </tr>
	 *            </table>
	 * @return <table name="idocit" border="1" cellspacing="0">
	 *         <tr>
	 *         <td>Element:</td>
	 *         <td>
	 *         de.akra.idocit.common.structure.RolesRecommendations:de.akra.idocit.common
	 *         .structure.RolesRecommendations</td>
	 *         </tr>
	 *         <tr>
	 *         <td>Role:</td>
	 *         <td>OBJECT</td>
	 *         </tr>
	 *         </table>
	 * @thematicgrid Creating Operations
	 */
	public static RolesRecommendations deriveRolesRecommendation(
			Collection<ThematicGrid> matchingGrids,
			SignatureElement selectedSignatureElement)
	{
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ACTION</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Tests whether the given rule expression has a valid syntax or not.</td>
	 * </tr>
	 * </table>
	 * <br />
	 * <table name="idocit" border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>COMPARISON</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>The rule expression is tested against the grammar of the iDocIt! Rule Language.
	 * </td>
	 * </tr>
	 * </table>
	 * 
	 * @param ruleExpression
	 * <br />
	 *            <table name="idocit" border="1" cellspacing="0">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>ruleExpression:java.lang.String</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>OBJECT</td>
	 *            </tr>
	 *            </table>
	 * @return <table name="idocit" border="1" cellspacing="0">
	 *         <tr>
	 *         <td>Element:</td>
	 *         <td>boolean:boolean</td>
	 *         </tr>
	 *         <tr>
	 *         <td>Role:</td>
	 *         <td>REPORT</td>
	 *         </tr>
	 *         </table>
	 * @thematicgrid Checking Operations
	 */
	public static boolean isRuleValid(String ruleExpression)
	{
		throw new RuntimeException("Not yet implemented");
	}
}
