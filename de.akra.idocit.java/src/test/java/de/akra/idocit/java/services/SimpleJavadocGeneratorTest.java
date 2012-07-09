/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package de.akra.idocit.java.services;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.junit.Test;

import de.akra.idocit.common.structure.RoleScope;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.JavadocTestUtils;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.TestDataFactory;

public class SimpleJavadocGeneratorTest
{

	private static final String OPTIONS_FOR_CUSTOM_TAGLETS = "-tag paraminfo:tcm:\"Parameter-Info:\" -tag returninfo:tcm:\"Return-Info:\" -tag throwsinfo:tcm:\"Throw-Info:\" -tag subreturn:tcm:\"Subreturn:\" -tag subreturninfo:tcm:\"Subreturn-Info:\" -tag subparam:tcm:\"Subparameter:\" -tag subparaminfo:tcm:\"Subparameter-Info:\" -tag subthrowsinfo:tcm:\"Subthrow-Info:\" -tag thematicgrid:tcm:\"Thematic Grid:\"";

	@Test
	public void testAppendDocsToJavadoc() throws FileNotFoundException, IOException,
			ParsingException
	{
		/*
		 * Positive tests
		 */
		{
			// #########################################################################
			// # Test case #1: generate a simple Javadoc with four thematic roles. Some
			// # are within another taglet (e.g. in @param) and some are own taglets (e.g.
			// # like @source).
			// #########################################################################
			{
				String referenceJavadoc = "/** \n "
						+ "* @ordering Alphabetically by lastname\n "
						+ "* @source CRM System\n "
						+ "* \n "
						+ "* @param parameters [COMPARISON] This is the customer.\n "
						+ "* @paraminfo parameters [SOURCE] This is the source.\n "
						+ "* @subparam firstName [COMPARISON]\n "
						+ "* @subparam lastName [COMPARISON]\n "
						+ "* \n "
						+ "* @return [OBJECT] This is the object.\n "
						+ "* @returninfo [SOURCE] This is the source.\n "
						+ "* \n "
						+ "* @throws IOException [ATTRIBUTE] This is also an attribute.\n "
						+ "* @thematicgrid Searching Operations\n " + "*/\n";
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();

				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				JavaInterface customerServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				JavaMethod findCustomerByIdMeth = (JavaMethod) customerServiceIntf
						.getOperations().get(0);
				Javadoc javadoc = findCustomerByIdMeth.getRefToASTNode().getJavadoc();

				assertEquals(referenceJavadoc, javadoc.toString());
			}

			// #########################################################################
			// # Test case #2: in case of an Checking Operation, the action is replaced
			// # by the rule.
			// #########################################################################
			{
				String referenceJavadoc = "/** \n "
						+ "* Rule: Maximum length of an address are 40 chars.\n "
						+ "* \n "
						+ "* @param mailAddress [OBJECT]\n "
						+ "* \n "
						+ "* @return [REPORT] <code>false</code> if the rule is violated\n "
						+ "* @thematicgrid Checking Operations\n " + "*/\n";

				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "InvariantService.java");
				CompilationUnit cu = output.getCompilationUnit();

				JavaInterfaceArtifact artifact = TestDataFactory.createInvariantService(
						"Developer", cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				JavaInterface invariantServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				JavaMethod checkInvariantMeth = (JavaMethod) invariantServiceIntf
						.getOperations().get(0);
				Javadoc javadoc = checkInvariantMeth.getRefToASTNode().getJavadoc();

				assertEquals(referenceJavadoc, javadoc.toString());
			}

			// #########################################################################
			// # Test case #3: an artifact with empty documentation maps leads to no
			// # Javadoc.
			// #########################################################################
			{
				// This Java file is parsed to get a valid abstract syntax tree. We won't
				// use its Javadoc!
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact customerService = TestDataFactory
						.createEmptyCustomerService(cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(customerService,
						SimpleJavadocGenerator.INSTANCE);

				JavaMethod methodFindCustById = (JavaMethod) customerService
						.getInterfaces().get(0).getOperations().get(0);

				assertEquals(createEmptyReferenceJDForCustomerService(),
						methodFindCustById.getRefToASTNode().getJavadoc().toString());

			}

			// #########################################################################
			// # Test case #4: an artifact with null documentation maps leads to no
			// # Javadoc.
			// #########################################################################
			{
				// This Java file is parsed to get a valid abstract syntax tree. We won't
				// use its Javadoc!
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact customerService = TestDataFactory
						.createNullCustomerService(cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(customerService,
						SimpleJavadocGenerator.INSTANCE);

				JavaMethod methodFindCustById = (JavaMethod) customerService
						.getInterfaces().get(0).getOperations().get(0);

				assertEquals(createEmptyReferenceJDForCustomerService(),
						methodFindCustById.getRefToASTNode().getJavadoc().toString());
			}

			// #########################################################################
			// # Test case #5: @subparams must be followed by a path to the actual
			// # signature element and not only by the signature element's identifier.
			// #########################################################################
			{
				String referenceJavadoc = "/** \n "
						+ "*  Only customers who placed an order within the last year are considered.\n "
						+ "* \n "
						+ "* @ordering Alphabetically by lastname\n "
						+ "* @source CRM System\n "
						+ "* \n "
						+ "* @param parameters\n "
						+ "* @subparam customer.firstName [COMPARISON]\n "
						+ "* @subparam customer.lastName [COMPARISON]\n "
						+ "* \n "
						+ "* @return [OBJECT]\n "
						+ "* @subreturn firstName [ATTRIBUTE] Won't be null, but could be an empty String\n "
						+ "* @subreturn lastName [ATTRIBUTE] Won't be null, but could be an empty String\n "
						+ "* \n " + "* @throws SpecialException\n "
						+ "* @thematicgrid Searching Operations\n " + "*/\n";
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();

				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				JavaInterface customerServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				JavaMethod findCustomerByIdMeth = (JavaMethod) customerServiceIntf
						.getOperations().get(1);
				Javadoc javadoc = findCustomerByIdMeth.getRefToASTNode().getJavadoc();

				assertEquals(referenceJavadoc, javadoc.toString());
			}
		}

		// #########################################################################
		// #########################################################################

		/*
		 * Negative tests
		 */
		// #########################################################################
		// # Test case #1: documentations for Testers lead to an
		// # IllegalArgumentException.
		// #########################################################################
		{
			ParserOutput output = JavadocTestUtils
					.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR + "CustomerService.java");
			CompilationUnit cu = output.getCompilationUnit();

			JavaInterfaceArtifact customerService = TestDataFactory
					.createCustomerService("Tester", true, cu);

			{
				boolean illegalArgumentException = false;

				try
				{
					JavaInterfaceGenerator.updateJavadocInAST(customerService,
							SimpleJavadocGenerator.INSTANCE);
				}
				catch (IllegalArgumentException iaEx)
				{
					illegalArgumentException = true;
				}

				assertTrue(illegalArgumentException);
			}
		}
	}

	private String createEmptyReferenceJDForCustomerService()
	{
		return "/** \n" + " * @param parameters\n" + " * @subparam firstName\n"
				+ " * @subparam lastName\n" + " * \n" + " * @return \n" + " * \n"
				+ " * @throws IOException\n" + " */\n";
	}

	/**
	 * Tests for {@link SimpleJavadocGenerator#generateJavadocOptions(List)}.
	 */
	@Test
	public void testGenerateJavadocOptions()
	{
		final List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.add(new ThematicRole("OBJECT", "object", RoleScope.BOTH));
		roles.add(new ThematicRole("TIME_TO_LIVE", "time to live",
				RoleScope.OPERATION_LEVEL));
		roles.add(new ThematicRole("YES-NO-ANSWER", "yes-no-answer",
				RoleScope.OPERATION_LEVEL));

		/*
		 * Positive tests
		 */
		// #########################################################################
		// # Test case #1: Check if the options for CustomTaglets are generated correct.
		// #########################################################################
		{
			final String options = SimpleJavadocGenerator.INSTANCE
					.generateJavadocOptions(Collections.<ThematicRole> emptyList());
			assertEquals(OPTIONS_FOR_CUSTOM_TAGLETS, options);
		}

		// #########################################################################
		// # Test case #2: Check if user defined roles are converted correct.
		// #########################################################################
		{
			final String options = SimpleJavadocGenerator.INSTANCE
					.generateJavadocOptions(roles);
			assertTrue(options.startsWith(OPTIONS_FOR_CUSTOM_TAGLETS));
			assertTrue(options.contains("-tag object:tcm:\"Object:\""));
			assertTrue(options.contains("-tag time_to_live:tcm:\"Time To Live:\""));
			assertTrue(options.contains("-tag yes-no-answer:tcm:\"Yes-no-answer:\""));
		}

		// #########################################################################
		// #########################################################################

		/*
		 * Negative tests
		 */
		// #########################################################################
		// # Test case #1: Test if thematic role names are converted wrong.
		// #########################################################################
		{
			final String options = SimpleJavadocGenerator.INSTANCE
					.generateJavadocOptions(roles);
			assertFalse(options.contains("OBJECT"));
			assertFalse(options.contains("TIME_TO_LIVE"));
			assertFalse(options.contains("YES-NO-ANSWER"));
			assertFalse(options.contains("@"));
		}
	}
}
