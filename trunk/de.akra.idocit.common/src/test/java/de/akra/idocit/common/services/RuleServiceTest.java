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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.structure.impl.TestInterface;
import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;

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
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", ""));
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
		documentationOBJECT.setThematicRole(new ThematicRole("OBJECT", ""));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomerByName;
	}

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
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", ""));
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
		documentationOBJECT.setThematicRole(new ThematicRole("OBJECT", ""));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomersByName;
	}

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
		documentationCOMPARISON.setThematicRole(new ThematicRole("COMPARISON", ""));
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
		documentationCustomerOBJECT.setThematicRole(new ThematicRole("OBJECT", ""));
		parameterDocumentations.add(documentationCOMPARISON);
		customerParameter.setDocumentations(parameterDocumentations);

		resultCustomerList.addParameter(customerParameter);

		List<Documentation> documentations = new ArrayList<Documentation>();

		Documentation documentationOBJECT = new Documentation();
		documentationOBJECT.setThematicRole(new ThematicRole("OBJECT", ""));
		documentations.add(documentationOBJECT);

		resultCustomerList.setDocumentations(documentations);

		return operationFindCustomersByName;
	}

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

		}

		// Negative tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();

			assertFalse(RuleService.evaluateRule("isPlural(\"OBJECT\");",
					operationFindCustomer_SINGULAR_ByLastname));
		}
	}

	@Test
	public void testHasAttributes()
	{
		// Positive tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_ATTRIBUTES_ByLastname = createFindCustomers_ATTRIBUTES_ByNameOperation();

			assertTrue(RuleService.evaluateRule("hasAttributes(\"COMPARISON\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));

		}

		// Negative tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_ATTRIBUTES_ByLastname = createFindCustomers_ATTRIBUTES_ByNameOperation();

			assertFalse(RuleService.evaluateRule("hasAttributes(\"OBJECT\");",
					operationFindCustomer_ATTRIBUTES_ByLastname));
		}
	}

	@Test
	public void testIsSingular()
	{
		// Positive tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_SINGULAR_ByLastname = createFindCustomer_SINGULAR_ByNameOperation();
			assertTrue(RuleService.evaluateRule("isSingular(\"OBJECT\");",
					operationFindCustomer_SINGULAR_ByLastname));
		}

		// Negative tests
		// ******************************************************************************
		{
			Operation operationFindCustomer_PLURAL_ByLastname = createFindCustomers_PLURAL_ByNameOperation();

			assertFalse(RuleService.evaluateRule("isSingular(\"OBJECT\");",
					operationFindCustomer_PLURAL_ByLastname));
		}
	}

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
