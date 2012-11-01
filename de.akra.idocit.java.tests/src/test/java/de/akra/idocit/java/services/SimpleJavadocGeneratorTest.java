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

import static de.akra.idocit.java.utils.JavaTestUtils.JAVADOC_NEW_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RoleScope;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.DocumentationUtils;
import de.akra.idocit.common.utils.TestUtils;
import de.akra.idocit.core.constants.AddresseeConstants;
import de.akra.idocit.core.utils.DescribedItemUtils;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.constants.Constants;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaTestUtils;
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
				final String referenceJavadoc = String
						.format("/** %1$s"
								+ " * @orderingAlphabetically by lastname%1$s"
								+ " * @sourceCRM System%1$s"
								+ " * %1$s"
								+ " * @paramparameters [COMPARISON] This is the customer.%1$s"
								+ " * @paraminfoparameters [SOURCE] This is the source.%1$s"
								+ " * @subparamfirstName [COMPARISON]%1$s"
								+ " * @subparamlastName [COMPARISON]%1$s"
								+ " * %1$s"
								+ " * @return[OBJECT] This is the object.%1$s"
								+ " * @returninfo[SOURCE] This is the source.%1$s"
								+ " * %1$s"
								+ " * @throwsIOException In case of an error%1$s"
								+ " * @throwsinfoIOException [ATTRIBUTE] This is also an attribute.%1$s"
								+ " * @thematicgridSearching Operations%1$s" + " */%1$s",
								JAVADOC_NEW_LINE);

				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();

				final JavaInterfaceArtifact artifact = TestDataFactory
						.createCustomerService("Developer", true, cu);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				final JavaInterface customerServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				final JavaMethod findCustomerByIdMeth = (JavaMethod) customerServiceIntf
						.getOperations().get(0);
				final Javadoc javadoc = findCustomerByIdMeth.getRefToASTNode()
						.getJavadoc();

				assertEquals(referenceJavadoc, javadoc.toString());
			}

			// #########################################################################
			// # Test case #2: in case of an Checking Operation, the action is replaced
			// # by the rule.
			// #########################################################################
			{
				final String referenceJavadoc = String
						.format("/** %1$s"
								+ " * Rule: Maximum length of an address are 40 chars.%1$s"
								+ " * %1$s"
								+ " * @ordering("
								+ Constants.ERROR_CASE_DOCUMENTATION_TEXT
								+ ")%1$s"
								+ " * %1$s"
								+ " * @parammailAddress [OBJECT]%1$s"
								+ " * %1$s"
								+ " * @return[REPORT] <code>false</code> if the rule is violated%1$s"
								+ " * @thematicgridChecking Operations%1$s" + " */%1$s",
								JAVADOC_NEW_LINE);

				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "InvariantService.java");
				final CompilationUnit cu = output.getCompilationUnit();

				final JavaInterfaceArtifact artifact = TestDataFactory
						.createInvariantService("Developer", cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				final JavaInterface invariantServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				final JavaMethod checkInvariantMeth = (JavaMethod) invariantServiceIntf
						.getOperations().get(0);
				final Javadoc javadoc = checkInvariantMeth.getRefToASTNode().getJavadoc();

				assertEquals(referenceJavadoc, javadoc.toString());
			}

			// #########################################################################
			// # Test case #3: an artifact with empty documentation maps leads to no
			// # Javadoc.
			// #########################################################################
			{
				// This Java file is parsed to get a valid abstract syntax tree. We won't
				// use its Javadoc!
				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();
				final JavaInterfaceArtifact customerService = TestDataFactory
						.createEmptyCustomerService(cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(customerService,
						SimpleJavadocGenerator.INSTANCE);

				final JavaMethod methodFindCustById = (JavaMethod) customerService
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
				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();
				final JavaInterfaceArtifact customerService = TestDataFactory
						.createNullCustomerService(cu, true);

				JavaInterfaceGenerator.updateJavadocInAST(customerService,
						SimpleJavadocGenerator.INSTANCE);

				final JavaMethod methodFindCustById = (JavaMethod) customerService
						.getInterfaces().get(0).getOperations().get(0);

				assertEquals(createEmptyReferenceJDForCustomerService(),
						methodFindCustById.getRefToASTNode().getJavadoc().toString());
			}

			// #########################################################################
			// # Test case #5: @subparams must be followed by a path to the actual
			// # signature element and not only by the signature element's identifier.
			// #########################################################################
			{
				final String referenceJavadoc = String
						.format("/** %1$s"
								+ " * Only customers who placed an order within the last year are considered.%1$s"
								+ " * %1$s"
								+ " * @orderingAlphabetically by lastname%1$s"
								+ " * @sourceCRM System%1$s"
								+ " * %1$s"
								+ " * @paramparameters%1$s"
								+ " * @subparamcustomer.firstName [COMPARISON]%1$s"
								+ " * @subparamcustomer.lastName [COMPARISON]%1$s"
								+ " * %1$s"
								+ " * @return[OBJECT]%1$s"
								+ " * @subreturnfirstName [ATTRIBUTE] Won't be null, but could be an empty String%1$s"
								+ " * @subreturnlastName [ATTRIBUTE] Won't be null, but could be an empty String%1$s"
								+ " * %1$s"
								+ " * @throwsSpecialException In case of an error%1$s"
								+ " * @thematicgridSearching Operations%1$s" + " */%1$s",
								JAVADOC_NEW_LINE);
				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();

				final JavaInterfaceArtifact artifact = TestDataFactory
						.createCustomerService("Developer", true, cu);

				JavaInterfaceGenerator.updateJavadocInAST(artifact,
						SimpleJavadocGenerator.INSTANCE);

				final JavaInterface customerServiceIntf = (JavaInterface) artifact
						.getInterfaces().get(0);
				final JavaMethod findCustomerByIdMeth = (JavaMethod) customerServiceIntf
						.getOperations().get(1);
				final Javadoc javadoc = findCustomerByIdMeth.getRefToASTNode()
						.getJavadoc();

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
			final ParserOutput output = JavaTestUtils
					.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
							+ "CustomerService.java");
			final CompilationUnit cu = output.getCompilationUnit();

			final JavaInterfaceArtifact customerService = TestDataFactory
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
		return String.format("/** %1$s" + " * @paramparameters%1$s"
				+ " * @subparamfirstName%1$s" + " * @subparamlastName%1$s" + " * %1$s"
				+ " * @return%1$s" + " * %1$s" + " * @throwsIOException%1$s" + " */%1$s",
				JAVADOC_NEW_LINE);
	}

	/**
	 * Tests for {@link SimpleJavadocGenerator#generateJavadocOptions(List)}.
	 */
	@Test
	public void testGenerateJavadocOptions()
	{
		final List<ThematicRole> threeRoles = new ArrayList<ThematicRole>();
		threeRoles.add(new ThematicRole("OBJECT", "object", RoleScope.BOTH));
		threeRoles.add(new ThematicRole("TIME_TO_LIVE", "time to live",
				RoleScope.OPERATION_LEVEL));
		threeRoles.add(new ThematicRole("YES-NO-ANSWER", "yes-no-answer",
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
		// # Test case #2: Check if generating options with one additional role.
		// #########################################################################
		{
			final List<ThematicRole> oneRole = new ArrayList<ThematicRole>();
			oneRole.add(new ThematicRole("TIME_TO_LIVE", "time to live",
					RoleScope.OPERATION_LEVEL));
			final String options = SimpleJavadocGenerator.INSTANCE
					.generateJavadocOptions(oneRole);
			assertTrue(options.startsWith(OPTIONS_FOR_CUSTOM_TAGLETS));
			assertTrue(options.contains("-tag time_to_live:tcm:\"Time To Live:\""));
		}

		// #########################################################################
		// # Test case #3: Check if user defined roles are converted correct.
		// #########################################################################
		{
			final String options = SimpleJavadocGenerator.INSTANCE
					.generateJavadocOptions(threeRoles);
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
					.generateJavadocOptions(threeRoles);
			assertFalse(options.contains("OBJECT"));
			assertFalse(options.contains("TIME_TO_LIVE"));
			assertFalse(options.contains("YES-NO-ANSWER"));
			assertFalse(options.contains("@"));
		}
	}

	/**
	 * Tests if the error case documentation is generated correctly. It is correct, if
	 * first comes the {@link Constants#ERROR_CASE_DOCUMENTATION_TEXT} and then the
	 * documentation text.
	 * 
	 * @throws FileNotFoundException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be found
	 * @throws IOException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be read
	 * @throws ParsingException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be parsed due to syntax errors
	 */
	@Test
	public void testGenerateJavaDocErrorCaseWithDocumentation()
			throws FileNotFoundException, IOException, ParsingException
	{
		final String referenceJavadoc = String
				.format("/** %1$s"
						+ " * Rule: Maximum length of an address are 40 chars.%1$s"
						+ " * %1$s"
						+ " * @ordering("
						+ Constants.ERROR_CASE_DOCUMENTATION_TEXT
						+ ") Ascending%1$s"
						+ " * %1$s"
						+ " * @parammailAddress [OBJECT]%1$s"
						+ " * %1$s"
						+ " * @return[REPORT] <code>false</code> if the rule is violated%1$s"
						+ " * @thematicgridChecking Operations%1$s" + " */%1$s",
						JAVADOC_NEW_LINE);

		final ParserOutput output = JavaTestUtils
				.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
						+ "InvariantService.java");
		final CompilationUnit cu = output.getCompilationUnit();

		final JavaInterfaceArtifact artifact = TestDataFactory.createInvariantService(
				AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, cu, true);
		final Addressee addressee = DescribedItemUtils
				.findAddressee(AddresseeConstants.MOST_IMPORTANT_ADDRESSEE);
		List<Documentation> documentations = artifact.getInterfaces().get(0)
				.getOperations().get(0).getDocumentations();
		Documentation doc = DocumentationUtils.findDocumentationByRoleName("ORDERING",
				documentations);
		doc.getDocumentation().put(addressee, "Ascending");

		JavaInterfaceGenerator.updateJavadocInAST(artifact,
				SimpleJavadocGenerator.INSTANCE);

		final JavaInterface invariantServiceIntf = (JavaInterface) artifact
				.getInterfaces().get(0);
		final JavaMethod checkInvariantMeth = (JavaMethod) invariantServiceIntf
				.getOperations().get(0);
		final Javadoc javadoc = checkInvariantMeth.getRefToASTNode().getJavadoc();

		assertEquals(referenceJavadoc, javadoc.toString());
	}

	/**
	 * Tests if the error case documentation is generated correctly. It is correct, if
	 * first comes the {@link Constants#ERROR_CASE_DOCUMENTATION_TEXT} and then the
	 * documentation text. It works even though there is an empty documentation string for
	 * the "Tester".
	 * 
	 * @throws FileNotFoundException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be found
	 * @throws IOException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be read
	 * @throws ParsingException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be parsed due to syntax errors
	 */
	@Test
	public void testAcceptEmptyDocsForNonDeveloperAddressees()
			throws FileNotFoundException, IOException, ParsingException
	{
		final String referenceJavadoc = String
				.format("/** %1$s"
						+ " * Rule: Maximum length of an address are 40 chars.%1$s"
						+ " * %1$s"
						+ " * @ordering("
						+ Constants.ERROR_CASE_DOCUMENTATION_TEXT
						+ ") Ascending%1$s"
						+ " * %1$s"
						+ " * @parammailAddress [OBJECT]%1$s"
						+ " * %1$s"
						+ " * @return[REPORT] <code>false</code> if the rule is violated%1$s"
						+ " * @thematicgridChecking Operations%1$s" + " */%1$s",
						JAVADOC_NEW_LINE);

		final ParserOutput output = JavaTestUtils
				.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
						+ "InvariantService.java");
		final CompilationUnit cu = output.getCompilationUnit();

		final JavaInterfaceArtifact artifact = TestDataFactory.createInvariantService(
				AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, cu, true);
		final Addressee addressee = DescribedItemUtils
				.findAddressee(AddresseeConstants.MOST_IMPORTANT_ADDRESSEE);
		List<Documentation> documentations = artifact.getInterfaces().get(0)
				.getOperations().get(0).getDocumentations();
		Documentation doc = DocumentationUtils.findDocumentationByRoleName("ORDERING",
				documentations);
		doc.getDocumentation().put(addressee, "Ascending");

		final Addressee tester = DescribedItemUtils.findAddressee("Tester");
		doc.getDocumentation().put(tester, "  \t  ");

		JavaInterfaceGenerator.updateJavadocInAST(artifact,
				SimpleJavadocGenerator.INSTANCE);

		final JavaInterface invariantServiceIntf = (JavaInterface) artifact
				.getInterfaces().get(0);
		final JavaMethod checkInvariantMeth = (JavaMethod) invariantServiceIntf
				.getOperations().get(0);
		final Javadoc javadoc = checkInvariantMeth.getRefToASTNode().getJavadoc();

		assertEquals(referenceJavadoc, javadoc.toString());
	}

	/**
	 * If there is a '>' or '<' in the documentation-text, these characters must be quoted.
	 *  
	 * @throws FileNotFoundException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be found
	 * @throws IOException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be read
	 * @throws ParsingException
	 *             If the reference java-file {@link AllIDocItJavaTests#SOURCE_DIR}
	 *             /InvariantService.java could not be parsed due to syntax errors
	 */
	@Test
	public void testXmlBracketsInDocumentationText() throws FileNotFoundException,
			IOException, ParsingException
	{
		final String referenceJavadoc = String
				.format("/** %1$s"
						+ " * Rule: Maximum length of an address are 40 chars.%1$s"
						+ " * %1$s"
						+ " * @ordering("
						+ Constants.ERROR_CASE_DOCUMENTATION_TEXT
						+ ") Ascending &lt;&gt;<br/>%1$s"
						+ " *  Test y &lt;= x<br/>%1$s"
						+ " *  Test y &gt;= x%1$s"
						+ " * %1$s"
						+ " * @parammailAddress [OBJECT]%1$s"
						+ " * %1$s"
						+ " * @return[REPORT] <code>false</code> if the rule is violated%1$s"
						+ " * @thematicgridChecking Operations%1$s" + " */%1$s",
						JAVADOC_NEW_LINE);

		final ParserOutput output = JavaTestUtils
				.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
						+ "InvariantService.java");
		final CompilationUnit cu = output.getCompilationUnit();

		final JavaInterfaceArtifact artifact = TestDataFactory.createInvariantService(
				AddresseeConstants.MOST_IMPORTANT_ADDRESSEE, cu, true);
		final Addressee addressee = DescribedItemUtils
				.findAddressee(AddresseeConstants.MOST_IMPORTANT_ADDRESSEE);
		List<Documentation> documentations = artifact.getInterfaces().get(0)
				.getOperations().get(0).getDocumentations();
		Documentation doc = DocumentationUtils.findDocumentationByRoleName("ORDERING",
				documentations);
		// HERE WE HAVE THE '<' and '>' characters!!!
		doc.getDocumentation().put(addressee, "Ascending <>\n Test y <= x\n Test y >= x");

		final Addressee tester = DescribedItemUtils.findAddressee("Tester");
		doc.getDocumentation().put(tester, "  \t  ");

		JavaInterfaceGenerator.updateJavadocInAST(artifact,
				SimpleJavadocGenerator.INSTANCE);

		final JavaInterface invariantServiceIntf = (JavaInterface) artifact
				.getInterfaces().get(0);
		final JavaMethod checkInvariantMeth = (JavaMethod) invariantServiceIntf
				.getOperations().get(0);
		final Javadoc javadoc = checkInvariantMeth.getRefToASTNode().getJavadoc();

		assertEquals(referenceJavadoc, javadoc.toString());
	}

	/**
	 * Test if the role ACTION or RULE is always at first position in the Javadoc comment.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParsingException
	 */
	@Test
	public void testCorrectJavadocTagOrdering() throws FileNotFoundException,
			IOException, ParsingException
	{
		final String[] referenceJavadocs = new String[] {
				/*
				 * Test 1: with thematic role ACTION
				 */
				String.format("/** %1$s" + " * The Action%1$s" + " * %1$s"
						+ " * @sourceThe Source%1$s" + " * %1$s" + " * @returnString%1$s"
						+ " * @thematicgridGetting Operations / Getter%1$s" + " */%1$s",
						JAVADOC_NEW_LINE),

				/*
				 * Test 2: with thematic role RULE
				 */
				String.format("/** %1$s" + " * Rule: The rule%1$s" + " * %1$s"
						+ " * @sourceThe Source%1$s" + " * %1$s" + " * @returnString%1$s"
						+ " * @thematicgridChecking Operations%1$s" + " */%1$s",
						JAVADOC_NEW_LINE) };

		for (int i = 0; i < referenceJavadocs.length; ++i)
		{
			final ParserOutput output = JavaTestUtils
					.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
							+ "TestInterface1.java");
			final CompilationUnit cu = output.getCompilationUnit();

			final JavaInterfaceArtifact artifact = TestDataFactory.createTestInterface1(
					TestUtils.createDeveloper(), cu, true, i);

			JavaInterfaceGenerator.updateJavadocInAST(artifact,
					SimpleJavadocGenerator.INSTANCE);

			final JavaInterface testIntf1 = (JavaInterface) artifact.getInterfaces().get(
					0);
			final JavaMethod getStringMeth = (JavaMethod) testIntf1.getOperations()
					.get(0);
			final Javadoc javadoc = getStringMeth.getRefToASTNode().getJavadoc();

			assertEquals(referenceJavadocs[i], javadoc.toString());
		}

	}
}