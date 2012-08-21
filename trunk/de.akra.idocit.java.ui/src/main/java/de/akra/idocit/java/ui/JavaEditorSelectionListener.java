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
package de.akra.idocit.java.ui;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.java.services.AbsJavadocParser;
import de.akra.idocit.java.services.JavaParser;
import de.akra.idocit.ui.components.RecommendedGridsView;
import de.akra.idocit.ui.components.RecommendedGridsViewSelection;

/**
 * Listener to notice selections in a JavaEditor. If a JavaMethod is selected, then the
 * iDocIt! Recommended Roles view ({@link RecommendedGridsView}) shall be updated with the
 * selected method's properties.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavaEditorSelectionListener implements ISelectionListener
{
	private static Logger LOG = Logger.getLogger(JavaEditorSelectionListener.class
			.getName());

	/**
	 * Always it is done a selection within a JavaEditor (mouse click or moving curser by
	 * keys) it is tried to find out out the selected JavaMethod. If a JavaMethod were
	 * found, the RecommendedGridsView of iDocIt! is updated with the method's properties.
	 * 
	 * @param part
	 *            [OBJECT] The selected editor.
	 * @param selection
	 *            [ATTRIBUTE]
	 */
	@Override
	@SuppressWarnings("restriction")
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection)
	{
		if (part instanceof JavaEditor)
		{
			final JavaEditor javaEditor = (JavaEditor) part;
			final ITypeRoot root = EditorUtility.getEditorInputJavaElement(javaEditor,
					false);
			final ISelection sel = javaEditor.getSelectionProvider().getSelection();

			if (sel instanceof ITextSelection)
			{
				// get iDocIt! view for active window
				final RecommendedGridsView view = (RecommendedGridsView) part.getSite()
						.getPage().findView(RecommendedGridsView.ID);

				final ITextSelection textSel = (ITextSelection) sel;
				IJavaElement elt = null;
				try
				{
					elt = root.getElementAt(textSel.getOffset());
					if (elt != null && elt.getElementType() == IJavaElement.METHOD)
					{
						view.setSelection(prepareViewSelection((IMethod) elt));
					}
					else
					{
						view.setSelection(null);
					}
				}
				catch (final JavaModelException e)
				{
					LOG.log(Level.WARNING,
							"Java method not found for \"{0}\" (file offset={1}).",
							new Object[] { textSel.getText(), textSel.getOffset() });
					view.setSelection(null);
				}
				catch (final Exception e)
				{
					LOG.log(Level.WARNING, "Failed to collect assigned ThematicRoles.", e);
					view.setSelection(null);
				}
			}
		}
	}

	/**
	 * Collect assigned ThematicRoles from the method and it's parents (classes,
	 * interfaces, enumerations) and find the reference ThematicGrid.
	 * 
	 * @param method
	 *            [SOURCE]
	 * @throws Exception
	 */
	private RecommendedGridsViewSelection prepareViewSelection(final IMethod method)
			throws Exception
	{
		final AbsJavadocParser javadocParser = JavaParser.getJavadocParser();
		final List<Addressee> addressees = ServiceManager.getInstance()
				.getPersistenceService().loadConfiguredAddressees();
		final List<ThematicRole> roles = ServiceManager.getInstance()
				.getPersistenceService().loadThematicRoles();

		final RecommendedGridsViewSelection selection = new RecommendedGridsViewSelection();
		selection.setOperationIdentifier(method.getElementName());

		final Set<ThematicRole> assignedThematicRoles = new TreeSet<ThematicRole>();
		selection.setAssignedThematicRoles(assignedThematicRoles);

		/*
		 * Collect documentations from method
		 */
		final String methodSource = extractCodeSnippetOf(method);
		final ASTNode node = parseCodeSnippet(methodSource.toCharArray(),
				ASTParser.K_CLASS_BODY_DECLARATIONS);

		if ((node.getNodeType() == ASTNode.TYPE_DECLARATION || node.getNodeType() == ASTNode.ENUM_DECLARATION))
		{
			final TypeDeclaration typeDeclaration = (TypeDeclaration) node;
			@SuppressWarnings("unchecked")
			final List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) typeDeclaration
					.bodyDeclarations();

			// only one method is parsed
			if (bodyDeclarations.size() == 1)
			{
				final BodyDeclaration bodyDeclaration = bodyDeclarations.get(0);
				if (bodyDeclaration.getNodeType() == ASTNode.METHOD_DECLARATION
						&& bodyDeclaration.getJavadoc() != null)
				{
					selection.setReferenceThematicGridName(javadocParser
							.parseIDocItReferenceGrid(bodyDeclaration.getJavadoc()));

					final List<Documentation> parsedDocs = javadocParser
							.parseIDocItJavadoc(bodyDeclaration.getJavadoc(), addressees,
									roles, null);
					for (final Documentation doc : parsedDocs)
					{
						if (doc.getThematicRole() != null)
						{
							assignedThematicRoles.add(doc.getThematicRole());
						}
					}
				}

				/*
				 * Collect documentations from parent interfaces, classes and enumerations
				 */
				IJavaElement parent = method.getParent();
				while (parent != null && parent.getElementType() == IJavaElement.TYPE)
				{
					final IType type = (IType) parent;
					final String typeSource = extractCodeSnippetOf(type);

					final ASTNode parentNode = parseCodeSnippet(typeSource.toCharArray(),
							ASTParser.K_CLASS_BODY_DECLARATIONS);

					if ((parentNode.getNodeType() == ASTNode.TYPE_DECLARATION || parentNode
							.getNodeType() == ASTNode.ENUM_DECLARATION))
					{
						final TypeDeclaration typeDecl = (TypeDeclaration) parentNode;

						@SuppressWarnings("unchecked")
						final List<BodyDeclaration> bodyDecls = (List<BodyDeclaration>) typeDecl
								.bodyDeclarations();

						// only one class, interface or enum is parsed
						if (bodyDecls.size() == 1)
						{
							final BodyDeclaration bodyDecl = bodyDecls.get(0);

							// process if it is a class, interface or enum and has Javadoc
							if ((bodyDecl.getNodeType() == ASTNode.TYPE_DECLARATION || bodyDecl
									.getNodeType() == ASTNode.ENUM_DECLARATION)
									&& bodyDecl.getJavadoc() != null)
							{
								final List<Documentation> parsedDocs = javadocParser
										.parseIDocItJavadoc(bodyDecl.getJavadoc(),
												addressees, roles, null);
								for (final Documentation doc : parsedDocs)
								{
									if (doc.getThematicRole() != null)
									{
										assignedThematicRoles.add(doc.getThematicRole());
									}
								}
							}
						}

						// parse next parent
						parent = parent.getParent();
					}
					else
					{
						LOG.fine("Stop parsing because of incorrect source code syntax.");
						parent = null;
					}
				}
			}
		}
		else
		{
			LOG.info("Parsing of code snippet failed. No assigned ThematicRoles can be collected.");
		}
		return selection;
	}

	/**
	 * Extracts the code snippet of this Java element {@code member} from the source code.
	 * 
	 * @param member
	 *            [SOURCE]
	 * @return [OBJECT] the code snippet.
	 * @throws JavaModelException
	 */
	private String extractCodeSnippetOf(final IMember member) throws JavaModelException
	{
		final ICompilationUnit cu = member.getCompilationUnit();
		final ISourceRange memberRange = member.getSourceRange();
		final String source = cu.getSource();

		// sometimes if characters were deleted, the gotten memberRange is greater than
		// the new real range of the member in the source code. Therefore, the length must
		// be corrected.
		final int len = memberRange.getLength() > source.length() ? source.length()
				: memberRange.getLength();

		return source.substring(memberRange.getOffset(), memberRange.getOffset() + len);
	}

	/**
	 * Parses the code snippet {@code source} and returns the parsed ASTNode. The caller
	 * have to check and cast the type of the ASTNode.
	 * 
	 * @param source
	 *            [SOURCE]
	 * @param codeSnippetKind
	 *            [ATTRIBUTE] use e.g. {@link ASTParser#K_COMPILATION_UNIT} or
	 *            {@link ASTParser#K_CLASS_BODY_DECLARATIONS}
	 * @return the parsed ASTNode. The caller have to check and cast the type of the
	 *         ASTNode.
	 * @see ASTParser#createAST(org.eclipse.core.runtime.IProgressMonitor)
	 */
	private ASTNode parseCodeSnippet(final char[] source, final int codeSnippetKind)
	{
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(codeSnippetKind);
		parser.setSource(source);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		return parser.createAST(Job.getJobManager().createProgressGroup());
	}
}
