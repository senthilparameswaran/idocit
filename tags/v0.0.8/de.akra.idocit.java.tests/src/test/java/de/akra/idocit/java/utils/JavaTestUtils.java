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
package de.akra.idocit.java.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.text.Document;

import de.akra.idocit.common.utils.TestUtils;
import de.akra.idocit.java.structure.ParserOutput;

public class JavaTestUtils
{
	/**
	 * Operating system's line separator.
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * {@link Javadoc#toString()} always uses '\n' as line separator.
	 */
	public static final String JAVADOC_NEW_LINE = "\n";

	/**
	 * Name of the test Java project in the test workspace.
	 */
	public static final String PROJECT_NAME = "iDocItTestProject";

	/**
	 * Relative path within the test Java project {@link #PROJECT_NAME} to the source
	 * files.
	 */
	public static final String REL_SOURCE_PATH = "src/source/";

	/**
	 * Parses a file and returns it as {@link CompilationUnit}.
	 * 
	 * @param fileName
	 *            The Java-file to parse
	 * 
	 * @return The {@link CompilationUnit}.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ParserOutput createCompilationUnit(String fileName)
			throws FileNotFoundException, IOException
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		String fileContent = TestUtils.readFile(fileName);

		Document document = new Document(fileContent);
		parser.setSource(document.get().toCharArray());
		// parser.setSource(fileContent.toCharArray());
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit cu = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);

		ParserOutput output = new ParserOutput();
		output.setCompilationUnit(cu);
		output.setDocument(document);

		return output;
	}

	/**
	 * Creates and initializes a IJavaProject within the currently running test workspace.
	 * 
	 * @destination The current test workspace.
	 * @param projectName
	 *            [PRIMARY_KEY]
	 * @param filesToAdd
	 *            [ATTRIBUTE] files to add to the test workspace
	 * @return [OBJECT] the created project.
	 * @throws CoreException
	 * @throws FileNotFoundException
	 * @thematicgrid Putting Operations
	 */
	public static IProject initProjectInWorkspace(final String projectName,
			final Collection<File> filesToAdd) throws CoreException,
			FileNotFoundException
	{
		/*
		 * The implementation of the initialization of the Test-Java Project has been
		 * guided by http://sdqweb.ipd.kit.edu/wiki/JDT_Tutorial:
		 * _Creating_Eclipse_Java_Projects_Programmatically.
		 * 
		 * Thanks to the authors.
		 */

		// Create Java Project
		final IProgressMonitor progressMonitor = new NullProgressMonitor();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		project.create(progressMonitor);
		project.open(progressMonitor);

		final IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		final IJavaProject javaProject = JavaCore.create(project);
		javaProject.open(progressMonitor);

		// Add Java Runtime to the project
		final List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		final IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		final LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations)
		{
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null,
					null));
		}

		// Add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]),
				null);

		// Create source folder
		final IFolder srcFolder = project.getFolder("src");
		srcFolder.create(true, true, progressMonitor);

		final IPackageFragmentRoot fragmentRoot = javaProject
				.getPackageFragmentRoot(srcFolder);
		final IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		final IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		// Create the package
		final IFolder packageFolder = srcFolder.getFolder("source");
		packageFolder.create(true, true, progressMonitor);

		// Create Java files
		for (final File file : filesToAdd)
		{
			final IFile customerWorkspaceFile = packageFolder.getFile(file.getName());
			customerWorkspaceFile
					.create(new FileInputStream(file), true, progressMonitor);
		}

		project.refreshLocal(IProject.DEPTH_INFINITE, progressMonitor);
		return project;
	}

	/**
	 * Deletes the project with the name {@code projectName} from the current test
	 * workspace.
	 * 
	 * @destination The current test workspace.
	 * @param projectName
	 *            [PRIMARY_KEY]
	 * @throws CoreException
	 * @thematicgrid Removing Operations
	 */
	public static void deleteProjectFromWorkspace(final String projectName)
			throws CoreException
	{
		final IProgressMonitor progressMonitor = new NullProgressMonitor();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		project.delete(true, progressMonitor);
	}
}
