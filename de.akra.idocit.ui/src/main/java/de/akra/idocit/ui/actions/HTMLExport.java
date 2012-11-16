/*******************************************************************************
 *   Copyright 2011, 2012 AKRA GmbH
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.akra.idocit.common.constants.Misc;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.services.impl.HTMLDocGenerator;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.core.utils.ResourceUtils;
import de.akra.idocit.ui.UIGlobals;
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
@SuppressWarnings("restriction")
public class HTMLExport implements IObjectActionDelegate
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(HTMLExport.class.getName());

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public HTMLExport()
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
	public void run(final IAction action)
	{
		final ISelectionService selectionService = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService();
		final IStructuredSelection structuredSelection = (IStructuredSelection) selectionService
				.getSelection();

		if (structuredSelection != null)
		{
			IFile file = null;
			if (structuredSelection.getFirstElement() instanceof CompilationUnit)
			{
				final CompilationUnit cu = (CompilationUnit) structuredSelection
						.getFirstElement();
				try
				{
					file = (IFile) cu.getCorrespondingResource();
				}
				catch (final JavaModelException e)
				{
					final String msg = "CompilationUnit \"" + cu.getPath().toOSString()
							+ "\" has no corresponding resource.";
					logger.log(Level.SEVERE, msg, e);
					MessageBoxUtils.openErrorBox(shell, msg + StringUtils.NEW_LINE
							+ StringUtils.NEW_LINE + e.getMessage());
				}

			}
			else if (structuredSelection.getFirstElement() instanceof IFile)
			{
				file = (IFile) structuredSelection.getFirstElement();
			}

			if (file != null)
			{
				// Get the interface as file ...
				final IFile interfaceIFile = new FileEditorInput(file).getFile();
				final File interfaceFile = interfaceIFile.getLocation().toFile();

				if (interfaceFile.exists())
				{
					// get target file
					final FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
					fileDialog.setText("Export Documentation as HTML-file");
					final String[] filterExt = { "*.html", "*.htm" };
					fileDialog.setFilterExtensions(filterExt);

					final String preselectPath = buildPreselectedPath(interfaceIFile);

					fileDialog.setFileName(preselectPath);
					String selectedFileName = fileDialog.open();

					boolean stored = false;

					while ((selectedFileName != null) && !stored)
					{
						final File destFile = new File(selectedFileName);
						UIGlobals.setLastSelectedPathInFileDialog(destFile
								.getParentFile().getAbsolutePath());

						final boolean exists = destFile.exists();
						final boolean overwrite = exists
								&& MessageBoxUtils
										.openQuestionDialogBox(
												shell,
												"The file "
														+ selectedFileName
														+ " already exists. Do you want to overwrite it?");

						if (overwrite || !exists)
						{
							// parse interface file.
							try
							{
								logger.log(Level.INFO, "Start parsing");
								final InterfaceArtifact interfaceArtifact = ServiceManager
										.getInstance().getPersistenceService()
										.loadInterface(interfaceIFile);
								logger.log(Level.INFO, "End parsing");
								logger.log(Level.INFO, "Start converting");
								final HTMLDocGenerator docGen = new HTMLDocGenerator(
										interfaceArtifact);
								final String html = docGen.generateHTML();
								logger.log(Level.INFO, "End converting");

								BufferedWriter writer = new BufferedWriter(
										new OutputStreamWriter(new FileOutputStream(
												destFile),
												Charset.forName(Misc.DEFAULT_CHARSET)));
								writer.write(html);
								writer.close();

								// copy css file
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(
												ResourceUtils
														.getResourceInputStream("stylesheet.css"),
												Charset.forName(Misc.DEFAULT_CHARSET)));
								final String cssFileName = selectedFileName.substring(0,
										selectedFileName.lastIndexOf(System
												.getProperty("file.separator")) + 1)
										+ "stylesheet.css";
								writer = new BufferedWriter(new OutputStreamWriter(
										new FileOutputStream(cssFileName),
										Charset.forName(Misc.DEFAULT_CHARSET)));
								String line = reader.readLine();
								while (line != null)
								{
									writer.write(line + "\n");
									line = reader.readLine();
								}
								reader.close();
								writer.close();

							}
							catch (final Exception ex)
							{
								final String msg = "Could not export documentation for "
										+ interfaceIFile.getFullPath();
								logger.log(Level.SEVERE, msg, ex);
								MessageBoxUtils.openErrorBox(shell, msg);
							}

							stored = true;
						}
						else
						{
							selectedFileName = fileDialog.open();
						}
					}
				}
				else
				{
					final String msg = "File is no longer available: "
							+ interfaceFile.getAbsolutePath();
					logger.log(Level.WARNING, msg);
					MessageBoxUtils.openErrorBox(shell, msg);
				}

			}
		}
	}

	private String buildPreselectedPath(final IFile file)
	{
		String preselectPath = UIGlobals.getLastSelectedPathInFileDialog();
		if (StringUtils.isBlank(preselectPath))
		{
			preselectPath = file.getLocation().toFile().getParentFile().getAbsolutePath();
		}

		final String sourceFileName = file.getName();
		preselectPath += File.separatorChar
				+ sourceFileName.substring(0, sourceFileName.length()
						- file.getFileExtension().length()) + "html";
		return preselectPath;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{}

}
