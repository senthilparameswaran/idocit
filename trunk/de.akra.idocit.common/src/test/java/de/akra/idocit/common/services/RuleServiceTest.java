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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.RoleScope;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.structure.impl.TestInterface;
import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;

/**
 * Test cases for {@link RuleService}
 * 
 * @author Jan Christian Krause
 * @author Florian Stumpf
 * 
 */
public class RuleServiceTest
{

	private ThematicGrid createFindingOperationsGrid()
	{
		Map<String, String> gridBasedRules = new HashMap<String, String>();
		gridBasedRules.put("COMPARISON", "!exists(\"PRIMARY_KEY\")");
		gridBasedRules.put("PRIMARY_KEY", "!exists(\"COMPARISON\")");
		gridBasedRules.put("SOURCE", "def()");
		gridBasedRules.put("AGENT", "def()");
		gridBasedRules.put("ACTION", "def()");

		ThematicGrid findingOperationsGrid = new ThematicGrid();
		findingOperationsGrid.setDescription("");
		findingOperationsGrid.setGridBasedRules(gridBasedRules);
		findingOperationsGrid.setName("Finding Operations");
		findingOperationsGrid.setRefernceVerb("find");

		ThematicRole roleAGENT = new ThematicRole("AGENT", "", RoleScope.INTERFACE_LEVEL);
		ThematicRole roleACTION = new ThematicRole("ACTION", "", RoleScope.OPERATION_LEVEL);
		roleAGENT.setRoleScope(RoleScope.INTERFACE_LEVEL);
		ThematicRole roleSOURCE = new ThematicRole("SOURCE", "", RoleScope.BOTH);
		ThematicRole roleCOMPARISON = new ThematicRole("COMPARISON", "", RoleScope.BOTH);
		ThematicRole rolePRIMARY_KEY = new ThematicRole("PRIMARY_KEY", "", RoleScope.BOTH);

		Map<ThematicRole, Boolean> roles = new HashMap<ThematicRole, Boolean>();
		roles.put(roleSOURCE, Boolean.TRUE);
		roles.put(roleCOMPARISON, Boolean.TRUE);
		roles.put(rolePRIMARY_KEY, Boolean.TRUE);
		roles.put(roleAGENT, Boolean.TRUE);
		roles.put(roleACTION, Boolean.TRUE);
		findingOperationsGrid.setRoles(roles);

		Set<String> verbs = new HashSet<String>();
		verbs.add("find");
		verbs.add("search");
		findingOperationsGrid.setVerbs(verbs);

		return findingOperationsGrid;
	}

	private Collection<ThematicGrid> createMatchingGrids()
	{
		List<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();
		matchingGrids.add(createFindingOperationsGrid());

		return matchingGrids;
	}

	private List<ThematicRole> createDefinedRoles()
	{
		List<ThematicRole> definedThematicRoles = new ArrayList<ThematicRole>();

		definedThematicRoles.add(new ThematicRole("SOURCE", "", RoleScope.BOTH));
		definedThematicRoles.add(new ThematicRole("PRIMARY_KEY", "", RoleScope.BOTH));
		definedThematicRoles.add(new ThematicRole("COMPARISON", "", RoleScope.BOTH));
		definedThematicRoles
				.add(new ThematicRole("ACTION", "", RoleScope.OPERATION_LEVEL));
		definedThematicRoles
				.add(new ThematicRole("AGENT", "", RoleScope.INTERFACE_LEVEL));

		return definedThematicRoles;
	}

	/**
	 * Test cases for
	 * {@link RuleService#deriveRolesRecommendation(java.util.Collection, SignatureElement)}
	 */
	@Test
	public void testDeriveRolesRecommendation()
	{
		// Positive tests
		// ******************************************************************************
		{
			// Test case #1: an undocumented operation with empty roles and no reference
			// grid leads to empty recommendations.
			{
				Operation findingOperation = createUndocumentedFindCustomerByNameOperation();
				Collection<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();
				List<ThematicRole> definedRoles = new ArrayList<ThematicRole>();

				List<ThematicRole> firstLevelRoles = new ArrayList<ThematicRole>();
				List<ThematicRole> secondLevelRoles = new ArrayList<ThematicRole>();

				RolesRecommendations referenceRecommendation = new RolesRecommendations(
						firstLevelRoles, secondLevelRoles);

				RolesRecommendations actualRecommendation = RuleService
						.deriveRolesRecommendation(matchingGrids, definedRoles,
								findingOperation);

				assertEquals(referenceRecommendation, actualRecommendation);
			}

			// Test case #2: for an undocumented operation the first level recommendations
			// should contain the whole grid plus the ACTION role. The
			{
				Operation undocumentedFinder = createUndocumentedFindCustomerByNameOperation();
				Collection<ThematicGrid> matchingGrids = createMatchingGrids();
				List<ThematicRole> definedRoles = createDefinedRoles();

				List<ThematicRole> firstLevelRoles = new ArrayList<ThematicRole>();
				firstLevelRoles.add(new ThematicRole("ACTION", "",
						RoleScope.OPERATION_LEVEL));
				firstLevelRoles.add(new ThematicRole("COMPARISON", "", RoleScope.BOTH));
				firstLevelRoles.add(new ThematicRole("PRIMARY_KEY", "", RoleScope.BOTH));
				firstLevelRoles.add(new ThematicRole("SOURCE", "", RoleScope.BOTH));

				List<ThematicRole> secondLevelRoles = new ArrayList<ThematicRole>();
				secondLevelRoles.add(new ThematicRole("AGENT", "",
						RoleScope.INTERFACE_LEVEL));

				RolesRecommendations referenceRecommendation = new RolesRecommendations(
						firstLevelRoles, secondLevelRoles);

				RolesRecommendations actualRecommendation = RuleService
						.deriveRolesRecommendation(matchingGrids, definedRoles,
								undocumentedFinder);

				assertEquals(referenceRecommendation, actualRecommendation);
			}

			{
				// Test case #3: documented role which are not required by the reference
				// grid should be added into the second level recommendations.
				Operation findingOperation = createFindCustomer_SINGULAR_ByNameOperation();
				// On Interface-Level we have no ThematicGrids (because of the missing
				// verb).
				Collection<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();
				List<ThematicRole> definedRoles = createDefinedRoles();

				RolesRecommendations actualRolesRecommendations = RuleService
						.deriveRolesRecommendation(matchingGrids, definedRoles,
								findingOperation);

				// Keep in mind: the roles were sorted by name by the RuleService!
				List<ThematicRole> firstLevelRecommendations = new ArrayList<ThematicRole>();
				firstLevelRecommendations.add(new ThematicRole("ACTION", "",
						RoleScope.OPERATION_LEVEL));
				firstLevelRecommendations.add(new ThematicRole("PRIMARY_KEY", "",
						RoleScope.BOTH));
				firstLevelRecommendations.add(new ThematicRole("SOURCE", "",
						RoleScope.BOTH));

				List<ThematicRole> secondLevelRecommendations = new ArrayList<ThematicRole>();
				secondLevelRecommendations.add(new ThematicRole("AGENT", "",
						RoleScope.INTERFACE_LEVEL));
				secondLevelRecommendations.add(new ThematicRole("COMPARISON", "",
						RoleScope.BOTH));
				secondLevelRecommendations.add(new ThematicRole("OBJECT", "",
						RoleScope.BOTH));

				RolesRecommendations referenceRolesRecommendations = new RolesRecommendations(
						firstLevelRecommendations, secondLevelRecommendations);

				assertEquals(referenceRolesRecommendations, actualRolesRecommendations);
			}

			{
				// Test case #4: for a selected interface, only the role AGENT should be
				// included on recommended first level.
				Operation findingOperation = createFindCustomer_SINGULAR_ByNameOperation();
				// On Intrface-Level we have no ThematicGrids (because of the missing
				// verb).
				Collection<ThematicGrid> matchingGrids = new ArrayList<ThematicGrid>();
				List<ThematicRole> definedRoles = createDefinedRoles();

				RolesRecommendations actualRolesRecommendations = RuleService
						.deriveRolesRecommendation(matchingGrids, definedRoles,
								findingOperation.getParent());

				// Keep in mind: the roles were sorted by name by the RuleService!
				List<ThematicRole> firstLevelRecommendations = new ArrayList<ThematicRole>();
				firstLevelRecommendations.add(new ThematicRole("AGENT", "",
						RoleScope.INTERFACE_LEVEL));
				firstLevelRecommendations.add(new ThematicRole("COMPARISON", "",
						RoleScope.BOTH));
				firstLevelRecommendations.add(new ThematicRole("PRIMARY_KEY", "",
						RoleScope.BOTH));
				firstLevelRecommendations.add(new ThematicRole("SOURCE", "",
						RoleScope.BOTH));

				List<ThematicRole> secondLevelRecommendations = new ArrayList<ThematicRole>();
				secondLevelRecommendations.add(new ThematicRole("ACTION", "",
						RoleScope.OPERATION_LEVEL));

				RolesRecommendations referenceRolesRecommendations = new RolesRecommendations(
						firstLevelRecommendations, secondLevelRecommendations);

				assertEquals(referenceRolesRecommendations, actualRolesRecommendations);
			}

		}

		// Negative tests
		// ******************************************************************************
		{

		}
	}

	/**
	 * Test cases for
	 * {@link RuleService#reduceGrid(de.akra.idocit.common.structure.ThematicGrid, SignatureElement)}
	 */
	@Test
	public void testReduceGrid()
	{
		// Positive tests
		// ******************************************************************************
		{
			// Test case #1: for a finding operation with a COMPARISON no PRIMARY_KEY
			// needs to be defined.
			{
				ThematicGrid findingOperationsGrid = createFindingOperationsGrid();
				ThematicGrid referenceGridComparison = (ThematicGrid) findingOperationsGrid
						.clone();
				referenceGridComparison.getRoles().remove(
						new ThematicRole("PRIMARY_KEY", "", RoleScope.BOTH));
				referenceGridComparison.getGridBasedRules().remove("PRIMARY_KEY");

				Operation findingOperationWithComparison = createFindCustomer_SINGULAR_ByNameOperation();
				ThematicGrid actualGridComparison = RuleService.reduceGrid(
						findingOperationsGrid, findingOperationWithComparison);

				assertEquals(referenceGridComparison, actualGridComparison);

			}
			// Test case #2: for a finding operation with a PRIMARY_KEY no COMPARISON
			// needs to be defined.
			{
				ThematicGrid findingOperationsGrid = createFindingOperationsGrid();
				ThematicGrid referenceGridPrimaryKey = (ThematicGrid) findingOperationsGrid
						.clone();
				referenceGridPrimaryKey.getRoles().remove(
						new ThematicRole("COMPARISON", "", RoleScope.BOTH));
				referenceGridPrimaryKey.getGridBasedRules().remove("COMPARISON");

				Operation findingOperationWithPrimaryKey = createFindCustomer_SINGULAR_ByNameOperationWithPrimaryKey();
				ThematicGrid actualGridPrimaryKey = RuleService.reduceGrid(
						findingOperationsGrid, findingOperationWithPrimaryKey);

				assertEquals(referenceGridPrimaryKey, actualGridPrimaryKey);
			}
		}

		// Negative tests
		// ******************************************************************************
		{

		}
	}

	/**
	 * Returns the undocumented {@link Operation} for test purposes.
	 * 
	 * @return The undocumented {@link Operation} for test purposes
	 */
	private Operation createUndocumentedFindCustomerByNameOperation()
	{
		Interface interfaceCustomerService = new TestInterface(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Interface", Numerus.SINGULAR);

		List<Operation> operations = new ArrayList<Operation>();
		Operation operationFindCustomerByName = new TestOperation(
				interfaceCustomerService, "Operation", "Finding Operations",
				Numerus.SINGULAR);
		operations.add(operationFindCustomerByName);
		interfaceCustomerService.setOperations(operations);

		Parameters inputs = new TestParameters(operationFindCustomerByName, "Paratemers",
				Numerus.SINGULAR);
		operationFindCustomerByName.setInputParameters(inputs);

		Parameter paramLastName = new TestParameter(inputs, "Parameter",
				Numerus.SINGULAR, false);
		paramLastName.setDataTypeName("java.lang.String");
		paramLastName.setIdentifier("lastname");

		inputs.addParameter(paramLastName);

		Parameters outputs = new TestParameters(operationFindCustomerByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomerByName.setOutputParameters(outputs);

		Parameter resultCustomerList = new TestParameter(outputs, "Results",
				Numerus.SINGULAR, false);
		resultCustomerList.setDataTypeName("java.lang.String");
		resultCustomerList.setIdentifier("result");
		outputs.addParameter(resultCustomerList);

		return operationFindCustomerByName;
	}

	/**
	 * Returns the {@link Operation} for test purposes of predicate "isSingular()" with a
	 * documented primary key.
	 * 
	 * @return The {@link Operation} for test purposes of predicate
	 *         "isSingular() with a documented primary keyÏ"
	 */
	private Operation createFindCustomer_SINGULAR_ByNameOperationWithPrimaryKey()
	{
		Interface interfaceCustomerService = new TestInterface(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Interface", Numerus.SINGULAR);

		List<Operation> operations = new ArrayList<Operation>();
		Operation operationFindCustomerByName = new TestOperation(
				interfaceCustomerService, "Operation", "Finding Operations",
				Numerus.SINGULAR);
		operations.add(operationFindCustomerByName);
		interfaceCustomerService.setOperations(operations);

		Parameters inputs = new TestParameters(operationFindCustomerByName, "Paratemers",
				Numerus.SINGULAR);
		operationFindCustomerByName.setInputParameters(inputs);

		Parameter paramLastName = new TestParameter(inputs, "Parameter",
				Numerus.SINGULAR, false);
		paramLastName.setDataTypeName("java.lang.String");
		paramLastName.setIdentifier("lastname");

		List<Documentation> documentationsInput = new ArrayList<Documentation>();
		Documentation documentationPRIMARY_KEY = new Documentation();
		documentationPRIMARY_KEY.setThematicRole(new ThematicRole("PRIMARY_KEY", "",
				RoleScope.BOTH));
		documentationsInput.add(documentationPRIMARY_KEY);

		paramLastName.setDocumentations(documentationsInput);

		inputs.addParameter(paramLastName);

		Parameters outputs = new TestParameters(operationFindCustomerByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomerByName.setOutputParameters(outputs);

		Parameter resultCustomerList = new TestParameter(outputs, "Results",
				Numerus.SINGULAR, false);
		resultCustomerList.setDataTypeName("java.lang.String");
		resultCustomerList.setIdentifier("result");
		outputs.addParameter(resultCustomerList);

		List<Documentation> documentations = new ArrayList<Documentation>();

		Documentation documentationOBJECT = new Documentation();
		documentationOBJECT
				.setThematicRole(new ThematicRole("OBJECT", "", RoleScope.BOTH));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomerByName;
	}

	/**
	 * Returns the {@link Operation} for test purposes of predicate "isSingular()".
	 * 
	 * @return The {@link Operation} for test purposes of predicate "isSingular()"
	 */
	private Operation createFindCustomer_SINGULAR_ByNameOperation()
	{
		Interface interfaceCustomerService = new TestInterface(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Interface", Numerus.SINGULAR);

		List<Operation> operations = new ArrayList<Operation>();
		Operation operationFindCustomerByName = new TestOperation(
				interfaceCustomerService, "Operation", "Finding Operations",
				Numerus.SINGULAR);
		operations.add(operationFindCustomerByName);
		interfaceCustomerService.setOperations(operations);

		Parameters inputs = new TestParameters(operationFindCustomerByName, "Paratemers",
				Numerus.SINGULAR);
		operationFindCustomerByName.setInputParameters(inputs);

		Parameter paramLastName = new TestParameter(inputs, "Parameter",
				Numerus.SINGULAR, false);
		paramLastName.setDataTypeName("java.lang.String");
		paramLastName.setIdentifier("lastname");

		List<Documentation> documentationsInput = new ArrayList<Documentation>();
		Documentation documentationCOMPARISON = new Documentation();
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", "",
				RoleScope.BOTH));
		documentationsInput.add(documentationCOMPARISON);

		paramLastName.setDocumentations(documentationsInput);

		inputs.addParameter(paramLastName);

		Parameters outputs = new TestParameters(operationFindCustomerByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomerByName.setOutputParameters(outputs);

		Parameter resultCustomerList = new TestParameter(outputs, "Results",
				Numerus.SINGULAR, false);
		resultCustomerList.setDataTypeName("java.lang.String");
		resultCustomerList.setIdentifier("result");
		outputs.addParameter(resultCustomerList);

		List<Documentation> documentations = new ArrayList<Documentation>();

		Documentation documentationOBJECT = new Documentation();
		documentationOBJECT
				.setThematicRole(new ThematicRole("OBJECT", "", RoleScope.BOTH));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomerByName;
	}

	/**
	 * Returns the {@link Operation} for test purposes of predicate "isPlural()".
	 * 
	 * @return The {@link Operation} for test purposes of predicate "isPlural()"
	 */
	private Operation createFindCustomers_PLURAL_ByNameOperation()
	{
		Interface interfaceCustomerService = new TestInterface(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Interface", Numerus.SINGULAR);

		List<Operation> operations = new ArrayList<Operation>();
		Operation operationFindCustomersByName = new TestOperation(
				interfaceCustomerService, "Operation", "Finding Operations",
				Numerus.SINGULAR);
		operations.add(operationFindCustomersByName);
		interfaceCustomerService.setOperations(operations);

		Parameters inputs = new TestParameters(operationFindCustomersByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomersByName.setInputParameters(inputs);

		Parameter paramLastName = new TestParameter(inputs, "Parameter",
				Numerus.SINGULAR, false);
		paramLastName.setDataTypeName("java.lang.String");
		paramLastName.setIdentifier("lastname");

		List<Documentation> documentationsInput = new ArrayList<Documentation>();
		Documentation documentationCOMPARISON = new Documentation();
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", "",
				RoleScope.BOTH));
		documentationsInput.add(documentationCOMPARISON);

		paramLastName.setDocumentations(documentationsInput);

		inputs.addParameter(paramLastName);

		Parameters outputs = new TestParameters(operationFindCustomersByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomersByName.setOutputParameters(outputs);

		Parameter resultCustomerList = new TestParameter(outputs, "Results",
				Numerus.PLURAL, false);
		resultCustomerList.setDataTypeName("java.util.List");
		resultCustomerList.setIdentifier("result");
		outputs.addParameter(resultCustomerList);

		List<Documentation> documentations = new ArrayList<Documentation>();

		Documentation documentationOBJECT = new Documentation();
		documentationOBJECT
				.setThematicRole(new ThematicRole("OBJECT", "", RoleScope.BOTH));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomersByName;
	}

	/**
	 * Returns the {@link Operation} for test purposes of predicate "hasAttributes()".
	 * 
	 * @return The {@link Operation} for test purposes of predicate "hasAttributes()"
	 */
	private Operation createFindCustomers_ATTRIBUTES_ByNameOperation()
	{
		Interface interfaceCustomerService = new TestInterface(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Interface", Numerus.SINGULAR);

		List<Operation> operations = new ArrayList<Operation>();
		Operation operationFindCustomersByName = new TestOperation(
				interfaceCustomerService, "Operation", "Finding Operations",
				Numerus.SINGULAR);
		operations.add(operationFindCustomersByName);
		interfaceCustomerService.setOperations(operations);

		Parameters inputs = new TestParameters(operationFindCustomersByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomersByName.setInputParameters(inputs);

		Parameter paramLastName = new TestParameter(inputs, "Parameter",
				Numerus.SINGULAR, false);
		paramLastName.setDataTypeName("java.lang.String");
		paramLastName.setIdentifier("lastname");

		List<Documentation> documentationsInput = new ArrayList<Documentation>();
		Documentation documentationCOMPARISON = new Documentation();
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", "",
				RoleScope.BOTH));
		documentationsInput.add(documentationCOMPARISON);

		paramLastName.setDocumentations(documentationsInput);

		inputs.addParameter(paramLastName);

		Parameters outputs = new TestParameters(operationFindCustomersByName,
				"Paratemers", Numerus.SINGULAR);
		operationFindCustomersByName.setOutputParameters(outputs);

		Parameter resultCustomerList = new TestParameter(outputs, "Results",
				Numerus.PLURAL, false);
		resultCustomerList.setDataTypeName("java.util.List");
		resultCustomerList.setIdentifier("result");
		outputs.addParameter(resultCustomerList);

		Parameter customerParameter = new TestParameter(resultCustomerList, "Parameter",
				Numerus.SINGULAR, true);
		customerParameter.setDataTypeName("de.bookservice.structure.Customer");

		List<Documentation> parameterDocumentations = new ArrayList<Documentation>();
		Documentation documentationCustomerOBJECT = new Documentation();
		documentationCustomerOBJECT.setThematicRole(new ThematicRole("OBJECT", "",
				RoleScope.BOTH));
		parameterDocumentations.add(documentationCOMPARISON);
		customerParameter.setDocumentations(parameterDocumentations);

		resultCustomerList.addParameter(customerParameter);

		List<Documentation> documentations = new ArrayList<Documentation>();

		Documentation documentationOBJECT = new Documentation();
		documentationOBJECT
				.setThematicRole(new ThematicRole("OBJECT", "", RoleScope.BOTH));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomersByName;
	}

	/**
	 * Test cases for JavaScript-predicate isPlural(ROLE)
	 */
	@Test
	public void testIsPlural()
	{
		// Positive tests
		// ******************************************************************************
		{
			// Test case #1: if the Output-SignatureElement of the given
			// Operation has numerus "PLURAL" and is documented as "OBJECT",
			// then the predicate isPlural("OBJECT") should evaluate to true
			// for this operation.
			Operation operationFindCustomer_PLURAL_ByLastname = createFindCustomers_PLURAL_ByNameOperation();
			assertTrue(RuleService.evaluateRule("isPlural(\"OBJECT\");",
					operationFindCustomer_PLURAL_ByLastname));

			Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
			assertFalse(RuleService.evaluateRule("isPlural(\"OBJECT\");",
					operationFindCustomer_SINGULAR_ByLastname));
		}

		// Negative tests
		// ******************************************************************************
		{
			// Test case #1: if no role is specified, an IllegalArgumentException is
			// thrown.
			{
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule("isPlural(null);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}

			{
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule(
							"isPlural(notDeclaredVariableIdentifier);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}
		}
	}

	/**
	 * Test cases for JavaScript-predicate exists(ROLE)
	 */
	@Test
	public void testExists()
	{
		// Positive tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_ATTRIBUTES_ByLastname = createFindCustomers_ATTRIBUTES_ByNameOperation();

			assertTrue(RuleService.evaluateRule("exists(\"COMPARISON\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));

			assertTrue(RuleService.evaluateRule("exists(\"OBJECT\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));

			assertFalse(RuleService.evaluateRule("exists(\"AGENT\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));
		}

		// Negative tests
		// ******************************************************************************
		{
			{
				// Test case #1: if no role is specified, an IllegalArgumentException is
				// thrown.
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule("exists(null);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}

			{
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule(
							"isPlural(notDeclaredVariableIdentifier);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}
		}
	}

	/**
	 * Test cases for JavaScript-predicate hasAttributes(ROLE)
	 */
	@Test
	public void testHasAttributes()
	{
		// Positive tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_ATTRIBUTES_ByLastname = createFindCustomers_ATTRIBUTES_ByNameOperation();

			assertTrue(RuleService.evaluateRule("hasAttributes(\"COMPARISON\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));

			assertFalse(RuleService.evaluateRule("hasAttributes(\"OBJECT\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));

		}

		// Negative tests
		// ******************************************************************************
		{
			{
				// Test case #1: if no role is specified, an IllegalArgumentException is
				// thrown.
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule("hasAttributes(null);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}

			{
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule(
							"hasAttributes(notDeclaredVariableIdentifier);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}
		}
	}

	/**
	 * Test cases for JavaScript-predicate isSingular(ROLE)
	 */
	@Test
	public void testIsSingular()
	{
		// Positive tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
			assertTrue(RuleService.evaluateRule("isSingular(\"OBJECT\");",
					operationFindCustomer_SINGULAR_ByLastname));

			Operation operationFindCustomer_PLURAL_ByLastname = createFindCustomers_PLURAL_ByNameOperation();

			assertFalse(RuleService.evaluateRule("isSingular(\"OBJECT\");",
					operationFindCustomer_PLURAL_ByLastname));
		}

		// Negative tests
		// ******************************************************************************
		{
			{
				// Test case #1: if no role is specified, an IllegalArgumentException is
				// thrown.
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule("isSingular(null);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}

			{
				boolean illegalArgumentExceptionCaught = false;
				try
				{
					Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
					assertFalse(RuleService.evaluateRule(
							"isSingular(notDeclaredVariableIdentifier);",
							operationFindCustomer_SINGULAR_ByLastname));
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}
		}
	}

	/**
	 * Test cases for {@link RuleService#evaluateRule(String, SignatureElement)}
	 */
	@Test
	public void testEvaluateRecommendation()
	{
		// Positive tests
		// ******************************************************************************
		{}

		// Negative tests
		// ******************************************************************************
		{
			// Test case #1: a null parameter causes an IllegalArgumentException.
			{
				boolean illegalArgumentExceptionCaught = false;

				try
				{
					Operation operationFindCustomer_PLURAL_ByLastname = createFindCustomers_PLURAL_ByNameOperation();
					RuleService.evaluateRule(null,
							operationFindCustomer_PLURAL_ByLastname);
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}

			{
				boolean illegalArgumentExceptionCaught = false;

				try
				{
					RuleService.evaluateRule("isPlural(\"OBJECT\")", null);
				}
				catch (IllegalArgumentException illEx)
				{
					illegalArgumentExceptionCaught = true;
				}

				assertTrue(illegalArgumentExceptionCaught);
			}
		}
	}

	/**
	 * Test cases for {@link RuleService#isRuleValid(String)}
	 */
	@Test
	public void testIsRuleValid()
	{
		/*
		 * Positive tests
		 * ***************************************************************************
		 * Test case #1: Check if a valid snippet of JS is regarded as valid rule.
		 * ****************************************************************************
		 */
		{
			assertTrue(RuleService.isRuleValid("println('Hello World!');"));
		}
		{
			final TestOperation param = new TestOperation(
					SignatureElement.EMPTY_SIGNATURE_ELEMENT, null, "Finding Operations",
					Numerus.SINGULAR);
			RuleService.evaluateRule("info()", param);
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
