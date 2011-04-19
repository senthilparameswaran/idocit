package de.akra.idocit.java.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.structure.Delimiters;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.utils.TestUtils;

/**
 * Tests for {@link JavaInterfaceParser}.
 * <p>
 * Hint: Does not work correctly
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
	 * Init things before tests.
	 */
	@Before
	public void before()
	{
		this.delimiters = new Delimiters();
		delimiters.pathDelimiter = "/";
		delimiters.namespaceDelimiter = ":";
		delimiters.typeDelimiter = "-";

		this.parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
	}

	/**
	 * Test for parsing a CompilationUnit.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParse() throws Exception
	{
		testParseWith("test/source/ParsingService.java");
		testParseWith("test/source/test.java");
	}

	/**
	 * Parses the file <code>fileName</code> and prints the structure.
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JavaModelException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private void testParseWith(String fileName) throws FileNotFoundException,
			IOException, JavaModelException, SAXException, ParserConfigurationException
	{
		String fileContent = TestUtils.readFile(fileName);

		parser.setSource(fileContent.toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		JavaInterfaceParser iParser = new JavaInterfaceParser(cu, "test", delimiters);
		InterfaceArtifact artifact = iParser.parse();

		StringBuffer hierarchy = new StringBuffer();
		TestUtils.buildHierarchy(hierarchy, artifact, 0);
		logger.log(Level.INFO, hierarchy.toString());
	}

	/**
	 * Tests writing of the CompilationUnit.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CoreException
	 */
	@Ignore
	public void testWrite() throws FileNotFoundException, IOException, CoreException
	{
		String fileName = "test/source/test.java";
		String fileContent = TestUtils.readFile(fileName);

		parser.setSource(fileContent.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) unit.types().get(0);
		AST node = typeDecl.getAST();
		Javadoc javadoc = node.newJavadoc();

		TagElement newTag = javadoc.getAST().newTagElement();
		newTag.setTagName(TagElement.TAG_DEPRECATED);

		// get the buffer manager
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = unit.getJavaElement().getPath();
		try
		{
			bufferManager.connect(path, LocationKind.IFILE, null);
			// retrieve the buffer
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path,
					LocationKind.IFILE);

			// IDocument document = textFileBuffer.getDocument();
			// ... edit the document here ...

			// commit changes to underlying file
			textFileBuffer.commit(null /* ProgressMonitor */, false /* Overwrite */);

		}
		finally
		{
			bufferManager.disconnect(path, LocationKind.IFILE, null);
		}
	}
}
