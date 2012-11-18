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
package de.akra.idocit.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import de.akra.idocit.common.utils.StringUtils;

/**
 * Emulated tooltip handler Notice that we could display anything in a tooltip text. For
 * instance, it might make sense to embed large tables of data or buttons linking data
 * under inspection to material elsewhere, or perform dynamic lookup for creating tooltip
 * text on the fly.
 * 
 * <pre>
 * Source: http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/HowtoimplementhoverhelpfeedbackusingtheMouseTrackListener.htm
 * </pre>
 * 
 * TODO: Currently only single monitor environments are supported. The position
 * calculation for the tooltip within multi monitor environments is not good at the
 * moment.
 */
public class ToolTipHandler
{
	/**
	 * The key to set and retrieve the tooltip from the widget.
	 * 
	 * @see Widget#setData(String, Object)
	 */
	public static final String KEY_TIP_TEXT = "TIP_TEXT";

	private Shell tipShell;

	private Label tipLabelText;

	private Widget tipWidget; // widget this tooltip is hovering over

	private Point tipPosition; // the position being hovered over

	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public ToolTipHandler(final Shell parent)
	{
		final Display display = parent.getDisplay();

		tipShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		tipShell.setLayout(gridLayout);

		tipShell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		tipLabelText = new Label(tipShell, SWT.NONE);
		tipLabelText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		tipLabelText.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tipLabelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
	}

	/**
	 * Enables customized hover tooltip for a specified control
	 * 
	 * @control the control on which to enable hover tooltip
	 */
	public void activateHoverToolTip(final Control control)
	{
		/*
		 * Get out of the way if we attempt to activate the control underneath the tooltip
		 */
		control.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e)
			{
				if (tipShell.isVisible())
					tipShell.setVisible(false);
			}
		});

		/*
		 * Trap hover events to pop-up tooltip
		 */
		control.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(final MouseEvent e)
			{
				if (tipShell.isVisible())
					tipShell.setVisible(false);
				tipWidget = null;
			}

			public void mouseHover(final MouseEvent event)
			{
				final Point pt = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget instanceof ToolBar)
				{
					final ToolBar w = (ToolBar) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Table)
				{
					final Table w = (Table) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Tree)
				{
					final Tree w = (Tree) widget;
					widget = w.getItem(pt);
				}
				if (widget == null)
				{
					tipShell.setVisible(false);
					tipWidget = null;
					return;
				}
				if (widget == tipWidget)
					return;
				tipWidget = widget;
				tipPosition = control.toDisplay(pt);
				final String text = (String) widget.getData(KEY_TIP_TEXT);
				tipLabelText.setText(text != null ? text : StringUtils.EMPTY);
				tipShell.pack();
				tipShell.setVisible(true);
				setHoverLocation(tipShell, tipPosition);
			}
		});
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param shell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 * @return the top-left location for a hovering box
	 */
	private void setHoverLocation(final Shell shell, final Point position)
	{
		// TODO improve calculating boundaries for multi monitor environments. Use
		// display.getMonitos() and calculate at which monitor we are at the moment.

		final Rectangle displayBounds = shell.getDisplay().getBounds();
		final Rectangle shellBounds = shell.getBounds();

		shellBounds.x = Math.min(position.x, displayBounds.width - shellBounds.width);
		shellBounds.y = Math.min(position.y + 16, displayBounds.height
				- shellBounds.height);
		shell.setBounds(shellBounds);
	}
}
