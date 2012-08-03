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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.statushandlers.StatusManager;

import de.akra.idocit.ui.Activator;
import de.akra.idocit.ui.constants.DialogConstants;

/**
 * Collection of messages boxes.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public final class MessageBoxUtils
{

	/**
	 * Shows an error message box with {@link SWT#OK} button and logs the exception.
	 * 
	 * @param parentShell
	 *            [DESTINATION] The parent shell for modal displaying.
	 * @param text
	 *            [OBEJCT] The displayed text in the message box.
	 */
	public static void openErrorBox(final Shell parentShell, final String text)
	{
		openErrorBox(parentShell, text, null);
	}

	/**
	 * Shows an error message box with {@link SWT#OK} button and logs the exception.
	 * 
	 * @param parentShell
	 *            [DESTINATION] The parent shell for modal displaying.
	 * @param text
	 *            [OBEJCT] The displayed text in the message box.
	 * @param t
	 *            [OBEJCT] The Throwable to log or <code>null</code>.
	 */
	public static void openErrorBox(final Shell parentShell, final String text,
			final Throwable t)
	{
		final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, text, t);
		StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
	}

	/**
	 * 
	 * Shows an question message box with {@link SWT#YES} and {@link SWT#NO} buttons.
	 * 
	 * @param parentShell
	 *            [DESTINATION] The parent shell for modal displaying.
	 * @param text
	 *            [OBEJCT] The displayed text in the message box.
	 * @return true, if the SWT.YES button was clicked in the dialog. If SWT.NO was
	 *         clicked, false is returned.
	 */
	public static boolean openQuestionDialogBox(Shell parentShell, String text)
	{
		MessageBox messageBox = new MessageBox(parentShell, SWT.YES | SWT.NO
				| SWT.ICON_QUESTION);
		messageBox.setText(DialogConstants.DIALOG_TITLE);
		messageBox.setMessage(text);
		return messageBox.open() == SWT.YES;
	}

	/**
	 * 
	 * Shows an warning message box with {@link SWT#YES} and {@link SWT#NO} buttons.
	 * 
	 * @param parentShell
	 *            [DESTINATION] The parent shell for modal displaying.
	 * @param text
	 *            [OBEJCT] The displayed text in the message box.
	 * @return [YES-NO-ANSWER] true, if the SWT.YES button was clicked in the dialog. If
	 *         SWT.NO was clicked, false is returned.
	 */
	public static boolean openYesNoWarningDialogBox(Shell parentShell, String text)
	{
		MessageBox messageBox = new MessageBox(parentShell, SWT.YES | SWT.NO
				| SWT.ICON_WARNING);
		messageBox.setText(DialogConstants.DIALOG_TITLE);
		messageBox.setMessage(text);
		return messageBox.open() == SWT.YES;
	}
}
