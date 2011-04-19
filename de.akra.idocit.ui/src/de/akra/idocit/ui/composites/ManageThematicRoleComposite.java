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

import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.utils.DescribedItemUtils;

/**
 * {@link Composite} to manage {@link ThematicRole}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ManageThematicRoleComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection>
{

	// Widgets
	private EditThematicRoleListComposite editThematicRoleListComposite;

	private EditThematicRoleComposite editThematicRoleComposite;

	// Listeners
	private ISelectionListener<EditThematicRoleCompositeSelection> editThematicRoleCompositeSelectionListener;

	private ISelectionListener<EditThematicRoleListCompositeSelection> editThematicRoleListCompositeSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public ManageThematicRoleComposite(Composite parent)
	{
		super(parent, SWT.NONE,EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void addAllListener()
	{
		editThematicRoleComposite
				.addSelectionListener(editThematicRoleCompositeSelectionListener);
		editThematicRoleListComposite
				.addSelectionListener(editThematicRoleListCompositeSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!

	}

	@Override
	protected void doSetSelection(ManageThematicRoleCompositeSelection oldSelection,
			ManageThematicRoleCompositeSelection newSelection)
	{
		// Update the EditItemListComposite.
		EditThematicRoleListCompositeSelection editItemListSelection = new EditThematicRoleListCompositeSelection();
		editItemListSelection.setItems(newSelection.getThematicRoles());
		editItemListSelection.setMinNumberOfItems(1);

		ThematicRole activeRole = newSelection.getActiveThematicRole();
		List<ThematicRole> activeRoles = new ArrayList<ThematicRole>();

		if (activeRole != null)
		{
			activeRoles.add(activeRole);
		}

		editItemListSelection.setActiveItems(activeRoles);

		editThematicRoleListComposite.setSelection(editItemListSelection);

		// Update the EditDescribedItemComposite.
		EditThematicRoleCompositeSelection editAddresseeCompositeSelection = new EditThematicRoleCompositeSelection();
		editAddresseeCompositeSelection.setItem(activeRole);

		if (activeRole != null)
		{
			ThematicRole modifiedItem = DescribedItemUtils.createNewThematicRole();
			modifiedItem.setName(activeRole.getName());
			modifiedItem.setDescription(activeRole.getDescription());

			editAddresseeCompositeSelection.setModifiedItem(modifiedItem);
		}

		editThematicRoleComposite.setSelection(editAddresseeCompositeSelection);
	}

	@Override
	protected void initGUI(Composite parent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Group grpEditList = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(grpEditList);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(grpEditList);
		grpEditList.setText("Defined Thematic Roles:");

		editThematicRoleListComposite = new EditThematicRoleListComposite(grpEditList);

		Group grpEditDescribedItem = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(grpEditDescribedItem);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grpEditDescribedItem);
		grpEditDescribedItem.setText("Edit selected Thematic Role:");

		editThematicRoleComposite = new EditThematicRoleComposite(grpEditDescribedItem);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		editThematicRoleCompositeSelectionListener = new ISelectionListener<EditThematicRoleCompositeSelection>() {
			@Override
			public void selectionChanged(EditThematicRoleCompositeSelection selection,
					PocUIComposite<EditThematicRoleCompositeSelection> changedComposite)
			{
				// If this composite fires a change event, the active Addressee
				// as been modified. We have to update this composite.

				// Get the addressee to change.
				ManageThematicRoleCompositeSelection editSelection = getSelection();
				ThematicRole activeThematicRole = editSelection.getActiveThematicRole();
				List<ThematicRole> thematicRoles = editSelection.getThematicRoles();

				// Remove the old ThematicRole.
				int roleIndex = thematicRoles.indexOf(activeThematicRole);
				thematicRoles.remove(roleIndex);

				// Add the changed ThematicRole at the same position as the old one.
				ThematicRole changedAddressee = selection.getModifiedItem();
				thematicRoles.add(roleIndex, changedAddressee);

				editSelection.setActiveThematicRole(changedAddressee);

				// Update the selection.
				editSelection.setThematicRoles(thematicRoles);
				setSelection(editSelection);

				fireChangeEvent();
			}
		};

		editThematicRoleListCompositeSelectionListener = new ISelectionListener<EditThematicRoleListCompositeSelection>() {
			@Override
			public void selectionChanged(
					EditThematicRoleListCompositeSelection selection,
					PocUIComposite<EditThematicRoleListCompositeSelection> changedComposite)
			{
				// If this composite fires a change event, a new active addressee has been
				// chosen.
				ManageThematicRoleCompositeSelection editSelection = getSelection();

				if (!selection.getActiveItems().isEmpty())
				{
					editSelection
							.setActiveThematicRole(selection.getActiveItems().get(0));
				}
				else
				{
					editSelection.setActiveThematicRole(DescribedItemUtils
							.createNewThematicRole());
				}

				setSelection(editSelection);
			}
		};
	}

	@Override
	protected void removeAllListener()
	{
		editThematicRoleComposite
				.removeSelectionListener(editThematicRoleCompositeSelectionListener);
		editThematicRoleListComposite
				.removeSelectionListener(editThematicRoleListCompositeSelectionListener);
	}
}
