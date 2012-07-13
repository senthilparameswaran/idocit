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
package de.akra.idocit.java.ui.composites;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.services.SimpleJavadocGenerator;
import de.akra.idocit.java.ui.RadioButtonGroup;

/**
 * {@link Composite} to manage settings for the Javadoc Generator.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class ManageJavadocGeneratorComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection>
{
	// Widgets
	private RadioButtonGroup radioButtonGroup;
	private Group grpJavadocGeneratorOptions;
	private Text javadocOptionsTextfield;

	// Listeners
	private IPropertyChangeListener radioBtnGroupPropChangeListener;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent Composite.
	 */
	public ManageJavadocGeneratorComposite(Composite parent)
	{
		super(parent, SWT.NONE, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void doSetSelection(ManageJavadocGeneratorCompositeSelection oldSelection,
			ManageJavadocGeneratorCompositeSelection newSelection, Object sourceControl)
	{
		if (newSelection != null && !newSelection.equals(oldSelection))
		{
			this.grpJavadocGeneratorOptions
					.setVisible(PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE
							.equals(newSelection.getSelectedJavadocType()));

			String javadocType = newSelection.getSelectedJavadocType();
			if (javadocType == null || javadocType.isEmpty())
			{
				javadocType = PreferenceStoreConstants.JAVADOC_GENERATION_MODE_COMPLEX;
			}
			this.radioButtonGroup.setSelected(javadocType);
		}
	}

	@Override
	protected void initGUI(Composite parent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		Group grpJavadocType = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(grpJavadocType);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grpJavadocType);
		grpJavadocType.setText("Javadoc Types");

		final Label lblJavadocType = new Label(grpJavadocType, SWT.NONE);
		lblJavadocType.setText("Which kind of Javadoc do you want to be generated?");

		// Array of radio button definitions. One definition consists of the label text of
		// the value.
		String[][] radioButtonData = new String[][] {
				{ "Complex Javadoc (supports many addressees)",
						PreferenceStoreConstants.JAVADOC_GENERATION_MODE_COMPLEX },
				{ "Simple Javadoc (supports only the addressee \"Developer\")",
						PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE } };

		this.radioButtonGroup = new RadioButtonGroup();

		for (String[] dataForBtn : radioButtonData)
		{
			final Button rdBtn = new Button(grpJavadocType, SWT.RADIO);
			rdBtn.setText(dataForBtn[0]);
			rdBtn.setData(dataForBtn[1]);
			radioButtonGroup.addRadioButton(rdBtn);
		}

		this.grpJavadocGeneratorOptions = new Group(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(grpJavadocGeneratorOptions);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(grpJavadocGeneratorOptions);
		grpJavadocGeneratorOptions.setText("Options for Javadoc Generator");

		final Label lblJavadocGeneratorOptions = new Label(grpJavadocGeneratorOptions,
				SWT.WRAP);
		lblJavadocGeneratorOptions
				.setText("Javadoc does not know the iDocIt! tags used in Javadoc. Here, you can generate options for Javadoc, that introduces the iDocIt! tags to Javadoc.\nCopy the generated options to your Javadoc command.");

		final Button btnGenerateOptions = new Button(grpJavadocGeneratorOptions, SWT.PUSH);
		btnGenerateOptions.setText("Generate options");
		btnGenerateOptions.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final String options = SimpleJavadocGenerator.INSTANCE
						.generateJavadocOptions(getSelection().getThematicRoles());
				javadocOptionsTextfield.setText(options);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// nothing to do
			}
		});

		// Create a multiple line text field
		this.javadocOptionsTextfield = new Text(grpJavadocGeneratorOptions, SWT.MULTI
				| SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		javadocOptionsTextfield.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		this.radioBtnGroupPropChangeListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getNewValue().equals(event.getOldValue()))
				{
					// if value was changed, update selection
					final ManageJavadocGeneratorCompositeSelection editSelection = cloneSelection(getSelection());
					editSelection.setSelectedJavadocType((String) event.getNewValue());
					setSelection(editSelection);
					fireChangeEvent(this);
				}
			}
		};
	}

	private ManageJavadocGeneratorCompositeSelection cloneSelection(
			final ManageJavadocGeneratorCompositeSelection selection)
	{
		final ManageJavadocGeneratorCompositeSelection newSelection = new ManageJavadocGeneratorCompositeSelection();
		newSelection.setThematicRoles(selection.getThematicRoles());
		newSelection.setSelectedJavadocType(selection.getSelectedJavadocType());
		return newSelection;
	}

	@Override
	protected void removeAllListener()
	{
		radioButtonGroup.setPropertyChangeListener(null);
	}

	@Override
	protected void addAllListener()
	{
		radioButtonGroup.setPropertyChangeListener(this.radioBtnGroupPropChangeListener);
	}

	@Override
	protected void doCleanUp()
	{
		// nothing to do
	}
}
