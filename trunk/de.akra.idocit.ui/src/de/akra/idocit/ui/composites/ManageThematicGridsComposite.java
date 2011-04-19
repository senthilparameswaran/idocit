package de.akra.idocit.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.structure.DescribedItem;
import de.akra.idocit.structure.ThematicGrid;

/**
 * The composite to manage {@link ThematicGrid}s in the preference page.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ManageThematicGridsComposite
		extends
		AbsComposite<ManageThematicGridsCompositeAC, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection>
{

	// Widgets
	private EditThematicGridListComposite editThematicGridListComposite;

	private EditThematicGridComposite editThematicGridComposite;

	// Listeners
	private ISelectionListener<EditThematicGridCompositeSelection> editThematicGridSelectionListener;

	private ISelectionListener<EditThematicGridListCompositeSelection> editThematicGridListSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 * @param actionConf
	 *            The action configuration {@link ManageThematicGridsCompositeAC}.
	 */
	public ManageThematicGridsComposite(Composite parent,
			ManageThematicGridsCompositeAC actionConf)
	{
		super(parent, SWT.NONE, actionConf, EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void addAllListener()
	{
		editThematicGridComposite.addSelectionListener(editThematicGridSelectionListener);
		editThematicGridListComposite
				.addSelectionListener(editThematicGridListSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}

	@Override
	protected void doSetSelection(ManageThematicGridsCompositeSelection oldSelection,
			ManageThematicGridsCompositeSelection newSelection)
	{
		EditThematicGridListCompositeSelection editItemListCompositeSelection = new EditThematicGridListCompositeSelection();
		editItemListCompositeSelection.setMinNumberOfItems(1);

		editItemListCompositeSelection.setItems(newSelection.getThematicGrids());

		List<ThematicGrid> activeItems = new ArrayList<ThematicGrid>();
		activeItems.add(newSelection.getActiveThematicGrid());
		editItemListCompositeSelection.setActiveItems(activeItems);

		editThematicGridListComposite.setSelection(editItemListCompositeSelection);

		/**********************************************************************/

		EditThematicGridCompositeSelection editThematicGridCompositeSelection = new EditThematicGridCompositeSelection();
		editThematicGridCompositeSelection.setRoles(newSelection.getRoles());
		editThematicGridCompositeSelection.setThematicGrid(newSelection
				.getActiveThematicGrid());
		editThematicGridCompositeSelection.setExistingGrids(newSelection
				.getThematicGrids());

		editThematicGridComposite.setSelection(editThematicGridCompositeSelection);
	}

	@Override
	protected void initGUI(Composite arg0) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Group grpEditList = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(grpEditList);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(grpEditList);
		grpEditList.setText("Defined Thematic Grids:");

		editThematicGridListComposite = new EditThematicGridListComposite(grpEditList);

		Group grpEditDescribedItem = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(grpEditDescribedItem);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grpEditDescribedItem);
		grpEditDescribedItem.setText("Edit selected Thematic Grid:");

		editThematicGridComposite = new EditThematicGridComposite(grpEditDescribedItem);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		editThematicGridSelectionListener = new ISelectionListener<EditThematicGridCompositeSelection>() {
			@Override
			public void selectionChanged(EditThematicGridCompositeSelection selection,
					PocUIComposite<EditThematicGridCompositeSelection> composite)
			{
				ManageThematicGridsCompositeSelection mySelection = getSelection();
				mySelection.setActiveThematicGrid(selection.getThematicGrid());

				setSelection(mySelection);
			}
		};

		editThematicGridListSelectionListener = new ISelectionListener<EditThematicGridListCompositeSelection>() {
			@Override
			public void selectionChanged(
					EditThematicGridListCompositeSelection selection,
					PocUIComposite<EditThematicGridListCompositeSelection> composite)
			{
				ManageThematicGridsCompositeSelection mySelection = getSelection();
				List<ThematicGrid> selectedItems = selection.getActiveItems();

				if ((selectedItems != null) && !selectedItems.isEmpty())
				{
					mySelection
							.setActiveThematicGrid((ThematicGrid) selectedItems.get(0));
				}

				List<ThematicGrid> grids = new ArrayList<ThematicGrid>();
				for (DescribedItem item : selection.getItems())
				{
					grids.add((ThematicGrid) item);
				}
				mySelection.setThematicGrids(grids);

				setSelection(mySelection);
			}
		};
	}

	@Override
	protected void removeAllListener()
	{
		editThematicGridComposite
				.removeSelectionListener(editThematicGridSelectionListener);
		editThematicGridListComposite
				.removeSelectionListener(editThematicGridListSelectionListener);
	}
}
