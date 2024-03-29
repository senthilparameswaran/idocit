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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.constants.ThematicGridConstants;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.ui.utils.ToolTipHandler;

/**
 * Composite with the {@link List} of recommended {@link ThematicRole}s. The list is only
 * used for displaying the information.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DisplayRecommendedRolesComposite
		extends
		AbsComposite<EmptyActionConfiguration, DisplayRecommendedRolesCompositeRC, DisplayRecommendedRolesCompositeSelection>
{
	private static final int TOOLTIP_MAX_LINE_LENGTH = 80;
	private static final String ASSOCIATED_ROLE = " (Associated)";
	private static final String REFERENCE_GRID = " (Reference Grid)";

	private Color GREEN;
	private Color RED;
	private Color ORANGE;
	private Font boldFont;

	/**
	 * The "read-only" {@link List} composite, that displays the recommended
	 * {@link ThamaticRole}s.
	 */
	private Tree rolesTree;

	private Menu tablePopUpMenu;
	private MenuItem menuItemSetReferenceGrid;
	private MenuItem menuItemUnsetReferenceGrid;
	private MenuDetectListener menuDetectListener;

	// Listener
	private SelectionListener menuItemSetReferenceGridSelectionListener;
	private SelectionListener menuItemUnsetReferenceGridSelectionListener;
	private TreeListener treeCollapseListener;

	/**
	 * Constructor.
	 * 
	 * @param pvParent
	 *            Must not be null
	 * @param pvStyle
	 * @param pvResourceConf
	 *            Must not be null
	 * @throws CompositeInitializationException
	 */
	public DisplayRecommendedRolesComposite(Composite pvParent, int pvStyle,
			DisplayRecommendedRolesCompositeRC pvResourceConf)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(), pvResourceConf);
	}

	@Override
	protected void initGUI(Composite parent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		final ToolTipHandler tooltip = new ToolTipHandler(this.getShell());
		rolesTree = new Tree(this, SWT.BORDER | SWT.VIRTUAL | SWT.SINGLE);
		tooltip.activateHoverToolTip(rolesTree);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(rolesTree);

		FontData[] fontData = rolesTree.getFont().getFontData();
		for (FontData data : fontData)
		{
			data.setStyle(SWT.BOLD);
		}
		boldFont = new Font(this.getDisplay(), fontData);

		GREEN = this.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
		RED = this.getDisplay().getSystemColor(SWT.COLOR_RED);
		ORANGE = new Color(this.getDisplay(), 255, 127, 0);

		tablePopUpMenu = new Menu(this.getShell(), SWT.POP_UP);
		menuItemSetReferenceGrid = new MenuItem(tablePopUpMenu, SWT.PUSH);
		menuItemSetReferenceGrid.setText("Mark as Reference Grid");
		menuItemUnsetReferenceGrid = new MenuItem(tablePopUpMenu, SWT.PUSH);
		menuItemUnsetReferenceGrid.setText("Clear Reference Grid");
	}

	@Override
	protected void doSetSelection(
			DisplayRecommendedRolesCompositeSelection oldInSelection,
			DisplayRecommendedRolesCompositeSelection newInSelection, Object sourceControl)
	{
		if (newInSelection != null && (!newInSelection.equals(oldInSelection)))
		{
			rolesTree.removeAll();
			final Map<String, ThematicGrid> recThematicGrids = newInSelection
					.getRecommendedThematicGrids();
			final Set<ThematicRole> allAssignedThematicRoles = newInSelection
					.getAssignedThematicRoles();
			final Set<ThematicRole> errorDocumentingRoles = newInSelection
					.getAssignedThematicRolesWithErrorDocumentation();

			// Sort verb classes alphabetically
			final TreeMap<String, ThematicGrid> sortedVerbClasses = new TreeMap<String, ThematicGrid>(
					recThematicGrids);

			for (final Entry<String, ThematicGrid> roleClass : sortedVerbClasses
					.entrySet())
			{
				final TreeItem verbClassRoot = new TreeItem(rolesTree, SWT.NONE);
				final String thematicGridName = roleClass.getKey();

				if (thematicGridName
						.equals(newInSelection.getReferenceThematicGridName()))
				{
					verbClassRoot.setText(thematicGridName + REFERENCE_GRID);
				}
				else
				{
					verbClassRoot.setText(thematicGridName);
				}

				if (roleClass.getValue() != null)
				{
					final ThematicGrid grid = roleClass.getValue();

					if (!StringUtils.isBlank(grid.getDescription()))
					{
						final StringBuilder tooltip = new StringBuilder(grid
								.getDescription().length() * 2);
						tooltip.append(grid.getDescription())
								.append(StringUtils.NEW_LINE)
								.append(StringUtils.NEW_LINE)
								.append("Verbs:")
								.append(StringUtils.NEW_LINE)
								.append(StringUtils.convertIntoCommaSeperatedTokens(grid
										.getVerbs()));
						verbClassRoot.setData(ToolTipHandler.KEY_TIP_TEXT, StringUtils
								.addLineBreaks(tooltip.toString(),
										TOOLTIP_MAX_LINE_LENGTH,
										StringUtils.SPACE.charAt(0)));
					}

					final Set<ThematicRole> displayNotAssignedRoles = new TreeSet<ThematicRole>();
					final Set<ThematicRole> displayAssignedRoles = new TreeSet<ThematicRole>();

					final Iterator<ThematicRole> iter = grid.getRoles().keySet()
							.iterator();
					while (iter.hasNext())
					{
						final ThematicRole recRole = iter.next();
						if (allAssignedThematicRoles.contains(recRole))
						{
							displayAssignedRoles.add(recRole);
						}
						else
						{
							displayNotAssignedRoles.add(recRole);
						}
					}

					/*
					 * insert items into list
					 */
					for (final ThematicRole role : displayAssignedRoles)
					{
						final TreeItem roleItem = new TreeItem(verbClassRoot, SWT.BOLD);
						roleItem.setText(role.getName() + ASSOCIATED_ROLE);
						roleItem.setForeground(GREEN);
						roleItem.setData(ToolTipHandler.KEY_TIP_TEXT, StringUtils
								.addLineBreaks(role.getDescription(),
										TOOLTIP_MAX_LINE_LENGTH,
										StringUtils.SPACE.charAt(0)));

						if (grid.getRoles().get(role))
						{
							// role is mandatory and associated
							roleItem.setFont(boldFont);
						}

						setMissingErrorDocumentationWarning(roleItem, role,
								errorDocumentingRoles);
					}
					for (final ThematicRole role : displayNotAssignedRoles)
					{
						final TreeItem roleItem = new TreeItem(verbClassRoot, SWT.NONE);
						roleItem.setText(role.getName());
						roleItem.setData(ToolTipHandler.KEY_TIP_TEXT, StringUtils
								.addLineBreaks(role.getDescription(),
										TOOLTIP_MAX_LINE_LENGTH,
										StringUtils.SPACE.charAt(0)));

						if (grid.getRoles().get(role))
						{
							// role is mandatory and not associated
							roleItem.setForeground(RED);
							roleItem.setFont(boldFont);
						}
						else
						{
							// role is optional and not associated
							roleItem.setForeground(ORANGE);
						}

						setMissingErrorDocumentationWarning(roleItem, role,
								errorDocumentingRoles);
					}
				}

				// expand Tree
				verbClassRoot.setExpanded(!newInSelection.getCollapsedThematicGridNames()
						.contains(roleClass.getKey()));
			}
		}
	}

	/**
	 * Sets the Standard-Eclipse Warning icon as image of the given TreeItem.
	 * 
	 * @object Image org.eclipse.jface.fieldassist.IMG_DEC_FIELD_WARNING
	 * @param thematicRoleItem
	 *            [DESTINATION]
	 * @param role
	 *            [COMPARISON] This role is represented by the TreeItem.
	 * @param rolesWithErrorDocumentation
	 *            [COMPARISON] These role have the activated error-flag.
	 * 
	 * @thematicgrid Setting Operation / Setter
	 */
	private void setMissingErrorDocumentationWarning(TreeItem thematicRoleItem,
			ThematicRole role, Set<ThematicRole> rolesWithErrorDocumentation)
	{
		if (ThematicRoleUtils.isRoleFailable(role)
				&& !rolesWithErrorDocumentation.contains(role))
		{
			thematicRoleItem.setImage(getResourceConfiguration()
					.getRoleWithoutErrorDocsWarningIcon());
		}
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		treeCollapseListener = new TreeListener() {

			@Override
			public void treeExpanded(TreeEvent e)
			{
				if (e.item != null)
				{
					DisplayRecommendedRolesCompositeSelection selection = getSelection();
					Set<String> collapsedGrids = selection
							.getCollapsedThematicGridNames();

					collapsedGrids.remove(((TreeItem) e.item).getText());

					selection = selection.setCollapsedThematicGridNames(collapsedGrids);
					setSelection(selection);

					fireChangeEvent(rolesTree);
				}
			}

			@Override
			public void treeCollapsed(TreeEvent e)
			{
				if (e.item != null)
				{
					DisplayRecommendedRolesCompositeSelection selection = getSelection();
					Set<String> collapsedGrids = selection
							.getCollapsedThematicGridNames();

					collapsedGrids.add(((TreeItem) e.item).getText());

					selection = selection.setCollapsedThematicGridNames(collapsedGrids);
					setSelection(selection);

					fireChangeEvent(rolesTree);
				}
			}
		};

		menuItemSetReferenceGridSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				DisplayRecommendedRolesCompositeSelection selection = getSelection();

				if (selection != null)
				{
					TreeItem[] selectedItems = rolesTree.getSelection();
					String referenceGridName = extractThematicGridName(selectedItems[0]
							.getText());
					selection = selection.setReferenceThematicGridName(referenceGridName);

					setSelection(selection);

					fireChangeEvent(rolesTree);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				// Nothing to do!
			}
		};

		menuDetectListener = new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent event)
			{
				TreeItem[] selectedItem = rolesTree.getSelection();
				String itemText = extractThematicGridName(selectedItem[0].getText());

				DisplayRecommendedRolesCompositeSelection selection = getSelection();

				if (selection != null)
				{
					boolean isThematicGridItem = selection.getRecommendedThematicGrids()
							.containsKey(itemText);
					tablePopUpMenu.setVisible(isThematicGridItem);
				}
			}
		};

		menuItemUnsetReferenceGridSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				DisplayRecommendedRolesCompositeSelection selection = getSelection();

				if (selection != null)
				{
					selection = selection
							.setReferenceThematicGridName(ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME);

					setSelection(selection);
					fireChangeEvent(rolesTree);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				// Do nothing!
			}
		};
	}

	private String extractThematicGridName(String treeNodeText)
	{
		int index = treeNodeText.indexOf(REFERENCE_GRID);

		if (index >= 0)
		{
			treeNodeText = treeNodeText.substring(0, index);
		}

		return treeNodeText;
	}

	@Override
	protected void addAllListener()
	{
		menuItemSetReferenceGrid
				.addSelectionListener(menuItemSetReferenceGridSelectionListener);
		menuItemUnsetReferenceGrid
				.addSelectionListener(menuItemUnsetReferenceGridSelectionListener);
		rolesTree.addMenuDetectListener(menuDetectListener);
		rolesTree.addTreeListener(treeCollapseListener);
	}

	@Override
	protected void removeAllListener()
	{
		rolesTree.removeMenuDetectListener(menuDetectListener);
		menuItemSetReferenceGrid
				.removeSelectionListener(menuItemSetReferenceGridSelectionListener);
		menuItemUnsetReferenceGrid
				.removeSelectionListener(menuItemUnsetReferenceGridSelectionListener);
		rolesTree.removeTreeListener(treeCollapseListener);
	}

	@Override
	protected void doCleanUp()
	{
		GREEN.dispose();
		RED.dispose();
		ORANGE.dispose();
		boldFont.dispose();
	}
}
