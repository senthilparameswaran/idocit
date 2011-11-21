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
package source;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;

/**
 * <p>
 * <b>This is a test class used in JUnit test!!</b>
 * </p>
 * The parser parses Java Interfaces, Classes and Enumerations and maps the structure to
 * the iDocIt structure.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaInterfaceParser
{

	/**
	 * This is the constructor.
	 * 
	 * @param compilationUnit
	 *            The {@link CompilationUnit} that should be parsed &amp; checked.
	 * @param artifactName
	 *            The name for the CompilationUnit (in general the Java source file).
	 * @param delimiters
	 *            The {@link Delimiters} for creating paths.
	 */
	public JavaInterfaceParser(CompilationUnit compilationUnit, String artifactName,
			Delimiters delimiters)
	{}

	/**
	 * Parses the {@link CompilationUnit} <code>compilationUnit</code> (Java source file)
	 * and converts it to a {@link JavaInterfaceArtifact}. (Read
	 * {@link JavaInterfaceArtifact#copy(de.akra.idocit.common.structure.SignatureElement)}
	 * )
	 * 
	 * @param anyNumber
	 *            This is only any number.
	 * @param anyString
	 *            This is only any simple String. {@literal This Is A Literal}.
	 * @param names
	 *            The list of names
	 * @return a new {@link JavaInterfaceArtifact}.
	 * @throws JavaModelException
	 *             if an error occurs by getting the source code from ICompilationUnit.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @see JavaModelException
	 * @see #parse(int, String)
	 * @author Dirk Meier-Eickhoff
	 * @since 0.0.1
	 * @version 0.0.4
	 */
	public InterfaceArtifact parse(int anyNumber, String anyString, List<String> names)
			throws JavaModelException, SAXException, IOException,
			ParserConfigurationException
	{
		JavaInterfaceArtifact artifact = null;
		return artifact;
	}
}
