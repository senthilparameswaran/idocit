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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.core.constants.ThematicGridConstants;
import de.akra.idocit.core.utils.DescribedItemUtils;
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavaParameter;
import de.akra.idocit.java.structure.JavaParameters;

/**
 * Tests for {@link JavaInterfaceParser}.
 * <p>
 * Run this test as JUnit Plug-in Test or initialize in {@link DescribedItemUtils} the
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
	 * Category constants are copied from {@link JavaInterfaceParser}.
	 */
	private static final String CATEGORY_ARTIFACT = "Artifact";
	private static final String CATEGORY_CLASS = "Class";
	private static final String CATEGORY_METHOD = "Method";
	private static final String CATEGORY_CONSTRUCTOR = "Constructor";
	private static final String CATEGORY_PARAMETERS = "Parameters";
	private static final String CATEGORY_RETURN_TYPE = "ReturnType";
	private static final String CATEGORY_THROWS = "Throws";

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
	 * Tests the conversion of existing Javadoc to {@link Documentation}s. The
	 * documentations must also be assigned to the right {@link SignatureElements}, e.g.
	 * "@param ..." to a method's input {@link Parameter} etc.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJavadocConversion() throws Exception
	{
		String testFileName = "test/source/JavaInterfaceParser.java";
		IFile iFile = TestUtils.makeIFileFromFileName(testFileName);

		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(iFile);
		Assert.assertNotNull(iCompilationUnit);

		parser.setSource(iCompilationUnit.getWorkingCopy(null));
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		JavaInterfaceParser iParser = new JavaInterfaceParser(cu, testFileName,
				delimiters);
		InterfaceArtifact actualArtifact = iParser.parse();

		JavaInterfaceArtifact expectedArtifact = createExpectedArtifact(testFileName, cu);
		Assert.assertEquals(TestUtils.toStringWithoutId(expectedArtifact),
				TestUtils.toStringWithoutId(actualArtifact));
	}

	/**
	 * Creates the expected {@link JavaInterfaceArtifact} for the source
	 * "test/source/JavaInterfaceParser.java".
	 * 
	 * @param fileName
	 *            The file name of the source file.
	 * @param cu
	 *            The {@link CompilationUnit} of the source file.
	 * @return {@link JavaInterfaceArtifact}
	 * @throws JavaModelException
	 */
	@SuppressWarnings("unchecked")
	private JavaInterfaceArtifact createExpectedArtifact(String fileName,
			CompilationUnit cu) throws JavaModelException
	{
		Addressee developer = DescribedItemUtils.findAddressee("Developer");

		JavaInterfaceArtifact artifact = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, CATEGORY_ARTIFACT, cu,
				Numerus.SINGULAR);
		artifact.setIdentifier(fileName);

		ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
		artifact.setOriginalDocument(icu.getSource());

		AST ast = AST.newAST(AST.JLS3);

		/*
		 * Interface
		 */
		JavaInterface jInterface = new JavaInterface(artifact, CATEGORY_CLASS,
				Numerus.SINGULAR);
		jInterface.setIdentifier("JavaInterfaceParser");
		jInterface.setQualifiedIdentifier("JavaInterfaceParser");
		jInterface
				.addDocpart(makeDocumentation(
						developer,
						null,
						"<p><b>This is a test class used in JUnit test!!</b></p>The parser parses Java Interfaces, Classes and Enumerations and maps the structure tothe iDocIt structure."));
		artifact.addInterface(jInterface);

		List<TagElement> tags = new ArrayList<TagElement>();
		TagElement tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_AUTHOR);
		TextElement textElem = ast.newTextElement();
		textElem.setText(" Dirk Meier-Eickhoff");
		tag.fragments().add(textElem);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_SINCE);
		textElem = ast.newTextElement();
		textElem.setText(" 0.0.1");
		tag.fragments().add(textElem);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_VERSION);
		textElem = ast.newTextElement();
		textElem.setText(" 0.0.1");
		tag.fragments().add(textElem);
		tags.add(tag);

		jInterface.setAdditionalTags(tags);

		List<JavaMethod> methods = new ArrayList<JavaMethod>();
		jInterface.setOperations(methods);

		/*
		 * Constructor
		 */
		JavaMethod method = new JavaMethod(jInterface, CATEGORY_CONSTRUCTOR,
				ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME, Numerus.SINGULAR);
		method.setIdentifier("JavaInterfaceParser");
		method.setQualifiedIdentifier("JavaInterfaceParser");
		method.addDocpart(makeDocumentation(developer, null,
				"This is the constructor."));
		methods.add(method);

		/*
		 * Constructor -> input params
		 */
		JavaParameters inputParams = new JavaParameters(method, CATEGORY_PARAMETERS,
				Numerus.SINGULAR, false);
		inputParams.setIdentifier("");
		inputParams.setQualifiedIdentifier("");
		method.setInputParameters(inputParams);

		JavaParameter param = new JavaParameter(inputParams, Numerus.SINGULAR, false);
		param.setIdentifier("compilationUnit");
		param.setQualifiedIdentifier("compilationUnit");
		param.setDataTypeName("CompilationUnit");
		param.setQualifiedDataTypeName("CompilationUnit");
		param.setSignatureElementPath("compilationUnit:CompilationUnit");
		param.addDocpart(makeDocumentation(developer, 
				"compilationUnit:CompilationUnit",
				"The {@link CompilationUnit} that should be parsed &amp; checked."));
		inputParams.addParameter(param);

		param = new JavaParameter(inputParams, Numerus.SINGULAR, false);
		param.setIdentifier("artifactName");
		param.setQualifiedIdentifier("artifactName");
		param.setDataTypeName("String");
		param.setQualifiedDataTypeName("String");
		param.setSignatureElementPath("artifactName:String");
		param.addDocpart(makeDocumentation(developer, 
				"artifactName:String",
				"The name for the CompilationUnit (in general the Java source file)."));
		inputParams.addParameter(param);

		param = new JavaParameter(inputParams, Numerus.SINGULAR, false);
		param.setIdentifier("delimiters");
		param.setQualifiedIdentifier("delimiters");
		param.setDataTypeName("Delimiters");
		param.setQualifiedDataTypeName("Delimiters");
		param.setSignatureElementPath("delimiters:Delimiters");
		param.addDocpart(makeDocumentation(developer, 
				"delimiters:Delimiters", "The {@link Delimiters} for creating paths."));
		inputParams.addParameter(param);

		/*
		 * Method
		 */
		method = new JavaMethod(jInterface, CATEGORY_METHOD,
				ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME, Numerus.SINGULAR);
		method.setIdentifier("parse");
		method.setQualifiedIdentifier("parse");
		method.addDocpart(makeDocumentation(
				developer,
				null,
				"Parses the {@link CompilationUnit} <code>compilationUnit</code> (Java source file)and converts it to a {@link JavaInterfaceArtifact}. (Read{@link JavaInterfaceArtifact#copy(de.akra.idocit.common.structure.SignatureElement)})"));

		tags = new ArrayList<TagElement>();
		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_SEE);
		SimpleName name = ast.newSimpleName("JavaModelException");
		tag.fragments().add(name);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_SEE);
		MethodRef mRef = ast.newMethodRef();
		mRef.setName(ast.newSimpleName("parse"));

		MethodRefParameter mRefParam = ast.newMethodRefParameter();
		mRefParam.setType(ast.newPrimitiveType(PrimitiveType.INT));
		mRef.parameters().add(mRefParam);

		mRefParam = ast.newMethodRefParameter();
		mRefParam.setType(ast.newSimpleType(ast.newName("String")));
		mRef.parameters().add(mRefParam);

		tag.fragments().add(mRef);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_AUTHOR);
		textElem = ast.newTextElement();
		textElem.setText(" Dirk Meier-Eickhoff");
		tag.fragments().add(textElem);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_SINCE);
		textElem = ast.newTextElement();
		textElem.setText(" 0.0.1");
		tag.fragments().add(textElem);
		tags.add(tag);

		tag = ast.newTagElement();
		tag.setTagName(TagElement.TAG_VERSION);
		textElem = ast.newTextElement();
		textElem.setText(" 0.0.4");
		tag.fragments().add(textElem);
		tags.add(tag);

		method.setAdditionalTags(tags);
		methods.add(method);

		/*
		 * Method -> input params
		 */
		inputParams = new JavaParameters(method, CATEGORY_PARAMETERS, Numerus.SINGULAR,
				false);
		inputParams.setIdentifier("");
		inputParams.setQualifiedIdentifier("");
		method.setInputParameters(inputParams);

		param = new JavaParameter(inputParams, Numerus.SINGULAR, false);
		param.setIdentifier("anyNumber");
		param.setQualifiedIdentifier("anyNumber");
		param.setDataTypeName("int");
		param.setQualifiedDataTypeName("int");
		param.setSignatureElementPath("anyNumber:int");
		param.addDocpart(makeDocumentation(developer, "anyNumber:int",
				"This is only any number."));
		inputParams.addParameter(param);

		param = new JavaParameter(inputParams, Numerus.SINGULAR, false);
		param.setIdentifier("anyString");
		param.setQualifiedIdentifier("anyString");
		param.setDataTypeName("String");
		param.setQualifiedDataTypeName("String");
		param.setSignatureElementPath("anyString:String");
		param.addDocpart(makeDocumentation(developer, "anyString:String",
				"This is only any simple String. {@literal  This Is A Literal}."));
		inputParams.addParameter(param);

		param = new JavaParameter(inputParams, Numerus.PLURAL, false);
		param.setIdentifier("names");
		param.setQualifiedIdentifier("names");
		param.setDataTypeName("List<String>");
		param.setQualifiedDataTypeName("List<String>");
		param.setSignatureElementPath("names:List<String>");
		param.addDocpart(makeDocumentation(developer, 
				"names:List<String>", "The list of names"));
		inputParams.addParameter(param);

		/*
		 * Method -> output param
		 */
		JavaParameters outputParam = new JavaParameters(method, CATEGORY_RETURN_TYPE,
				Numerus.SINGULAR, false);
		outputParam.setIdentifier("");
		outputParam.setQualifiedIdentifier("");
		method.setOutputParameters(outputParam);

		param = new JavaParameter(outputParam, Numerus.SINGULAR, true);
		param.setIdentifier("InterfaceArtifact");
		param.setQualifiedIdentifier("InterfaceArtifact");
		param.setDataTypeName("InterfaceArtifact");
		param.setQualifiedDataTypeName("InterfaceArtifact");
		param.setSignatureElementPath("InterfaceArtifact:InterfaceArtifact");
		param.addDocpart(makeDocumentation(developer, 
				"InterfaceArtifact:InterfaceArtifact",
				"a new {@link JavaInterfaceArtifact}."));
		outputParam.addParameter(param);

		/*
		 * Method -> exceptions
		 */
		List<JavaParameters> exceptionList = new ArrayList<JavaParameters>();
		method.setExceptions(exceptionList);

		JavaParameters exceptions = new JavaParameters(method, CATEGORY_THROWS,
				Numerus.SINGULAR, false);
		exceptions.setIdentifier("");
		exceptions.setQualifiedIdentifier("");
		exceptionList.add(exceptions);

		param = new JavaParameter(exceptions, Numerus.SINGULAR, false);
		param.setIdentifier("JavaModelException");
		param.setQualifiedIdentifier("JavaModelException");
		param.setDataTypeName("JavaModelException");
		param.setQualifiedDataTypeName("JavaModelException");
		param.setSignatureElementPath("JavaModelException:JavaModelException");
		param.addDocpart(makeDocumentation(developer, 
				"JavaModelException:JavaModelException",
				"if an error occurs by getting the source code from ICompilationUnit."));
		exceptions.addParameter(param);

		param = new JavaParameter(exceptions, Numerus.SINGULAR, false);
		param.setIdentifier("SAXException");
		param.setQualifiedIdentifier("SAXException");
		param.setDataTypeName("SAXException");
		param.setQualifiedDataTypeName("SAXException");
		param.setSignatureElementPath("SAXException:SAXException");
		param.addDocpart(makeDocumentation(developer, 
				"SAXException:SAXException", ""));
		exceptions.addParameter(param);

		param = new JavaParameter(exceptions, Numerus.SINGULAR, false);
		param.setIdentifier("IOException");
		param.setQualifiedIdentifier("IOException");
		param.setDataTypeName("IOException");
		param.setQualifiedDataTypeName("IOException");
		param.setSignatureElementPath("IOException:IOException");
		param.addDocpart(makeDocumentation(developer, 
				"IOException:IOException", ""));
		exceptions.addParameter(param);

		param = new JavaParameter(exceptions, Numerus.SINGULAR, false);
		param.setIdentifier("ParserConfigurationException");
		param.setQualifiedIdentifier("ParserConfigurationException");
		param.setDataTypeName("ParserConfigurationException");
		param.setQualifiedDataTypeName("ParserConfigurationException");
		param.setSignatureElementPath("ParserConfigurationException:ParserConfigurationException");
		param.addDocpart(makeDocumentation(developer,
				"ParserConfigurationException:ParserConfigurationException", ""));
		exceptions.addParameter(param);

		return artifact;
	}

	/**
	 * Create a new {@link Documentation} with the given information.
	 * 
	 * @param addressee
	 * @param signatureElementIdentifier
	 * @param text
	 * @return A new {@link Documentation} initialized with the given information.
	 */
	private Documentation makeDocumentation(Addressee addressee,
			String signatureElementIdentifier, String text)
	{
		Documentation doc = new Documentation();
		doc.setSignatureElementIdentifier(signatureElementIdentifier);
		doc.getAddresseeSequence().add(addressee);
		doc.getDocumentation().put(addressee, text);
		return doc;
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
		IFile iFile = TestUtils.makeIFileFromFileName(fileName);

		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(iFile);
		Assert.assertNotNull(iCompilationUnit);

		parser.setSource(iCompilationUnit.getWorkingCopy(null));
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		JavaInterfaceParser iParser = new JavaInterfaceParser(cu, fileName, delimiters);
		InterfaceArtifact artifact = iParser.parse();

		StringBuffer hierarchy = new StringBuffer();
		TestUtils.buildHierarchy(hierarchy, artifact, 0);
		logger.log(Level.FINER, fileName);
		logger.log(Level.FINER, hierarchy.toString());
		return hierarchy.toString();
	}
}
