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
package de.akra.idocit.java.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Scope;
import de.akra.idocit.core.utils.DescribedItemUtils;

/**
 * Tests for {@link JavadocParser}.
 * <p>
 * Run this test as JUnit Plug-in Test or initialize in {@link DescribedItemUtils} the
 * private attributes <code>supportedAddressees</code> and
 * <code>supportedThematicRoles</code> with {@link Collections#emptyList()}, because the
 * Eclipse Workspace is not available in JUnit Test.
 * </p>
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavadocParserTest
{
	private static Logger logger = Logger.getLogger(JavadocParserTest.class.getName());

	/**
	 * Test for {@link JavadocParser#parseIDocItJavadoc(Javadoc)}.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testParse() throws SAXException, IOException,
			ParserConfigurationException
	{
		AST a = AST.newAST(AST.JLS3);
		Javadoc javadoc = a.newJavadoc();

		List<Documentation> paramDocumentations = createParamDocumentations();
		List<Documentation> returnDocumentations = createReturnDocumentations();
		JavadocGenerator.appendDocsToJavadoc(paramDocumentations, null, null, javadoc);
		JavadocGenerator.appendDocsToJavadoc(paramDocumentations, TagElement.TAG_PARAM,
				"person", javadoc);
		JavadocGenerator.appendDocsToJavadoc(returnDocumentations, TagElement.TAG_RETURN,
				null, javadoc);
		JavadocGenerator.appendDocsToJavadoc(paramDocumentations, TagElement.TAG_THROWS,
				"IllegalArgException", javadoc);

		// add the number of used documentations to this list, to make an assertion
		List<Documentation> allUsedDocs = new ArrayList<Documentation>(
				paramDocumentations.size() * 4);
		allUsedDocs.addAll(paramDocumentations);
		allUsedDocs.addAll(paramDocumentations);
		allUsedDocs.addAll(returnDocumentations);
		allUsedDocs.addAll(paramDocumentations);

		logger.log(Level.FINE, javadoc.toString());

		List<Documentation> convertedDocs = JavadocParser.parseIDocItJavadoc(javadoc);

		logger.log(Level.FINE, allUsedDocs.toString());
		logger.log(Level.FINE, convertedDocs.toString());

		Assert.assertEquals(allUsedDocs, convertedDocs);
	}
	
	/**
	 * Tests {@link JavadocParser#parseIDocItReferenceGrid(Javadoc)}.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testParseIDocItReferenceGrid(){
		
		
		AST a = AST.newAST(AST.JLS3);
		Javadoc javadoc = a.newJavadoc();
		
		TagElement tagElement = a.newTagElement();
		tagElement.setTagName(JavadocParser.JAVADOC_TAG_THEMATICGRID);
		
		List<ASTNode> fragments = (List<ASTNode>) tagElement.fragments();
		TextElement textElement = a.newTextElement();
		textElement.setText("Searching Operations");
		fragments.add(textElement);
		
		List<TagElement> tags = javadoc.tags();
		tags.add(tagElement);
		
		String thematicGridName = JavadocParser.parseIDocItReferenceGrid(javadoc);
		assertEquals("Searching Operations", thematicGridName);
	}

	/**
	 * Create a list of {@link Documentation}s for testing.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createParamDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createParamDocumentation());
		documentations.add(createParamDocumentation());
		return documentations;
	}

	/**
	 * Create a list with one {@link Documentation} for a method return value.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createReturnDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createReturnDocumentation());
		return documentations;
	}

	/**
	 * Create a test Documentation.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createParamDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(DescribedItemUtils.findThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("/person:Person/name:java.lang.String");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = DescribedItemUtils.findAddressee("Developer");
		Addressee manager = DescribedItemUtils.findAddressee("Manager");

		docMap.put(developer, "Documenation for developers.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Documenation for managers.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}

	/**
	 * Create a test Documentation for a method return value.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createReturnDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(DescribedItemUtils.findThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("double:double");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = DescribedItemUtils.findAddressee("Developer");
		Addressee manager = DescribedItemUtils.findAddressee("Manager");

		docMap.put(developer, "Developer: Result as floating-point number.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Manager: Result as floating-point number.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}
}
