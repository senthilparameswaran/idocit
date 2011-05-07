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
import org.eclipse.swt.widgets.Label;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.services.ThematicGridService;
import de.akra.idocit.structure.DescribedItem;
import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.Activator;
import de.akra.idocit.ui.utils.MessageBoxUtils;

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

	private Button btnExportGridsXml;

	private Button btnExportGridsHtml;

	// Listeners
	private ISelectionListener<EditThematicGridCompositeSelection> editThematicGridSelectionListener;

	private ISelectionListener<EditThematicGridListCompositeSelection> editThematicGridListSelectionListener;

	private SelectionListener btnImportListener;

	private SelectionListener btnExportListenerXml;

	private SelectionListener btnExportListenerHtml;

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
		btnExportGridsXml.addSelectionListener(btnExportListenerXml);
		btnExportGridsHtml.addSelectionListener(btnExportListenerHtml);
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

		btnExportGridsXml = new Button(btnComposite, SWT.PUSH);
		btnExportGridsXml.setText("Export Thematic Grids as XML");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnExportGridsXml);

		new Label(btnComposite, SWT.NONE);
		btnExportGridsHtml = new Button(btnComposite, SWT.PUSH);
		btnExportGridsHtml.setText("Export Thematic Grids as HTML");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnExportGridsHtml);
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

						List<ThematicRole> roles = ThematicGridService.collectThematicRoles(grids, selection.getRoles());
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

		btnExportListenerXml = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setText("Export Thematic Grids as XML-file");
				String[] filterExt = { "*.xml" };
				fileDialog.setFilterExtensions(filterExt);
				String selectedFileName = fileDialog.open();
				boolean stored = false;

				while ((selectedFileName != null) && !stored) {
					try {
						boolean exists = new File(selectedFileName).exists();
						boolean overwrite = exists
								&& MessageBoxUtils.openQuestionDialogBox(getShell(), "The file " + selectedFileName
										+ " already exists. Do you want to overwrite it?");

						if (overwrite || !exists) {
							ManageThematicGridsCompositeSelection selection = getSelection();
							PersistenceService.exportThematicGridsAsXml(new File(selectedFileName), selection.getThematicGrids());

							stored = true;
						} else {
							selectedFileName = fileDialog.open();
						}
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

		btnExportListenerHtml = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setText("Export Thematic Grids as HTML-file");
				String[] filterExt = { "*.html" };
				fileDialog.setFilterExtensions(filterExt);
				String selectedFileName = fileDialog.open();
				boolean stored = false;

				while ((selectedFileName != null) && !stored) {
					try {
						boolean exists = new File(selectedFileName).exists();
						boolean overwrite = exists
								&& MessageBoxUtils.openQuestionDialogBox(getShell(), "The file " + selectedFileName
										+ " already exists. Do you want to overwrite it?");

						if (overwrite || !exists) {
							ManageThematicGridsCompositeSelection selection = getSelection();
							PersistenceService.exportThematicGridsAsHtml(new File(selectedFileName), selection.getThematicGrids());

							stored = true;
						} else {
							selectedFileName = fileDialog.open();
						}
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
		btnExportGridsXml.removeSelectionListener(btnExportListenerXml);
		btnExportGridsHtml.removeSelectionListener(btnExportListenerHtml);
	}
}
