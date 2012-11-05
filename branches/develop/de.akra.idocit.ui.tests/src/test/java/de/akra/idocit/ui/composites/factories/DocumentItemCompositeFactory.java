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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.ui.composites.DocumentItemComposite;
import de.akra.idocit.ui.composites.DocumentItemCompositeSelection;

/**
 * Factory to create a {@link DocumentItemComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentItemCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection> createComposite(
			Composite pvParent, int style)
	{
		DocumentItemComposite docItemComp = new DocumentItemComposite(pvParent, SWT.NONE);
		DocumentItemCompositeSelection selection = new DocumentItemCompositeSelection();

		List<Addressee> addresseeList = new ArrayList<Addressee>();
		// Here we have to use the same roles as returned by
		// DocumentationTestFactory.createDocumentation().
		Addressee addresseeDeveloper = new Addressee("Developer");
		Addressee addresseeArchitect = new Addressee("Tester");
		Addressee addresseeProjectManager = new Addressee("Manager");

		addresseeList.add(addresseeDeveloper);
		addresseeList.add(addresseeArchitect);
		addresseeList.add(addresseeProjectManager);

		List<ThematicRole> thematicRoleList = new ArrayList<ThematicRole>();
		ThematicRole roleAGENT = new ThematicRole("AGENT");
		ThematicRole roleACTION = new ThematicRole("ACTION");
		ThematicRole roleCOMPARISON = new ThematicRole("COMPARISON");

		thematicRoleList.add(roleAGENT);
		thematicRoleList.add(roleACTION);
		thematicRoleList.add(roleCOMPARISON);
		
		ThematicRole roleOBJECT = new ThematicRole("OBJECT");
		List<ThematicRole> moreRolesList = new ArrayList<ThematicRole>();
		moreRolesList.add(roleOBJECT);

		selection.setAddresseeList(addresseeList);
		
		RolesRecommendations recommendations = new RolesRecommendations(thematicRoleList, moreRolesList);
		
		selection.setRolesRecommendations(recommendations);

		// create documentation
		Documentation doc = DocumentationTestFactory.createDocumentation();

		// set test documentation to the composite
		selection.setDocumentation(doc);
		docItemComp.setSelection(selection);

		docItemComp.addSelectionListener(new ISelectionListener<DocumentItemCompositeSelection>() {
			
			@Override
			public void selectionChanged(DocumentItemCompositeSelection selection,
					PocUIComposite<DocumentItemCompositeSelection> arg1, Object arg2)
			{
				System.out.println(selection);
			}
		});
		
		pvParent.pack();
		pvParent.layout();
		return docItemComp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
