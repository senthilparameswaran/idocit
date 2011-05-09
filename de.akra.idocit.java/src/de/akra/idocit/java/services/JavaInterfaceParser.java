package de.akra.idocit.java.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.xml.sax.SAXException;

import de.akra.idocit.core.structure.Delimiters;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.utils.ObjectStructureUtils;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavaParameter;
import de.akra.idocit.java.structure.JavaParameters;

/**
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
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(JavaInterfaceParser.class.getName());

	private static final String CATEGORY_ARTIFACT = "Artifact";
	private static final String CATEGORY_INTERFACE = "Interface";
	private static final String CATEGORY_CLASS = "Class";
	private static final String CATEGORY_ENUM = "Enum";
	private static final String CATEGORY_METHOD = "Method";
	private static final String CATEGORY_CONSTRUCTOR = "Constructor";
	private static final String CATEGORY_PARAMETERS = "Parameters";
	private static final String CATEGORY_RETURN_TYPE = "ReturnType";
	private static final String CATEGORY_THROWS = "Throws";
	private static final String RETURN_TYPE_VOID = "void";

	/**
	 * Copy of the abstract syntax tree (AST) of the artifact.
	 */
	private CompilationUnit compilationUnit;

	/**
	 * The name of this artifact
	 */
	private String artifactName;

	/**
	 * The delimiters to use for the parameters' path building.
	 */
	private Delimiters delimiters;

	/**
	 * Constructor.
	 * 
	 * @param compilationUnit
	 *            The {@link CompilationUnit} that should be parsed.
	 * @param artifactName
	 *            The name for the CompilationUnit (in general the Java source file).
	 * @param delimiters
	 *            The {@link Delimiters} for creating paths.
	 */
	public JavaInterfaceParser(CompilationUnit compilationUnit, String artifactName,
			Delimiters delimiters)
	{
		this.compilationUnit = compilationUnit;
		this.artifactName = artifactName;
		this.delimiters = delimiters;
	}

	/**
	 * Parses the {@link CompilationUnit} <code>compilationUnit</code> (Java source file)
	 * and converts it to a {@link JavaInterfaceArtifact}.
	 * 
	 * @return a new {@link JavaInterfaceArtifact}.
	 * @throws JavaModelException
	 *             if an error occurs by getting the source code from ICompilationUnit.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public InterfaceArtifact parse() throws JavaModelException, SAXException,
			IOException, ParserConfigurationException
	{
		JavaInterfaceArtifact artifact = (JavaInterfaceArtifact) processCompilationUnit(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, compilationUnit);

		artifact.setOriginalDocument(((ICompilationUnit) compilationUnit.getJavaElement())
				.getSource());

		return artifact;
	}

	/**
	 * Convert a {@link CompilationUnit} (Java source file) to a
	 * {@link JavaInterfaceArtifact}.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param compilationUnit
	 *            The {@link CompilationUnit} to process.
	 * @return a new {@link JavaInterfaceArtifact}.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private JavaInterfaceArtifact processCompilationUnit(SignatureElement parent,
			CompilationUnit compilationUnit) throws SAXException, IOException,
			ParserConfigurationException
	{
		JavaInterfaceArtifact artifact = new JavaInterfaceArtifact(parent,
				CATEGORY_ARTIFACT, compilationUnit);
		artifact.setIdentifier(artifactName);

		@SuppressWarnings("unchecked")
		List<AbstractTypeDeclaration> types = (List<AbstractTypeDeclaration>) compilationUnit
				.types();
		List<Interface> interfaces = Collections.emptyList();
		if (types.size() > 0)
		{
			// process all classes, interfaces and enum declarations
			interfaces = new ArrayList<Interface>(types.size());
			for (AbstractTypeDeclaration type : types)
			{
				if (ReflectionHelper.isPublic(type.getModifiers()))
				{
					switch (type.getNodeType())
					{
					case ASTNode.TYPE_DECLARATION:
					{
						TypeDeclaration typeDec = (TypeDeclaration) type;
						interfaces.add((Interface) processTypeDeclaration(artifact,
								typeDec));
						break;
					}
					case ASTNode.ENUM_DECLARATION:
					{
						EnumDeclaration enumDec = (EnumDeclaration) type;
						interfaces.add((Interface) processEnumDeclaration(artifact,
								enumDec));
						break;
					}
					}
				}
			}
		}

		artifact.setInterfaces(interfaces);
		return artifact;
	}

	/**
	 * Convert a {@link TypeDeclaration} to a {@link JavaInterface}.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param typeDeclaration
	 *            The {@link TypeDeclaration} to process.
	 * @return a new {@link JavaInterface}.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private JavaInterface processTypeDeclaration(SignatureElement parent,
			TypeDeclaration typeDeclaration) throws SAXException, IOException,
			ParserConfigurationException
	{
		return processAbstractTypeDeclaration(parent, typeDeclaration,
				typeDeclaration.isInterface() ? CATEGORY_INTERFACE : CATEGORY_CLASS);
	}

	/**
	 * Convert an {@link EnumDeclaration} to a {@link JavaInterface}.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param enumDeclaration
	 *            The {@link EnumDeclaration} to process.
	 * @return a new {@link JavaInterface}.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private JavaInterface processEnumDeclaration(SignatureElement parent,
			EnumDeclaration enumDeclaration) throws SAXException, IOException,
			ParserConfigurationException
	{
		return processAbstractTypeDeclaration(parent, enumDeclaration, CATEGORY_ENUM);
	}

	/**
	 * Convert an {@link AbstractTypeDeclaration} to a {@link JavaInterface}.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param absTypeDeclaration
	 *            The {@link AbstractTypeDeclaration} to process.
	 * @param category
	 *            The category for the {@link JavaInterface}.
	 * @return a new {@link JavaInterface}.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @see AbstractTypeDeclaration
	 */
	private JavaInterface processAbstractTypeDeclaration(SignatureElement parent,
			AbstractTypeDeclaration absTypeDeclaration, String category)
			throws SAXException, IOException, ParserConfigurationException
	{
		JavaInterface jInterface = new JavaInterface(parent, category);
		jInterface.setIdentifier(absTypeDeclaration.getName().getIdentifier());
		jInterface.setQualifiedIdentifier(absTypeDeclaration.getName()
				.getFullyQualifiedName());
		jInterface.setRefToASTNode(absTypeDeclaration);

		jInterface
				.setDocumentations(JavadocParser.parse(absTypeDeclaration.getJavadoc()));

		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDeclaration
				.bodyDeclarations();

		List<Interface> innerInterface = Collections.emptyList();
		List<Operation> operations = Collections.emptyList();

		for (BodyDeclaration bodyDec : bodyDeclarations)
		{
			// only public elements are processed
			if (ReflectionHelper.isPublic(bodyDec.getModifiers()))
			{
				switch (bodyDec.getNodeType())
				{
				case ASTNode.TYPE_DECLARATION:
				{
					if (innerInterface == Collections.EMPTY_LIST)
					{
						innerInterface = new ArrayList<Interface>(
								SignatureElement.DEFAULT_ARRAY_SIZE);
					}
					TypeDeclaration typeDec = (TypeDeclaration) bodyDec;
					innerInterface.add(processTypeDeclaration(jInterface, typeDec));
					break;
				}
				case ASTNode.ENUM_DECLARATION:
				{
					if (innerInterface == Collections.EMPTY_LIST)
					{
						innerInterface = new ArrayList<Interface>(
								SignatureElement.DEFAULT_ARRAY_SIZE);
					}
					EnumDeclaration enumDec = (EnumDeclaration) bodyDec;
					innerInterface.add(processEnumDeclaration(jInterface, enumDec));
					break;
				}
				case ASTNode.METHOD_DECLARATION:
				{
					MethodDeclaration methodDec = (MethodDeclaration) bodyDec;
					if (ReflectionHelper.isPublic(methodDec.getModifiers()))
					{
						if (operations == Collections.EMPTY_LIST)
						{
							// init with number of all field, method, enum, class etc.
							// declarations to avoid resizing of the array
							operations = new ArrayList<Operation>(bodyDeclarations.size());
						}
						operations.add(processMethodDeclaration(jInterface, methodDec));
					}
					break;
				}
				}
			}
		}

		jInterface.setInnerInterfaces(innerInterface);
		jInterface.setOperations(operations);
		return jInterface;
	}

	/**
	 * Converts a {@link MethodDeclaration} to a {@link JavaMethod}. If there exists no
	 * parameters, return value or thrown exceptions they are set to <code>null</code>.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param methodDeclaration
	 *            The {@link MethodDeclaration} to process.
	 * @return a new {@link JavaMethod}.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private JavaMethod processMethodDeclaration(SignatureElement parent,
			MethodDeclaration methodDeclaration) throws SAXException, IOException,
			ParserConfigurationException
	{
		String category = CATEGORY_METHOD;
		if (methodDeclaration.isConstructor())
		{
			category = CATEGORY_CONSTRUCTOR;
		}

		JavaMethod method = new JavaMethod(parent, category);
		method.setIdentifier(methodDeclaration.getName().getIdentifier());
		method.setQualifiedIdentifier(methodDeclaration.getName().getFullyQualifiedName());
		method.setRefToASTNode(methodDeclaration);

		List<Documentation> documentations = JavadocParser.parse(methodDeclaration
				.getJavadoc());

		/*
		 * Add input parameters, if existing
		 */
		if (methodDeclaration.parameters().size() > 0)
		{
			JavaParameters inputParameters = new JavaParameters(method,
					CATEGORY_PARAMETERS);
			inputParameters.setDocumentationAllowed(false);
			inputParameters.setIdentifier("");
			inputParameters.setQualifiedIdentifier("");

			@SuppressWarnings("unchecked")
			List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) methodDeclaration
					.parameters();
			for (SingleVariableDeclaration parameter : parameters)
			{
				JavaParameter param = processParameter(inputParameters, parameter);
				ObjectStructureUtils.setParametersPaths(delimiters,
						inputParameters.getQualifiedIdentifier(), param);

				inputParameters.addParameter(param);
			}

			attachDocsToParameters(documentations, inputParameters);
			method.setInputParameters(inputParameters);
		}

		/*
		 * Add return type (always existing), void is ignored
		 */
		JavaParameters outputParameters = new JavaParameters(method, CATEGORY_RETURN_TYPE);
		Type retType = methodDeclaration.getReturnType2();
		JavaParameter returnType = processReturnType(outputParameters, retType);

		// add only if there is a type and that type is not void
		if (returnType != null)
		{
			outputParameters.setDocumentationAllowed(false);
			outputParameters.setIdentifier("");
			outputParameters.setQualifiedIdentifier("");
			outputParameters.addParameter(returnType);

			ObjectStructureUtils.setParametersPaths(delimiters,
					outputParameters.getQualifiedIdentifier(), returnType);
			
			attachDocsToParameters(documentations, outputParameters);
			method.setOutputParameters(outputParameters);
		}

		/*
		 * Add thrown exceptions, if existing
		 */
		if (methodDeclaration.thrownExceptions().size() > 0)
		{
			@SuppressWarnings("unchecked")
			List<Name> thrownExceptions = (List<Name>) methodDeclaration
					.thrownExceptions();

			// Java has only one list of list (JavaParameters contains a list) for thrown
			// exceptions. (We need the second list for WSDL fault messages.)
			List<JavaParameters> exceptionList = new ArrayList<JavaParameters>(1);
			JavaParameters exception = processThrownExceptions(method, thrownExceptions);
			attachDocsToParameters(documentations, exception);
			exceptionList.add(exception);
			method.setExceptions(exceptionList);
		}

		/*
		 * Add the documentations without element path to the method.
		 */
		Iterator<Documentation> iterDocs = documentations.iterator();
		while (iterDocs.hasNext())
		{
			Documentation doc = iterDocs.next();
			if (doc.getSignatureElementIdentifier() == null)
			{
				method.addDocpart(doc);
				iterDocs.remove();
			}
		}

		logNotAttachedDocs(documentations);
		return method;
	}

	/**
	 * Log the {@link Documentation}s that could not be assigned to a parameter.
	 * 
	 * @param documentations
	 *            The not assignable documentations.
	 */
	private void logNotAttachedDocs(List<Documentation> documentations)
	{
		if (documentations != null && !documentations.isEmpty())
		{
			StringBuffer msg = new StringBuffer(String.valueOf(documentations.size()));
			msg.append(" documentation(s) could not be attached: ");
			for (Documentation doc : documentations)
			{
				msg.append(doc.toString());
				msg.append("\n");
			}
			logger.log(Level.INFO, msg.toString());
		}
	}

	/**
	 * Attaches the {@link Documentation}s to the matching {@link JavaParameters} or
	 * {@link JavaParameter}.
	 * 
	 * @param documentations
	 *            The {@link Documentation}s that should be added.
	 *            <p>
	 *            <b>HINT:</b> If a documentation could be added, it is removed from the
	 *            list.
	 *            </p>
	 * @param parameters
	 *            The parameters to which the documentations should be added, if they
	 *            match.
	 * @see Parameters#addMatchingDocumentation(Delimiters, Documentation, StringBuffer)
	 * @see Parameter#addMatchingDocumentation(Delimiters, Documentation, StringBuffer)
	 */
	private void attachDocsToParameters(List<Documentation> documentations,
			JavaParameters parameters)
	{
		Iterator<Documentation> iterDocs = documentations.iterator();
		while (iterDocs.hasNext())
		{
			Documentation doc = iterDocs.next();
			if (parameters.addMatchingDocumentation(delimiters, doc))
			{
				iterDocs.remove();
			}
		}
	}

	/**
	 * Convert a parameter <code>variableDeclaration</code> to a {@link JavaParameter}.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param variableDeclaration
	 *            The {@link SingleVariableDeclaration} to process.
	 * @return a new {@link JavaParameter}.
	 */
	private JavaParameter processParameter(SignatureElement parent,
			SingleVariableDeclaration variableDeclaration)
	{
		JavaParameter containingAttributes = ReflectionHelper.reflectParameter(parent,
				variableDeclaration.resolveBinding().getType(), variableDeclaration
						.getName().getIdentifier(), variableDeclaration.getName()
						.getFullyQualifiedName());

		return containingAttributes;
	}

	/**
	 * Create the list of thrown exceptions. The list is wrapped in {@link JavaParameters}
	 * .
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param thrownExceptions
	 *            The {@link List} of {@link SimpleName} of the thrown exceptions to
	 *            process.
	 * @return a new {@link JavaParameters}.
	 */
	private JavaParameters processThrownExceptions(SignatureElement parent,
			List<Name> thrownExceptions)
	{
		JavaParameters exceptions = new JavaParameters(parent, CATEGORY_THROWS);
		exceptions.setDocumentationAllowed(false);
		exceptions.setIdentifier("");
		exceptions.setQualifiedIdentifier("");

		for (Name name : thrownExceptions)
		{
			ITypeBinding typeBinding = name.resolveTypeBinding();
			JavaParameter exception = new JavaParameter(exceptions);
			exception.setIdentifier(typeBinding.getName());
			exception.setQualifiedIdentifier(typeBinding.getQualifiedName());
			exception.setDataTypeName(typeBinding.getName());
			exception.setQualifiedDataTypeName(typeBinding.getQualifiedName());

			ObjectStructureUtils.setParametersPaths(delimiters,
					exceptions.getQualifiedIdentifier(), exception);
			
			exceptions.addParameter(exception);
		}
		return exceptions;
	}

	/**
	 * Create the return type as {@link JavaParameter}. The return type has only an
	 * identifier no further inner structure or data type.
	 * 
	 * @param parent
	 *            The parent {@link SignatureElement}.
	 * @param type
	 *            The {@link Type} to process.
	 * @return a new {@link JavaParameter}, or <code>null</code> if
	 *         <code>type == null</code> or the identifier of type equals
	 *         <code>void</code>.
	 */
	private JavaParameter processReturnType(SignatureElement parent, Type type)
	{
		JavaParameter returnType = null;
		if (type != null)
		{
			ITypeBinding typeBinding = type.resolveBinding();
			String identifier = typeBinding.getName();
			if (!identifier.equals(RETURN_TYPE_VOID))
			{
				returnType = ReflectionHelper.reflectParameter(parent, typeBinding,
						identifier, typeBinding.getQualifiedName());
			}
		}
		return returnType;
	}
}
