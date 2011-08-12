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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Scope;
import de.akra.idocit.core.utils.ObjectStructureUtils;

/**
 * Tests for {@link HTMLTableParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class HTMLTableParserTest
{

	/**
	 * Test for {@link HTMLTableParser#convertJavadocToDocumentations(String)}.
	 * <p>
	 * Initialize in {@link ObjectStructureUtils} the private attributes
	 * <code>supportedAddressees</code> and <code>supportedThematicRoles</code> with
	 * {@link Collections#emptyList()}, because the Eclipse Workspace is not available in
	 * JUnit Test.
	 * </p>
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testConvertJavadocToDocumentations() throws SAXException, IOException,
			ParserConfigurationException
	{
		// create expected doc
		List<Documentation> expectedDoc = new ArrayList<Documentation>();
		Documentation doc = new Documentation();
		expectedDoc.add(doc);
		doc.setScope(Scope.IMPLICIT);
		doc.setSignatureElementIdentifier("filter.id");
		doc.setThematicRole(ObjectStructureUtils.findThematicRole("CRITERION"));

		Map<Addressee, String> addresseeDocs = new HashMap<Addressee, String>();
		doc.setDocumentation(addresseeDocs);

		Addressee developer = ObjectStructureUtils.findAddressee("Developer");
		addresseeDocs.put(developer,
				"The existing documentation: this is a really good filter ;)");
		doc.getAddresseeSequence().add(developer);

		Addressee manager = ObjectStructureUtils.findAddressee("Manager");
		addresseeDocs.put(manager, "Really good info for manager.");
		doc.getAddresseeSequence().add(manager);

		// parse html
		List<Documentation> docs = HTMLTableParser
				.convertJavadocToDocumentations("<table border=\"1\"><tr><td>Element:</td><td>filter.id</td></tr><tr><td>Role:</td><td>CRITERION</td></tr><tr><td>Scope:</td><td>implicit</td></tr><tr><td><b>Developer</b>:</td><td>The existing documentation: this is a really good filter ;)</td></tr><tr><td><b>Manager</b>:</td><td>Really good info for manager.</td></tr></table>");

		Assert.assertEquals(expectedDoc, docs);
	}
}
