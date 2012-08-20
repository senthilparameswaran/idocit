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
package de.akra.idocit.java.ui;

import java.beans.IndexedPropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * Group for {@link Button}s of the type {@link SWT#RADIO} that belongs together. You can
 * add several buttons to this group and set an {@link IPropertyChangeListener} to get
 * notified if a selection changed.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RadioButtonGroup
{
	/**
	 * Property name constant (value "radio_button_group_value") to signal a change in the
	 * value of this radio button group.
	 */
	public static final String VALUE = "radio_button_group_value";

	private List<Button> radioButtons;

	private IPropertyChangeListener callbackPropertyChangeListener;

	private SelectionListener btnSelectionListener;

	/**
	 * The current selected button, or <code>null</code> if none.
	 */
	private Button selectedButton;

	public RadioButtonGroup()
	{
		btnSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Button issuedBtn = (Button) e.widget;
				fireSelectionChanged(issuedBtn);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				final Button issuedBtn = (Button) e.widget;
				fireSelectionChanged(issuedBtn);
			}
		};
	}

	private void fireSelectionChanged(final Button issuedBtn)
	{
		// check if this button is the selected one or that one which has lost
		// the selection
		if (issuedBtn.getSelection())
		{
			if (callbackPropertyChangeListener != null)
			{
				final Object oldValue = selectedButton != null ? selectedButton.getData()
						: null;
				final Object newValue = issuedBtn.getData();

				final PropertyChangeEvent propChangeEvt = new PropertyChangeEvent(
						issuedBtn, VALUE, oldValue, newValue);

				// fire change event
				callbackPropertyChangeListener.propertyChange(propChangeEvt);
			}
			// remember current selection
			selectedButton = issuedBtn;
		}
	}

	/**
	 * Adds the button to this radio button group.
	 * 
	 * @param radioButton
	 *            [OBJECT] button to add to this group.
	 * @throws IllegalStateException
	 *             if the button has no value (see {@link Button#setData()}).
	 * @throws IllegalArgumentException
	 *             if the value of the button to add already exists in the
	 *             {@link RadioButtonGroup}.
	 */
	public void addRadioButton(final Button radioButton)
	{
		if (radioButton.getData() == null)
		{
			throw new IllegalStateException(
					"Value is not set for the radio button. Use Button.setData(Object) to set the value.");
		}
		if (radioButtons == null)
		{
			radioButtons = new ArrayList<Button>();
		}
		if (existButtonsValue(radioButton))
		{
			throw new IllegalArgumentException("The buttons's value '"
					+ radioButton.getData()
					+ "' exists already in the radio button group.");
		}
		radioButton.addSelectionListener(btnSelectionListener);
		radioButtons.add(radioButton);
	}

	/**
	 * Check if the {@link Button}'s value equals to an existing button.
	 * 
	 * @param radioButton
	 *            The button whose value shall be tested.
	 * @return <code>true</code> if the value already exists for one {@link Button}.
	 */
	private boolean existButtonsValue(final Button radioButton)
	{
		for (Button btn : radioButtons)
		{
			final Object data = btn.getData();
			final Object otherData = radioButton.getData();
			if (data.equals(otherData))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Set new selection without firing a {@link IndexedPropertyChangeEvent}.
	 * 
	 * @param value
	 *            Radio button's value to select.
	 */
	public void setSelected(final String value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Value must not be null.");
		}

		final Iterator<Button> btnIter = this.radioButtons.iterator();
		boolean found = false;
		while (btnIter.hasNext() && !found)
		{
			final Button btn = btnIter.next();
			if (value.equals(btn.getData()))
			{
				found = true;
				btn.setSelection(true);
				this.selectedButton = btn;
			}
		}

		if (!found)
		{
			throw new IllegalArgumentException("Value '" + value + "' not found.");
		}
	}

	/**
	 * 
	 * @return the selected {@link Button} or <code>null</code> if no one is selected.
	 */
	public Button getSelected()
	{
		return selectedButton;
	}

	/**
	 * 
	 * @param key
	 *            [PRIMARY_KEY] The key to get the widget's data.
	 * @return The buttons's data for the given <code>key</code>.
	 * @see Button#getData(String)
	 */
	public Object getSelectedValue(String key)
	{
		if (this.selectedButton != null)
		{
			return this.selectedButton.getData(key);
		}
		return null;
	}

	/**
	 * 
	 * @return the value of the selected button.
	 * @see Button#getData()
	 */
	public Object getSelectedValue()
	{
		if (this.selectedButton != null)
		{
			return this.selectedButton.getData();
		}
		return null;
	}

	/**
	 * Set or remove a selection change listener to the radio button group. If one button
	 * gains selection a property change event is fired.
	 * 
	 * @param listener
	 *            [OBJECT] the listener which should be notified. <code>null</code> to
	 *            remove the listener.
	 */
	public void setPropertyChangeListener(IPropertyChangeListener listener)
	{
		this.callbackPropertyChangeListener = listener;
	}
}