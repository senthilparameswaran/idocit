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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavaParameter;
import de.akra.idocit.java.structure.JavaParameters;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaTestUtils;
import de.akra.idocit.java.utils.TestDataFactory;

/**
 * Contains test cases for {@link AddresseeUtils}.
 * 
 * @author Jan Christian Krause
 * 
 */
public class AddresseeUtilsTest
{

	/**
	 * Tests
	 * {@link AddresseeUtils#containsOnlyOneAddressee(JavaInterfaceArtifact, String)}
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_NULL_PARAM_DEREF_NONVIRTUAL", justification = "The null-parameter is part of the test-scenario.")
	@Test
	public void testContainsOnlyOneAddressee() throws FileNotFoundException, IOException
	{
		/*
		 * Positive tests
		 */
		{
			// #########################################################################
			// # Test case #1: the interface artifact contains only data for developers
			// # and is accepted.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);
				assertTrue(AddresseeUtils.containsOnlyOneAddressee(artifact, "Developer"));
			}
			// #########################################################################
			// # Test case #2: the interface artifact contains only data for testers
			// # and is rejected.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Tester", true, cu);
				assertTrue(AddresseeUtils.containsOnlyOneAddressee(artifact, "Tester"));
			}

			// #########################################################################
			// # Test case #3: the interface artifact contains documentation at the
			// # first input parameter-level for developers and testers. It is also
			// # rejected.
			// #########################################################################
			{
				// The first test is made on the first parameter level.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					JavaParameters parameters = (JavaParameters) method
							.getInputParameters();

					TestDataFactory.addDocumentedParameter(parameters, "Tester", "NONE",
							"This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
				// Now, let's check if nested attributes of parameters are also tested.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					JavaParameters parameters = (JavaParameters) method
							.getInputParameters();
					JavaParameter firstNameParam = (JavaParameter) parameters
							.getParameters().get(0).getComplexType().get(0);

					TestDataFactory.addDocumentedParameter(firstNameParam, "Tester",
							"NONE", "This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
			}

			// #########################################################################
			// # Test case #4: the interface artifact contains documentation at the
			// # first output parameter-level for developers and testers. It is also
			// # rejected.
			// #########################################################################
			{
				// The first test is made on the return value level.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					JavaParameters parameters = (JavaParameters) method
							.getOutputParameters();

					TestDataFactory.addDocumentedParameter(parameters, "Tester", "NONE",
							"This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
				// Now, let's check if nested attributes of return values are also tested.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					JavaParameters parameters = (JavaParameters) method
							.getOutputParameters();
					JavaParameter listReturnVal = (JavaParameter) parameters
							.getParameters().get(0);

					TestDataFactory.addDocumentedParameter(listReturnVal, "Tester",
							"NONE", "This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
			}

			// #########################################################################
			// # Test case #5: the interface artifact contains documentation at the
			// # first exception-level for developers and testers. It is also
			// # rejected.
			// #########################################################################
			{
				// The first test is made on the return value level.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					@SuppressWarnings("unchecked")
					List<JavaParameters> parameters = (List<JavaParameters>) method
							.getExceptions();

					TestDataFactory.addDocumentedParameter(parameters.get(0), "Tester",
							"NONE", "This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
				// Now, let's check if nested attributes of exceptions are also tested.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);
					JavaParameters ioException = (JavaParameters) method.getExceptions()
							.get(0);
					JavaParameter ioExceptionParam = (JavaParameter) ioException
							.getParameters().get(0);

					TestDataFactory.addDocumentedParameter(ioExceptionParam, "Tester",
							"NONE", "This is documentation.");

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
			}

			// #########################################################################
			// # Test case #6: the interface artifact contains documentation at the
			// # operation-level for developers and testers. It is also rejected.
			// #########################################################################
			{
				// The first test is made on the operation level.
				{
					ParserOutput output = JavaTestUtils
							.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
									+ "CustomerService.java");
					CompilationUnit cu = output.getCompilationUnit();
					JavaInterfaceArtifact artifact = TestDataFactory
							.createCustomerService("Developer", true, cu);
					JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
							.getOperations().get(0);

					TestDataFactory.addDocumentation(method, "Tester", "NONE",
							"This is documenation.", method.getIdentifier(), false);

					assertFalse(AddresseeUtils.containsOnlyOneAddressee(artifact,
							"Developer"));
				}
			}

			// #########################################################################
			// # Test case #7: the interface artifact contains documentation at the
			// # interface-level for developers and testers. It is also rejected.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);
				JavaInterface iface = (JavaInterface) artifact.getInterfaces().get(0);

				TestDataFactory.addDocumentation(iface, "Tester", "NONE",
						"This is documenation.", iface.getIdentifier(), false);

				assertFalse(AddresseeUtils
						.containsOnlyOneAddressee(artifact, "Developer"));
			}

			// #########################################################################
			// # Test case #8: the interface artifact contains documentation at the
			// # artifact-level for developers and testers. It is also rejected.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				TestDataFactory.addDocumentation(artifact, "Tester", "NONE",
						"This is documenation.", artifact.getIdentifier(), false);

				assertFalse(AddresseeUtils
						.containsOnlyOneAddressee(artifact, "Developer"));
			}

			// #########################################################################
			// # Test case #9: the interface artifact contains documentation for
			// developers and testers at an inner interface. It is also rejected.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				JavaInterface emptyInterface = new JavaInterface(artifact, "Interface",
						Numerus.SINGULAR);

				artifact.addInterface(emptyInterface);

				TestDataFactory.addDocumentation(emptyInterface, "Tester", "NONE",
						"This is documenation.", emptyInterface.getIdentifier(), false);

				assertFalse(AddresseeUtils
						.containsOnlyOneAddressee(artifact, "Developer"));
			}

			// #########################################################################
			// # Test case #10: the interface artifact contains null-documentation maps
			// # on all levels and is accepted.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory
						.createNullCustomerService(cu, true);

				assertTrue(AddresseeUtils.containsOnlyOneAddressee(artifact, "Developer"));
			}

			// #########################################################################
			// # Test case #11: the interface artifact contains empty-documentation maps
			// # on all levels and is accepted.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory
						.createEmptyCustomerService(cu, true);

				assertTrue(AddresseeUtils.containsOnlyOneAddressee(artifact, "Developer"));
			}

			// #########################################################################
			// # Test case #12: the interface artifact contains documentation for
			// # "Developer"s. But in its inner interface, there is documentation for the
			// # "Tester". It should be rejected.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				JavaInterface innerCustomerService = new JavaInterface(artifact,
						"Interface", Numerus.SINGULAR);
				TestDataFactory.addDocumentation(innerCustomerService, "Tester", "AGENT",
						"This inner service does a lot of important tasks.",
						innerCustomerService.getIdentifier(), false);
				List<JavaInterface> innerInterfaces = new ArrayList<JavaInterface>();
				innerInterfaces.add(innerCustomerService);

				artifact.getInterfaces().get(0).setInnerInterfaces(innerInterfaces);

				assertFalse(AddresseeUtils
						.containsOnlyOneAddressee(artifact, "Developer"));
			}
		}

		// #########################################################################
		// #########################################################################

		/*
		 * Negative tests
		 */
		// #########################################################################
		// # Test case #1: null-JavaInterfaceArtifact leads to
		// # IllegalArgumentException.
		// #########################################################################
		{
			boolean illegalArgumentException = false;

			try
			{
				AddresseeUtils.containsOnlyOneAddressee(null, "Developer");
			}
			catch (IllegalArgumentException iaEx)
			{
				illegalArgumentException = true;
			}

			assertTrue(illegalArgumentException);
		}

		// #########################################################################
		// # Test case #2: null-addressee name leads to
		// # IllegalArgumentException.
		// #########################################################################
		{
			boolean illegalArgumentException = false;

			try
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory.createCustomerService(
						"Developer", true, cu);

				AddresseeUtils.containsOnlyOneAddressee(artifact, null);

			}
			catch (IllegalArgumentException iaEx)
			{
				illegalArgumentException = true;
			}

			assertTrue(illegalArgumentException);
		}
	}
}
