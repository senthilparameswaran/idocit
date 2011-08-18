/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.core.utils.ObjectStructureUtils;
import de.akra.idocit.core.utils.TestUtils;

/**
 * Tests for {@link JavaInterfaceParser}.
 * <p>
 * Run this test as JUnit Plug-in Test or initialize in {@link ObjectStructureUtils} the
 * private attributes <code>supportedAddressees</code> and
 * <code>supportedThematicRoles</code> with {@link Collections#emptyList()}, because the
 * Eclipse Workspace is not available in JUnit Test.
 * </p>
 * <p>
 * Note: Resolving type binding does not work in the tests. Therefore the inner structure
 * (public accessible attributes) could not be tested.
 * </p>
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaInterfaceParserTest
{
	private static Logger logger = Logger.getLogger(JavaInterfaceParserTest.class
			.getName());

	private Delimiters delimiters;

	private ASTParser parser;

	/**
	 * Contains no public class, interface etc.
	 */
	private static final String EXPECTED_CLASS_STRUCTURE_TEST = "test/source/test.java [Artifact]\n";

	/**
	 * Class with two methods.
	 */
	private static final String EXPECTED_CLASS_STRUCTURE_PARSING_SERVICE = "test/source/ParsingService.java [Artifact]\n"
			+ "	ParsingService [Class]\n"
			+ "		getParser [Method]\n"
			+ "			 [Parameters]\n"
			+ "				type (Type: String)\n"
			+ "			 [ReturnType]\n"
			+ "				Parser (Type: Parser)\n"
			+ "		isSupported [Method]\n"
			+ "			 [Parameters]\n"
			+ "				type (Type: String)\n"
			+ "			 [ReturnType]\n"
			+ "				boolean (Type: boolean)\n";

	/**
	 * Class with recursive object definition.
	 */
	private static final String EXPECTED_CLASS_STRUCTURE_TYPE_RECURSION = "test/source/TypeRecursion.java [Artifact]\n"
			+ "	TypeRecursion [Class]\n"
			+ "		test [Method]\n"
			+ "			 [Parameters]\n"
			+ "				t (Type: TypeRecursion)\n"
			+ "		setTypeRec [Method]\n"
			+ "			 [Parameters]\n"
			+ "				typeRec (Type: TypeRecursion)\n"
			+ "				typeRec2 (Type: TypeRecursion)\n"
			+ "		getTypeRec [Method]\n"
			+ "			 [ReturnType]\n" + "				TypeRecursion (Type: TypeRecursion)\n";

	/**
	 * Class with recursive object definition and two inner classes.
	 */
	private static final String EXPECTED_CLASS_STRUCTURE_TEST_RECURSION = "test/source/TestRecursion.java [Artifact]\n"
			+ "	TestRecursion [Class]\n"
			+ "		getSomeContainer [Method]\n"
			+ "			 [Parameters]\n"
			+ "				parent (Type: Container)\n"
			+ "			 [ReturnType]\n"
			+ "				Container (Type: Container)\n"
			+ "		getSomeContainer2 [Method]\n"
			+ "			 [Parameters]\n"
			+ "				parent (Type: Container2)\n"
			+ "			 [ReturnType]\n"
			+ "				Container2 (Type: Container2)\n"
			+ "		Container [Class]\n"
			+ "			getId [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					Integer (Type: Integer)\n"
			+ "			setId [Method]\n"
			+ "				 [Parameters]\n"
			+ "					id (Type: Integer)\n"
			+ "			getSerial [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					long (Type: long)\n"
			+ "			setSerial [Method]\n"
			+ "				 [Parameters]\n"
			+ "					serial (Type: long)\n"
			+ "			setParent [Method]\n"
			+ "				 [Parameters]\n"
			+ "					parent (Type: Container)\n"
			+ "			getParent [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					Container (Type: Container)\n"
			+ "			setInnerContainers [Method]\n"
			+ "				 [Parameters]\n"
			+ "					innerContainers (Type: List<Container>)\n"
			+ "			getInnerContainers [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					List<Container> (Type: List<Container>)\n"
			+ "		Container2 [Class]\n"
			+ "			getContainer [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					Container (Type: Container)\n"
			+ "			setContainer [Method]\n"
			+ "				 [Parameters]\n"
			+ "					container (Type: Container)\n"
			+ "			getContainers [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					List<Container> (Type: List<Container>)\n"
			+ "			setContainers [Method]\n"
			+ "				 [Parameters]\n"
			+ "					containers (Type: List<Container>)\n"
			+ "			setContainer2 [Method]\n"
			+ "				 [Parameters]\n"
			+ "					container2 (Type: Container)\n"
			+ "			getContainer2 [Method]\n"
			+ "				 [ReturnType]\n"
			+ "					Container (Type: Container)\n";

	/**
	 * Init things before tests.
	 */
	@Before
	public void before()
	{
		this.delimiters = new Delimiters();
		delimiters.pathDelimiter = "/";
		delimiters.namespaceDelimiter = ".";
		delimiters.typeDelimiter = ":";

		this.parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
	}

	/**
	 * Test for parsing a CompilationUnit. Tests only the class structure, no Javadoc.
	 * <ul>
	 * <li>Tests a common class</li>
	 * <li>Tests a class file with no public class, interface etc.</li>
	 * <li>Tests a class with recursive object definitions</li>
	 * <li>Tests a class with recursive object definitions and two inner classes</li>
	 * </ul>
	 * 
	 * @throws Exception
	 * @see {@link ReflectionHelper#reflectParameter(de.akra.idocit.core.structure.SignatureElement, org.eclipse.jdt.core.dom.ITypeBinding, String, String)}
	 */
	@Test
	public void testParse() throws Exception
	{
		Assert.assertEquals(EXPECTED_CLASS_STRUCTURE_PARSING_SERVICE,
				testParseWith("test/source/ParsingService.java"));

		Assert.assertEquals(EXPECTED_CLASS_STRUCTURE_TEST,
				testParseWith("test/source/test.java"));

		Assert.assertEquals(EXPECTED_CLASS_STRUCTURE_TYPE_RECURSION,
				testParseWith("test/source/TypeRecursion.java"));

		Assert.assertEquals(EXPECTED_CLASS_STRUCTURE_TEST_RECURSION,
				testParseWith("test/source/TestRecursion.java"));
	}

	/**
	 * Parses the file <code>fileName</code>, prints the structure and returns the
	 * structure as string.
	 * 
	 * @param fileName
	 * @return a simple string representation of the class structure.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CoreException
	 */
	private String testParseWith(String fileName) throws FileNotFoundException,
			IOException, SAXException, ParserConfigurationException, CoreException
	{
		File file = new File(fileName);

		// create project to link source file to it. Is needed to get an IFile.
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject project = ws.getRoot().getProject("External Files");
		if (!project.exists())
		{
			project.create(null);
		}
		if (!project.isOpen())
		{
			project.open(null);
		}

		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile iFile = project.getFile(location.lastSegment());
		Assert.assertNotNull(iFile);
		iFile.createLink(location, IResource.NONE, null);

		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(iFile);
		Assert.assertNotNull(iCompilationUnit);

		parser.setSource(iCompilationUnit.getWorkingCopy(null));
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		JavaInterfaceParser iParser = new JavaInterfaceParser(cu, fileName, delimiters);
		InterfaceArtifact artifact = iParser.parse();

		StringBuffer hierarchy = new StringBuffer();
		TestUtils.buildHierarchy(hierarchy, artifact, 0);
		logger.log(Level.INFO, fileName);
		logger.log(Level.INFO, hierarchy.toString());
		return hierarchy.toString();
	}
}
