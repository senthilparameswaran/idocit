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
package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.DocumentItemListComposite;
import de.akra.idocit.ui.composites.DocumentItemListCompositeSelection;

/**
 * Factory to create a {@link DocumentItemListComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentItemListCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection> createComposite(
			Composite pvParent, int style)
	{
		DocumentItemListComposite docItemComp = new DocumentItemListComposite(pvParent,
				SWT.NONE);
		DocumentItemListCompositeSelection selection = new DocumentItemListCompositeSelection();

		// Here we have to use the same roles as returned by
		// DocumentationTestFactory.createDocumentation().
		List<Addressee> addresseeList = new ArrayList<Addressee>();
		Addressee addresseeDeveloper = new Addressee("Developer");
		Addressee addresseeArchitect = new Addressee("Tester");
		Addressee addresseeProjectManager = new Addressee("Manager");

		addresseeList.add(addresseeDeveloper);
		addresseeList.add(addresseeArchitect);
		addresseeList.add(addresseeProjectManager);

		List<ThematicRole> thematicRoleList = new ArrayList<ThematicRole>();
		ThematicRole roleAGENT = new ThematicRole("AGENT");
		ThematicRole roleACTION = new ThematicRole("ACTION");

		thematicRoleList.add(roleAGENT);
		thematicRoleList.add(roleACTION);
		selection.setAddresseeList(addresseeList);
		selection.setThematicRoleList(thematicRoleList);

		selection
				.setDisplayedAddresseesOfDocumentations(new ArrayList<List<Addressee>>());

		List<Documentation> docs = new Vector<Documentation>();
		docs.add(DocumentationTestFactory.createDocumentation());
		docs.add(DocumentationTestFactory.createDocumentation());
		docs.add(DocumentationTestFactory.createDocumentation());
		selection.setDocumentations(docs);

		List<Integer> activeAddressees = new Vector<Integer>();
		activeAddressees.add(1);
		activeAddressees.add(2);
		activeAddressees.add(7);
		selection.setActiveAddressees(activeAddressees);

		docItemComp.setSelection(selection);
		return docItemComp;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
