package de.akra.idocit.ui.composites;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.structure.ThematicGrid;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.utils.StringUtils;

/**
 * Composite to edit a {@link ThematicGrid}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class EditThematicGridComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicGridCompositeSelection>
{

	private static final int MOUSE_BUTTON_RIGHT = 3;
	private static final String TABLE_HEADER_THEMATIC_ROLE = "Thematic Role";
	private static final String TABLE_HEADER_STATUS = "Status";
	private static final String STATUS_MANDATORY = "mandatory";
	private static final String STATUS_OPTIONAL = "optional";

	private static final int ROLE_TABLE_COL_ROLE_NAME = 0;
	private static final int ROLE_TABLE_COL_STATUS = 1;

	/*
	 * Widgets
	 */
	private Text txtName;

	private Text txtDescription;

	private Text txtVerbs;

	private Table tabRoles;

	private TableItem[] items;

	private Menu tablePopUpMenu;
	private MenuItem menuItemSetMandatory;
	private MenuItem menuItemSetOptional;

	/*
	 * Listener
	 */
	private ModifyListener txtVerbModifyListener;

	private ModifyListener txtNameModifyListener;

	private ModifyListener txtDescriptionModifyListener;

	private Listener tableItemMouseListener;
	private MenuDetectListener tableMenuDetectListener;
	private MouseListener tableMouseListener;

	private SelectionListener menuItemMandatorySelectionListener;
	private SelectionListener menuItemOptionalSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public EditThematicGridComposite(Composite parent)
	{
		super(parent, SWT.NONE, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite arg0) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Label lblName = new Label(this, SWT.NONE);
		lblName.setText("Name:");

		txtName = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtName);

		Label lblDescription = new Label(this, SWT.NONE);
		lblDescription.setText("Description:");

		txtDescription = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(txtDescription);

		Label lblVerbs = new Label(this, SWT.NONE);
		lblVerbs.setText("Verbs (seperate verbs with commas):");

		txtVerbs = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtVerbs);

		Label lblRoles = new Label(this, SWT.NONE);
		lblRoles.setText("Associated Thematic Roles:");

		tabRoles = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().hint(200, 0).grab(true, true).applyTo(tabRoles);
		tabRoles.setHeaderVisible(true);
		tabRoles.setToolTipText("Use the context menu to change the status.");

		TableColumn colRoles = new TableColumn(tabRoles, SWT.NONE);
		colRoles.setText(TABLE_HEADER_THEMATIC_ROLE);

		TableColumn colMandatory = new TableColumn(tabRoles, SWT.NONE);
		colMandatory.setText(TABLE_HEADER_STATUS);
		colMandatory.setAlignment(SWT.CENTER);

		tablePopUpMenu = new Menu(this.getShell(), SWT.POP_UP);
		// tabRoles.setMenu(tablePopUpMenu);

		menuItemSetMandatory = new MenuItem(tablePopUpMenu, SWT.PUSH);
		menuItemSetMandatory.setText("Set mandatory");
		menuItemSetOptional = new MenuItem(tablePopUpMenu, SWT.CASCADE);
		menuItemSetOptional.setText("Set optional");
	}

	/**
	 * Checks if the coordinates (<code>x</code>, <code>y</code>) are in the range of the
	 * items in {@link #tabRoles}.
	 * 
	 * @param x
	 * @param y
	 * @return true, if the point (<code>x</code>, <code>y</code>) is in the range of the
	 *         items of the table.
	 */
	private boolean isInTableItemRange(int x, int y)
	{
		int yRange = tabRoles.getItemHeight() * tabRoles.getItemCount()
				+ (tabRoles.getHeaderVisible() ? tabRoles.getHeaderHeight() : 0);

		int xRange = 0;

		TableColumn[] columns = tabRoles.getColumns();
		for (int i = 0; i < columns.length; ++i)
		{
			xRange += columns[i].getWidth();
		}

		// if the table header is shown, then the y coordinate must not be in the header.
		return y > (tabRoles.getHeaderVisible() ? tabRoles.getHeaderHeight() : 0)
				&& x >= 0 && y <= yRange && x <= xRange;
	}

	@Override
	protected void doSetSelection(EditThematicGridCompositeSelection oldSelection,
			EditThematicGridCompositeSelection newSelection)
	{
		if (!newSelection.equals(oldSelection) && newSelection.getThematicGrid() != null)
		{
			String newName = newSelection.getThematicGrid().getName();
			String oldName = (oldSelection != null && oldSelection.getThematicGrid() != null) ? oldSelection
					.getThematicGrid().getName() : null;

			if (!newName.equals(oldName))
			{
				txtName.setText(newName);
			}

			String newDescription = newSelection.getThematicGrid().getDescription();
			String oldDescription = (oldSelection != null && oldSelection
					.getThematicGrid() != null) ? oldSelection.getThematicGrid()
					.getDescription() : null;

			if (!newDescription.equals(oldDescription))
			{
				txtDescription.setText(newDescription);
			}

			Set<String> newVerbs = newSelection.getThematicGrid().getVerbs();
			Set<String> oldVerbs = (oldSelection != null) ? oldSelection
					.getThematicGrid().getVerbs() : null;

			if (!newVerbs.equals(oldVerbs))
			{}
			txtVerbs.setText(StringUtils.convertIntoCommaSeperatedTokens(newVerbs));

			List<ThematicRole> newRoles = newSelection.getRoles();
			List<ThematicRole> oldRoles = (oldSelection != null) ? oldSelection
					.getRoles() : null;

			if (!newRoles.equals(oldRoles))
			{
				tabRoles.clearAll();
				if (items != null)
				{
					for (TableItem item : items)
					{
						item.dispose();
					}
				}

				items = new TableItem[newRoles.size()];

				for (int i = 0; i < newRoles.size(); i++)
				{
					ThematicRole role = newRoles.get(i);
					items[i] = new TableItem(tabRoles, SWT.NONE);
					items[i].setText(ROLE_TABLE_COL_ROLE_NAME, role.getName());

					// by default roles are mandatory
					items[i].setText(ROLE_TABLE_COL_STATUS, STATUS_MANDATORY);
					items[i].setData(role);
				}

				TableColumn[] tabCols = tabRoles.getColumns();
				for (int i = 0; i < tabCols.length; i++)
				{
					tabCols[i].pack();
				}
			}

			Map<ThematicRole, Boolean> newSelectedRoles = newSelection.getThematicGrid()
					.getRoles();
			List<ThematicRole> roles = newSelection.getRoles();

			int i = 0;
			Boolean isMandatory = true;
			for (ThematicRole role : roles)
			{
				if ((isMandatory = newSelectedRoles.get(role)) != null)
				{
					items[i].setChecked(true);
					if (!isMandatory)
					{
						items[i].setText(ROLE_TABLE_COL_STATUS, STATUS_OPTIONAL);
					}
				}
				i++;
			}
		}
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		txtVerbModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				ThematicGrid grid = selection.getThematicGrid();

				Set<String> verbs = StringUtils.convertIntoTokenSet(txtVerbs.getText(),
						",");
				grid.setVerbs(verbs);
				selection.setThematicGrid(grid);

				setSelection(selection);
				fireChangeEvent();
			}
		};

		txtNameModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				ThematicGrid grid = selection.getThematicGrid();
				grid.setName(txtName.getText());
				selection.setThematicGrid(grid);

				setSelection(selection);
				fireChangeEvent();
			}
		};

		txtDescriptionModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				ThematicGrid grid = selection.getThematicGrid();

				grid.setDescription(txtDescription.getText());

				selection.setThematicGrid(grid);

				setSelection(selection);
				fireChangeEvent();
			}
		};

		tableItemMouseListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				updateThematicGridInSelection();
				fireChangeEvent();
			}
		};

		tableMenuDetectListener = new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent e)
			{
				// TODO hide popup menu if user clicks on the column headers!

				// show popup menu if the context menu key on the keyboard is pressed and
				// an item is selected
				tablePopUpMenu.setVisible(tabRoles.getSelectionIndex() > -1);
			}
		};

		tableMouseListener = new MouseAdapter() {

			/**
			 * @see org.eclipse.swt.events.MouseAdapter#mouseUp(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseUp(MouseEvent e)
			{
				if (e.button == MOUSE_BUTTON_RIGHT)
				{
					tablePopUpMenu.setVisible(isInTableItemRange(e.x, e.y));
				}
			}
		};

		menuItemMandatorySelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				changeSelectedItemStatus(STATUS_MANDATORY);
				updateThematicGridInSelection();
				fireChangeEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		};

		menuItemOptionalSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				changeSelectedItemStatus(STATUS_OPTIONAL);
				updateThematicGridInSelection();
				fireChangeEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		};
	}

	/**
	 * Load all selected {@link ThematicRole}s into the {@link ThematicGrid} in the
	 * selection.
	 */
	private void updateThematicGridInSelection()
	{
		TableItem[] selectedItems = tabRoles.getItems();
		EditThematicGridCompositeSelection selection = getSelection();
		ThematicGrid grid = selection.getThematicGrid();

		Map<ThematicRole, Boolean> roles = grid.getRoles();
		roles.clear();

		for (TableItem item : selectedItems)
		{
			if (item.getChecked())
			{
				ThematicRole role = (ThematicRole) item.getData();
				roles.put(role,
						item.getText(ROLE_TABLE_COL_STATUS).equals(STATUS_MANDATORY));
			}
		}

		grid.setRoles(roles);
		selection.setThematicGrid(grid);

		setSelection(selection);
	}

	/**
	 * Sets the <code>status</code> to the current selected item in {@link #tabRoles}.
	 * 
	 * @param status
	 *            The status {@link #STATUS_MANDATORY} or {@link #STATUS_OPTIONAL} to set.
	 */
	private void changeSelectedItemStatus(String status)
	{
		int row = -1;
		if ((row = tabRoles.getSelectionIndex()) > -1)
		{
			tabRoles.getItem(row).setText(ROLE_TABLE_COL_STATUS, status);
		}
	}

	@Override
	protected void addAllListener()
	{
		txtName.addModifyListener(txtNameModifyListener);
		txtVerbs.addModifyListener(txtVerbModifyListener);
		txtDescription.addModifyListener(txtDescriptionModifyListener);
		tabRoles.addListener(SWT.Selection, tableItemMouseListener);
		tabRoles.addMenuDetectListener(tableMenuDetectListener);
		tabRoles.addMouseListener(tableMouseListener);
		menuItemSetMandatory.addSelectionListener(menuItemMandatorySelectionListener);
		menuItemSetOptional.addSelectionListener(menuItemOptionalSelectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		txtName.removeModifyListener(txtNameModifyListener);
		txtVerbs.removeModifyListener(txtVerbModifyListener);
		txtDescription.removeModifyListener(txtDescriptionModifyListener);
		tabRoles.removeListener(SWT.Selection, tableItemMouseListener);
		tabRoles.removeMenuDetectListener(tableMenuDetectListener);
		tabRoles.removeMouseListener(tableMouseListener);
		menuItemSetMandatory.removeSelectionListener(menuItemMandatorySelectionListener);
		menuItemSetOptional.removeSelectionListener(menuItemOptionalSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}
}
