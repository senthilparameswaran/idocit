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
package de.akra.idocit.java.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.JavadocTestUtils;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.ParserOutput;
import de.akra.idocit.java.utils.JavaInterfaceArtifactComparatorUtils;
import de.akra.idocit.java.utils.TestDataFactory;

/**
 * Tests for {@link JavaParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaParserTest
{
	private static Logger logger = Logger.getLogger(JavaParserTest.class.getName());

	private static final String PROJECT_NAME = "iDocItTestProject";

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

		// Create Java files
		File customerFile = new File("test/source/Customer.java");
		IFile customerWorkspaceFile = packageFolder.getFile("Customer.java");
		customerWorkspaceFile.create(new FileInputStream(customerFile), true,
				progressMonitor);

		File nameParametersFile = new File("test/source/NameParameters.java");
		IFile nameParametersWorkspaceFile = packageFolder.getFile("NameParameters.java");
		nameParametersWorkspaceFile.create(new FileInputStream(nameParametersFile), true,
				progressMonitor);

		File customerNameParamFile = new File("test/source/CustomerNameParameters.java");
		IFile customerNameParamWorkspaceFile = packageFolder
				.getFile("CustomerNameParameters.java");
		customerNameParamWorkspaceFile.create(new FileInputStream(customerNameParamFile),
				true, progressMonitor);

		File javaFile = new File("test/source/CustomerService.java");
		IFile javaWorkspaceFile = packageFolder.getFile("CustomerService.java");
		javaWorkspaceFile.create(new FileInputStream(javaFile), true, progressMonitor);

		File specialExceptionFile = new File("test/source/SpecialException.java");
		IFile specialExceptionWorkspaceFile = packageFolder
				.getFile("SpecialException.java");
		specialExceptionWorkspaceFile.create(new FileInputStream(specialExceptionFile),
				true, progressMonitor);

		project.refreshLocal(IProject.DEPTH_INFINITE, progressMonitor);

		// Activate Simple Parser
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE);
	}

	@After
	public void clearWorkspace() throws CoreException
	{
		// Delete Java Project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.delete(true, progressMonitor);

		// Deactivate Simple Parser
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(PreferenceStoreConstants.JAVADOC_GENERATION_MODE, "");

		// Delete classpath file
		File classPathFile = new File("test/source/.classpath");
		classPathFile.delete();
	}

	/**
	 * Tests {@link JavaParser#parse(org.eclipse.core.resources.IFile)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJavaParserWithSimpleParser() throws Exception
	{
		ParserOutput output = JavadocTestUtils
				.createCompilationUnit("test/source/CustomerService.java");
		CompilationUnit cu = output.getCompilationUnit();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		IFile testSourceFolder = project.getFile("src/source/CustomerService.java");

		JavaInterfaceArtifact refInterfaceArtifact = TestDataFactory
				.createCustomerService("Developer", false, cu);

		JavaInterfaceArtifact actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager
				.getInstance().getPersistenceService().loadInterface(testSourceFolder);

		JavaInterface refInterface = (JavaInterface) refInterfaceArtifact.getInterfaces()
				.get(0);
		JavaInterface actInterface = (JavaInterface) actInterfaceArtifact.getInterfaces()
				.get(0);

		JavaMethod refMethod = (JavaMethod) refInterface.getOperations().get(0);
		JavaMethod actMethod = (JavaMethod) actInterface.getOperations().get(0);

		List<? extends Parameter> refInputParameters = refMethod.getInputParameters()
				.getParameters();
		List<? extends Parameter> actInputParameters = actMethod.getInputParameters()
				.getParameters();

		List<? extends Parameter> refOutputParameters = refMethod.getOutputParameters()
				.getParameters();
		List<? extends Parameter> actOutputParameters = actMethod.getOutputParameters()
				.getParameters();

		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsParameters(
				refInputParameters, actInputParameters));
		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsParameters(
				refOutputParameters, actOutputParameters));
		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsExceptions(
				refMethod.getExceptions(), actMethod.getExceptions()));
		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsMethods(refMethod,
				actMethod));
		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsInterfaces(
				refInterface, actInterface));
		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsInterfaceArtifacts(
				refInterfaceArtifact, actInterfaceArtifact));

		ServiceManager.getInstance().getPersistenceService()
				.writeInterface(actInterfaceArtifact, testSourceFolder);

		actInterfaceArtifact = (JavaInterfaceArtifact) ServiceManager.getInstance()
				.getPersistenceService().loadInterface(testSourceFolder);

		Assert.assertTrue(JavaInterfaceArtifactComparatorUtils.equalsInterfaceArtifacts(
				refInterfaceArtifact, actInterfaceArtifact));
	}

	/**
	 * Try using the AST.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAST() throws Exception
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		String fileContent = TestUtils.readFile("test/source/ParsingService.java");

		parser.setSource(fileContent.toCharArray());
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit cu = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);

		@SuppressWarnings("unchecked")
		List<TypeDeclaration> types = (List<TypeDeclaration>) cu.types();
		logger.log(Level.INFO, "types.size=" + types.size());

		TypeDeclaration td = types.get(0);
		MethodDeclaration[] methods = td.getMethods();
		logger.log(Level.INFO, "methods.length=" + methods.length);

		for (MethodDeclaration method : methods)
		{
			logger.log(Level.INFO, "\t"
					+ (method.getJavadoc() != null ? method.getJavadoc().toString()
							: "// no javadoc"));
			logger.log(Level.INFO, "\t" + method.getName().toString());
		}
	}

}