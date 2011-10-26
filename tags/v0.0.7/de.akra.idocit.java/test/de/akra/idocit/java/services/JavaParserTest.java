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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;

import de.akra.idocit.core.utils.TestUtils;

/**
 * Tests for {@link JavaParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaParserTest
{
	private static Logger logger = Logger.getLogger(JavaParserTest.class.getName());

	/**
	 * Tests {@link JavaParser#parse(org.eclipse.core.resources.IFile)}
	 */
	@Test
	public void testJavaParser()
	{
		
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
			logger.log(Level.INFO, "\t" + (method.getJavadoc() != null ? method.getJavadoc().toString() : "// no javadoc"));
			logger.log(Level.INFO, "\t" + method.getName().toString());
		}
	}

}