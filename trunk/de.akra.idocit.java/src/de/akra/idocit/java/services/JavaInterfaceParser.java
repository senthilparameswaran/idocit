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
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.SignatureElementUtils;
import de.akra.idocit.core.constants.ThematicGridConstants;
import de.akra.idocit.core.services.impl.ServiceManager;
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
	 * Helper to traverse the data type structure.
	 */
	private ReflectionHelper reflectionHelper;

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
		this.reflectionHelper = new ReflectionHelper();
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
	public InterfaceArtifact parse(AbsJavadocParser parser) throws JavaModelException,
			SAXException, IOException, ParserConfigurationException
	{
		JavaInterfaceArtifact artifact = (JavaInterfaceArtifact) processCompilationUnit(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, compilationUnit, parser);

		ICompilationUnit cu = (ICompilationUnit) compilationUnit.getJavaElement();
		artifact.setOriginalDocument(cu.getSource());

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
			CompilationUnit compilationUnit, AbsJavadocParser parser)
			throws SAXException, IOException, ParserConfigurationException
	{
		JavaInterfaceArtifact artifact = new JavaInterfaceArtifact(parent,
				CATEGORY_ARTIFACT, compilationUnit, Numerus.SINGULAR);
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
								typeDec, parser));
						break;
					}
					case ASTNode.ENUM_DECLARATION:
					{
						EnumDeclaration enumDec = (EnumDeclaration) type;
						interfaces.add((Interface) processEnumDeclaration(artifact,
								enumDec, parser));
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
			TypeDeclaration typeDeclaration, AbsJavadocParser parser)
			throws SAXException, IOException, ParserConfigurationException
	{
		return processAbstractTypeDeclaration(parent, typeDeclaration,
				typeDeclaration.isInterface() ? CATEGORY_INTERFACE : CATEGORY_CLASS,
				parser);
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
			EnumDeclaration enumDeclaration, AbsJavadocParser parser)
			throws SAXException, IOException, ParserConfigurationException
	{
		return processAbstractTypeDeclaration(parent, enumDeclaration, CATEGORY_ENUM,
				parser);
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
			AbstractTypeDeclaration absTypeDeclaration, String category,
			AbsJavadocParser parser) throws SAXException, IOException,
			ParserConfigurationException
	{
		JavaInterface jInterface = new JavaInterface(parent, category, Numerus.SINGULAR);
		jInterface.setIdentifier(absTypeDeclaration.getName().getIdentifier());
		jInterface.setQualifiedIdentifier(absTypeDeclaration.getName()
				.getFullyQualifiedName());
		jInterface.setRefToASTNode(absTypeDeclaration);

		Javadoc javadoc = absTypeDeclaration.getJavadoc();
		List<Addressee> addressees = ServiceManager.getInstance().getPersistenceService()
				.loadConfiguredAddressees();
		List<ThematicRole> roles = ServiceManager.getInstance().getPersistenceService()
				.loadThematicRoles();
		List<Documentation> docs = parser.parseIDocItJavadoc(javadoc, addressees, roles,
				null);
		if (docs.isEmpty())
		{
			docs = parser.convertExistingJavadoc(javadoc);
		}
		jInterface.setDocumentations(docs);

		List<ThematicRole> knownRoles = ServiceManager.getInstance()
				.getPersistenceService().loadThematicRoles();

		List<TagElement> additionalTags = parser.findAdditionalTags(javadoc, knownRoles);
		jInterface.setAdditionalTags(additionalTags);

		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) absTypeDeclaration
				.bodyDeclarations();

		List<Interface> innerInterface = Collections.emptyList();
		List<Operation> operations = Collections.emptyList();

		for (BodyDeclaration bodyDec : bodyDeclarations)
		{
			// only public elements are processed. In Java interfaces everything is
			// public.
			if (ReflectionHelper.isPublic(bodyDec.getModifiers())
					|| CATEGORY_INTERFACE.equals(category))
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
					innerInterface
							.add(processTypeDeclaration(jInterface, typeDec, parser));
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
					innerInterface
							.add(processEnumDeclaration(jInterface, enumDec, parser));
					break;
				}
				case ASTNode.METHOD_DECLARATION:
				{
					if (operations == Collections.EMPTY_LIST)
					{
						// init with number of all declarations for fields, methods,
						// enumerations, classes etc. to avoid resizing of the array
						operations = new ArrayList<Operation>(bodyDeclarations.size());
					}
					MethodDeclaration methodDec = (MethodDeclaration) bodyDec;
					operations.add(processMethodDeclaration(jInterface, methodDec,
							parser, knownRoles));
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
			MethodDeclaration methodDeclaration, AbsJavadocParser parser,
			List<ThematicRole> knownRoles) throws SAXException, IOException,
			ParserConfigurationException
	{
		String category = CATEGORY_METHOD;
		if (methodDeclaration.isConstructor())
		{
			category = CATEGORY_CONSTRUCTOR;
		}

		Javadoc javadoc = methodDeclaration.getJavadoc();
		String thematicGridName = parser.parseIDocItReferenceGrid(javadoc);

		if (thematicGridName == null)
		{
			thematicGridName = ThematicGridConstants.THEMATIC_GRID_DEFAULT_NAME;
		}

		JavaMethod method = new JavaMethod(parent, category, thematicGridName,
				Numerus.SINGULAR);
		method.setIdentifier(methodDeclaration.getName().getIdentifier());
		method.setQualifiedIdentifier(methodDeclaration.getName().getFullyQualifiedName());
		method.setRefToASTNode(methodDeclaration);

		List<TagElement> additionalTags = parser.findAdditionalTags(javadoc, knownRoles);
		method.setAdditionalTags(additionalTags);

		/*
		 * Add input parameters, if existing
		 */
		if (methodDeclaration.parameters().size() > 0)
		{
			JavaParameters inputParameters = new JavaParameters(method,
					CATEGORY_PARAMETERS, Numerus.SINGULAR, false);
			inputParameters.setDocumentationAllowed(false);
			inputParameters.setIdentifier("");
			inputParameters.setQualifiedIdentifier("");

			@SuppressWarnings("unchecked")
			List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) methodDeclaration
					.parameters();
			for (SingleVariableDeclaration parameter : parameters)
			{
				JavaParameter param = processParameter(inputParameters, parameter);
				SignatureElementUtils.setParametersPaths(delimiters,
						inputParameters.getQualifiedIdentifier(), param);
				inputParameters.addParameter(param);
			}

			method.setInputParameters(inputParameters);
		}

		/*
		 * Add return type (always existing), void is ignored
		 */
		JavaParameters outputParameters = new JavaParameters(method,
				CATEGORY_RETURN_TYPE, Numerus.SINGULAR, false);
		Type retType = methodDeclaration.getReturnType2();
		JavaParameter returnType = processReturnType(outputParameters, retType);

		// add only if there is a type and that type is not void
		if (returnType != null)
		{
			outputParameters.setDocumentationAllowed(false);
			outputParameters.setIdentifier("");
			outputParameters.setQualifiedIdentifier("");
			outputParameters.addParameter(returnType);

			SignatureElementUtils.setParametersPaths(delimiters,
					outputParameters.getQualifiedIdentifier(), returnType);

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

			// Java has only one list of list (JavaParameters contains a list)
			// for thrown
			// exceptions. (We need the second list for WSDL fault messages.)
			List<JavaParameters> exceptionList = new ArrayList<JavaParameters>(1);
			JavaParameters exception = processThrownExceptions(method, thrownExceptions);
			exceptionList.add(exception);
			method.setExceptions(exceptionList);
		}

		List<Addressee> addressees = ServiceManager.getInstance().getPersistenceService()
				.loadConfiguredAddressees();
		List<ThematicRole> roles = ServiceManager.getInstance().getPersistenceService()
				.loadThematicRoles();
		List<Documentation> convertedJavadoc = null;
		List<Documentation> documentations = parser.parseIDocItJavadoc(javadoc,
				addressees, roles, method);
		if (documentations.isEmpty() && javadoc != null)
		{
			convertedJavadoc = parser.convertExistingJavadoc(javadoc);
		}

		/*
		 * Add the documentations without element path to the method.
		 */
		if (convertedJavadoc == null)
		{
			if ((method.getExceptions() != null) && !method.getExceptions().isEmpty())
			{
				attachDocsToParameters(documentations, (JavaParameters) method
						.getExceptions().get(0));
			}

			if (method.getInputParameters() != null)
			{
				attachDocsToParameters(documentations,
						(JavaParameters) method.getInputParameters());
			}

			if (outputParameters != null)
			{
				attachDocsToParameters(documentations, outputParameters);
			}

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
		}

		/*
		 * Add existing Javadoc (converted to Documentation objects) to the iDocIt object
		 * structure.
		 */
		if (convertedJavadoc != null)
		{
			attachConvertedDocs(convertedJavadoc, method);
		}

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
	 * Attaches the converted Javadoc to the methods elements. It tries to attach the
	 * documentation of the parameters (@param, @return, @throws) to the right
	 * {@link Parameter} of the method. If a documentation could not be assigned to a
	 * parameter it is added to the method's documentations.
	 * 
	 * @param convertedJavadocs
	 *            The list of {@link Documentation}s, converted from the existing Javadoc.
	 * @param method
	 *            The {@link JavaMethod} to which's elements the documentations should be
	 *            added.
	 */
	private void attachConvertedDocs(List<Documentation> convertedJavadocs,
			JavaMethod method)
	{
		Iterator<Documentation> iterDocs = convertedJavadocs.iterator();
		while (iterDocs.hasNext())
		{
			Documentation doc = iterDocs.next();
			String identifier = doc.getSignatureElementIdentifier();
			if (identifier == null)
			{
				method.addDocpart(doc);
			}
			else
			{
				String name = identifier.substring(
						identifier.indexOf(JavaParser.delimiters.pathDelimiter) + 1,
						identifier.length());

				if (!((identifier
						.startsWith(AbsJavadocParser.CONVERTED_JAVADOC_TAG_PARAM) && attachConvertedDocToParameters(
						name, doc, method.getInputParameters()))
						|| (identifier
								.startsWith(AbsJavadocParser.CONVERTED_JAVADOC_TAG_RETURN) && attachConvertedDocToParameters(
								name, doc, method.getOutputParameters())) || (identifier
						.startsWith(AbsJavadocParser.CONVERTED_JAVADOC_TAG_THROWS) && attachConvertedDocToParameters(
						name, doc, method.getExceptions()))))
				{
					logger.info("Converted and not assignable Documentation is attached to the JavaMethod's documentations: "
							+ doc);
					method.addDocpart(doc);
				}
			}
		}
	}

	/**
	 * Attaches the documentation to the Parameter with the identifier
	 * <code>searchName</code>.
	 * 
	 * @param searchName
	 *            The identifier of the {@link Parameter} to which the <code>doc</code>
	 *            should be added.
	 * @param doc
	 *            The {@link Documentation} which should be attached to a Parameter.
	 * @param exceptions
	 *            List of exceptions. The <code>doc</code> should be added to one of the
	 *            including {@link Parameter}s.
	 * @return true, if the documentation could be assigned to a parameter.
	 * @see #attachConvertedDocToParameters(String, Documentation, Parameters)
	 */
	private boolean attachConvertedDocToParameters(String searchName, Documentation doc,
			List<? extends Parameters> exceptions)
	{
		boolean found = false;
		if (exceptions != null)
		{
			Iterator<? extends Parameters> iterExceptions = exceptions.iterator();
			while (iterExceptions.hasNext() && !found)
			{
				found = attachConvertedDocToParameters(searchName, doc,
						iterExceptions.next());
			}
		}
		return found;
	}

	/**
	 * Attaches the documentation to the Parameter with the identifier
	 * <code>searchName</code>.
	 * 
	 * @param searchName
	 *            The identifier of the {@link Parameter} to which the <code>doc</code>
	 *            should be added.
	 * @param doc
	 *            The {@link Documentation} which should be attached to a Parameter.
	 * @param parameters
	 *            The <code>doc</code> should be added to one of the including
	 *            {@link Parameter}s.
	 * @return true, if the documentation could be assigned to a parameter.
	 */
	private boolean attachConvertedDocToParameters(String searchName, Documentation doc,
			Parameters parameters)
	{
		boolean found = false;
		if (parameters != null)
		{
			Iterator<Parameter> iterParams = parameters.getParameters().iterator();
			while (iterParams.hasNext() && !found)
			{
				Parameter param = iterParams.next();
				if (param.getIdentifier().equals(searchName)
						|| (CATEGORY_RETURN_TYPE.equals(parameters.getCategory())))
				{
					param.addDocpart(doc);
					doc.setSignatureElementIdentifier(param.getSignatureElementPath());
					found = true;
				}
			}
		}
		return found;
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
		JavaParameter containingAttributes = null;

		IVariableBinding resolvedBinding = variableDeclaration.resolveBinding();
		if (resolvedBinding != null)
		{
			ITypeBinding typeBinding = resolvedBinding.getType();
			containingAttributes = reflectionHelper.reflectParameter(parent, typeBinding,
					variableDeclaration.getName().getIdentifier(), variableDeclaration
							.getName().getFullyQualifiedName());
		}
		else
		{
			// if resolveBinding() gives no result try
			// variableDeclaration.getType()
			// and work with that Type.
			Type type = variableDeclaration.getType();
			containingAttributes = reflectionHelper.reflectParameter(parent, type,
					variableDeclaration.getName().getIdentifier(), variableDeclaration
							.getName().getFullyQualifiedName());
		}

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
		JavaParameters exceptions = new JavaParameters(parent, CATEGORY_THROWS,
				Numerus.SINGULAR, false);
		exceptions.setDocumentationAllowed(false);
		exceptions.setIdentifier("");
		exceptions.setQualifiedIdentifier("");

		for (Name name : thrownExceptions)
		{
			ITypeBinding typeBinding = name.resolveTypeBinding();
			JavaParameter exception = new JavaParameter(exceptions,
					reflectionHelper.deriveNumerus(typeBinding),
					reflectionHelper.hasPublicAccessableAttributes(typeBinding));

			if (typeBinding != null)
			{
				// TODO should be the inner structure of Exceptions be
				// documentable? It
				// might be make sense for custom Exceptions.
				exception.setIdentifier(typeBinding.getName());
				exception.setQualifiedIdentifier(typeBinding.getQualifiedName());
				exception.setDataTypeName(typeBinding.getName());
				exception.setQualifiedDataTypeName(typeBinding.getQualifiedName());
			}
			else
			{
				String identifier;
				String qIdentifier = name.getFullyQualifiedName();

				if (name.isQualifiedName())
				{
					QualifiedName qName = (QualifiedName) name;
					identifier = qName.getName().getIdentifier();
				}
				else
				{
					SimpleName sName = (SimpleName) name;
					identifier = sName.getIdentifier();
				}
				exception.setIdentifier(identifier);
				exception.setQualifiedIdentifier(qIdentifier);
				exception.setDataTypeName(identifier);
				exception.setQualifiedDataTypeName(qIdentifier);
			}

			SignatureElementUtils.setParametersPaths(delimiters,
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
			if (typeBinding != null)
			{
				String identifier = typeBinding.getName();
				if (!identifier.equals(RETURN_TYPE_VOID))
				{
					returnType = reflectionHelper.reflectParameter(parent, typeBinding,
							identifier, typeBinding.getQualifiedName());
				}
			}
			else
			{
				String identifier = ReflectionHelper.getIdentifierFrom(type);
				if (!identifier.equals(RETURN_TYPE_VOID))
				{
					returnType = reflectionHelper.reflectParameter(parent, type,
							identifier, identifier);
				}
			}
		}
		return returnType;
	}
}