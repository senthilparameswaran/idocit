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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.AllIDocItJavaTests;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaTestUtils;
import de.akra.idocit.java.utils.TestDataFactory;

public class SimpleJavadocParserTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void testParseIDocItJavadoc() throws FileNotFoundException, IOException,
			SAXException, ParserConfigurationException, ParsingException
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
				final List<Documentation> referenceDocs = new ArrayList<Documentation>();

				// Ordering
				{
					final Documentation orderingDoc = new Documentation();
					orderingDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					final Map<Addressee, String> docOrdering = new HashMap<Addressee, String>();
					docOrdering.put(TestUtils.createDeveloper(),
							"Alphabetically by lastname");
					orderingDoc.setDocumentation(docOrdering);

					orderingDoc.setSignatureElementIdentifier(null);
					orderingDoc.setThematicRole(TestUtils.createOrdering());

					referenceDocs.add(orderingDoc);
				}

				// Source
				{
					final Documentation sourceDoc = new Documentation();
					sourceDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					final Map<Addressee, String> docSource = new HashMap<Addressee, String>();
					docSource.put(TestUtils.createDeveloper(), "CRM System");
					sourceDoc.setDocumentation(docSource);

					sourceDoc.setSignatureElementIdentifier(null);
					sourceDoc.setThematicRole(TestUtils.createSource());

					referenceDocs.add(sourceDoc);
				}

				// Parameter parameters > COMPARISON
				{
					final Documentation paramParametersDoc = new Documentation();
					paramParametersDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					final Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(),
							"This is the customer.");
					paramParametersDoc.setDocumentation(docSubParamFirstname);

					paramParametersDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters");
					paramParametersDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(paramParametersDoc);
				}

				// Parameter parameters > SOURCE
				{
					final Documentation paramParametersDoc = new Documentation();
					paramParametersDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					final Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(),
							"This is the source.");
					paramParametersDoc.setDocumentation(docSubParamFirstname);

					paramParametersDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters");
					paramParametersDoc.setThematicRole(TestUtils.createSource());
					referenceDocs.add(paramParametersDoc);
				}

				// Subparameter firstname
				{
					final Documentation subparamFirstnameDoc = new Documentation();
					subparamFirstnameDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					final Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(),
							StringUtils.EMPTY);
					subparamFirstnameDoc.setDocumentation(docSubParamFirstname);

					subparamFirstnameDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters/source.NameParameters.firstName:java.lang.String");
					subparamFirstnameDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(subparamFirstnameDoc);
				}

				// Subparameter lastname
				{
					final Documentation subparamLastnameDoc = new Documentation();
					subparamLastnameDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					final Map<Addressee, String> docSubparamLastname = new HashMap<Addressee, String>();
					docSubparamLastname.put(TestUtils.createDeveloper(),
							StringUtils.EMPTY);
					subparamLastnameDoc.setDocumentation(docSubparamLastname);

					subparamLastnameDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters/source.NameParameters.lastName:java.lang.String");
					subparamLastnameDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(subparamLastnameDoc);
				}

				// Return Object
				{
					final Documentation returnDoc = new Documentation();
					returnDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					final Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(), "This is the object.");
					returnDoc.setDocumentation(docReturn);

					returnDoc
							.setSignatureElementIdentifier("java.util.List<source.Customer>:java.util.List<source.Customer>");
					returnDoc.setThematicRole(TestUtils.createObject());
					referenceDocs.add(returnDoc);
				}

				// Return Info Source
				{
					final Documentation returnDoc = new Documentation();
					returnDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					final Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(), "This is the source.");
					returnDoc.setDocumentation(docReturn);

					returnDoc
							.setSignatureElementIdentifier("java.util.List<source.Customer>:java.util.List<source.Customer>");
					returnDoc.setThematicRole(TestUtils.createSource());
					referenceDocs.add(returnDoc);
				}

				// Throws IOException
				{
					final Documentation throwsDoc = new Documentation();
					throwsDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					final Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(), "In case of an error");
					throwsDoc.setDocumentation(docReturn);

					throwsDoc
							.setSignatureElementIdentifier("java.io.IOException:java.io.IOException");
					throwsDoc.setThematicRole(null);
					referenceDocs.add(throwsDoc);
				}

				// Throws Info IOException > ATTRIBUTE
				{
					final Documentation throwsInfoDoc = new Documentation();
					throwsInfoDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());
					final Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(),
							"This is also an attribute.");
					throwsInfoDoc.setDocumentation(docReturn);

					throwsInfoDoc
							.setSignatureElementIdentifier("java.io.IOException:java.io.IOException");
					throwsInfoDoc.setThematicRole(TestUtils.createAttribute());
					referenceDocs.add(throwsInfoDoc);
				}

				final ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "CustomerService.java");
				final CompilationUnit cu = output.getCompilationUnit();
				final JavaInterfaceArtifact artifact = TestDataFactory
						.createCustomerService("Developer", true, cu);
				final JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(0);

				final AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				final List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();

				// The intention of this assertion is to ensure that the
				// CustomerService-code, which is part of this test, is not extended by
				// further methods without refactoring this test-case.
				assertTrue(bodyDeclarations.size() == 4);

				final List<Addressee> addressees = TestUtils.createDeveloperSequence();
				final List<ThematicRole> thematicRoles = TestUtils
						.createReferenceThematicRoles();
				final List<Documentation> actualDocs = SimpleJavadocParser.INSTANCE
						.parseIDocItJavadoc(bodyDeclarations.get(1).getJavadoc(),
								addressees, thematicRoles, method);

				assertEquals(referenceDocs.toString(), actualDocs.toString());
			}

			// #########################################################################
			// # Test case #2: an undocumented Java-interface leads to an empty list
			// # of documentations
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "EmptyService.java");
				CompilationUnit cu = output.getCompilationUnit();

				AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();
				JavaInterfaceArtifact artifact = TestDataFactory.createEmptyService(cu,
						true);
				JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(0);

				// The intention of this assertion is to ensure that the
				// CustomerService-code, which is part of this test, is not extended by
				// further methods without refactoring this test-case.
				assertTrue(bodyDeclarations.size() == 1);

				List<Addressee> addressees = TestUtils.createDeveloperSequence();
				List<ThematicRole> thematicRoles = TestUtils
						.createReferenceThematicRoles();
				List<Documentation> actualDocs = SimpleJavadocParser.INSTANCE
						.parseIDocItJavadoc(bodyDeclarations.get(0).getJavadoc(),
								addressees, thematicRoles, method);

				assertEquals(new ArrayList<Documentation>(), actualDocs);
			}

			// #########################################################################
			// # Test case #3: in case of a Checking Operation, the prefix "Rule: " is
			// # removed from the introduction sentence.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "ExampleService.java");
				CompilationUnit cu = output.getCompilationUnit();

				AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();
				JavaInterfaceArtifact artifact = TestDataFactory.createExampleService(cu,
						true, "Developer");
				JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(1);

				List<Addressee> addressees = TestUtils.createDeveloperSequence();
				List<ThematicRole> thematicRoles = TestUtils
						.createReferenceThematicRoles();

				List<Documentation> actDocumentations = SimpleJavadocParser.INSTANCE
						.parseIDocItJavadoc(bodyDeclarations.get(1).getJavadoc(),
								addressees, thematicRoles, method);

				String actDocText = actDocumentations.get(0).getDocumentation()
						.get(TestUtils.createDeveloper());

				assertEquals("Check the beat.", actDocText);
			}
		}

		/*
		 * Negative tests
		 */
		{
			// #########################################################################
			// # Test case #1: a documented subparam which does not exist in the
			// # parameter-class causes a RuntimeException.
			// #########################################################################
			{
				ParserOutput output = JavaTestUtils
						.createCompilationUnit(AllIDocItJavaTests.SOURCE_DIR
								+ "ExampleService.java");
				CompilationUnit cu = output.getCompilationUnit();

				AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();
				JavaInterfaceArtifact artifact = TestDataFactory.createExampleService(cu,
						true, "Developer");
				JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(0);

				List<Addressee> addressees = TestUtils.createDeveloperSequence();
				List<ThematicRole> thematicRoles = TestUtils
						.createReferenceThematicRoles();
				boolean runtimeExceptionOccured = false;

				try
				{
					SimpleJavadocParser.INSTANCE.parseIDocItJavadoc(
							bodyDeclarations.get(0).getJavadoc(), addressees,
							thematicRoles, method);

				}
				catch (ParsingException rEx)
				{
					runtimeExceptionOccured = rEx
							.getMessage()
							.equals("No more subparameters to search in for the identifier notexistingattribute in method foo");
				}

				assertTrue(runtimeExceptionOccured);
			}
		}
	}
}
