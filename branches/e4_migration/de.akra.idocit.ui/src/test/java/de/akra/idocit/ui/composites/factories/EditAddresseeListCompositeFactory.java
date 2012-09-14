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

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.ui.composites.EditAddresseeListComposite;
import de.akra.idocit.ui.composites.EditAddresseeListCompositeSelection;

/**
 * Factory for {@link EditAddresseeListCompositeFactory}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class EditAddresseeListCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, EditAddresseeListCompositeSelection>
{

	@Override
	public EditAddresseeListComposite createComposite(Composite pvParent, int style)
	{
		EditAddresseeListComposite editItemListComposite = new EditAddresseeListComposite(
				pvParent);

		EditAddresseeListCompositeSelection selection = new EditAddresseeListCompositeSelection();
		List<Addressee> items = new ArrayList<Addressee>();

		Addressee addressee1 = new Addressee("Manager");
		addressee1.setDescription("A Manager");
		items.add(addressee1);

		Addressee addressee2 = new Addressee("Developer");
		addressee2.setDescription("A developer");
		items.add(addressee2);

		Addressee addressee3 = new Addressee("Client");
		addressee3.setDescription("A client");
		items.add(addressee3);

		List<Addressee> activeItems = new ArrayList<Addressee>();
		activeItems.add(addressee2);
		activeItems.add(addressee3);

		selection.setActiveAddressees(activeItems);
		selection.setAddressees(items);
		selection.setMinNumberOfItems(1);

		editItemListComposite.setSelection(selection);

		return editItemListComposite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
