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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import de.akra.idocit.common.services.ThematicGridService;
import de.akra.idocit.common.structure.DescribedItem;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.utils.DescribedItemUtils;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * Composite to edit {@link DescribedItem}s in the preference page.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicRoleListComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicRoleListCompositeSelection>
{
	private static Logger LOG = Logger.getLogger(EditThematicRoleListComposite.class
			.getName());

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
	public EditThematicRoleListComposite(Composite parent)
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
		GridDataFactory.fillDefaults().grab(true, true).hint(100, 100).span(2, 1)
				.applyTo(itemList);

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
				final ThematicRole newItem = DescribedItemUtils.createNewThematicRole();

				final EditThematicRoleListCompositeSelection selection = getSelection();

				if (!DescribedItemUtils.containsName(newItem,
						selection.getThematicRoles()))
				{
					selection.getThematicRoles().add(newItem);
					selection.setActiveThematicRole(newItem);

					fireChangeEvent(btnAdd);
				}
				else
				{
					MessageBoxUtils.openErrorBox(getShell(),
							"The name of an item has to be unique. There exists already an item with the name \""
									+ newItem.getName() + "\"");
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
				final EditThematicRoleListCompositeSelection selection = getSelection();

				if (selection.getActiveThematicRole() != null)
				{
					int numberOfItems = selection.getThematicRoles().size() - 1;

					// Check if this remove-operation violates the minimum number of
					// items.
					if (numberOfItems >= selection.getMinNumberOfItems())
					{
						final ThematicRole roleToDelete = selection
								.getActiveThematicRole();

						boolean deleteRole = true;
						if (isThematicRoleUsedInGrids(roleToDelete))
						{
							deleteRole = MessageBoxUtils
									.openQuestionDialogBox(
											getShell(),
											"The thematic role is used in at least one thematic grid. If the role is deleted it is also deleted from all thematic grids.\n\nDo you really want to delete the role?");
						}

						if (deleteRole)
						{
							selection.setRemovedThematicRole(roleToDelete);

							final java.util.List<ThematicRole> allRoles = selection
									.getThematicRoles();

							// calculate next selected item
							int nextSelIndex = allRoles.indexOf(roleToDelete);
							if (nextSelIndex >= allRoles.size() - 1)
							{
								nextSelIndex = allRoles.size() - 2;
							}

							allRoles.remove(roleToDelete);
							selection.setActiveThematicRole(allRoles.get(nextSelIndex));

							fireChangeEvent(btnRemove);
						}
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
				final EditThematicRoleListCompositeSelection selection = getSelection();

				if (itemList.getSelectionCount() >= 1)
				{
					selection.setActiveThematicRole(selection.getThematicRoles().get(
							itemList.getSelectionIndex()));
				}
				else
				{
					selection.setActiveThematicRole(null);
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

	/**
	 * Check if there the thematic role is configured in at least one ThematicGrid.
	 * 
	 * @source All configured {@link ThematicGrid}s.
	 * @param thematicRole
	 * @return [REPORT] <code>true</code> if the role is used in at least one
	 *         ThematicGrid.
	 * @thematicgrid Checking Operations
	 */
	private boolean isThematicRoleUsedInGrids(final ThematicRole thematicRole)
	{
		boolean isUsed = false;
		try
		{
			final java.util.List<ThematicGrid> grids = ServiceManager.getInstance()
					.getPersistenceService().loadThematicGrids();

			final Iterator<ThematicGrid> gridIter = grids.iterator();
			while (gridIter.hasNext() && !isUsed)
			{
				final ThematicGrid grid = gridIter.next();
				isUsed = ThematicGridService.containsRole(grid.getRoles().keySet(),
						thematicRole);
			}
		}
		catch (final UnitializedIDocItException e)
		{
			final String msg = "Searching for a thematic role within all configured thematic grids failed. Reopen the preferences.";
			LOG.log(Level.SEVERE, msg, e);
			MessageBoxUtils.openErrorBox(getShell(), msg + "\n\nError:\n" + e.toString(),
					e);
		}
		return isUsed;
	}

	@Override
	protected void doSetSelection(EditThematicRoleListCompositeSelection oldInSelection,
			EditThematicRoleListCompositeSelection newInSelection, Object sourceControl)
	{
		itemList.removeAll();

		if (newInSelection.getThematicRoles() != null)
		{
			for (final DescribedItem item : newInSelection.getThematicRoles())
			{
				itemList.add(item.getName());
			}
		}

		if (newInSelection.getActiveThematicRole() != null)
		{
			final int index = newInSelection.getThematicRoles().indexOf(
					newInSelection.getActiveThematicRole());
			itemList.select(index);
			itemList.showSelection();
		}

		btnRemove.setEnabled(itemList.getSelectionCount() > 0);
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
