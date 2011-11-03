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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.DescribedItemNameComparator;
import de.akra.idocit.common.utils.SignatureElementUtils;
import de.akra.idocit.common.utils.StringUtils;

/**
 * <table name="idocit" border="1" cellspacing="0">
 * <tr>
 * <td>Role:</td>
 * <td>AGENT</td>
 * </tr>
 * <tr>
 * <td><b>Developer</b>:</td>
 * <td>Service to execute role-based and grid-based rules. Rules are used to reduce the
 * grid to the minimum set of roles required to be documented.</td>
 * </tr>
 * </table>
 * 
 * @author Jan Christian Krause
 * @author Florian Stumpf
 * 
 */
public final class RuleService
{
	private static final Logger LOG = Logger.getLogger(RuleService.class.getName());

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
	 *            selectedSignatureElement:de.akra.idocit.common.structure.
	 *            SignatureElement</td>
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
	 *         de.akra.idocit.common.structure.ThematicGrid:de.akra.idocit.common.
	 *         structure .ThematicGrid</td>
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
	public static ThematicGrid reduceGrid(final ThematicGrid gridToReduce,
			final SignatureElement selectedSignatureElement)
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
	 * recommended to document 2nd level: these roles could be documented, but they do not
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
	 * <td>The derivation algorithm in pseudo code:
	 * 
	 * <pre>
	 * For each defined thematic role
	 * {
	 * 	Execute role-based rule
	 * 	if(role-rule == true)
	 * 	{
	 * 		add role to 1st level list
	 * 	}
	 * 	else
	 * 	{
	 * 		add role to 2nd level list
	 * 	}
	 * }
	 * Get the Operation which the given signature element belongs to.
	 * If a reference grid exists or the given collection contains only 1 grid
	 * {
	 * 	for each role in grid
	 * 	{
	 * 		if role exists in 1st level list
	 * 		{
	 * 			Execute grid-based rule
	 * 			if(grid-rule == false)
	 * 			{
	 * 				Remove rule from first level list
	 * 			}
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * </td>
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
	 *            selectedSignatureElement:de.akra.idocit.common.structure.
	 *            SignatureElement</td>
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
			final Collection<ThematicGrid> matchingGrids,
			final SignatureElement selectedSignatureElement)
	{
		final Set<ThematicRole> firstLevel = new HashSet<ThematicRole>();
		final Set<ThematicRole> secondLevel = new HashSet<ThematicRole>();

		if (matchingGrids != null)
		{
			evaluateRoleBasedRules(matchingGrids, selectedSignatureElement, firstLevel,
					secondLevel);

			evaluateGridBasedRules(matchingGrids, selectedSignatureElement, firstLevel,
					secondLevel);

		}

		return new RolesRecommendations(sortByName(firstLevel), sortByName(secondLevel));
	}

	private static void evaluateRoleBasedRules(
			final Collection<ThematicGrid> matchingGrids,
			final SignatureElement selectedSignatureElement,
			final Set<ThematicRole> firstLevel, final Set<ThematicRole> secondLevel)
	{
		for (final ThematicGrid grid : matchingGrids)
		{
			final Map<ThematicRole, Boolean> roles = grid.getRoles();

			if (roles != null)
			{
				for (final ThematicRole role : roles.keySet())
				{
					if (role.getRoleBasedRule() != null
							&& !role.getRoleBasedRule().isEmpty())
					{
						if (evaluateRule(role.getRoleBasedRule(), role,
								selectedSignatureElement))
						{
							firstLevel.add(role);
						}
						else
						{
							secondLevel.add(role);
						}
					}
					else
					{
						LOG.log(Level.SEVERE, "No role-based-rule found for role " + role);
						throw new IllegalStateException(
								"No Role-based rule found for role " + role.getName());
					}
				}
			}
		}
	}

	/**
	 * Important: The Sets must not be null!
	 * 
	 * @param matchingGrids
	 * @param selectedSignatureElement
	 * @param firstLevel
	 * @param secondLevel
	 */
	private static void evaluateGridBasedRules(final Collection<ThematicGrid> matchingGrids,
			final SignatureElement selectedSignatureElement,
			final Set<ThematicRole> firstLevel, final Set<ThematicRole> secondLevel)
	{
		final Operation op = (Operation) SignatureElementUtils.findOperationForParameter(selectedSignatureElement);
		final ThematicGrid theOne = getUnambiguousGrid(matchingGrids, op);

		if (theOne != null)
		{
			final Set<ThematicRole> gridRoles = theOne.getRoles().keySet();
			
			for (final ThematicRole role : firstLevel) {
				if (!gridRoles.contains(role))
				{
					//Remove roles that are not within the reference/only available thematic grid:
					firstLevel.remove(role);
					secondLevel.add(role);
				}
				else if (!evaluateRule(theOne.getGridBasedRule(), role, selectedSignatureElement)) {
					//Remove role if the grid-based-rule does not apply:
					firstLevel.remove(role);
					secondLevel.add(role);
				}
			}
		}
	}

	/**
	 * Evaluates the given rule for the given {@link SignatureElement}. The rule can
	 * either be role- or grid-based.
	 * 
	 * @param rule
	 *            The rule to evaluate.
	 * @param sigElem
	 *            The {@link SignatureElement} to apply the rule to.
	 * @return The result of the rule-evaluation, thus either <code>true</code> or
	 *         <code>false</code>
	 */
	public static boolean evaluateRule(final String rule, final ThematicRole role,
			final SignatureElement sigElem)
	{
		Boolean result;
		final ScriptEngine engine = getScriptEngine();

		engine.put("role", role);
		engine.put("operation", SignatureElementUtils.findOperationForParameter(sigElem));

		try
		{
			result = (Boolean) engine.eval(rule);
		}
		catch (ScriptException e)
		{
			LOG.log(Level.SEVERE, "Error evaluating rule \"" + rule + "\".", e);
			throw new IllegalArgumentException("Error evaluating rule \"" + rule + "\".");
		}

		return result.booleanValue();
	}

	/**
	 * Filters the given collection of ThematicGrids to return either the reference-grid,
	 * or - in case the collection contains only one grid - the only available grid.
	 * 
	 * If the collection is empty or no reference grid is chosen, this method will return
	 * <code>null</code>
	 * 
	 * @param grids
	 *            A collection of ThematicGrids.
	 * @param operation
	 *            The current {@link Operation} to get the reference-grid's name from.
	 * @return Either the only available grid, the reference grid or <code>null</code>
	 */
	private static ThematicGrid getUnambiguousGrid(final Collection<ThematicGrid> grids,
			final Operation operation)
	{
		ThematicGrid result = null;

		if (grids != null && !grids.isEmpty())
		{
			if (grids.size() == 1)
			{
				// There's only one grid. No big choice, using this one.
				result = grids.iterator().next();
			}
			else if (operation.getThematicGridName() != null)
			{
				// Get the thematic grid matching the reference name from the operation:
				for (final ThematicGrid grid : grids)
				{
					if (grid.getName().equals(operation.getThematicGridName()))
					{
						result = grid;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Sorts the given {@link Set} of {@link ThematicRole}s by name.
	 * 
	 * @param roles
	 *            Unsorted (well, it's a Set...) roles.
	 * @return A List of the given roles, sorted by name.
	 */
	private static List<ThematicRole> sortByName(final Set<ThematicRole> roles)
	{
		final List<ThematicRole> result = new ArrayList<ThematicRole>(roles);
		Collections.sort(result, DescribedItemNameComparator.getInstance());

		return result;
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
		final ScriptEngine engine = getScriptEngine();
		boolean valid = false;

		try
		{
			((Compilable) engine).compile(ruleExpression);
			valid = true;
		}
		catch (final ScriptException e)
		{
			LOG.log(Level.INFO, "Cannot compile rule \"" + ruleExpression + "\".", e);
		}

		return valid;
	}

	/**
	 * Prepares and returns a {@link ScriptEngine}. With this ScriptEngine the rules
	 * (either role- or grid-based) can be evaluated.
	 * 
	 * Currently the ScriptEngine used is meant for JavaScript. Maybe at some time it
	 * might be necessary to implement a specific ScriptEngine to support only elements
	 * used for the rule-syntax.
	 * 
	 * @return A ScriptEngine for evaluating the rules.
	 */
	private static ScriptEngine getScriptEngine()
	{
		// final String predicates =
		// StringUtils.toString(RuleService.class.getResourceAsStream("basicRules.js"));
		String predicates = "";
		try
		{
			predicates = StringUtils.toString(new FileInputStream(
					"src/main/resources/basicRules.js"));
			//TODO: Load basicRules.js via FileLocator
		}
		catch (FileNotFoundException e1)
		{}

		final ScriptEngine engine = new ScriptEngineManager()
				.getEngineByName("JavaScript");

		try
		{
			engine.eval(predicates);
		}
		catch (final ScriptException e)
		{
			LOG.log(Level.SEVERE, "Error loading basic predicates.", e);
		}

		return engine;
	}
}
