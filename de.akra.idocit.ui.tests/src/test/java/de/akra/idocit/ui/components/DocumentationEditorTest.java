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
package de.akra.idocit.ui.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.impl.ServiceManager;

public class DocumentationEditorTest
{

	private static final String PROJECT_NAME = "iDocItTestProject";

	private static final String SOURCE_DIR = "src/test/resources/source/";

	@Before
	public void setupWorkspace() throws CoreException, IOException
	{
		/*
		 * The implementation of the initialization of the Test-Java Project has been
		 * guided by http://sdqweb.ipd.kit.edu/wiki/JDT_Tutorial:
		 * _Creating_Eclipse_Java_Projects_Programmatically.
		 * 
		 * Thanks to the authors.
		 */

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

		// Add Java Runtime to the project
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations)
		{
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null,
					null));
		}

		// Add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]),
				null);

		// Create source folder
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(true, true, progressMonitor);

		IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(srcFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		// Create the package
		IFolder packageFolder = srcFolder.getFolder("source");
		packageFolder.create(true, true, progressMonitor);

		// Create Java file
		File customerFile = new File(SOURCE_DIR + "EmptyInterface.java");
		IFile customerWorkspaceFile = packageFolder.getFile("EmptyInterface.java");
		
		FileInputStream javaStream = null;
		
		try
		{
			javaStream = new FileInputStream(customerFile);
			customerWorkspaceFile.create(javaStream, true,
					progressMonitor);
		} 
		finally
		{
			if(javaStream != null)
			{
				javaStream.close();
			}
		}


		project.refreshLocal(IProject.DEPTH_INFINITE, progressMonitor);
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

	@Test
	public void testUseIdocitAsDefaultEditor() throws PartInitException,
			UnitializedIDocItException, InterruptedException
	{
		/*
		 * Positive tests
		 */
		{
			// #########################################################################
			// # Test case #1: iDocIt! does not change the default editor of a file.
			// #########################################################################
			{
				// THIS TEST REQUIRES THE iDocIt! Java Plugin!
				assertTrue(ServiceManager.getInstance().getParsingService()
						.isSupported("java"));

				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(PROJECT_NAME);
				IFolder srcFolder = project.getFolder("src");
				IFolder packageFolder = srcFolder.getFolder("source");
				IFile file = packageFolder.getFile("EmptyInterface.java");

				IPreferenceStore store = PlatformUI.getPreferenceStore();
				{
					store.setValue(PreferenceStoreConstants.DEFAULT_EDITOR_PREFERENCE,
							true);

					IEditorDescriptor editor = IDE.getDefaultEditor(file);
					String oldEditorId = editor.getId();

					// Open iDocIt! editor
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(page, new FileEditorInput(file), DocumentationEditor.ID);
					IDE.setDefaultEditor(file, DocumentationEditor.ID);

					// Close iDocIt! editor
					page.closeAllEditors(true);

					String newEditorId = IDE.getDefaultEditor(file).getId();
					assertEquals(oldEditorId, newEditorId);
				}

				{
					store.setValue(PreferenceStoreConstants.DEFAULT_EDITOR_PREFERENCE,
							false);

					IEditorDescriptor editor = IDE.getDefaultEditor(file);
					String oldEditorId = editor.getId();

					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(page, new FileEditorInput(file), DocumentationEditor.ID);
					IDE.setDefaultEditor(file, DocumentationEditor.ID);

					// Close iDocIt! editor
					page.closeAllEditors(true);

					String newEditorId = IDE.getDefaultEditor(file).getId();
					assertFalse(oldEditorId.equals(newEditorId));
				}
			}
		}

		/*
		 * Negative tests
		 */
		{

		}
	}
}
