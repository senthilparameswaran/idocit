package de.akra.idocit.ui.composites;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.structure.DescribedItem;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.Activator;

/**
 * The composite to manage {@link ThematicGrid}s in the preference page.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ManageThematicGridsComposite extends
		AbsComposite<ManageThematicGridsCompositeAC, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection> {

	// Widgets
	private EditThematicGridListComposite editThematicGridListComposite;

	private EditThematicGridComposite editThematicGridComposite;

	private Button btnImportGrids;

	private Button btnExportGrids;

	// Listeners
	private ISelectionListener<EditThematicGridCompositeSelection> editThematicGridSelectionListener;

	private ISelectionListener<EditThematicGridListCompositeSelection> editThematicGridListSelectionListener;

	private SelectionListener btnImportListener;

	private SelectionListener btnExportListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 * @param actionConf
	 *            The action configuration
	 *            {@link ManageThematicGridsCompositeAC}.
	 */
	public ManageThematicGridsComposite(Composite parent, ManageThematicGridsCompositeAC actionConf) {
		super(parent, SWT.NONE, actionConf, EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void addAllListener() {
		editThematicGridComposite.addSelectionListener(editThematicGridSelectionListener);
		editThematicGridListComposite.addSelectionListener(editThematicGridListSelectionListener);

		btnImportGrids.addSelectionListener(btnImportListener);
		btnExportGrids.addSelectionListener(btnExportListener);
	}

	@Override
	protected void doCleanUp() {
		// Nothing to do!
	}

	@Override
	protected void doSetSelection(ManageThematicGridsCompositeSelection oldSelection, ManageThematicGridsCompositeSelection newSelection) {
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
		editThematicGridCompositeSelection.setThematicGrid(newSelection.getActiveThematicGrid());
		editThematicGridCompositeSelection.setExistingGrids(newSelection.getThematicGrids());

		editThematicGridComposite.setSelection(editThematicGridCompositeSelection);
	}

	@Override
	protected void initGUI(Composite arg0) throws CompositeInitializationException {
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Group grpEditList = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(grpEditList);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(grpEditList);
		grpEditList.setText("Defined Thematic Grids:");

		editThematicGridListComposite = new EditThematicGridListComposite(grpEditList);

		Group grpEditDescribedItem = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(grpEditDescribedItem);
		GridDataFactory.fillDefaults().span(1, 2).grab(true, true).applyTo(grpEditDescribedItem);
		grpEditDescribedItem.setText("Edit selected Thematic Grid:");

		editThematicGridComposite = new EditThematicGridComposite(grpEditDescribedItem);

		Composite btnComposite = new Composite(this, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(btnComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnComposite);

		btnImportGrids = new Button(btnComposite, SWT.PUSH);
		btnImportGrids.setText("Import Thematic Grids");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnImportGrids);

		btnExportGrids = new Button(btnComposite, SWT.PUSH);
		btnExportGrids.setText("Export Thematic Grids");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnExportGrids);
	}

	private List<ThematicRole> collectThematicRoles(List<ThematicGrid> grids, List<ThematicRole> existingRoles) {
		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.addAll(existingRoles);

		for (ThematicGrid grid : grids) {
			for (ThematicRole role : grid.getRoles().keySet()) {
				if (!roles.contains(role)) {
					roles.add(role);
				}
			}
		}

		return roles;
	}

	@Override
	protected void initListener() throws CompositeInitializationException {
		editThematicGridSelectionListener = new ISelectionListener<EditThematicGridCompositeSelection>() {
			@Override
			public void selectionChanged(EditThematicGridCompositeSelection selection, PocUIComposite<EditThematicGridCompositeSelection> composite) {
				ManageThematicGridsCompositeSelection mySelection = getSelection();
				mySelection.setActiveThematicGrid(selection.getThematicGrid());

				setSelection(mySelection);
			}
		};

		editThematicGridListSelectionListener = new ISelectionListener<EditThematicGridListCompositeSelection>() {
			@Override
			public void selectionChanged(EditThematicGridListCompositeSelection selection,
					PocUIComposite<EditThematicGridListCompositeSelection> composite) {
				ManageThematicGridsCompositeSelection mySelection = getSelection();
				List<ThematicGrid> selectedItems = selection.getActiveItems();

				if ((selectedItems != null) && !selectedItems.isEmpty()) {
					mySelection.setActiveThematicGrid((ThematicGrid) selectedItems.get(0));
				}

				List<ThematicGrid> grids = new ArrayList<ThematicGrid>();
				for (DescribedItem item : selection.getItems()) {
					grids.add((ThematicGrid) item);
				}
				mySelection.setThematicGrids(grids);

				setSelection(mySelection);
			}
		};

		btnImportListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
				fileDialog.setText("Import Thematic Grids");
				String[] filterExt = { "*.xml" };
				fileDialog.setFilterExtensions(filterExt);
				String selectedFileName = fileDialog.open();

				if (selectedFileName != null) {
					try {
						List<ThematicGrid> importedGrids = PersistenceService.importThematicGrids(new File(selectedFileName));

						ManageThematicGridsCompositeSelection selection = getSelection();
						List<ThematicGrid> grids = selection.getThematicGrids();

						if (grids == null) {
							grids = new ArrayList<ThematicGrid>();
						}

						grids.addAll(importedGrids);
						selection.setThematicGrids(importedGrids);

						List<ThematicRole> roles = collectThematicRoles(grids, selection.getRoles());
						selection.setRoles(roles);

						setSelection(selection);
					} catch (IOException ioEx) {
						Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, ioEx.getLocalizedMessage(), null);
						ErrorDialog.openError(Display.getCurrent().getActiveShell(), "iDocIt",
								"An error occured while importing thematic grids from the selected resource.", status);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		btnExportListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setText("Export Thematic Grids");
				String[] filterExt = { "*.xml" };
				fileDialog.setFilterExtensions(filterExt);
				String selectedFileName = fileDialog.open();

				if (selectedFileName != null) {
					try {
						ManageThematicGridsCompositeSelection selection = getSelection();
						PersistenceService.exportThematicGrids(new File(selectedFileName), selection.getThematicGrids());
					} catch (IOException ioEx) {
						Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, ioEx.getLocalizedMessage(), null);
						ErrorDialog.openError(Display.getCurrent().getActiveShell(), "iDocIt",
								"An error occured while importing thematic grids from the selected resource.", status);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}

	@Override
	protected void removeAllListener() {
		editThematicGridComposite.removeSelectionListener(editThematicGridSelectionListener);
		editThematicGridListComposite.removeSelectionListener(editThematicGridListSelectionListener);

		btnImportGrids.removeSelectionListener(btnImportListener);
		btnExportGrids.removeSelectionListener(btnExportListener);
	}
}
