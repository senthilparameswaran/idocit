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
package de.akra.idocit.ui.composites;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.DescribedItem;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.ui.utils.DescribedItemUtils;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * Composite to edit {@link DescribedItem}s in the preference page.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditAddresseeListComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditAddresseeListCompositeSelection>
{

	// Widgets
	private List itemList;

	private Button btnAdd;

	private Button btnRemove;

	// Listener
	private SelectionListener btnAddSelectionListener;

	private SelectionListener btnRemoveSelectionListener;

	private SelectionListener itemListSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public EditAddresseeListComposite(Composite parent)
	{
		super(parent, SWT.NONE, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Label lblListDescription = new Label(this, SWT.NONE);
		lblListDescription.setText("Defined Items:");

		Label lblEmpty = new Label(this, SWT.NONE);
		lblEmpty.setText(StringUtils.EMPTY);

		itemList = new List(this, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().hint(100, 100).grab(true, true).span(2, 1).applyTo(itemList);

		btnAdd = new Button(this, SWT.PUSH);
		btnAdd.setText("Add Item");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnAdd);

		btnRemove = new Button(this, SWT.PUSH);
		btnRemove.setText("Remove selected Item(s)");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnRemove);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		btnAddSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Addressee newItem = DescribedItemUtils.createNewAddressee();

				EditAddresseeListCompositeSelection selection = getSelection();

				if (!DescribedItemUtils.containsName(newItem, selection.getAddressees()))
				{
					java.util.List<Addressee> items = selection.getAddressees();

					items.add(newItem);
					selection.setAddressees(items);

					// Changes due to Issue #32
					java.util.List<Addressee> activeAddressees = new ArrayList<Addressee>(
							1);
					activeAddressees.add(newItem);
					selection.setActiveAddressees(activeAddressees);
					// End changes due to Issue #32
					setSelection(selection);

					fireChangeEvent(btnAdd);
				}
				else
				{
					MessageBoxUtils.openErrorBox(getShell(),
							"The name of an item has to be unique. There exists already an item with the name "
									+ newItem.getName());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Nothing to do!
			}
		};

		btnRemoveSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				EditAddresseeListCompositeSelection selection = getSelection();

				if (selection.getActiveAddressees() != null)
				{
					int numberOfItems = selection.getAddressees().size()
							- selection.getActiveAddressees().size();

					// Check if this remove-operation violates the minimum number of
					// items.
					if (numberOfItems >= selection.getMinNumberOfItems())
					{
						// Remove the selected item.
						java.util.List<Addressee> items = selection.getAddressees();

						// Changes due to Issue #10
						int nextSelIndex = -1;
						if (!selection.getActiveAddressees().isEmpty())
						{
							nextSelIndex = items.indexOf(selection.getActiveAddressees()
									.get(0));
							if (nextSelIndex >= items.size() - 1)
							{
								nextSelIndex = items.size() - 2;
							}
						}
						// End changes due to Issue #10

						items.removeAll(selection.getActiveAddressees());
						selection.setAddressees(items);

						// Changes due to Issue #10
						selection
								.setActiveAddressees(nextSelIndex > -1 ? new ArrayList<Addressee>(
										items.subList(nextSelIndex, nextSelIndex + 1))
										: null);
						// End changes due to Issue #10

						setSelection(selection);
						fireChangeEvent(btnRemove);
					}
					else
					{
						MessageBoxUtils.openErrorBox(
								getShell(),
								"You have to keep at least "
										+ selection.getMinNumberOfItems()
										+ " item(s) in this list.");
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Nothing to do!
			}
		};

		itemListSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String[] selectedItems = itemList.getSelection();
				EditAddresseeListCompositeSelection selection = getSelection();

				if (selectedItems.length >= 1)
				{
					java.util.List<Addressee> activeItems = new ArrayList<Addressee>();
					int[] selectedItemIndices = itemList.getSelectionIndices();

					for (int selectedIndex : selectedItemIndices)
					{
						activeItems.add(selection.getAddressees().get(selectedIndex));
					}

					selection.setActiveAddressees(activeItems);
				}
				else
				{
					selection.setActiveAddressees(new ArrayList<Addressee>());
				}

				fireChangeEvent(itemList);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Nothing to do!
			}
		};
	}

	@Override
	protected void doSetSelection(EditAddresseeListCompositeSelection oldInSelection,
			EditAddresseeListCompositeSelection newInSelection, Object sourceControl)
	{
		itemList.removeAll();

		if (newInSelection.getAddressees() != null)
		{
			for (DescribedItem item : newInSelection.getAddressees())
			{
				itemList.add(item.getName());
			}
		}

		if (newInSelection.getActiveAddressees() != null)
		{
			int[] selectedItemIndices = new int[newInSelection.getActiveAddressees()
					.size()];

			for (int i = 0; i < newInSelection.getActiveAddressees().size(); i++)
			{
				DescribedItem item = newInSelection.getActiveAddressees().get(i);
				int index = newInSelection.getAddressees().indexOf(item);
				selectedItemIndices[i] = index;
			}
			
			itemList.select(selectedItemIndices);
			itemList.showSelection();
		}

		btnRemove.setEnabled((newInSelection.getActiveAddressees() != null)
				&& (newInSelection.getActiveAddressees().size() > 0));
	}

	@Override
	protected void addAllListener()
	{
		itemList.addSelectionListener(itemListSelectionListener);
		btnAdd.addSelectionListener(btnAddSelectionListener);
		btnRemove.addSelectionListener(btnRemoveSelectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		itemList.removeSelectionListener(itemListSelectionListener);
		btnAdd.removeSelectionListener(btnAddSelectionListener);
		btnRemove.removeSelectionListener(btnRemoveSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}
}
