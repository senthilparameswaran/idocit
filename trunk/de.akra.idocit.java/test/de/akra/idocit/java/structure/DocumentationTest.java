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
package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Tests for {@link Documentation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentationTest
{

	/**
	 * Tests {@link Documentation#copy()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create a Documentation, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			Documentation sourceDoc = createDocumentation();
			Documentation copiedDoc = sourceDoc.copy();

			assertEquals(true, sourceDoc.equals(copiedDoc));
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create a Documentation, copy it and change Scope. Equals test
		 * should return false.
		 * ****************************************************************************
		 * None
		 */
		{
			Documentation sourceDoc = createDocumentation();
			Documentation copiedDoc = sourceDoc.copy();
			assertEquals(true, sourceDoc.equals(copiedDoc));

			copiedDoc.getDocumentation().put(new Addressee("DEVELOPER"),
					"other description");
			assertEquals(false, sourceDoc.equals(copiedDoc));
		}
	}

	/**
	 * Create a test Documentation.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	public static Documentation createDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setThematicRole(new ThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("test.wsdl.CustomerService.find.input(Customer).Customer");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = new Addressee("DEVELOPER");
		Addressee manager = new Addressee("MANAGER");

		docMap.put(developer, "Documenation for developers.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Documenation for managers.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}

}
