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
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.JavadocTestUtils;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.TestDataFactory;

public class SimpleJavadocParserTest
{

	@Test
	public void testParseIDocItJavadoc() throws FileNotFoundException, IOException,
			SAXException, ParserConfigurationException
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
				List<Documentation> referenceDocs = new ArrayList<Documentation>();

				// Action Doc Text
				{
					Documentation actionDoc = new Documentation();
					actionDoc.setThematicRole(TestUtils.createAction());

					Map<Addressee, String> documentationsAction = new HashMap<Addressee, String>();
					documentationsAction
							.put(TestUtils.createDeveloper(),
									"Only customers who placed an order within the last year are considered.");
					actionDoc.setDocumentation(documentationsAction);

					actionDoc.setSignatureElementIdentifier(null);
					actionDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());
					referenceDocs.add(actionDoc);
				}

				// Ordering
				{
					Documentation orderingDoc = new Documentation();
					orderingDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					Map<Addressee, String> docOrdering = new HashMap<Addressee, String>();
					docOrdering.put(TestUtils.createDeveloper(),
							"Alphabetically by lastname");
					orderingDoc.setDocumentation(docOrdering);

					orderingDoc.setSignatureElementIdentifier(null);
					orderingDoc.setThematicRole(TestUtils.createOrdering());

					referenceDocs.add(orderingDoc);
				}

				// Source
				{
					Documentation sourceDoc = new Documentation();
					sourceDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					Map<Addressee, String> docSource = new HashMap<Addressee, String>();
					docSource.put(TestUtils.createDeveloper(), "CRM System");
					sourceDoc.setDocumentation(docSource);

					sourceDoc.setSignatureElementIdentifier(null);
					sourceDoc.setThematicRole(TestUtils.createSource());

					referenceDocs.add(sourceDoc);
				}

				// Parameter parameters > COMPARISON
				{
					Documentation paramParametersDoc = new Documentation();
					paramParametersDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(), "This is the customer.");
					paramParametersDoc.setDocumentation(docSubParamFirstname);

					paramParametersDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters");
					paramParametersDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(paramParametersDoc);	
				}

				// Parameter parameters > SOURCE
				{
					Documentation paramParametersDoc = new Documentation();
					paramParametersDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(), "This is the source.");
					paramParametersDoc.setDocumentation(docSubParamFirstname);

					paramParametersDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters");
					paramParametersDoc.setThematicRole(TestUtils.createSource());
					referenceDocs.add(paramParametersDoc);	
				}
				
				// Subparameter firstname
				{
					Documentation subparamFirstnameDoc = new Documentation();
					subparamFirstnameDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					Map<Addressee, String> docSubParamFirstname = new HashMap<Addressee, String>();
					docSubParamFirstname.put(TestUtils.createDeveloper(), "");
					subparamFirstnameDoc.setDocumentation(docSubParamFirstname);

					subparamFirstnameDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters/source.NameParameters.firstName:java.lang.String");
					subparamFirstnameDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(subparamFirstnameDoc);
				}

				// Subparameter lastname
				{
					Documentation subparamLastnameDoc = new Documentation();
					subparamLastnameDoc.setAddresseeSequence(TestUtils
							.createDeveloperSequence());

					Map<Addressee, String> docSubparamLastname = new HashMap<Addressee, String>();
					docSubparamLastname.put(TestUtils.createDeveloper(), "");
					subparamLastnameDoc.setDocumentation(docSubparamLastname);

					subparamLastnameDoc
							.setSignatureElementIdentifier("parameters:source.NameParameters/source.NameParameters.lastName:java.lang.String");
					subparamLastnameDoc.setThematicRole(TestUtils.createComparison());
					referenceDocs.add(subparamLastnameDoc);
				}

				// Return Object
				{
					Documentation returnDoc = new Documentation();
					returnDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(), "This is the object.");
					returnDoc.setDocumentation(docReturn);

					returnDoc.setSignatureElementIdentifier("java.util.List<source.Customer>:java.util.List<source.Customer>");
					returnDoc.setThematicRole(TestUtils.createObject());
					referenceDocs.add(returnDoc);
				}
				
				// Return Info Source
				{
					Documentation returnDoc = new Documentation();
					returnDoc.setAddresseeSequence(TestUtils.createDeveloperSequence());

					Map<Addressee, String> docReturn = new HashMap<Addressee, String>();
					docReturn.put(TestUtils.createDeveloper(), "This is the source.");
					returnDoc.setDocumentation(docReturn);

					returnDoc.setSignatureElementIdentifier("java.util.List<source.Customer>:java.util.List<source.Customer>");
					returnDoc.setThematicRole(TestUtils.createSource());
					referenceDocs.add(returnDoc);
				}

				ParserOutput output = JavadocTestUtils
						.createCompilationUnit("test/source/CustomerService.java");
				CompilationUnit cu = output.getCompilationUnit();
				JavaInterfaceArtifact artifact = TestDataFactory
						.createCustomerService("Developer", true, cu);
				JavaMethod method = (JavaMethod) artifact.getInterfaces().get(0)
						.getOperations().get(0);

				AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();

				// The intention of this assertion is to ensure that the
				// CustomerService-code, which is part of this test, is not extended by
				// further methods without refactoring this test-case.
				assertTrue(bodyDeclarations.size() == 3);

				List<Addressee> addressees = TestUtils.createDeveloperSequence();
				List<ThematicRole> thematicRoles = TestUtils
						.createReferenceThematicRoles();
				List<Documentation> actualDocs = SimpleJavadocParser.INSTANCE
						.parseIDocItJavadoc(bodyDeclarations.get(1).getJavadoc(),
								addressees, thematicRoles, method);

				assertEquals(referenceDocs.toString(), actualDocs.toString());
			}

			// #########################################################################
			// # Test case #2: an undocumented Java-interface leads to an empty list
			// # of documentations
			// #########################################################################
			{
				ParserOutput output = JavadocTestUtils
						.createCompilationUnit("test/source/EmptyService.java");
				CompilationUnit cu = output.getCompilationUnit();

				AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu
						.types().get(0);
				List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDecl
						.bodyDeclarations();
				JavaInterfaceArtifact artifact = TestDataFactory
						.createEmptyService(cu, true);
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
		}

		/*
		 * Negative tests
		 */
		{

		}
	}
}
