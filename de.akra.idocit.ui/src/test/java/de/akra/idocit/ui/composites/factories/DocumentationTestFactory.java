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
package de.akra.idocit.ui.composites.factories;

import java.util.List;
import java.util.Map;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Helper to create a {@link Documentation} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentationTestFactory
{
	/**
	 * The supported thematic roles for the signature elements.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * 
	 */
	public static enum ThematicRoleEnum
	{
		SOURCE, CRITERION, OBJECT, ACTION, AGENT;
	}

	/**
	 * String values for {@link ThematicRole}s.
	 */
	public static String[] thematicRoleArray = new String[] { "SOURCE", "CRITERION",
			"OBJECT", "ACTION", "AGENT" };

	/**
	 * Create a dummy {@link Documentation}.
	 * 
	 * @return a new {@link Documentation} with test data.
	 */
	public static Documentation createDocumentation()
	{
		Documentation doc = new Documentation();

		ThematicRole d = new ThematicRole("CRITERION");
		doc.setThematicRole(d);

		Map<Addressee, String> docs = doc.getDocumentation();
		List<Addressee> addresseeSequence = doc.getAddresseeSequence();

		Addressee developer = new Addressee("Developer");
		docs.put(developer, "Doku f�r " + developer.getName());
		addresseeSequence.add(developer);

		Addressee manager = new Addressee("Manager");
		docs.put(manager, "Doku f�r " + manager.getName());
		addresseeSequence.add(manager);

		Addressee tester = new Addressee("Tester");
		docs.put(tester, "Doku f�r " + tester.getName());
		addresseeSequence.add(tester);

		doc.setErrorCase(true);

		return doc;
	}
}
