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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.constants.ThematicRoleConstants;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.DescribedItemNameComparator;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * Composite that displays a {@link Documentation} for editing.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class DocumentItemComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection>
{
	private static final String ITEM_DATA_KEY = "Addressee";

	private static final int MIN_HEIGHT = 120;

	private static final int MIN_WIDTH = 120;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger
			.getLogger(DocumentItemComposite.class.getName());

	private TabFolder addresseeTabFolder;

	private Button btnThematicRole;

	private Menu addresseesMenu;

	private TabItem addAddresseeTab;

	private TabItem removeAddresseeTab;

	private Menu rolePopUpMenu;

	private Button checkBoxErrorCase;

	private MenuItem[] roleFirstLevelItems;

	private MenuItem[] roleSecondLevelItems;

	/**
	 * {@link LinkedHashMap} from Addressee to a {@link Text}-field, containing the
	 * documentation for the Addressee. The map entries are insertion-ordered by using the
	 * iterator.
	 */
	private Map<Addressee, Text> addresseeDocTextField;

	/**
	 * {@link FocusListener} for the {@link Text} fields.
	 */
	private FocusListener textFocusListener;

	/**
	 * {@link SelectionListener} for <code>btnThematicRole</code>.
	 */
	private SelectionListener btnThematicRoleSelectionListener;

	/**
	 * {@link SelectionListener} for <code>checkBoxErrorCase</code>.
	 */
	private SelectionListener checkBoxErrorCaseSelectionListener;

	/**
	 * {@link MenuListener} for the menu to add addressee-tabs.
	 */
	private MenuListener addresseesMenuListener;

	/**
	 * {@link SelectionListener} to show the {@link #addresseesMenu} if the
	 * {@link #addAddresseeTab} is selected.
	 */
	private SelectionListener addresseeTabFolderSelectionListener;

	/**
	 * Listener to update the active docu text while the user is typing.
	 */
	private ModifyListener textModifyListener;

	/**
	 * Thread to fire the change events after a predefined interval, if the focus is still
	 * on a text field and modifications were made.
	 */
	private Thread textModifyCheckThread;

	/**
	 * Timestamp of the last modification time of the focused text field.
	 */
	private long lastTextModification;

	/**
	 * True, if the focus is on a documentation text field.
	 */
	private boolean isInTextField = false;

	/**
	 * Selection Listener for Menu items in role-menu
	 */
	private SelectionListener roleMenuItemSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param pvParent
	 * @param pvStyle
	 * @throws CompositeInitializationException
	 */
	public DocumentItemComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).minSize(MIN_WIDTH, MIN_HEIGHT)
				.applyTo(this);

		// create container for the two comboboxes
		Composite container = new Composite(pvParent, SWT.NONE);
		RowLayoutFactory.fillDefaults().type(SWT.HORIZONTAL).wrap(false)
				.applyTo(container);

		// create container for thematic role information
		Composite themRoleContainer = new Composite(container, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(themRoleContainer);

		Label labelThematicRole = new Label(themRoleContainer, SWT.NONE);
		labelThematicRole.setText("Thematic Role:");
		btnThematicRole = new Button(themRoleContainer, SWT.PUSH);

		checkBoxErrorCase = new Button(themRoleContainer, SWT.CHECK);
		checkBoxErrorCase.setText("Documents an Error Case");

		// create tab folder
		addresseeTabFolder = new TabFolder(pvParent, SWT.NONE | SWT.RESIZE);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
				.applyTo(addresseeTabFolder);
		addresseeDocTextField = new LinkedHashMap<Addressee, Text>();

		addresseesMenu = new Menu(this.getShell(), SWT.POP_UP);

		rolePopUpMenu = new Menu(this.getShell(), SWT.POP_UP);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		textFocusListener = new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e)
			{
				super.focusLost(e);
				if (updateDocForActiveAddressee((Text) e.widget))
				{
					fireChangeEvent((Text) e.widget);
				}
				isInTextField = false;
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				super.focusGained(e);
				isInTextField = true;
				lastTextModification = System.currentTimeMillis();
			}
		};

		textModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e)
			{
				lastTextModification = System.currentTimeMillis();
			}
		};

		textModifyCheckThread = new Thread() {

			private final long checkInterval = 1500;

			@Override
			public void run()
			{
				try
				{
					while (!isInterrupted())
					{
						sleep(checkInterval);
						long intervalDiff = System.currentTimeMillis()
								- lastTextModification;
						if (isInTextField && intervalDiff >= checkInterval)
						{
							if (!isDisposed())
							{
								// start a terminating thread in SWT thread handling to be
								// allowed to access the widgets
								getDisplay().asyncExec(new Runnable() {
									public void run()
									{
										updateDocForActiveAddressee();
									}
								});
							}
						}
					}
				}
				catch (InterruptedException e)
				{
					logger.log(Level.WARNING, "textModifyCheckThread is interrupted", e);
				}
			}
		};
		textModifyCheckThread.start();

		btnThematicRoleSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				rolePopUpMenu.setVisible(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}

		};

		roleMenuItemSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				ThematicRole role = (ThematicRole) ((MenuItem) arg0.getSource())
						.getData();
				DocumentItemCompositeSelection selection = getSelection();

				Documentation documentation = selection.getDocumentation();
				documentation.setThematicRole(role);

				if (!ThematicRoleUtils.isRoleFailable(role))
				{
					documentation.setErrorCase(false);
				}

				selection.setDocumentation(documentation);

				setSelection(selection);
				fireChangeEvent(btnThematicRole);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				widgetSelected(arg0);
			}
		};

		addresseesMenuListener = new MenuListener() {

			@Override
			public void menuShown(MenuEvent e)
			{
				// delete menu items
				MenuItem[] items = addresseesMenu.getItems();
				for (MenuItem item : items)
				{
					removeAllSelectionListeners(item);
					item.dispose();
				}

				/*
				 * add not shown addressees alphabetically ordered to menu, so they can be
				 * added
				 */
				List<Addressee> allAddressees = getSelection().getAddresseeList();
				List<Addressee> displayedAddressees = getSelection()
						.getDisplayedAddressees();
				Set<Addressee> orderedNotDisplayedAddressees = new TreeSet<Addressee>(
						DescribedItemNameComparator.getInstance());

				for (Addressee addressee : allAddressees)
				{
					if (!displayedAddressees.contains(addressee))
					{
						orderedNotDisplayedAddressees.add(addressee);
					}
				}

				for (Addressee addressee : orderedNotDisplayedAddressees)
				{
					MenuItem item = new MenuItem(addresseesMenu, SWT.PUSH);
					item.setText(addressee.getName());
					item.setData(ITEM_DATA_KEY, addressee);
					item.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e)
						{
							// add new addressee tab
							Addressee addressee = (Addressee) e.widget
									.getData(ITEM_DATA_KEY);
							addAddresseeTab(getSelection().getDisplayedAddressees(),
									addressee);

							// set focus on new tab.
							int newSelIndex = getSelection().getDisplayedAddressees()
									.size() - 1;
							addresseeTabFolder.setSelection(newSelIndex);
							getSelection().setActiveAddressee(newSelIndex);

							fireChangeEvent(e.widget);
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e)
						{}
					});
				}
			}

			/**
			 * Removes all {@link SelectionListener}s added to the <code>item</code>.
			 * 
			 * @param item
			 *            The {@link MenuItem} from which all SelectionListeners should be
			 *            removed.
			 */
			private void removeAllSelectionListeners(MenuItem item)
			{
				Listener[] listeners = item.getListeners(SWT.Selection);
				for (Listener l : listeners)
				{
					item.removeListener(SWT.Selection, l);
				}
			}

			@Override
			public void menuHidden(MenuEvent e)
			{}
		};

		addresseeTabFolderSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (addresseeTabFolder.getItem(addresseeTabFolder.getSelectionIndex()) == addAddresseeTab)
				{
					addresseesMenu.setVisible(true);

					// select the last selected tab, so the add-addressee-tab is
					// nevern shown.
					addresseeTabFolder.setSelection(getSelection().getActiveAddressee());
				}
				else if (addresseeTabFolder.getItem(addresseeTabFolder
						.getSelectionIndex()) == removeAddresseeTab)
				{
					// select the last selected tab, so the add-addressee-tab is
					// never shown.
					addresseeTabFolder.setSelection(getSelection().getActiveAddressee());

					// one documentation must be left
					if (getSelection().getDisplayedAddressees().size() > 1
							&& MessageBoxUtils
									.openQuestionDialogBox(getShell(),
											"Remove the selected addressee tab together with the inserted text?"))
					{
						// remove all occurrences where something is stored
						// about that addresses's documentation
						TabItem selItem = addresseeTabFolder.getItem(addresseeTabFolder
								.getSelectionIndex());
						Addressee addressee = (Addressee) selItem.getData(ITEM_DATA_KEY);
						addresseeDocTextField.remove(addressee);
						getSelection().getDisplayedAddressees().remove(addressee);
						getSelection().getDocumentation().getDocumentation()
								.remove(addressee);
						getSelection().getDocumentation().getAddresseeSequence()
								.remove(addressee);

						// save index of the tab next to the removed one
						int lastSelIndex = getSelection().getActiveAddressee();
						getSelection().setActiveAddressee(
								lastSelIndex > 0 ? lastSelIndex - 1 : lastSelIndex);

						// The item must be disposed after setSelection, because otherwise
						// this listener would be triggered with the next selected
						// TabItem.
						selItem.dispose();

						fireChangeEvent(e.widget);
					}
				}
				else
				{
					// save new active addressee
					getSelection().setActiveAddressee(
							addresseeTabFolder.getSelectionIndex());
					fireChangeEvent(e.widget);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};

		checkBoxErrorCaseSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DocumentItemCompositeSelection selection = getSelection();
				Documentation doc = selection.getDocumentation();
				doc.setErrorCase(checkBoxErrorCase.getSelection());
				selection.setDocumentation(doc);

				setSelection(selection);

				fireChangeEvent(e.widget);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};
	}

	/**
	 * Updates the current active addressee documentation.
	 * 
	 * @param text
	 *            The text field of the active addressee tab.
	 * @return true, if the documentation text were changed and the fireChangeEvent has to
	 *         be fired.
	 */
	private synchronized boolean updateDocForActiveAddressee(Text text)
	{
		Addressee activeAddressee = (Addressee) addresseeTabFolder.getItem(
				addresseeTabFolder.getSelectionIndex()).getData(ITEM_DATA_KEY);
		return updateDocForActiveAddressee(text, activeAddressee);
	}

	/**
	 * Updates the current active addressee documentation and fires the fireChangeEvent if
	 * the documentation text were changed.
	 */
	private synchronized void updateDocForActiveAddressee()
	{
		Addressee activeAddressee = (Addressee) addresseeTabFolder.getItem(
				addresseeTabFolder.getSelectionIndex()).getData(ITEM_DATA_KEY);
		Text text = addresseeDocTextField.get(activeAddressee);

		if (updateDocForActiveAddressee(text, activeAddressee))
		{
			fireChangeEvent(addresseeTabFolder);
		}
	}

	/**
	 * Updates the current active addressee documentation.
	 * 
	 * @param text
	 *            The text field of the active addressee.
	 * @param activeAddressee
	 *            the active {@link Addressee}.
	 * @return true, if the documentation text were changed and the fireChangeEvent has to
	 *         be fired.
	 */
	private synchronized boolean updateDocForActiveAddressee(Text text,
			Addressee activeAddressee)
	{
		boolean changed = false;
		DocumentItemCompositeSelection selection = getSelection();

		// update documentation for the addressee
		Documentation documentation = selection.getDocumentation();
		Map<Addressee, String> docMap = documentation.getDocumentation();

		if (docMap.get(activeAddressee) == null
				|| !docMap.get(activeAddressee).equals(text.getText()))
		{
			changed = true;
			docMap.put(activeAddressee, text.getText());

			// add addressee to sequence, if not already added
			if (!documentation.getAddresseeSequence().contains(activeAddressee))
			{
				documentation.getAddresseeSequence().add(activeAddressee);
			}
		}
		return changed;
	}

	@Override
	protected void doSetSelection(DocumentItemCompositeSelection oldInSelection,
			DocumentItemCompositeSelection newInSelection, Object sourceControl)
	{
		// create tabs if addressee list changed, in general by first use
		if (oldInSelection == null
				|| !newInSelection.getAddresseeList().equals(
						oldInSelection.getAddresseeList()))
		{
			if (newInSelection.getDisplayedAddressees().isEmpty())
			{
				initDefaultAddresseeTabs(newInSelection.getDisplayedAddressees(),
						newInSelection.getAddresseeList());
			}
			else
			{
				initDisplayedAddressees(newInSelection.getDisplayedAddressees(),
						newInSelection.getDisplayedAddressees());
			}
			appendAddAddresseeTab();
			appendRemoveAddresseeTab();
		}

		// create combobox items if thematic role list changed, in general by
		// first use
		if (oldInSelection == null
				|| !newInSelection.getRolesRecommendations().equals(
						oldInSelection.getRolesRecommendations()))
		{
			initThematicRolesCtrl(newInSelection.getRolesRecommendations());
		}

		Documentation doc = newInSelection.getDocumentation();

		if (doc != null)
		{
			if (doc.getThematicRole() != null)
			{
				btnThematicRole.setText(doc.getThematicRole().getName());
			}
			else
			{
				btnThematicRole.setText("No role selected");
			}

			boolean isRoleFailable = ThematicRoleUtils.isRoleFailable(doc
					.getThematicRole());
			checkBoxErrorCase.setEnabled(isRoleFailable);
			checkBoxErrorCase.setSelection(isRoleFailable && doc.isErrorCase());

			/*
			 * set the documentation text for addressees
			 */
			Map<Addressee, String> docMap = doc.getDocumentation();

			// use always the same sequence
			for (Addressee addressee : doc.getAddresseeSequence())
			{
				Text text = addresseeDocTextField.get(addressee);

				if (text == null)
				{
					// if addressee-tab not existing, append it
					text = addAddresseeTab(newInSelection.getDisplayedAddressees(),
							addressee);
				}

				// set documentation to text field
				text.setText(docMap.get(addressee));
			}

			int selectionIndex = newInSelection.getActiveAddressee();
			if (selectionIndex < 0)
			{
				// do default tab selection
				// find first addressee tab with documentation
				Iterator<Entry<Addressee, Text>> iter = addresseeDocTextField.entrySet()
						.iterator();
				for (int i = 0; iter.hasNext() && selectionIndex < 0; ++i)
				{
					Entry<Addressee, Text> entry = iter.next();
					if (docMap.get(entry.getKey()) != null)
					{
						selectionIndex = i;
					}
				}
			}
			addresseeTabFolder.setSelection(selectionIndex);
			newInSelection.setActiveAddressee(selectionIndex);
		}
		else
		{
			logger.log(Level.INFO, "No documentation found.");
		}
	}

	/**
	 * Creates the tabs for all addressees from the list whose attribute
	 * <code>isDefault</code> is <code>true</code>.
	 * 
	 * @param displayedAddressees
	 *            The list of all displayed Addressees. The new <code>addressees</code>
	 *            are added also to this list.
	 * @param addressees
	 *            The {@link Addressee}s to add.
	 */
	private void initDefaultAddresseeTabs(List<Addressee> displayedAddressees,
			List<Addressee> addressees)
	{
		for (Addressee addressee : addressees)
		{
			if (addressee.isDefault())
			{
				addAddresseeTab(displayedAddressees, addressee);
			}
		}
	}

	/**
	 * Creates the tabs for all addressees from the list.
	 * 
	 * @param displayedAddressees
	 *            The list of all displayed Addressees. The new <code>addressees</code>
	 *            are added also to this list.
	 * @param addressees
	 *            The {@link Addressee}s to add.
	 */
	private void initDisplayedAddressees(List<Addressee> displayedAddressees,
			List<Addressee> addressees)
	{
		for (Addressee addressee : addressees)
		{
			addAddresseeTab(displayedAddressees, addressee);
		}
	}

	/**
	 * Adds a tab with {@link Text} field for an {@link Addressee} to the
	 * {@link #addresseeTabFolder}. The text field is also cached in
	 * {@link #addresseeDocTextField}. It is inserted before the {@link #addAddresseeTab}
	 * and {@link #removeAddresseeTab}.
	 * 
	 * @param displayedAddressees
	 *            The list of all displayed Addressees. The new <code>addressee</code> is
	 *            added also to this list.
	 * @param addressee
	 *            The {@link Addressee} to add.
	 * @return The corresponding {@link Text} field for the {@link Addressee}'s
	 *         documentation.
	 */
	private Text addAddresseeTab(List<Addressee> displayedAddressees, Addressee addressee)
	{
		TabItem item = new TabItem(addresseeTabFolder, SWT.NONE,
				addresseeDocTextField.size());
		item.setText(addressee.getName());
		item.setToolTipText(addressee.getDescription());
		item.setData(ITEM_DATA_KEY, addressee);

		// Changes due to Issue #5
		Text textField = new Text(addresseeTabFolder, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL);
		// End changes due to Issue #5
		textField.addFocusListener(textFocusListener);
		item.setControl(textField);

		addresseeDocTextField.put(addressee, textField);

		if (!displayedAddressees.contains(addressee))
		{
			displayedAddressees.add(addressee);
		}
		return textField;
	}

	/**
	 * Appends the tab providing a context menu to add not-default Addressees.
	 */
	private void appendAddAddresseeTab()
	{
		addAddresseeTab = new TabItem(addresseeTabFolder, SWT.CENTER);
		addAddresseeTab.setText("    +");
		addAddresseeTab.setToolTipText("Add Addressee");
	}

	/**
	 * Appends the tab providing a context menu to remove displayed Addressees.
	 */
	private void appendRemoveAddresseeTab()
	{
		removeAddresseeTab = new TabItem(addresseeTabFolder, SWT.CENTER);
		removeAddresseeTab.setText("   \u2014"); // Unicode em dash
		removeAddresseeTab.setToolTipText("Remove Addressee");
	}

	private void createMenuItems(List<ThematicRole> roles, MenuItem[] menuItems,
			Menu parent)
	{
		for (int i = 0; i < roles.size(); i++)
		{
			ThematicRole role = roles.get(i);

			menuItems[i] = new MenuItem(parent, SWT.PUSH);
			menuItems[i].setText(role.getName());
			menuItems[i].setData(role);
		}

	}

	/**
	 * Adds the items from <code>thematicRoles</code> to the thematic role combobox.
	 * 
	 * @param rolesRecommendations
	 *            The items for the thematic role combobox.
	 */
	private void initThematicRolesCtrl(RolesRecommendations rolesRecommendations)
	{
		roleFirstLevelItems = new MenuItem[rolesRecommendations
				.getFirstLevelRecommendations().size()];
		roleSecondLevelItems = new MenuItem[rolesRecommendations
				.getSecondLevelRecommendations().size()];

		List<ThematicRole> firstLevelRecommendations = rolesRecommendations
				.getFirstLevelRecommendations();
		List<ThematicRole> secondLevelRoles = rolesRecommendations
				.getSecondLevelRecommendations();

		if ((firstLevelRecommendations != null) && !firstLevelRecommendations.isEmpty())
		{
			createMenuItems(firstLevelRecommendations, roleFirstLevelItems, rolePopUpMenu);

			if ((secondLevelRoles != null) && (!secondLevelRoles.isEmpty()))
			{
				final MenuItem openItem = new MenuItem(rolePopUpMenu, SWT.CASCADE);
				openItem.setText("More");
				final Menu submenu = new Menu(this.getShell(), SWT.DROP_DOWN);
				openItem.setMenu(submenu);

				createMenuItems(rolesRecommendations.getSecondLevelRecommendations(),
						roleSecondLevelItems, submenu);
			}
		}
		else
		{
			// In this case we must have second level roles.
			createMenuItems(secondLevelRoles, roleSecondLevelItems, rolePopUpMenu);
		}
	}

	private void addSelectionListenerToMenuItems(SelectionListener listener,
			MenuItem[] items)
	{
		if (items != null)
		{
			for (MenuItem menuItem : items)
			{
				menuItem.addSelectionListener(listener);
			}
		}
	}

	private void removeSelectionListenerToMenuItems(SelectionListener listener,
			MenuItem[] items)
	{
		if (items != null)
		{
			for (MenuItem menuItem : items)
			{
				menuItem.removeSelectionListener(listener);
			}
		}
	}

	@Override
	protected void addAllListener()
	{
		for (Text text : addresseeDocTextField.values())
		{
			text.addFocusListener(textFocusListener);
			text.addModifyListener(textModifyListener);
		}
		btnThematicRole.addSelectionListener(btnThematicRoleSelectionListener);
		addresseesMenu.addMenuListener(addresseesMenuListener);
		addresseeTabFolder.addSelectionListener(addresseeTabFolderSelectionListener);

		addSelectionListenerToMenuItems(roleMenuItemSelectionListener,
				roleFirstLevelItems);
		addSelectionListenerToMenuItems(roleMenuItemSelectionListener,
				roleSecondLevelItems);

		checkBoxErrorCase.addSelectionListener(checkBoxErrorCaseSelectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		for (Text text : addresseeDocTextField.values())
		{
			text.removeFocusListener(textFocusListener);
			text.removeModifyListener(textModifyListener);
		}
		btnThematicRole.removeSelectionListener(btnThematicRoleSelectionListener);
		addresseesMenu.removeMenuListener(addresseesMenuListener);
		addresseeTabFolder.removeSelectionListener(addresseeTabFolderSelectionListener);

		removeSelectionListenerToMenuItems(roleMenuItemSelectionListener,
				roleFirstLevelItems);
		removeSelectionListenerToMenuItems(roleMenuItemSelectionListener,
				roleSecondLevelItems);

		checkBoxErrorCase.removeSelectionListener(checkBoxErrorCaseSelectionListener);
	}

	@Override
	public void doCleanUp()
	{
		textModifyCheckThread.interrupt();
	}
}
