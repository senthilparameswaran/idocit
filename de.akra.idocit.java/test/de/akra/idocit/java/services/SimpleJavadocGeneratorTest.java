package de.akra.idocit.java.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.Javadoc;
import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.java.JavadocTestUtils;
import de.akra.idocit.java.structure.ParserOutput;

public class SimpleJavadocGeneratorTest
{
	@Test
	public void testAppendDocsToJavadoc() throws FileNotFoundException, IOException
	{
		/*
		 * Positive tests
		 */
		{
			ParserOutput output = JavadocTestUtils
					.createCompilationUnit("test/source/CustomerService.java");
			
			Documentation referenceDocumentation = new Documentation(); 
		}

		// #########################################################################
		// #########################################################################

		/*
		 * Negative tests
		 */
	}
}
