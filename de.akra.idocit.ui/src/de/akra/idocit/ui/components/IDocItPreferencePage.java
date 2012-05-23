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
package de.akra.idocit.ui.components;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.core.IDocItActivator;

/**
 * A {@link PreferencePage} for the iDocIt! settings.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class IDocItPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	/**
	 * Constructor.
	 */
	public IDocItPreferencePage()
	{
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Label lblWordNet = new Label(parent, SWT.NONE);
		lblWordNet.setText("Welcome to the customization area of iDocIt!");

		return super.createContents(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		// Nothing to do!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkState()
	{
		// Changes due to Issue #11
		// the FieldEditorPreferencePage does the check already
		super.checkState();
		updateApplyButton();
		// End changes due to Issue #11
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE))
		{
			checkState();
		}
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public boolean performOk()
	{
		boolean saveState = super.performOk();
		IDocItActivator.initializeIDocIt();
		return saveState;
	}
}
