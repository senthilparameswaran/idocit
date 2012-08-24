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
package de.akra.idocit.java;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jface.text.Document;

import de.akra.idocit.core.utils.TestUtils;
import de.akra.idocit.java.structure.ParserOutput;

public class JavadocTestUtils
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
}
