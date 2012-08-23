/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JavaEditorSelectionListenerTest
{
	private static final String PROJECT_NAME = "iDocItTestProject";

	private static final String SEPERATOR = System.getProperty("file.separator");

	@Before
	public void setupWorkspace() throws CoreException, FileNotFoundException
	{
		// Create Java Project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.create(progressMonitor);
		project.open(progressMonitor);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		javaProject.open(progressMonitor);

		// Create source folder
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(true, true, progressMonitor);

		// Create the package
		IFolder packageFolder = srcFolder.getFolder("source");
		packageFolder.create(true, true, progressMonitor);

		// Create Java files
		File customerFile = new File("src" + SEPERATOR + "test" + SEPERATOR + "resources"
				+ SEPERATOR + "source" + SEPERATOR + "Customer.java");
		IFile customerWorkspaceFile = packageFolder.getFile("Customer.java");
		customerWorkspaceFile.create(new FileInputStream(customerFile), true,
				progressMonitor);
	}

	/**
	 * Instanciates a {@link JavaEditorSelectionListener} and calls its
	 * selectionChanged-method with a null selection. No exception is expected to be
	 * thrown.
	 */
	@Test
	public void testSelectionChangedWithNullSelection() throws Exception
	{
		PrintStream out = System.out;
		File tmpFileOutput = File.createTempFile("test", "out");
		System.setOut(new PrintStream(tmpFileOutput));
		try
		{

			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
			page.closeAllEditors(true);

			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(PROJECT_NAME);
			IFolder srcFolder = project.getFolder("src");
			IFolder packageFolder = srcFolder.getFolder("source");
			IFile customerWorkspaceFile = packageFolder.getFile("Customer.java");

			final JavaEditorSelectionListener listener = new JavaEditorSelectionListener();

			for (final IWorkbenchWindow window : PlatformUI.getWorkbench()
					.getWorkbenchWindows())
			{
				window.getSelectionService().addPostSelectionListener(listener);
			}

			// add listener to notice also new windows
			PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {

				@Override
				public void windowOpened(IWorkbenchWindow window)
				{}

				@Override
				public void windowDeactivated(IWorkbenchWindow window)
				{
					window.getSelectionService().removePostSelectionListener(listener);
				}

				@Override
				public void windowClosed(IWorkbenchWindow window)
				{}

				@Override
				public void windowActivated(IWorkbenchWindow window)
				{
					window.getSelectionService().addPostSelectionListener(listener);
				}
			});

			for (IWorkbenchWindow window : PlatformUI.getWorkbench()
					.getWorkbenchWindows())
			{

				for (IWorkbenchPage wbPage : window.getPages())
				{
					for (IViewReference reference : wbPage.getViewReferences())
					{
						page.hideView(reference);
					}

				}
			}

			IDE.openEditor(page, new FileEditorInput(customerWorkspaceFile),
					"org.eclipse.jdt.ui.CompilationUnitEditor");
		}
		finally
		{
			System.setOut(out);
		}

		BufferedReader reader = new BufferedReader(new FileReader(tmpFileOutput));
		String line = reader.readLine();

		while (line != null)
		{
			if (line.toLowerCase().contains("nullpointerexception"))
			{
				throw new NullPointerException(tmpFileOutput.toString());
			}

			line = reader.readLine();
		}
	}

	@After
	public void clearWorkspace() throws CoreException
	{
		// Delete Java Project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.delete(true, progressMonitor);
	}

}
