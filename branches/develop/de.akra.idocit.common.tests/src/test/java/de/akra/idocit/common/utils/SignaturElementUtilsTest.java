/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.ThematicRole;

public class SignaturElementUtilsTest
{
	private static final String DEVELOPER = "Developer";
	private static final String AGENT = "AGENT";
	private static final String SOURCE = "SOURCE";
	private static final String DESTINATION = "DESTINATION";
	private static final String ACTION = "ACTION";
	private static final String OBJECT = "OBJECT";
	private static final String INSTRUMENT = "INSTRUMENT";
	private static final String PRIMARY_KEY = "PRIMARY_KEY";

	private Documentation createDocumentation(String roleName, boolean errorCaseDoc)
	{
		Documentation docAgentError = new Documentation();

		Addressee addressee = new Addressee(DEVELOPER);
		List<Addressee> addressees = new ArrayList<Addressee>();
		addressees.add(addressee);

		docAgentError.setAddresseeSequence(addressees);
		docAgentError.setDocumentation(new HashMap<Addressee, String>());
		docAgentError.setThematicRole(new ThematicRole(roleName));
		docAgentError.setErrorCase(errorCaseDoc);
		docAgentError.setSignatureElementIdentifier("");

		return docAgentError;
	}

	private InterfaceArtifact createDocumentedInterfaceArtifact()
	{
		InterfaceArtifact artifact = TestUtils.createInterfaceArtifact();

		artifact.getInterfaces().get(0).addDocpart(createDocumentation(AGENT, true));
		artifact.getInterfaces().get(0).getOperations().get(0)
				.addDocpart(createDocumentation(ACTION, false));
		artifact.getInterfaces().get(0).getOperations().get(0)
				.addDocpart(createDocumentation(OBJECT, true));
		artifact.getInterfaces().get(0).getOperations().get(0).getInputParameters()
				.getParameters().get(0).addDocpart(createDocumentation(SOURCE, true));
		artifact.getInterfaces().get(0).getOperations().get(0).getInputParameters()
				.getParameters().get(0)
				.addDocpart(createDocumentation(PRIMARY_KEY, false));
		artifact.getInterfaces().get(0).getOperations().get(0).getOutputParameters()
				.getParameters().get(0)
				.addDocpart(createDocumentation(DESTINATION, true));
		artifact.getInterfaces().get(0).getOperations().get(0).getOutputParameters()
				.getParameters().get(0)
				.addDocpart(createDocumentation(INSTRUMENT, false));

		return artifact;
	}

	@Test
	public void collectAssociatedThematicRolesWithErrorCases()
	{
		Set<ThematicRole> refThematicRoles = new HashSet<ThematicRole>();
		refThematicRoles.add(new ThematicRole(AGENT));
		refThematicRoles.add(new ThematicRole(OBJECT));
		refThematicRoles.add(new ThematicRole(SOURCE));
		refThematicRoles.add(new ThematicRole(DESTINATION));

		InterfaceArtifact artifact = createDocumentedInterfaceArtifact();
		Operation operation = artifact.getInterfaces().get(0).getOperations().get(0);

		Set<ThematicRole> actThematicRoles = new HashSet<ThematicRole>();
		SignatureElementUtils.collectAssociatedThematicRoles(actThematicRoles, operation,
				true);

		assertEquals(refThematicRoles, actThematicRoles);
	}

	@Test
	public void collectAssociatedThematicRoles()
	{
		Set<ThematicRole> refThematicRoles = new HashSet<ThematicRole>();
		refThematicRoles.add(new ThematicRole(AGENT));
		refThematicRoles.add(new ThematicRole(OBJECT));
		refThematicRoles.add(new ThematicRole(SOURCE));
		refThematicRoles.add(new ThematicRole(DESTINATION));
		refThematicRoles.add(new ThematicRole(ACTION));
		refThematicRoles.add(new ThematicRole(INSTRUMENT));
		refThematicRoles.add(new ThematicRole(PRIMARY_KEY));

		InterfaceArtifact artifact = createDocumentedInterfaceArtifact();
		Operation operation = artifact.getInterfaces().get(0).getOperations().get(0);

		Set<ThematicRole> actThematicRoles = new HashSet<ThematicRole>();
		SignatureElementUtils.collectAssociatedThematicRoles(actThematicRoles, operation,
				false);

		assertEquals(refThematicRoles, actThematicRoles);
	}
	
	@Test
	public void collectAssociatedThematicRolesFromArtifact(){
		Set<ThematicRole> roles = new HashSet<ThematicRole>();
		InterfaceArtifact artifact = createDocumentedInterfaceArtifact();
		
		SignatureElementUtils.collectAssociatedThematicRoles(roles, artifact,
				false);
		assertTrue(roles.isEmpty());
		
		SignatureElementUtils.collectAssociatedThematicRoles(roles, artifact,
				true);
		assertTrue(roles.isEmpty());
	}
}
