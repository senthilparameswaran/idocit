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

import de.akra.idocit.core.structure.DescribedItem;
import de.akra.idocit.core.structure.ThematicRole;
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
		lblEmpty.setText("");

		itemList = new List(this, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(itemList);

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
				ThematicRole newItem = DescribedItemUtils.createNewThematicRole();

				EditThematicRoleListCompositeSelection selection = getSelection();

				if (!DescribedItemUtils.containsName(newItem, selection.getItems()))
				{
					java.util.List<ThematicRole> items = selection.getItems();

					items.add(newItem);

					selection.setItems(items);
					setSelection(selection);

					fireChangeEvent();
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
				EditThematicRoleListCompositeSelection selection = getSelection();

				if (selection.getActiveItems() != null)
				{
					int numberOfItems = selection.getItems().size()
							- selection.getActiveItems().size();

					// Check if this remove-operation violates the minimum number of
					// items.
					if (numberOfItems >= selection.getMinNumberOfItems())
					{
						// Remove the selected item.
						java.util.List<ThematicRole> items = selection.getItems();
						items.removeAll(selection.getActiveItems());

						selection.setItems(items);
						setSelection(selection);

						fireChangeEvent();
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
				EditThematicRoleListCompositeSelection selection = getSelection();

				if (selectedItems.length >= 1)
				{
					java.util.List<ThematicRole> activeItems = new ArrayList<ThematicRole>();
					int[] selectedItemIndices = itemList.getSelectionIndices();

					for (int selectedIndex : selectedItemIndices)
					{
						activeItems.add(selection.getItems().get(selectedIndex));
					}

					selection.setActiveItems(activeItems);
				}
				else
				{
					selection.setActiveItems(new ArrayList<ThematicRole>());
				}

				setSelection(selection);
				fireChangeEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Nothing to do!
			}
		};
	}

	@Override
	protected void doSetSelection(EditThematicRoleListCompositeSelection oldInSelection,
			EditThematicRoleListCompositeSelection newInSelection)
	{
		itemList.removeAll();

		if (newInSelection.getItems() != null)
		{
			for (DescribedItem item : newInSelection.getItems())
			{
				itemList.add(item.getName());
			}
		}

		if (newInSelection.getActiveItems() != null)
		{
			int[] selectedItemIndices = new int[newInSelection.getActiveItems().size()];

			for (int i = 0; i < newInSelection.getActiveItems().size(); i++)
			{
				DescribedItem item = newInSelection.getActiveItems().get(i);
				int index = newInSelection.getItems().indexOf(item);
				selectedItemIndices[i] = index;
			}

			itemList.select(selectedItemIndices);
		}

		btnRemove.setEnabled((newInSelection.getActiveItems() != null)
				&& (newInSelection.getActiveItems().size() > 0));
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
