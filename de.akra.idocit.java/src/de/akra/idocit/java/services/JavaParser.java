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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import de.akra.idocit.extensions.Parser;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.structure.Delimiters;
import de.akra.idocit.structure.InterfaceArtifact;

/**
 * Parser implementation for Java.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.extensions.Parser#parse(IFile)
	 */
	@Override
	public InterfaceArtifact parse(IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "parse file: "
				+ iFile.getLocation().toFile().getAbsolutePath());

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

		JavaInterfaceParser jInterfaceParser = new JavaInterfaceParser(compilationUnit,
				compilationUnit.getJavaElement().getElementName(), delimiters);
		InterfaceArtifact artifact = jInterfaceParser.parse();

		return artifact;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.extensions.Parser#write(InterfaceArtifact, IFile)
	 */
	@Override
	public void write(InterfaceArtifact interfaceStructure, IFile iFile) throws Exception
	{
		logger.log(Level.INFO, "write file: "
				+ iFile.getLocation().toFile().getAbsolutePath());

		JavaInterfaceArtifact artifact = (JavaInterfaceArtifact) interfaceStructure;
		JavaInterfaceGenerator.updateJavadocInAST(artifact);
		writeToFile(artifact);
	}

	/**
	 * Write the changes in the {@link JavaInterfaceArtifact} to the underlying file.
	 * 
	 * @param artifact
	 *            The {@link JavaInterfaceArtifact} which changes should be written to the
	 *            file.
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 * @throws CoreException
	 * @see {@link CompilationUnit#rewrite(IDocument, java.util.Map)}
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
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.extensions.Parser#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String type)
	{
		return type.equalsIgnoreCase(SUPPORTED_TYPE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.extensions.Parser#getSupportedType()
	 */
	@Override
	public String getSupportedType()
	{
		return SUPPORTED_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.extensions.Parser#getDelimiters()
	 */
	@Override
	public Delimiters getDelimiters()
	{
		return delimiters;
	}

}
