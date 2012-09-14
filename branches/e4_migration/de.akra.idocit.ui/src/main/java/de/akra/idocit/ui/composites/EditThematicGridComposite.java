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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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

import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.services.RuleService;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;

/**
 * Composite to edit a {@link ThematicGrid}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicGridComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicGridCompositeSelection>
{

	private static final int MOUSE_BUTTON_RIGHT = 3;
	private static final String TABLE_HEADER_THEMATIC_ROLE = "Thematic Role";
	private static final String TABLE_HEADER_STATUS = "Status";
	private static final String TABLE_HEADER_RULE = "Show Role when ...";
	private static final String STATUS_MANDATORY = "mandatory";
	private static final String STATUS_OPTIONAL = "optional";

	private static final int ROLE_TABLE_COL_ROLE_NAME = 0;
	private static final int ROLE_TABLE_COL_STATUS = 1;
	private static final int ROLE_TABLE_COL_RULE = 2;

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
	private MenuItem menuItemEditRule;

	/*
	 * Listener
	 */
	private ModifyListener txtVerbModifyListener;

	private ModifyListener txtNameModifyListener;

	private ModifyListener txtDescriptionModifyListener;

	private Listener tableItemMouseListener;
	private MenuDetectListener tableMenuDetectListener;
	private MouseListener tableMouseListener;

	private SelectionListener tabRolesSelectionListener;
	private SelectionListener menuItemMandatorySelectionListener;
	private SelectionListener menuItemOptionalSelectionListener;
	private SelectionListener menuItemEditRuleSelectionListener;

	private IInputValidator ruleValidator;

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
		GridDataFactory.fillDefaults().hint(100, 50).grab(true, true).applyTo(txtDescription);

		Label lblVerbs = new Label(this, SWT.NONE);
		lblVerbs.setText("Verbs (seperate verbs with commas):");

		txtVerbs = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridDataFactory.fillDefaults().hint(100, 120).grab(true, true).applyTo(txtVerbs);

		Label lblRoles = new Label(this, SWT.NONE);
		lblRoles.setText("Associated Thematic Roles:");

		Label lblHintContextMenu = new Label(this, SWT.NONE);
		lblHintContextMenu
				.setText("Please note: you can edit further attributes via a right click on the table.");
		lblHintContextMenu.setForeground(getDisplay()
				.getSystemColor(SWT.COLOR_DARK_GREEN));

		tabRoles = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().hint(500, 200).grab(true, true).applyTo(tabRoles);
		tabRoles.setHeaderVisible(true);
		tabRoles.setToolTipText("Use the context menu to change the status.");

		TableColumn colRoles = new TableColumn(tabRoles, SWT.LEFT);
		colRoles.setText(TABLE_HEADER_THEMATIC_ROLE);

		TableColumn colMandatory = new TableColumn(tabRoles, SWT.CENTER);
		colMandatory.setText(TABLE_HEADER_STATUS);
		colMandatory.setWidth(80);

		TableColumn colRule = new TableColumn(tabRoles, SWT.LEFT);
		colRule.setText(TABLE_HEADER_RULE);
		colRule.setWidth(300);

		tablePopUpMenu = new Menu(this.getShell(), SWT.POP_UP);
		// tabRoles.setMenu(tablePopUpMenu);

		menuItemSetMandatory = new MenuItem(tablePopUpMenu, SWT.PUSH);
		menuItemSetMandatory.setText("Set mandatory");
		menuItemSetOptional = new MenuItem(tablePopUpMenu, SWT.CASCADE);
		menuItemSetOptional.setText("Set optional");
		menuItemEditRule = new MenuItem(tablePopUpMenu, SWT.PUSH);
		menuItemEditRule.setText("Edit rule");
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
			EditThematicGridCompositeSelection newSelection, Object sourceControl)
	{
		if (!newSelection.equals(oldSelection)
				&& newSelection.getActiveThematicGrid() != null)
		{
			String newName = newSelection.getActiveThematicGrid().getName();

			if ((sourceControl == null)
					|| ((sourceControl != null) && (!sourceControl.equals(txtName))))
			{
				txtName.setText(newName);
			}

			String newDescription = newSelection.getActiveThematicGrid().getDescription();

			if ((sourceControl == null)
					|| ((sourceControl != null) && (!sourceControl.equals(txtDescription))))
			{
				txtDescription.setText(newDescription);
			}

			Set<String> newVerbs = newSelection.getActiveThematicGrid().getVerbs();

			if ((sourceControl == null)
					|| ((sourceControl != null) && (!sourceControl.equals(txtVerbs))))
			{
				txtVerbs.setText(StringUtils.convertIntoCommaSeperatedTokens(newVerbs));
			}

			List<ThematicRole> newRoles = newSelection.getRoles();
			List<ThematicRole> oldRoles = (oldSelection != null) ? oldSelection
					.getRoles() : null;

			// rebuild table if role list differs or the table is not initialized
			if (!newRoles.equals(oldRoles) || items == null)
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

				tabRoles.getColumns()[0].pack(); // Column "Thematic Role"
			}
			// Changes due to Issue #27
			else
			{
				// uncheck all items in the table and set status to default
				for (int i = 0; i < items.length; ++i)
				{
					items[i].setChecked(false);
					items[i].setText(ROLE_TABLE_COL_STATUS, STATUS_MANDATORY);
					items[i].setText(ROLE_TABLE_COL_RULE, StringUtils.EMPTY);
				}
			}
			// End changes due to Issue #27

			Map<ThematicRole, Boolean> newSelectedRoles = newSelection
					.getActiveThematicGrid().getRoles();
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

				ThematicGrid activeGrid = newSelection.getActiveThematicGrid();
				if (activeGrid != null)
				{
					Map<String, String> gridBasedRules = activeGrid.getGridBasedRules();
					Map<ThematicRole, Boolean> checkedThematicRoles = activeGrid
							.getRoles();

					if (gridBasedRules != null)
					{
						String rule = gridBasedRules.get(role.getName());

						if ((rule != null) && checkedThematicRoles.containsKey(role))
						{
							items[i].setText(ROLE_TABLE_COL_RULE, rule);
						}
						else
						{
							items[i].setText(ROLE_TABLE_COL_RULE, StringUtils.EMPTY);
						}

					}
				}

				i++;
			}

			ThematicRole activeRole = newSelection.getActiveRole();
			if (activeRole != null)
			{
				int selectionIndex = roles.indexOf(activeRole);
				tabRoles.select(selectionIndex);
			}

			tablePopUpMenu.setEnabled((activeRole != null)
					&& (newSelectedRoles.containsKey(activeRole)));
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
				ThematicGrid grid = selection.getActiveThematicGrid();

				Set<String> verbs = StringUtils.convertIntoTokenSet(txtVerbs.getText(),
						",");
				grid.setVerbs(verbs);
				selection = selection.setActiveThematicGrid(grid);

				setSelection(selection, txtVerbs);
				fireChangeEvent(txtVerbs);
			}
		};

		txtNameModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				ThematicGrid grid = selection.getActiveThematicGrid();
				grid.setName(txtName.getText());
				selection = selection.setActiveThematicGrid(grid);

				setSelection(selection, txtName);
				fireChangeEvent(txtName);
			}
		};

		txtDescriptionModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				ThematicGrid grid = selection.getActiveThematicGrid();
				grid.setDescription(txtDescription.getText());
				selection = selection.setActiveThematicGrid(grid);

				setSelection(selection, txtDescription);
				fireChangeEvent(txtDescription);
			}
		};

		tableItemMouseListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				updateThematicGridInSelection();
				fireChangeEvent(tabRoles);
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
				fireChangeEvent(tabRoles);
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
				fireChangeEvent(tabRoles);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		};

		ruleValidator = new IInputValidator() {

			@Override
			public String isValid(String rule)
			{
				boolean isValid = RuleService.isRuleValid(rule);

				return isValid ? null : "Rule has invalid syntax. Please correct it.";
			}
		};

		menuItemEditRuleSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				ThematicRole selectedRole = getSelection().getActiveRole();

				if (selectedRole != null)
				{
					EditThematicGridCompositeSelection selection = getSelection();
					ThematicGrid activeGrid = selection.getActiveThematicGrid();
					String gridBasedRule = activeGrid.getGridBasedRules().get(
							selectedRole.getName());

					InputDialog ruleInputDialog = new InputDialog(
							getShell(),
							StringUtils.EMPTY,
							"Please specify when the role "
									+ selectedRole.getName()
									+ " should be recommended in the thematic grid "
									+ activeGrid.getName()
									+ ".\n\nYou can use the following predicates (in JavaScript-Syntax):\nalways()\nisSingular(\"ROLENAME\")\nisPlural(\"ROLENAME\")\nexists(\"ROLENAME\")\nhasAttributes(\"ROLENAME\")\nisPredicate(\"VERB\")",
							gridBasedRule, ruleValidator);

					int dialogResult = ruleInputDialog.open();

					if (dialogResult == InputDialog.OK)
					{
						activeGrid.getGridBasedRules().put(selectedRole.getName(),
								ruleInputDialog.getValue());
						selection = selection.setActiveThematicGrid(activeGrid);

						setSelection(selection);

						fireChangeEvent(tabRoles);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				widgetSelected(arg0);
			}
		};

		tabRolesSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				EditThematicGridCompositeSelection selection = getSelection();
				int selectionIndex = tabRoles.getSelectionIndex();

				if (selectionIndex >= 0)
				{
					ThematicRole selectedRole = getSelection().getRoles().get(
							selectionIndex);

					selection = selection.setActiveRole(selectedRole);
				}
				else
				{
					selection.setActiveRole(null);
				}

				setSelection(selection);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				widgetSelected(arg0);
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
		ThematicGrid grid = selection.getActiveThematicGrid();

		Map<ThematicRole, Boolean> roles = grid.getRoles();
		roles.clear();

		Map<String, String> gridBasedRules = grid.getGridBasedRules();
		Map<String, String> newGridBasedRules = new HashMap<String, String>();

		for (TableItem item : selectedItems)
		{
			if (item.getChecked())
			{
				ThematicRole role = (ThematicRole) item.getData();
				roles.put(role,
						item.getText(ROLE_TABLE_COL_STATUS).equals(STATUS_MANDATORY));

				if (gridBasedRules.containsKey(role.getName()))
				{
					newGridBasedRules.put(role.getName(),
							gridBasedRules.get(role.getName()));
				}
				else
				{
					newGridBasedRules.put(role.getName(),
							ThematicGridConstants.DEFAULT_RULE);
				}
			}
		}

		grid.setRoles(roles);
		grid.setGridBasedRules(newGridBasedRules);
		selection = selection.setActiveThematicGrid(grid);

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
		tabRoles.addSelectionListener(tabRolesSelectionListener);
		menuItemSetMandatory.addSelectionListener(menuItemMandatorySelectionListener);
		menuItemSetOptional.addSelectionListener(menuItemOptionalSelectionListener);
		menuItemEditRule.addSelectionListener(menuItemEditRuleSelectionListener);
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
		tabRoles.removeSelectionListener(tabRolesSelectionListener);
		menuItemSetMandatory.removeSelectionListener(menuItemMandatorySelectionListener);
		menuItemSetOptional.removeSelectionListener(menuItemOptionalSelectionListener);
		menuItemEditRule.removeSelectionListener(menuItemEditRuleSelectionListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}
}
