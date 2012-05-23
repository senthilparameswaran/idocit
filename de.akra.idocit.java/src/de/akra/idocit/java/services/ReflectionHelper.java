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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.java.structure.JavaParameter;

/**
 * Amongst others, some useful methods to reflect classes.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ReflectionHelper
{
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
	 */
	private void resetReflectedTypes()
	{
		this.reflectedTypes = new HashSet<String>();
	}

	/**
	 * Reflects the type <code>typeBinding</code> and mapped it into a
	 * {@link JavaParameter} structure which is returned.
	 * 
	 * @param parent
	 *            The parent for the new {@link JavaParameter}.
	 * @param typeBinding
	 *            The {@link ITypeBinding} belonging to the parameter that should be
	 *            reflected.
	 * @param identifier
	 *            The identifier of the parameter that should be reflected.
	 * @param qualifiedIdentifier
	 *            The qualified identifier of the parameter that should be reflected.
	 * @return The reflected object structure as {@link JavaParameter}.
	 */
	public JavaParameter reflectParameter(SignatureElement parent,
			ITypeBinding typeBinding, String identifier, String qualifiedIdentifier)
	{
		resetReflectedTypes();
		return doReflectParameter(parent, typeBinding, identifier, qualifiedIdentifier);
	}

	public boolean isCollection(ITypeBinding type)
	{
		if (type != null)
		{
			ITypeBinding[] implementedTypes = type.getInterfaces();

			if (implementedTypes != null)
			{
				for (ITypeBinding implementedType : implementedTypes)
				{
					String qualifiedName = implementedType.getQualifiedName();
					
					if ((qualifiedName != null) && qualifiedName.startsWith("java.util.Collection"))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean hasPublicAccessableAttributes(ITypeBinding type)
	{
		return (type != null)
				&& !findAttributesWithPublicGetterOrSetter(type.getDeclaredFields(),
						type.getDeclaredMethods()).isEmpty();
	}

	public Numerus deriveNumerus(ITypeBinding type)
	{
		return isCollection(type) ? Numerus.PLURAL : Numerus.SINGULAR;
	}

	/**
	 * @see #reflectParameter(SignatureElement, ITypeBinding, String, String)
	 */
	private JavaParameter doReflectParameter(SignatureElement parent,
			ITypeBinding typeBinding, String identifier, String qualifiedIdentifier)
	{
		JavaParameter returnParameter = new JavaParameter(parent,
				deriveNumerus(typeBinding), hasPublicAccessableAttributes(typeBinding));
		returnParameter.setIdentifier(identifier);
		returnParameter.setQualifiedIdentifier(qualifiedIdentifier);
		returnParameter.setDataTypeName(typeBinding.getName());

		String qTypeName = typeBinding.getQualifiedName();
		qTypeName = qTypeName != null ? qTypeName : typeBinding.getName();
		returnParameter.setQualifiedDataTypeName(qTypeName);

		if (!reflectedTypes.contains(qTypeName))
		{
			reflectedTypes.add(qTypeName);
			List<IVariableBinding> accessableAttributes = findAttributesWithPublicGetterOrSetter(
					typeBinding.getDeclaredFields(), typeBinding.getDeclaredMethods());

			for (IVariableBinding attribute : accessableAttributes)
			{
				returnParameter.addParameter(doReflectParameter(
						returnParameter,
						attribute.getType(),
						attribute.getName(),
						qTypeName + JavaParser.delimiters.namespaceDelimiter
								+ attribute.getName()));
			}

			// check super classes for attributes with public getter or setter
			ITypeBinding superType = typeBinding.getSuperclass();

			if (superType != null
					&& !superType.getQualifiedName().equals(Object.class.getName()))
			{
				returnParameter.addParameter(doReflectParameter(returnParameter,
						superType, SUPER_CLASS_IDENTIFIER, SUPER_CLASS_IDENTIFIER));
			}

			// remove type again, because reflecting this type ends
			reflectedTypes.remove(qTypeName);
		}
		else
		{
			logger.fine("Recursion terminated for type \"" + qTypeName + "\"");
		}

		return returnParameter;
	}

	/**
	 * Tries to reflects the Type <code>type</code> and maps it into a
	 * {@link JavaParameter} structure which is returned.
	 * 
	 * @param parent
	 *            The parent for the new {@link JavaParameter}.
	 * @param type
	 *            The {@link Type} belonging to the parameter that should be reflected.
	 * @param identifier
	 *            The identifier of the parameter that should be reflected.
	 * @param qualifiedIdentifier
	 *            The qualified identifier of the parameter that should be reflected.
	 * @return The reflected object structure as {@link JavaParameter}.
	 */
	public JavaParameter reflectParameter(SignatureElement parent, Type type,
			String identifier, String qualifiedIdentifier)
	{
		resetReflectedTypes();
		return doReflectParameter(parent, type, identifier, qualifiedIdentifier);
	}

	/**
	 * @see #reflectParameter(SignatureElement, Type, String, String)
	 */
	private JavaParameter doReflectParameter(SignatureElement parent, Type type,
			String identifier, String qualifiedIdentifier)
	{
		// try to resolve binding on Type
		ITypeBinding typeBinding = type.resolveBinding();
		if (typeBinding != null)
		{
			return doReflectParameter(parent, typeBinding, identifier,
					qualifiedIdentifier);
		}

		JavaParameter returnParameter = new JavaParameter(parent,
				deriveNumerus(typeBinding), hasPublicAccessableAttributes(typeBinding));
		returnParameter.setIdentifier(identifier);
		returnParameter.setQualifiedIdentifier(qualifiedIdentifier);
		String typeName = getIdentifierFrom(type);
		returnParameter.setDataTypeName(typeName);
		returnParameter.setQualifiedDataTypeName(typeName);
		return returnParameter;
	}

	/**
	 * Searches for all attributes <code>variableBindings</code> a public getter or setter
	 * method accordingly to the Java Bean convention, that the prefix is "get", "is" or
	 * "set" plus the identifier, whose first character is swapped to upper case. If a
	 * getter or setter is found, the attribute is added to the result list.
	 * 
	 * @param variableBindings
	 *            The attributes to look for public getter or setters.
	 * @param methodBindings
	 *            The methods declared in the class.
	 * @return List with attributes ({@link IVariableBinding}) that have a public getter
	 *         or setter.
	 */
	private static List<IVariableBinding> findAttributesWithPublicGetterOrSetter(
			IVariableBinding[] variableBindings, IMethodBinding[] methodBindings)
	{
		List<IVariableBinding> foundGetterAndSetter = new ArrayList<IVariableBinding>(
				variableBindings.length);

		for (IVariableBinding variableBinding : variableBindings)
		{
			String name = variableBinding.getName();
			String preparedMethodName = name.substring(0, 1).toUpperCase()
					+ name.substring(1, name.length());

			try
			{
				findMethod(methodBindings, GETTER_PREFIX + preparedMethodName,
						variableBinding.getType(), (ITypeBinding[]) null);
				// method was found, because no exception is thrown,
				// so variableBinding can be added
				foundGetterAndSetter.add(variableBinding);
			}
			catch (NoSuchMethodException e)
			{
				logger.log(Level.FINEST, e.getMessage());

				try
				{
					findMethod(methodBindings, SETTER_PREFIX + preparedMethodName, null,
							variableBinding.getType());
					// method was found, because no exception is thrown,
					// so variableBinding can be added
					foundGetterAndSetter.add(variableBinding);
				}
				catch (NoSuchMethodException ex)
				{
					logger.log(Level.FINEST, ex.getMessage());

					try
					{
						findMethod(methodBindings, BOOLEAN_GETTER_PREFIX
								+ preparedMethodName, variableBinding.getType(),
								(ITypeBinding[]) null);
						// method was found, because no exception is thrown,
						// so variableBinding can be added
						foundGetterAndSetter.add(variableBinding);
					}
					catch (NoSuchMethodException exc)
					{
						logger.log(Level.FINEST, exc.getMessage());
					}
				}
			}
		}
		return foundGetterAndSetter;
	}

	/**
	 * Seek in <code>methods</code> for a method with the identifier
	 * <code>searchedMethodName</code>, the <code>returnType</code> and the
	 * <code>parameters</code> (array of types).
	 * 
	 * @param methods
	 *            The array of methods in which the searched method should be sought.
	 * @param searchedMethodName
	 *            Identifier of the searched method.
	 * @param returnType
	 *            The return type of the searched method. <code>null</code> is handled as
	 *            <code>void</code>.
	 * @param parameters
	 *            Array of parameter types. <code>null</code> is handled as zero
	 *            parameters.
	 * @return The found {@link IMethodBinding}, otherwise the exception is thrown.
	 * @throws NoSuchMethodException
	 *             If the method was not found.
	 */
	private static IMethodBinding findMethod(IMethodBinding[] methods,
			String searchedMethodName, ITypeBinding returnType,
			ITypeBinding... parameters) throws NoSuchMethodException
	{
		IMethodBinding foundMethod = null;
		for (int i = 0; i < methods.length && foundMethod == null; ++i)
		{
			IMethodBinding method = methods[i];
			if (searchedMethodName.equals(method.getName())
					&& isPublic(method.getModifiers()))
			{
				foundMethod = method;
			}
		}
		if (foundMethod == null)
		{
			throw new NoSuchMethodException("Method could not be found: "
					+ searchedMethodName);
		}
		return foundMethod;
	}

	/**
	 * Get an identifier from the {@link Type} depending an the containing type.
	 * 
	 * @param type
	 *            The Type whose identifier should found.
	 * @return The identifier for the type.
	 * @see Type
	 */
	public static String getIdentifierFrom(Type type)
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
			String name = getIdentifierFrom(arrayType.getElementType());
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
			return getIdentifierFrom(qType.getQualifier()) + "."
					+ qType.getName().getIdentifier();
		}
		case ASTNode.PARAMETERIZED_TYPE:
		{
			ParameterizedType paramType = (ParameterizedType) type;
			StringBuffer identifier = new StringBuffer(
					getIdentifierFrom(paramType.getType()));
			identifier.append('<');

			@SuppressWarnings("unchecked")
			Iterator<Type> iterTypeArgs = (Iterator<Type>) paramType.typeArguments()
					.iterator();
			while (iterTypeArgs.hasNext())
			{
				Type typeArg = iterTypeArgs.next();
				identifier.append(getIdentifierFrom(typeArg));
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
			return identifier + getIdentifierFrom(wildcard.getBound());
		}
		}
		return SignatureElement.ANONYMOUS_IDENTIFIER;
	}

	/**
	 * Checks if the modifier {@link Modifier#PUBLIC} is in <code>modifiers</code>.
	 * 
	 * @param modifiers
	 *            The {@link Modifier}s set in a bit mask.
	 * @return true if public is set.
	 * @see Modifier
	 */
	public static boolean isPublic(int modifiers)
	{
		return (modifiers & Modifier.PUBLIC) == Modifier.PUBLIC;
	}
}
