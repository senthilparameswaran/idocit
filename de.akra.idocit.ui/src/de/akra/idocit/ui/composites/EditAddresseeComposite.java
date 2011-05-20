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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.core.structure.Addressee;

/**
 * Composite to edit an {@link Addressee}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditAddresseeComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditAddresseeCompositeSelection>
{

	// Widgets
	private Text txtName;

	private Text txtDescription;

	private Button chkDefault;

	// Listeners
	private FocusListener textFieldListener;

	private ModifyListener txtNameListener;

	private ModifyListener txtDescriptionListener;

	private SelectionListener chkDefaultListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public EditAddresseeComposite(Composite parent)
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
		GridDataFactory.fillDefaults().grab(true, true).applyTo(txtDescription);
		
		Label lblDefault = new Label(this, SWT.NONE);
		lblDefault.setText("Default:");
		chkDefault = new Button(this, SWT.CHECK);
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
				EditAddresseeCompositeSelection selection = getSelection();
				Addressee item = selection.getModifiedAddressee();

				item.setName(txtName.getText());

				selection.setModifiedAddressee(item);
				setSelection(selection);
			}
		};

		txtDescriptionListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				EditAddresseeCompositeSelection selection = getSelection();
				Addressee item = selection.getModifiedAddressee();

				item.setDescription(txtDescription.getText());

				selection.setModifiedAddressee(item);
				setSelection(selection);
			}
		};

		chkDefaultListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				EditAddresseeCompositeSelection selection = getSelection();
				Addressee item = selection.getModifiedAddressee();
				item.setDefault(chkDefault.getSelection());

				selection.setModifiedAddressee(item);
				setSelection(selection);
				
				fireChangeEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};
	}

	@Override
	protected void doSetSelection(EditAddresseeCompositeSelection oldInSelection,
			EditAddresseeCompositeSelection newInSelection)
	{
		if (!newInSelection.equals(oldInSelection))
		{
			Addressee item = newInSelection.getModifiedAddressee();

			if (item != null)
			{
				txtName.setText(item.getName());
				txtDescription.setText(item.getDescription());
				chkDefault.setSelection(item.isDefault());
			}
		}
	}

	@Override
	protected void addAllListener()
	{
		txtName.addModifyListener(txtNameListener);
		txtDescription.addModifyListener(txtDescriptionListener);
		txtDescription.addFocusListener(textFieldListener);
		txtName.addFocusListener(textFieldListener);
		chkDefault.addSelectionListener(chkDefaultListener);
	}

	@Override
	protected void removeAllListener()
	{
		txtName.removeModifyListener(txtNameListener);
		txtDescription.removeModifyListener(txtDescriptionListener);
		txtDescription.removeFocusListener(textFieldListener);
		txtName.removeFocusListener(textFieldListener);
		chkDefault.removeSelectionListener(chkDefaultListener);
	}

	@Override
	protected void doCleanUp()
	{
		// Nothing to do!
	}
}
