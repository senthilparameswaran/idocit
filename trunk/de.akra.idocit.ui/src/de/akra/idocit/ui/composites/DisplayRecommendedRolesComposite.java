/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.core.structure.ThematicRole;

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
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DisplayRecommendedRolesCompositeSelection>
{
	private static final String ASSOCIATED_ROLE = " (Associated)";
	private Color GREEN;
	private Color RED;
	private Color ORANGE;
	private Font boldFont;

	/**
	 * {@link SelectionListener} for {@link DisplayRecommendedRolesComposite#rolesList} to
	 * deselect the selection.
	 */
	private SelectionListener listSelectionListener;

	/**
	 * The "read-only" {@link List} composite, that displays the recommended
	 * {@link ThamaticRole}s.
	 */
	// private List rolesList;
	private Tree rolesTree;

	/**
	 * Constructor.
	 * 
	 * @param pvParent
	 * @param pvStyle
	 * @throws CompositeInitializationException
	 */
	public DisplayRecommendedRolesComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite parent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		rolesTree = new Tree(this, SWT.BORDER | SWT.SINGLE | SWT.VIRTUAL);

		rolesTree.addTreeListener(new TreeListener() {

			@Override
			public void treeExpanded(TreeEvent e)
			{
				if (e.item != null)
				{
					getSelection().getCollapsedThematicGridNames().remove(
							((TreeItem) e.item).getText());
					fireChangeEvent();
				}
			}

			@Override
			public void treeCollapsed(TreeEvent e)
			{
				if (e.item != null)
				{
					getSelection().getCollapsedThematicGridNames().add(
							((TreeItem) e.item).getText());
					fireChangeEvent();
				}
			}
		});
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
	}

	@Override
	protected void doSetSelection(
			DisplayRecommendedRolesCompositeSelection oldInSelection,
			DisplayRecommendedRolesCompositeSelection newInSelection)
	{
		if (newInSelection != null && (!newInSelection.equals(oldInSelection)))
		{
			rolesTree.removeAll();
			Map<String, Map<ThematicRole, Boolean>> recThematicRoles = newInSelection
					.getRecommendedThematicRoles();
			Set<ThematicRole> allAssignedThematicRoles = newInSelection
					.getAssignedThematicRoles();

			// Sort verb classes alphabetically
			TreeMap<String, Map<ThematicRole, Boolean>> sortedVerbClasses = new TreeMap<String, Map<ThematicRole, Boolean>>(
					recThematicRoles);

			for (Entry<String, Map<ThematicRole, Boolean>> roleClass : sortedVerbClasses
					.entrySet())
			{
				TreeItem verbClassRoot = new TreeItem(rolesTree, SWT.NONE);
				verbClassRoot.setText(roleClass.getKey());

				if (roleClass.getValue() != null)
				{
					Set<ThematicRole> displayNotAssignedRoles = new TreeSet<ThematicRole>();
					Set<ThematicRole> displayAssignedRoles = new TreeSet<ThematicRole>();

					Iterator<ThematicRole> iter = roleClass.getValue().keySet()
							.iterator();
					while (iter.hasNext())
					{
						ThematicRole recRole = iter.next();
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
					for (ThematicRole role : displayAssignedRoles)
					{
						TreeItem roleItem = new TreeItem(verbClassRoot, SWT.BOLD);
						roleItem.setText(role.getName() + ASSOCIATED_ROLE);
						roleItem.setForeground(GREEN);

						if (roleClass.getValue().get(role))
						{
							// role is mandatory and associated
							roleItem.setFont(boldFont);
						}
					}
					for (ThematicRole role : displayNotAssignedRoles)
					{
						TreeItem roleItem = new TreeItem(verbClassRoot, SWT.NONE);
						roleItem.setText(role.getName());

						if (roleClass.getValue().get(role))
						{
							// role is mandatory and not associated
							roleItem.setForeground(RED);
						}
						else
						{
							// role is optional and not associated
							roleItem.setForeground(ORANGE);
						}
					}
				}

				// expand Tree
				verbClassRoot.setExpanded(!newInSelection.getCollapsedThematicGridNames()
						.contains(roleClass.getKey()));
			}
		}
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		listSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				onListItemSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				onListItemSelected(e);
			}
		};

	}

	/**
	 * The user should not be able to select an item. The list is only for showing the
	 * roles.
	 * 
	 * @param e
	 *            the {@link SelectionEvent}
	 */
	private void onListItemSelected(SelectionEvent e)
	{
		rolesTree.deselectAll();
	}

	@Override
	protected void addAllListener()
	{
		rolesTree.addSelectionListener(listSelectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		if (rolesTree.getListeners(SWT.Selection).length > 0)
		{
			rolesTree.removeSelectionListener(listSelectionListener);
		}
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
