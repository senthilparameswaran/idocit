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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Scope;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.structure.JavadocTagElement;

/**
 * Tests for {@link JavadocGenerator}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavadocGeneratorTest
{
	private static Logger logger = Logger.getLogger(JavadocGeneratorTest.class.getName());

	private static final String lineSeparator = "\n";

	/**
	 * The result "@paramperson" is correct because javadoc.toString() does not write a
	 * space between them. But if it is written into a file it is correct "@param person".
	 * The same applies to "@return".
	 */
	private static final String EXPECTED_JAVADOC = "/** "
			+ lineSeparator
			+ " * <table name=\"idocit\" border=\"1\" cellspacing=\"0\">"
			+ lineSeparator
			+ "<tr><td>Element:</td><td>person:Person/name:java.lang.String</td></tr>"
			+ lineSeparator
			+ "<tr><td>Role:</td><td>OBJECT</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Developer</b>:</td><td>Documenation for developers.</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Manager</b>:</td><td>Documenation for managers.</td></tr>"
			+ lineSeparator
			+ "</table>"
			+ lineSeparator
			+ " * "
			+ lineSeparator
			+ "<br /><table name=\"idocit\" border=\"1\" cellspacing=\"0\">"
			+ lineSeparator
			+ "<tr><td>Element:</td><td>person:Person/name:java.lang.String</td></tr>"
			+ lineSeparator
			+ "<tr><td>Role:</td><td>OBJECT</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Developer</b>:</td><td>Documenation for developers.</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Manager</b>:</td><td>Documenation for managers.</td></tr>"
			+ lineSeparator
			+ "</table>"
			+ lineSeparator
			+ " * @paramperson"
			+ lineSeparator
			+ " * "
			+ lineSeparator
			+ "<br /><table name=\"idocit\" border=\"1\" cellspacing=\"0\">"
			+ lineSeparator
			+ "<tr><td>Element:</td><td>person:Person/name:java.lang.String</td></tr>"
			+ lineSeparator
			+ "<tr><td>Role:</td><td>OBJECT</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Developer</b>:</td><td>Documenation for developers.</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Manager</b>:</td><td>Documenation for managers.</td></tr>"
			+ lineSeparator
			+ "</table>"
			+ lineSeparator
			+ " * "
			+ lineSeparator
			+ "<br /><table name=\"idocit\" border=\"1\" cellspacing=\"0\">"
			+ lineSeparator
			+ "<tr><td>Element:</td><td>person:Person/name:java.lang.String</td></tr>"
			+ lineSeparator
			+ "<tr><td>Role:</td><td>OBJECT</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Developer</b>:</td><td>Documenation for developers.</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Manager</b>:</td><td>Documenation for managers.</td></tr>"
			+ lineSeparator
			+ "</table>"
			+ lineSeparator
			+ " * @return<table name=\"idocit\" border=\"1\" cellspacing=\"0\">"
			+ lineSeparator
			+ "<tr><td>Element:</td><td>double:double</td></tr>"
			+ lineSeparator
			+ "<tr><td>Role:</td><td>RESULT</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Developer</b>:</td><td>Developer: Result as floating-point number.</td></tr>"
			+ lineSeparator
			+ "<tr><td><b>Manager</b>:</td><td>Manager: Result as floating-point number.</td></tr>"
			+ lineSeparator + "</table>" + lineSeparator
			+ " * @thematicgrid Searching Operations" + lineSeparator + " */"
			+ lineSeparator;

	/**
	 * The delimiters for Java.
	 */
	private static Delimiters delimiters = new Delimiters();

	private Document document;

	/**
	 * Initialization method.
	 */
	@Before
	public void init()
	{
		// must be the same delimiters as in JavaParser
		delimiters.pathDelimiter = "/";
		delimiters.namespaceDelimiter = ".";
		delimiters.typeDelimiter = ":";
	}

	/**
	 * Tests {@link JavadocGenerator#appendDocsToJavadoc(List, String, String, Javadoc)} .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGeneradeJavadoc() throws FileNotFoundException, IOException
	{
		CompilationUnit cu = createCompilationUnit("test/source/ParsingService.java");

		AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		AST ast = absTypeDecl.getAST();
		Javadoc javadoc = ast.newJavadoc();

		List<Documentation> documentations = createParamDocumentations();
		JavadocGenerator.appendDocsToJavadoc(documentations, null, null, javadoc);
		JavadocGenerator.appendDocsToJavadoc(documentations, TagElement.TAG_PARAM,
				"person", javadoc);
		JavadocGenerator.appendDocsToJavadoc(createReturnDocumentations(),
				TagElement.TAG_RETURN, null, javadoc);

		TagElement tagElement = ast.newTagElement();
		tagElement.setTagName(JavadocParser.JAVADOC_TAG_THEMATICGRID);

		List<ASTNode> fragments = (List<ASTNode>) tagElement.fragments();
		TextElement textElement = ast.newTextElement();
		textElement.setText(" Searching Operations");
		fragments.add(textElement);

		List<TagElement> tags = javadoc.tags();
		tags.add(tagElement);
		
		// logger.info(javadoc.toString());

		Assert.assertEquals(EXPECTED_JAVADOC, javadoc.toString());
	}

	/**
	 * Test for
	 * {@link JavaInterfaceGenerator#updateJavadocInAST(de.akra.idocit.java.structure.JavaInterfaceArtifact)}
	 * . Creates a Javadoc and writes it into a Java source file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testJavaInterfaceGenerator() throws FileNotFoundException, IOException
	{
		CompilationUnit cu = createCompilationUnit("test/source/ParsingService.java");
		cu.recordModifications();

		TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);
		AST ast = typeDecl.getAST();

		List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
		JavadocTagElement tagElem = new JavadocTagElement(TagElement.TAG_PARAM,
				"paramName", createParamDocumentations());
		jDocTags.add(tagElem);

		tagElem = new JavadocTagElement(TagElement.TAG_RETURN, null,
				createReturnDocumentations());
		jDocTags.add(tagElem);

		List<TagElement> additionalTags = Collections.emptyList();
		Javadoc javadoc = JavaInterfaceGenerator.createOrUpdateJavadoc(jDocTags,
				additionalTags, typeDecl.getJavadoc(), ast, null);
		typeDecl.setJavadoc(javadoc);

		cu.rewrite(document, null);

		TypeDeclaration newTypeDecl = (TypeDeclaration) cu.types().get(0);

		// logger.log(Level.INFO, javadoc.toString());
		// logger.log(Level.INFO, cu.toString());

		Assert.assertTrue(
				"The written Javadoc in the CompilationUnit is not the same as the created Javadoc.",
				cu.toString().contains(javadoc.toString()));
		Assert.assertEquals(javadoc, newTypeDecl.getJavadoc());
	}

	/**
	 * This is not a real unit test. It is a trial to use the ITextFileBufferManager to
	 * save the changes in a CompilationUnit.<br />
	 * Test for
	 * {@link JavaInterfaceGenerator#updateJavadocInAST(de.akra.idocit.java.structure.JavaInterfaceArtifact)}
	 * .
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BadLocationException
	 * @throws MalformedTreeException
	 * @throws CoreException
	 */
	@Ignore
	public void testJavaInterfaceGenerator2() throws FileNotFoundException, IOException,
			MalformedTreeException, BadLocationException, CoreException
	{

		CompilationUnit unit = createCompilationUnit("test/source/ParsingService2.java");

		String originalCU = unit.toString();

		TypeDeclaration typeDecl = (TypeDeclaration) unit.types().get(0);
		AST ast = typeDecl.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
		JavadocTagElement tagElem = new JavadocTagElement(TagElement.TAG_PARAM,
				"paramName", createParamDocumentations());
		jDocTags.add(tagElem);

		List<TagElement> additionalTags = Collections.emptyList();
		Javadoc javadoc = JavaInterfaceGenerator.createOrUpdateJavadoc(jDocTags,
				additionalTags, ast.newJavadoc(), ast, null);

		rewriter.set(typeDecl, TypeDeclaration.JAVADOC_PROPERTY, javadoc, null);

		// get the buffer manager
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = unit.getJavaElement().getPath();

		try
		{
			bufferManager.connect(path, LocationKind.IFILE, null);
			// retrieve the buffer
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path,
					LocationKind.IFILE);

			if (textFileBuffer != null)
			{
				logger.log(Level.INFO,
						"isSynchronized=" + textFileBuffer.isSynchronized());

				IDocument document = textFileBuffer.getDocument();

				logger.log(Level.INFO, document.toString());

				// ... edit the document ...

				TextEdit edit = rewriter.rewriteAST(document, null);
				edit.apply(document);

				// iCompUnit.getBuffer().setContents(document.get());
				// unit =
				// iCompUnit.reconcile(ICompilationUnit.ENABLE_BINDINGS_RECOVERY,
				// false, null, null);

				// commit changes to underlying file
				textFileBuffer.commit(null /* ProgressMonitor */, false /* Overwrite */);
			}
			else
			{
				logger.log(Level.SEVERE, "textFileBuffer == null");
			}
		}
		finally
		{
			bufferManager.disconnect(path, LocationKind.IFILE, null);
		}

		String changedCU = unit.toString();

		logger.log(Level.INFO, originalCU);
		logger.log(Level.INFO, changedCU);
	}

	/**
	 * Parses a file and returns it as {@link CompilationUnit}.
	 * 
	 * @return The {@link CompilationUnit}.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private CompilationUnit createCompilationUnit(String fileName)
			throws FileNotFoundException, IOException
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		String fileContent = TestUtils.readFile(fileName);

		document = new Document(fileContent);
		parser.setSource(document.get().toCharArray());
		// parser.setSource(fileContent.toCharArray());
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit cu = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);
		return cu;
	}

	/**
	 * Create a list of {@link Documentation}s for a method parameter.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createParamDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createParamDocumentation());
		documentations.add(createParamDocumentation());
		return documentations;
	}

	/**
	 * Create a list with one {@link Documentation} for a method return value.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createReturnDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createReturnDocumentation());
		return documentations;
	}

	/**
	 * Create a test Documentation for a method parameter.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createParamDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(new ThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("person:Person/name:java.lang.String");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = new Addressee("Developer");
		Addressee manager = new Addressee("Manager");

		docMap.put(developer, "Documenation for developers.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Documenation for managers.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}

	/**
	 * Create a test Documentation for a method return value.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createReturnDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(new ThematicRole("RESULT"));

		newDoc.setSignatureElementIdentifier("double:double");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = new Addressee("Developer");
		Addressee manager = new Addressee("Manager");

		docMap.put(developer, "Developer: Result as floating-point number.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Manager: Result as floating-point number.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}
}
