/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

import java.util.Locale;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.structure.DescribedItem;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Composite to edit a {@link ThematicRole}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditThematicRoleComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicRoleCompositeSelection>
{

	// Widgets
	private Text txtName;

	private Text txtDescription;

	// Listeners
	private FocusListener textFieldListener;

	private ModifyListener txtNameListener;

	private ModifyListener txtDescriptionListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public EditThematicRoleComposite(Composite parent)
	{
		super(parent, SWT.NONE, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Label lblName = new Label(this, SWT.NONE);
		lblName.setText("Name:");

		txtName = new Text(this, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtName);

		Label lblDescription = new Label(this, SWT.NONE);
		lblDescription.setText("Description:");

		txtDescription = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridDataFactory.fillDefaults().hint(300, 600).grab(true, true)
				.applyTo(txtDescription);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		textFieldListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e)
			{
				fireChangeEvent();
			}

			@Override
			public void focusGained(FocusEvent e)
			{}
		};

		txtNameListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicRoleCompositeSelection selection = getSelection();
				ThematicRole item = selection.getModifiedItem();
				item.setName(txtName.getText().toUpperCase(Locale.ENGLISH));
				selection.setLastCurserPosition(txtName.getSelection().x);
				selection.setModifiedItem(item);

				fireChangeEvent();
			}
		};

		txtDescriptionListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditThematicRoleCompositeSelection selection = getSelection();
				ThematicRole item = selection.getModifiedItem();

				item.setDescription(txtDescription.getText());

				selection.setModifiedItem(item);
				setSelection(selection);
			}
		};
	}

	@Override
	protected void doSetSelection(EditThematicRoleCompositeSelection oldInSelection,
			EditThematicRoleCompositeSelection newInSelection)
	{
		if (!newInSelection.equals(oldInSelection))
		{
			DescribedItem item = newInSelection.getModifiedItem();

			if (item != null)
			{
				txtName.setText(item.getName());
				txtName.setSelection(newInSelection.getLastCurserPosition());
				if (item.getDescription() != null)
				{
					txtDescription.setText(item.getDescription());
				}
				else
				{
					txtDescription.setText("");
				}
			}
		}
	}

	@Override
	protected void addAllListener()
	{
		txtName.addModifyListener(txtNameListener);
		txtDescription.addModifyListener(txtDescriptionListener);
		txtDescription.addFocusListener(textFieldListener);
	}

	@Override
	protected void removeAllListener()
	{
		txtName.removeModifyListener(txtNameListener);
		txtDescription.removeModifyListener(txtDescriptionListener);
		txtDescription.removeFocusListener(textFieldListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}
}
