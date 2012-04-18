package de.akra.idocit.java;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;

import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.structure.ParserOutput;

public class JavadocTestUtils
{
	/**
	 * Parses a file and returns it as {@link CompilationUnit}.
	 * 
	 * @param fileName	The Java-file to parse
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

}
