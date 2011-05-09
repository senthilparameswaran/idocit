package de.akra.idocit.java.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.Before;
import org.junit.Ignore;

import de.akra.idocit.java.structure.JavadocTagElement;
import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.Delimiters;
import de.akra.idocit.structure.Documentation;
import de.akra.idocit.structure.Scope;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.utils.TestUtils;

/**
 * Tests for {@link JavadocGenerator}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavadocGeneratorTest
{
	private static Logger logger = Logger.getLogger(JavadocGeneratorTest.class.getName());

	private Document document;

	/**
	 * The delimiters for Java.
	 */
	private static Delimiters delimiters = new Delimiters();

	/**
	 * Initialization method.
	 */
	@Before
	public void init()
	{
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
	@Ignore
	public void testGeneradeJavadoc() throws FileNotFoundException, IOException
	{
		CompilationUnit cu = createCompilationUnit("test/source/ParsingService.java");

		AbstractTypeDeclaration absTypeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		AST ast = absTypeDecl.getAST();
		Javadoc javadoc = ast.newJavadoc();

		List<Documentation> documentations = createDocumentations();
		JavadocGenerator.appendDocsToJavadoc(documentations, null, null, javadoc);
		JavadocGenerator.appendDocsToJavadoc(documentations, TagElement.TAG_PARAM,
				"person", javadoc);

		logger.log(Level.INFO, javadoc.toString());
	}

	/**
	 * Test for
	 * {@link JavaInterfaceGenerator#updateJavadocInAST(de.akra.idocit.java.structure.JavaInterfaceArtifact)}
	 * .
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Ignore
	public void testJavaInterfaceGenerator() throws FileNotFoundException, IOException
	{

		CompilationUnit cu = createCompilationUnit("test/source/ParsingService.java");
		cu.recordModifications();

		TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);
		AST ast = typeDecl.getAST();

		List<JavadocTagElement> jDocTags = new ArrayList<JavadocTagElement>();
		JavadocTagElement tagElem = new JavadocTagElement(TagElement.TAG_PARAM,
				"paramName", createDocumentations());
		jDocTags.add(tagElem);

		Javadoc javadoc = JavaInterfaceGenerator.createOrUpdateJavadoc(jDocTags,
				typeDecl.getJavadoc(), ast);
		typeDecl.setJavadoc(javadoc);

		cu.rewrite(document, null);

		logger.log(Level.INFO, javadoc.toString());
		logger.log(Level.INFO, cu.toString());
	}

	/**
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
				"paramName", createDocumentations());
		jDocTags.add(tagElem);

		Javadoc javadoc = JavaInterfaceGenerator.createOrUpdateJavadoc(jDocTags,
				ast.newJavadoc(), ast);

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
	 * <table border="1">
	 * <tr>
	 * <td>Element:</td>
	 * <td>/person:Person/name:java.lang.String</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td>Role:</td>
	 * <td>OBJECT</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Documenation for developers.</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td><b>Manager</b>:</td>
	 * <td>Documenation for managers.</td>
	 * </tr>
	 * <tr>
	 * </table>
	 * 
	 * <br />
	 * <table border="1">
	 * <tr>
	 * <td>Element:</td>
	 * <td>/person:Person/name:java.lang.String</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td>Role:</td>
	 * <td>OBJECT</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Documenation for developers.</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td><b>Manager</b>:</td>
	 * <td>Documenation for managers.</td>
	 * </tr>
	 * <tr>
	 * </table>
	 * 
	 * @param person
	 * <br />
	 *            <table border="1">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>/person:Person/name:java.lang.String</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>OBJECT</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>Documenation for developers.</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td><b>Manager</b>:</td>
	 *            <td>Documenation for managers.</td>
	 *            </tr>
	 *            <tr>
	 *            </table>
	 * 
	 * <br />
	 *            <table border="1">
	 *            <tr>
	 *            <td>Element:</td>
	 *            <td>/person:Person/name:java.lang.String</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td>Role:</td>
	 *            <td>OBJECT</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td><b>Developer</b>:</td>
	 *            <td>Documenation for developers.</td>
	 *            </tr>
	 *            <tr>
	 *            <tr>
	 *            <td><b>Manager</b>:</td>
	 *            <td>Documenation for managers.</td>
	 *            </tr>
	 *            <tr>
	 *            </table>
	 */
	@SuppressWarnings("unused")
	private void t()
	{
		// method exists only to see a created javadoc comment
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
	 * Create a list of {@link Documentation}s for testing.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createDocumentation());
		documentations.add(createDocumentation());
		return documentations;
	}

	/**
	 * Create a test Documentation.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(new ThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("/person:Person/name:java.lang.String");

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
}
