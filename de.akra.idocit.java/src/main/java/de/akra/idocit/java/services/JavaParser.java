/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.extensions.ValidationReport.ValidationCode;
import de.akra.idocit.java.constants.PreferenceStoreConstants;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;

/**
 * Parser implementation for Java.
 * 
 * @instrument  iDocIt! supports two different representations of thematic grids in Javadoc: <br/>
 * - The simplified version is very compact, but supports only the addressee &quot;Developer&quot;.<br/>
 * - The complex version supports all addressees, but uses a lot of HTML-code.
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaParser implements Parser
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(JavaParser.class.getName());

	/**
	 * The type of supported files.
	 */
	private static final String SUPPORTED_TYPE = "java";

	/**
	 * The delimiters for Java.
	 */
	public static final Delimiters delimiters;

	/**
	 * Initialize <code>delimiters</code>.
	 */
	static
	{
		delimiters = new Delimiters();
		delimiters.pathDelimiter = "/";
		delimiters.namespaceDelimiter = ".";
		delimiters.typeDelimiter = ":";
	}

	/**
	 * {@inheritDoc }
	 * 
	 * @source_format  Java and Javadoc according to their current specifications<br/>
	 * - <a href="http://docs.oracle.com/javase/specs/">Java</a> <br/>
	 * - <a href="http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html">Javadoc</a>
	 * 
	 * @instrument  To parse the Java and Javadoc code, the parser provided by the <a href="http://www.eclipse.org/jdt/">Eclipse Java Development Tools</a> is used.
	 * 
	 * @param  iFile [SOURCE]
	 * 
	 * @return  [OBJECT]
	 * 
	 * @throws  Exception
	 * @see de.akra.idocit.core.extensions.Parser#parse(IFile)
	 * @thematicgrid  Parsing Operations
	 */
	@Override
	public InterfaceArtifact parse(IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "parse file: "
				+ iFile.getFullPath().toFile().getAbsolutePath());

		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(iFile);

		if (iCompilationUnit == null)
		{
			logger.log(Level.SEVERE, "Can not create ICompilationUnit from "
					+ iFile.getLocation().toFile().getAbsolutePath());
			return InterfaceArtifact.NOT_SUPPORTED_ARTIFACT;
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(iCompilationUnit.getWorkingCopy(null));
		parser.setResolveBindings(true); // bindings are needed for object reflection
		parser.setBindingsRecovery(true);
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(Job
				.getJobManager().createProgressGroup());
		compilationUnit.recordModifications();

		de.akra.idocit.java.services.JavaInterfaceParser jInterfaceParser = new JavaInterfaceParser(
				compilationUnit, compilationUnit.getJavaElement().getElementName(),
				delimiters);

		AbsJavadocParser javadocParser = isSimpleModeConfigured() ? SimpleJavadocParser.INSTANCE
				: JavadocParser.INSTANCE;
		InterfaceArtifact artifact = jInterfaceParser.parse(javadocParser);

		return artifact;
	}

	/**
	 * {@inheritDoc }
	 * 
	 * @destination_format  Java and Javadoc according to their current specifications<br/>
	 * - <a href="http://docs.oracle.com/javase/specs/">Java</a> <br/>
	 * - <a href="http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html">Javadoc</a>
	 * 
	 * @param  interfaceStructure [OBJECT] The interface[SOURCE] The written file
	 * @subparam  interfaces [ACTION] mn,m,m
	 * 
	 * @param  iFile [DESTINATION] The name can be read by:<br/>
	 * <code><br/>
	 * IFile file = [...];<br/>
	 * file.getName();<br/>
	 * </code>dfd
	 * 
	 * @paraminfo  iFile [NAME] The name can be read by:<br/>
	 * <code><br/>
	 * IFile file = [...];<br/>
	 * file.getName();<br/>
	 * </code>dfd
	 * 
	 * @throws  Exception
	 * @see de.akra.idocit.core.extensions.Parser#write(InterfaceArtifact, IFile)
	 * @thematicgrid  Putting Operations
	 */
	@Override
	public void write(InterfaceArtifact interfaceStructure, IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "write file: "
				+ iFile.getFullPath().toFile().getAbsolutePath());

		JavaInterfaceArtifact artifact = (JavaInterfaceArtifact) interfaceStructure;

		if (isSimpleModeConfigured())
		{
			JavaInterfaceGenerator.updateJavadocInAST(artifact,
					SimpleJavadocGenerator.INSTANCE);
		}
		else
		{
			JavaInterfaceGenerator
					.updateJavadocInAST(artifact, JavadocGenerator.INSTANCE);
		}
		writeToFile(artifact);
	}

	/**
	 * Write the changes in the {@link JavaInterfaceArtifact} to the underlying file.
	 * 
	 * @param artifact [DESTINATION] The {@link JavaInterfaceArtifact} which changes should be written to the
	 * 	file.
	 * 
	 * @throws MalformedTreeException 
	 * @throws BadLocationException
	 * @throws CoreException
	 * 
	 * @see {@link CompilationUnit#rewrite(IDocument, java.util.Map)}
	 * @thematicgrid  Putting Operations
	 */
	private void writeToFile(JavaInterfaceArtifact artifact)
			throws MalformedTreeException, BadLocationException, CoreException
	{
		CompilationUnit unit = artifact.getCompilationUnit();

		ICompilationUnit iCompUnit = (ICompilationUnit) unit.getJavaElement();

		IDocument document = new Document(artifact.getOriginalDocument());
		TextEdit textEdit = unit.rewrite(document, null);
		textEdit.apply(document);

		iCompUnit.getBuffer().setContents(document.get());
		iCompUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
		iCompUnit.commitWorkingCopy(true, null);
		iCompUnit.makeConsistent(null);
	}

	/**
	 * {@inheridDoc}<br/>
	 * <br/>
	 * isSupported(&quot;java&quot;) == true
	 * 
	 * @comparison  This parser supports only "java".
	 * 
	 * @param  type [OBJECT]
	 * 
	 * @return  [REPORT]
	 * @see de.akra.idocit.core.extensions.Parser#isSupported(java.lang.String)
	 * @thematicgrid  Checking Operations
	 */
	@Override
	public boolean isSupported(String type)
	{
		return type.equalsIgnoreCase(SUPPORTED_TYPE);
	}

	/**
	 * {@inheritDoc }
	 * 
	 * @return  [OBJECT] "java"
	 * @see de.akra.idocit.core.extensions.Parser#getSupportedType()
	 * @thematicgrid  Getting Operations / Getter
	 */
	@Override
	public String getSupportedType()
	{
		return SUPPORTED_TYPE;
	}

	/**
	 * {@inheritDoc }<br/>
	 * <br/>
	 * <code><br/>
	 * getDelimiters().pathDelimiter == &quot;/&quot;;<br/>
	 * getDelimiters().namespaceDelimiter == &quot;.&quot;;<br/>
	 * getDelimiters().typeDelimiter == &quot;:&quot;;<br/>
	 * </code>
	 * 
	 * @return  [OBJECT]
	 * @see de.akra.idocit.core.extensions.Parser#getDelimiters()
	 * @thematicgrid  Getting Operations / Getter
	 */
	@Override
	public Delimiters getDelimiters()
	{
		return delimiters;
	}

	/**
	 * Checks if the key {@link PreferenceStoreConstants#JAVADOC_GENERATION_MODE} has the
	 * value {@link PreferenceStoreConstants#JAVADOC_GENERATION_MODE_SIMPLE}.
	 * 
	 * @return [REPORT]
	 * 
	 * @thematicgrid  Checking Operations
	 */
	private boolean isSimpleModeConfigured()
	{
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		String mode = store.getString(PreferenceStoreConstants.JAVADOC_GENERATION_MODE);

		return PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE.equals(mode);
	}

	/**
	 * Rule: The {@link JavaInterfaceArtifact} should only contain {@link Documentation}s for the addressee "Developer".
	 * 
	 * @param  artifact [OBJECT]
	 * 
	 * @return  [REPORT]
	 * @thematicgrid  Checking Operations
	 */
	@Override
	public ValidationReport validateArtifact(InterfaceArtifact artifact)
	{
		if (artifact instanceof JavaInterfaceArtifact)
		{
			ValidationReport report = new ValidationReport();

			if (isSimpleModeConfigured())
			{
				JavaInterfaceArtifact javaArtifact = (JavaInterfaceArtifact) artifact;

				if (AddresseeUtils.containsOnlyOneAddressee(javaArtifact, "Developer"))
				{
					report.setReturnCode(ValidationCode.OK);
					report.setMessage("");
				}
				else
				{
					report.setReturnCode(ValidationCode.ERROR);
					report.setMessage("The generation of simplified Javadoc works only for addressee \"Developer\"");
				}
			}
			else
			{
				report.setReturnCode(ValidationCode.OK);
				report.setMessage("");
			}

			return report;
		}
		else
		{
			throw new RuntimeException("This Java Parser only accepts "
					+ JavaInterfaceArtifact.class.getName() + "s for validation.");
		}
	}

}
