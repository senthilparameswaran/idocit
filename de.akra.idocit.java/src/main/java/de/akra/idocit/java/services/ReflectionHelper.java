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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.java.constants.Constants;
import de.akra.idocit.java.structure.JavaAttribute;
import de.akra.idocit.java.structure.JavaParameter;

/**
 * Contains helper-methods for Java Reflection purposes.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class ReflectionHelper
{
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ReflectionHelper.class.getName());

	/**
	 * The identifier for super classes.
	 */
	private static final String SUPER_CLASS_IDENTIFIER = "super";

	private static final String GETTER_PREFIX = "get";
	private static final String BOOLEAN_GETTER_PREFIX = "is";
	private static final String SETTER_PREFIX = "set";

	/**
	 * Set of already reflected types. If a type is already in the set, stop reflection to
	 * avoid infinite loops.
	 */
	private Set<String> reflectedTypes;

	/**
	 * Resets the set {@link #reflectedTypes}. Must be invoked before starting the type
	 * reflection.
	 * 
	 * @object This instance
	 * @source No attributes are set, but the set of reflected types is cleared.
	 * 
	 * @thematicgrid Resetting Operations
	 */
	private void resetReflectedTypes()
	{
		this.reflectedTypes = new HashSet<String>();
	}

	/**
	 * Creates a {@link JavaParameter} with attributes read from the given
	 * <code>typeBinding</code>.
	 * 
	 * @param parent
	 *            [DESTINATION] Parent of the newly created Java-Parameter
	 * 
	 * @param typeBinding
	 *            [SOURCE] The {@link ITypeBinding} belonging to the parameter that should
	 *            be analysed with Java Reflection.
	 * 
	 * @param identifier
	 *            [ATTRIBUTE]
	 * 
	 * @param qualifiedIdentifier
	 *            [ATTRIBUTE]
	 * 
	 * @return [OBJECT] Never <code>null</code>
	 * @thematicgrid Creating Operations
	 */
	public JavaParameter createParameter(final SignatureElement parent,
			final ITypeBinding typeBinding, final String identifier,
			final String qualifiedIdentifier)
	{
		resetReflectedTypes();
		return doReflectParameter(parent, typeBinding, identifier, qualifiedIdentifier);
	}

	/**
	 * Rule: Returns <code>true</code> with the Java Type represented by the given binding
	 * implements the interface "java.util.Collection".
	 * 
	 * @param type
	 *            [OBJECT]
	 * 
	 * @return [REPORT]
	 * @thematicgrid Checking Operations
	 */
	private static boolean isCollection(final ITypeBinding type)
	{
		if (type != null)
		{
			final ITypeBinding[] implementedTypes = type.getInterfaces();

			if (implementedTypes != null)
			{
				for (final ITypeBinding implementedType : implementedTypes)
				{
					final String qualifiedName = implementedType.getQualifiedName();
					if ((qualifiedName != null)
							&& qualifiedName.startsWith("java.util.Collection"))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Rule: Returns <code>true</code> if the Java-Class represented by the given binding
	 * has member variables which are accessible via a getter and a setter. The getter and
	 * setter must follow the naming conventions defined by the Java Beans Specification:<br/>
	 * <br/>
	 * http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html
	 * 
	 * @param type
	 *            [OBJECT]
	 * 
	 * @return [REPORT]
	 * @thematicgrid Checking Operations
	 */
	public boolean hasPublicAccessableAttributes(final ITypeBinding type)
	{
		return (type != null)
				&& !findAttributesWithPublicGetterOrSetter(type.getDeclaredMethods())
						.isEmpty();
	}

	/**
	 * @rule Rule: Returns {@link Numerus.PLURAL} if the given binding represents a Java
	 *       Collection.
	 * 
	 * @param type
	 *            [ATTRIBUTE]
	 * 
	 * @return [OBJECT]
	 * @thematicgrid Concluding Operations
	 */
	public static Numerus deriveNumerus(ITypeBinding type)
	{
		return isCollection(type) ? Numerus.PLURAL : Numerus.SINGULAR;
	}

	/**
	 * @see #createParameter(SignatureElement, ITypeBinding, String, String)
	 */
	private JavaParameter doReflectParameter(final SignatureElement parent,
			final ITypeBinding typeBinding, final String identifier,
			final String qualifiedIdentifier)
	{
		final JavaParameter newParameter = new JavaParameter(parent,
				deriveNumerus(typeBinding), hasPublicAccessableAttributes(typeBinding));
		newParameter.setIdentifier(identifier);
		newParameter.setQualifiedIdentifier(qualifiedIdentifier);
		newParameter.setDataTypeName(typeBinding.getName());

		String qTypeName = typeBinding.getQualifiedName();
		qTypeName = qTypeName != null ? qTypeName : typeBinding.getName();
		newParameter.setQualifiedDataTypeName(qTypeName);

		if (!reflectedTypes.contains(qTypeName))
		{
			reflectedTypes.add(qTypeName);
			final List<JavaAttribute> accessableAttributes = findAttributesWithPublicGetterOrSetter(typeBinding
					.getDeclaredMethods());

			for (final JavaAttribute attribute : accessableAttributes)
			{
				newParameter.addParameter(doReflectParameter(
						newParameter,
						attribute.getType(),
						attribute.getName(),
						qTypeName + JavaParser.delimiters.namespaceDelimiter
								+ attribute.getName()));
			}

			// check super classes for attributes with public getter or setter
			final ITypeBinding superType = typeBinding.getSuperclass();

			if (superType != null
					&& !superType.getQualifiedName().equals(Object.class.getName()))
			{
				newParameter.addParameter(doReflectParameter(newParameter, superType,
						SUPER_CLASS_IDENTIFIER, SUPER_CLASS_IDENTIFIER));
			}

			// remove type again, because reflecting this type ends
			reflectedTypes.remove(qTypeName);
		}
		else
		{
			logger.fine("Recursion terminated for type \"" + qTypeName + "\"");
		}

		return newParameter;
	}

	/**
	 * Creates a {@link JavaParameter} with attributes read from the given
	 * <code>typeBinding</code>.
	 * 
	 * @param parent
	 *            [DESTINATION] Parent of the newly created Java-Parameter
	 * 
	 * @param type
	 *            [SOURCE] The {@link ITypeBinding} belonging to the parameter that should
	 *            be analysed with Java Reflection.
	 * 
	 * @param identifier
	 *            [ATTRIBUTE]
	 * 
	 * @param qualifiedIdentifier
	 *            [ATTRIBUTE]
	 * 
	 * @return [OBJECT] Never <code>null</code>
	 * @thematicgrid Creating Operations
	 */
	public JavaParameter createParameter(final SignatureElement parent, final Type type,
			final String identifier, final String qualifiedIdentifier)
	{
		resetReflectedTypes();
		return doReflectParameter(parent, type, identifier, qualifiedIdentifier);
	}

	/**
	 * @see #createParameter(SignatureElement, Type, String, String)
	 */
	private JavaParameter doReflectParameter(final SignatureElement parent,
			final Type type, final String identifier, final String qualifiedIdentifier)
	{
		// try to resolve binding on Type
		final ITypeBinding typeBinding = type.resolveBinding();
		if (typeBinding != null)
		{
			return doReflectParameter(parent, typeBinding, identifier,
					qualifiedIdentifier);
		}

		final JavaParameter returnParameter = new JavaParameter(parent,
				deriveNumerus(typeBinding), hasPublicAccessableAttributes(typeBinding));
		returnParameter.setIdentifier(identifier);
		returnParameter.setQualifiedIdentifier(qualifiedIdentifier);
		final String typeName = extractIdentifierFrom(type);
		returnParameter.setDataTypeName(typeName);
		returnParameter.setQualifiedDataTypeName(typeName);
		return returnParameter;
	}

	/**
	 * Searches for all accessible attributes according to the Java Beans Standard. There
	 * must be a getter with the method name prefix "get" or "is" and a setter with the
	 * method name prefix "set" for an attribute.
	 * 
	 * @param methodBindings
	 *            [SOURCE] The methods declared in the class.
	 * 
	 * @return [OBJECT] List with attributes ({@link JavaAttribute}) that have a public
	 *         getter and setter.
	 * @since 0.0.2
	 * @thematicgrid Searching Operations
	 */
	private static List<JavaAttribute> findAttributesWithPublicGetterOrSetter(
			final IMethodBinding[] methodBindings)
	{
		final Set<JavaAttribute> foundGetterAndSetter = new TreeSet<JavaAttribute>();
		final Set<JavaAttribute> foundGetter = new TreeSet<JavaAttribute>();
		final Set<JavaAttribute> foundSetter = new TreeSet<JavaAttribute>();

		for (final IMethodBinding methodBinding : methodBindings)
		{
			if (isPublic(methodBinding.getModifiers()) || isInInterface(methodBinding))
			{
				final ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
				final String methodName = methodBinding.getName();

				if (methodName.startsWith(GETTER_PREFIX)
						|| methodName.startsWith(BOOLEAN_GETTER_PREFIX))
				{
					// consider only getter with a return type and 0 parameters
					if (!Constants.RETURN_TYPE_VOID.equals(methodBinding.getReturnType()
							.getName())
							&& (parameterTypes == null || parameterTypes.length == 0))
					{
						final String prefix = methodName.startsWith(GETTER_PREFIX) ? GETTER_PREFIX
								: BOOLEAN_GETTER_PREFIX;
						final JavaAttribute javaAttribute = new JavaAttribute(
								extractAttibuteName(methodName, prefix),
								methodBinding.getReturnType());
						if (foundSetter.contains(javaAttribute))
						{
							foundSetter.remove(javaAttribute);
							foundGetterAndSetter.add(javaAttribute);
						}
						else
						{
							foundGetter.add(javaAttribute);
						}
					}
				}
				else if (methodName.startsWith(SETTER_PREFIX))
				{
					// consider only setter with one parameter
					if (parameterTypes != null && parameterTypes.length == 1)
					{
						final JavaAttribute javaAttribute = new JavaAttribute(
								extractAttibuteName(methodName, SETTER_PREFIX),
								parameterTypes[0]);
						if (foundGetter.contains(javaAttribute))
						{
							foundGetter.remove(javaAttribute);
							foundGetterAndSetter.add(javaAttribute);
						}
						else
						{
							foundSetter.add(javaAttribute);
						}
					}
					else
					{
						logger.log(
								Level.WARNING,
								"Setter \"{0}\" has {1} parameters. Allowed is only 1 parameter for a setter.",
								new Object[] {
										methodName,
										(parameterTypes != null ? parameterTypes.length
												: 0) });
					}
				}
			}
		}

		return new ArrayList<JavaAttribute>(foundGetterAndSetter);
	}

	/**
	 * Check if the method is declared in a Java interface.
	 * 
	 * @param methodBinding
	 *            [OBJECT]
	 * @return [REPORT] {@code true} if the method is in a Java interface.
	 */
	private static boolean isInInterface(final IMethodBinding methodBinding)
	{
		boolean isInterface = false;
		final IJavaElement method = methodBinding.getJavaElement();
		if (method != null)
		{
			final IJavaElement parent = method.getParent();

			if (parent.getElementType() == IJavaElement.TYPE)
			{
				final IType type = (IType) parent;
				try
				{
					isInterface = type.isInterface() && !type.isAnnotation();
				}
				catch (JavaModelException e)
				{
					logger.log(Level.SEVERE,
							"Failed to check if the method is in an interface.", e);
				}
			}
		}
		else
		{
			logger.log(Level.INFO, "Method \"" + methodBinding.getName()
					+ "\" has no parent element.");
		}
		return isInterface;
	}

	/**
	 * Convert the {@code methodName} to the corresponding attribute name. The
	 * {@code prefix} is cut and the first character followed by the prefix is swapped to
	 * lower case.
	 * 
	 * @param methodName
	 *            [SOURCE]
	 * @param prefix
	 *            [OBJECT] is removed from method name.
	 * @return [DESTINATION] the extracted attribute name.
	 */
	private static String extractAttibuteName(final String methodName, final String prefix)
	{
		return methodName.substring(prefix.length(), prefix.length() + 1).toLowerCase()
				+ methodName.substring(prefix.length() + 1);
	}

	/**
	 * Returns the identifier from the {@link Type} depending on the containing type.
	 * 
	 * @param type
	 *            [SOURCE]
	 * 
	 * @return [OBJECT]
	 * @see Type
	 * @thematicgrid Extracting Operations
	 */
	public static String extractIdentifierFrom(Type type)
	{
		switch (type.getNodeType())
		{
		case ASTNode.PRIMITIVE_TYPE:
		{
			PrimitiveType primitiveType = (PrimitiveType) type;
			return primitiveType.getPrimitiveTypeCode().toString();
		}
		case ASTNode.ARRAY_TYPE:
		{
			ArrayType arrayType = (ArrayType) type;
			String name = extractIdentifierFrom(arrayType.getElementType());
			StringBuffer identifier = new StringBuffer(name);

			for (int i = 0; i < arrayType.getDimensions(); i++)
			{
				identifier.append("[]");
			}
			return identifier.toString();
		}
		case ASTNode.SIMPLE_TYPE:
		{
			SimpleType simpleType = (SimpleType) type;
			return simpleType.getName().getFullyQualifiedName();
		}
		case ASTNode.QUALIFIED_TYPE:
		{
			QualifiedType qType = (QualifiedType) type;
			return extractIdentifierFrom(qType.getQualifier()) + "."
					+ qType.getName().getIdentifier();
		}
		case ASTNode.PARAMETERIZED_TYPE:
		{
			ParameterizedType paramType = (ParameterizedType) type;
			StringBuffer identifier = new StringBuffer(
					extractIdentifierFrom(paramType.getType()));
			identifier.append('<');

			@SuppressWarnings("unchecked")
			Iterator<Type> iterTypeArgs = (Iterator<Type>) paramType.typeArguments()
					.iterator();
			while (iterTypeArgs.hasNext())
			{
				Type typeArg = iterTypeArgs.next();
				identifier.append(extractIdentifierFrom(typeArg));
				if (iterTypeArgs.hasNext())
				{
					identifier.append(',');
				}
			}

			identifier.append('>');
			return identifier.toString();
		}
		case ASTNode.WILDCARD_TYPE:
		{
			WildcardType wildcard = (WildcardType) type;
			String identifier = "? extends ";
			if (!wildcard.isUpperBound())
			{
				identifier = "? super ";
			}
			return identifier + extractIdentifierFrom(wildcard.getBound());
		}
		}
		return SignatureElement.ANONYMOUS_IDENTIFIER;
	}

	/**
	 * Checks if the modifier {@link Modifier#PUBLIC} is in <code>modifiers</code>.
	 * 
	 * @param modifiers
	 *            [OBJECT]
	 * 
	 * @return [REPORT]
	 * @see Modifier
	 * @thematicgrid Checking Operations
	 */
	public static boolean isPublic(int modifiers)
	{
		return (modifiers & Modifier.PUBLIC) == Modifier.PUBLIC;
	}
}
