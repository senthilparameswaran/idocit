package de.akra.idocit.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.DescribedItem;
import de.akra.idocit.ui.utils.DescribedItemUtils;

/**
 * {@link Composite} to manage {@link DescribedItem}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ManageAddresseeComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageAddresseeCompositeSelection>
{

	// Widgets
	private EditAddresseeListComposite editAddresseeListComposite;

	private EditAddresseeComposite editAddresseeComposite;

	// Listeners
	private ISelectionListener<EditAddresseeCompositeSelection> editAddresseeCompositeSelectionListener;

	private ISelectionListener<EditAddresseeListCompositeSelection> editAddresseeListCompositeSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public ManageAddresseeComposite(Composite parent)
	{
		super(parent, SWT.NONE, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void addAllListener()
	{
		editAddresseeComposite
				.addSelectionListener(editAddresseeCompositeSelectionListener);
		editAddresseeListComposite
				.addSelectionListener(editAddresseeListCompositeSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!

	}

	@Override
	protected void doSetSelection(ManageAddresseeCompositeSelection oldSelection,
			ManageAddresseeCompositeSelection newSelection)
	{
		// Update the EditAddresseeListComposite.
		EditAddresseeListCompositeSelection editItemListSelection = new EditAddresseeListCompositeSelection();
		editItemListSelection.setAddressees(newSelection.getAddressees());
		editItemListSelection.setMinNumberOfItems(1);

		Addressee activeAddressee = newSelection.getActiveAddressee();
		List<Addressee> activeAddressees = new ArrayList<Addressee>();

		if (activeAddressee != null)
		{
			activeAddressees.add(activeAddressee);
		}

		editItemListSelection.setActiveAddressees(activeAddressees);

		editAddresseeListComposite.setSelection(editItemListSelection);

		// Update the EditDescribedItemComposite.
		EditAddresseeCompositeSelection editAddresseeCompositeSelection = new EditAddresseeCompositeSelection();
		editAddresseeCompositeSelection.setAddressee(activeAddressee);

		if (activeAddressee != null)
		{
			Addressee modifiedItem = DescribedItemUtils.createNewAddressee();
			modifiedItem.setName(activeAddressee.getName());
			modifiedItem.setDescription(activeAddressee.getDescription());
			modifiedItem.setDefault(activeAddressee.isDefault());

			editAddresseeCompositeSelection.setModifiedAddressee(modifiedItem);
		}

		editAddresseeComposite.setSelection(editAddresseeCompositeSelection);
	}

	@Override
	protected void initGUI(Composite parent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Group grpEditList = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(grpEditList);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(grpEditList);
		grpEditList.setText("Defined Addressees:");

		editAddresseeListComposite = new EditAddresseeListComposite(grpEditList);

		Group grpEditDescribedItem = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(grpEditDescribedItem);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grpEditDescribedItem);
		grpEditDescribedItem.setText("Edit selected Addressee:");

		editAddresseeComposite = new EditAddresseeComposite(grpEditDescribedItem);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		editAddresseeCompositeSelectionListener = new ISelectionListener<EditAddresseeCompositeSelection>() {
			@Override
			public void selectionChanged(EditAddresseeCompositeSelection selection,
					PocUIComposite<EditAddresseeCompositeSelection> changedComposite)
			{
				// If this composite fires a change event, the active Addressee
				// has been modified. We have to update this composite.

				// Get the addressee to change.
				ManageAddresseeCompositeSelection editSelection = getSelection();
				Addressee activeAddressee = editSelection.getActiveAddressee();
				List<Addressee> addressees = editSelection.getAddressees();

				// Remove the old addressee.
				int addresseeIndex = addressees.indexOf(activeAddressee);
				addressees.remove(addresseeIndex);

				// Add the changed Addressee at the same position as the old one.
				Addressee changedAddressee = selection.getModifiedAddressee();
				addressees.add(addresseeIndex, changedAddressee);

				editSelection.setActiveAddressee(changedAddressee);

				// Update the selection.
				editSelection.setAddressees(addressees);
				setSelection(editSelection);

				fireChangeEvent();
			}
		};

		editAddresseeListCompositeSelectionListener = new ISelectionListener<EditAddresseeListCompositeSelection>() {
			@Override
			public void selectionChanged(EditAddresseeListCompositeSelection selection,
					PocUIComposite<EditAddresseeListCompositeSelection> changedComposite)
			{
				// If this composite fires a change event, a new active addressee has been
				// chosen.
				ManageAddresseeCompositeSelection editSelection = getSelection();

				if (!selection.getActiveAddressees().isEmpty())
				{
					editSelection.setActiveAddressee(selection.getActiveAddressees().get(
							0));
				}
				else
				{
					editSelection.setActiveAddressee(DescribedItemUtils
							.createNewAddressee());
				}

				setSelection(editSelection);
			}
		};
	}

	@Override
	protected void removeAllListener()
	{
		editAddresseeComposite
				.removeSelectionListener(editAddresseeCompositeSelectionListener);
		editAddresseeListComposite
				.removeSelectionListener(editAddresseeListCompositeSelectionListener);
	}
}
