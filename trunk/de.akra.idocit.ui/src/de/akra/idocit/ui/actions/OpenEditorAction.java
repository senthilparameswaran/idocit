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
package de.akra.idocit.ui.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.akra.idocit.ui.components.DocumentationEditor;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * PopupMenu action to open the {@link DocumentationEditor} with the selected file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class OpenEditorAction implements IObjectActionDelegate
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(OpenEditorAction.class.getName());

	private static final String EDITOR_ID = "de.akra.idocit.ui.components.DocumentationEditor";

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public OpenEditorAction()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
		IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench()
				.getEditorRegistry().findEditor(EDITOR_ID);

		ISelectionService selectionService = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService();
		IStructuredSelection structuredSelection = (IStructuredSelection) selectionService
				.getSelection();

		if (structuredSelection != null)
		{
			IFile file = (IFile) structuredSelection.getFirstElement();
			if (file != null)
			{
				try
				{
					page.openEditor(new FileEditorInput(file), editorDescriptor.getId());
				}
				catch (PartInitException e)
				{
					logger.log(Level.SEVERE, e.getMessage());
					MessageBoxUtils.openErrorBox(shell, e.getMessage());
				}
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{}

}
