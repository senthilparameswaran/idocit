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
package de.akra.idocit.core.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Delimiters;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.ParameterPathElement;
import de.akra.idocit.core.structure.ParameterType;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.structure.ThematicRole;

/**
 * 
 * Some useful methods, e.g. to traverse the object structure.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ObjectStructureUtils
{

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(ObjectStructureUtils.class.getName());

	private static List<Addressee> supportedAddressees = PersistenceService
			.loadConfiguredAddressees();

	private static List<ThematicRole> supportedThematicRoles = PersistenceService
			.loadThematicRoles();

	// use this for testing with JUnit, because the workspace is not available
	// private static List<Addressee> supportedAddressees = Collections.emptyList();
	// private static List<ThematicRole> supportedThematicRoles = Collections.emptyList();

	/**
	 * Finds the {@link ParameterType} for <code>searchFor</code>.<br>
	 * From the <code>currentElem</code> we went up to the corresponding {@link Operation}
	 * . If the operation is found, we searched in its parameters for
	 * <code>searchFor</code>.
	 * 
	 * @param searchFor
	 *            The parameter for which the type is searched.
	 * @param currentElem
	 *            The current {@link SignatureElement} from which the search is started.
	 * @return the {@link ParameterType}
	 */
	public static ParameterType findParameterType(SignatureElement searchFor,
			SignatureElement currentElem)
	{
		// if parameter not initialized or the root is reached, or... stop the recursion
		if (searchFor == null || searchFor == SignatureElement.EMPTY_SIGNATURE_ELEMENT
				|| currentElem == null
				|| currentElem == SignatureElement.EMPTY_SIGNATURE_ELEMENT
				|| !(searchFor instanceof Parameter || searchFor instanceof Parameters))
		{
			return ParameterType.NONE;
		}

		// look into operation to find parameter
		if (currentElem instanceof Operation)
		{
			Operation op = (Operation) currentElem;
			ParameterType res = ParameterType.NONE;
			boolean found = findParameterInOperation(searchFor, op.getInputParameters());

			if (found)
			{
				res = ParameterType.INPUT;
			}
			else
			{
				found = findParameterInOperation(searchFor, op.getOutputParameters());
				if (found)
				{
					res = ParameterType.OUTPUT;
				}
				else
				{
					Iterator<? extends Parameters> exceptionIterator = op.getExceptions()
							.iterator();
					while (exceptionIterator.hasNext() && !found)
					{
						Parameters exception = exceptionIterator.next();
						found = findParameterInOperation(searchFor, exception);
					}
					if (found)
					{
						res = ParameterType.EXCEPTION;
					}
				}
			}

			return res;
		}
		return findParameterType(searchFor, currentElem.getParent());
	}

	/**
	 * Finds the {@link Parameter} <code>searchFor</code> in <code>currentElem</code>.
	 * 
	 * @param searchFor
	 *            The parameter which is searched.
	 * @param currentElem
	 *            in which <code>searchFor</code> is searched.
	 * @return true if found.
	 */
	private static boolean findParameterInOperation(SignatureElement searchFor,
			SignatureElement currentElem)
	{
		if (currentElem == null)
		{
			return false;
		}
		else if (searchFor == currentElem)
		{
			return true;
		}

		Iterator<Parameter> itParam;
		boolean found = false;

		if (currentElem instanceof Parameter)
		{
			itParam = ((Parameter) currentElem).getComplexType().iterator();
		}
		else if (currentElem instanceof Parameters)
		{
			itParam = ((Parameters) currentElem).getParameters().iterator();
		}
		else
		{
			logger.log(Level.SEVERE,
					"Not expected SignatureElement: " + currentElem.toString());
			return false;
		}

		while (itParam.hasNext() && !found)
		{
			Parameter param = itParam.next();
			found = findParameterInOperation(searchFor, param);
		}
		return found;
	}

	/**
	 * Searches higher (<code>parent</code>) in structure for the first {@link Operation}.
	 * If found, then it is returned, otherwise if the root reached or
	 * <code>parameter == null</code> {@link SignatureElement#EMPTY_SIGNATURE_ELEMENT} is
	 * returned.
	 * 
	 * @param parameter
	 *            The {@link SignatureElement} to which the corresponding
	 *            {@link Operation} should be found.
	 * @return the found {@link Operation} or
	 *         {@link SignatureElement#EMPTY_SIGNATURE_ELEMENT}.
	 */
	public static SignatureElement findOperationForParameter(SignatureElement parameter)
	{
		// if parameter not initialized or the root is reached, stop the recursion
		if (parameter == null || parameter == SignatureElement.EMPTY_SIGNATURE_ELEMENT)
		{
			return SignatureElement.EMPTY_SIGNATURE_ELEMENT;
		}

		// return operation if found
		if (parameter instanceof Operation)
		{
			return parameter;
		}
		return findOperationForParameter(parameter.getParent());
	}

	/**
	 * Collects all already associated {@link ThematicRole}s belonging to the
	 * <code>sigElem</code>.
	 * 
	 * @param associatedThematicRoles
	 *            {@link Set} of associated {@link ThematicRole}s. All found associated
	 *            roles are added to the set.
	 * @param sigElem
	 *            The {@link SignatureElement} for which the roles are collected.
	 */
	public static void collectAssociatedThematicRoles(
			Set<ThematicRole> associatedThematicRoles, SignatureElement sigElem)
	{
		SignatureElement operation = findOperationForParameter(sigElem);

		if (operation != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
		{
			// use first the parent, because operation itself is processed in
			// collectAssociatedThematicRolesDownwards. I have done it this way, because
			// so it is easier to traverse the structure
			collectAssociatedThematicRolesUpwards(associatedThematicRoles,
					operation.getParent());
			collectAssociatedThematicRolesDownwards(associatedThematicRoles, operation);
		}
	}

	/**
	 * Collects all associated {@link ThematicRole}s from the <code>currentSigElem</code>
	 * up to the highest {@link Interface}.
	 * 
	 * @param associatedThematicRoles
	 *            {@link Set} of associated {@link ThematicRole}s. All found associated
	 *            roles are added to the set.
	 * @param currentSigElem
	 *            The current {@link SignatureElement} used as iterator.
	 */
	private static void collectAssociatedThematicRolesUpwards(
			Set<ThematicRole> associatedThematicRoles, SignatureElement currentSigElem)
	{
		if (currentSigElem != null && !(currentSigElem instanceof InterfaceArtifact))
		{
			collectThematicRolesFromDocs(associatedThematicRoles,
					currentSigElem.getDocumentations());
			collectAssociatedThematicRolesUpwards(associatedThematicRoles,
					currentSigElem.getParent());
		}
	}

	/**
	 * Collects all associated {@link ThematicRole}s from the <code>currentSigElem</code>
	 * down to last {@link Parameter}.
	 * 
	 * @param associatedThematicRoles
	 *            {@link Set} of associated {@link ThematicRole}s. All found associated
	 *            roles are added to the set.
	 * @param currentSigElem
	 *            The current {@link SignatureElement} used as iterator.
	 */
	private static void collectAssociatedThematicRolesDownwards(
			Set<ThematicRole> associatedThematicRoles, SignatureElement currentSigElem)
	{
		if (currentSigElem != null)
		{
			collectThematicRolesFromDocs(associatedThematicRoles,
					currentSigElem.getDocumentations());

			if (currentSigElem instanceof Operation)
			{
				Operation op = (Operation) currentSigElem;
				collectAssociatedThematicRolesDownwards(associatedThematicRoles,
						op.getInputParameters());
				collectAssociatedThematicRolesDownwards(associatedThematicRoles,
						op.getOutputParameters());

				List<? extends Parameters> exceptions = op.getExceptions();
				for (Parameters exception : exceptions)
				{
					collectAssociatedThematicRolesDownwards(associatedThematicRoles,
							exception);
				}
			}
			else
			{
				Iterator<Parameter> itParam;
				if (currentSigElem instanceof Parameter)
				{
					itParam = ((Parameter) currentSigElem).getComplexType().iterator();
				}
				else if (currentSigElem instanceof Parameters)
				{
					itParam = ((Parameters) currentSigElem).getParameters().iterator();
				}
				else
				{
					logger.log(Level.SEVERE, "Not expected SignatureElement: "
							+ currentSigElem.toString());
					return;
				}

				while (itParam.hasNext())
				{
					Parameter param = itParam.next();
					collectAssociatedThematicRolesDownwards(associatedThematicRoles,
							param);
				}
			}
		}
	}

	/**
	 * Adds the {@link ThematicRole}s from the <code>documentations</code> to
	 * <code>thematicRoles</code>.
	 * 
	 * @param thematicRoles
	 *            {@link Set} of {@link ThematicRole}s to which the found roles are added.
	 * @param documentations
	 *            The {@link Documentation}s from which the roles are get.
	 */
	private static void collectThematicRolesFromDocs(Set<ThematicRole> thematicRoles,
			List<Documentation> documentations)
	{
		for (Documentation doc : documentations)
		{
			if (doc.getThematicRole() != null)
			{
				thematicRoles.add(doc.getThematicRole());
			}
		}
	}

	/**
	 * Sets to all {@link Parameter}s in the structure the attribute
	 * {@link Parameter#setSignatureElementPath(String)}. The path must start with the
	 * qualified identifier of a {@link Parameters}, so that must be the initialization
	 * part of the path by the first invoke of the method. The first path delimiter is
	 * omitted if the identifier of {@link Parameters} is empty. That means in generall,
	 * that a documentation is not allowed for the Parameters element.
	 * 
	 * @param delimiters
	 *            The {@link Delimiters} to use for the path.
	 * @param path
	 *            The path up to the parent element.
	 * @param parameter
	 *            The {@link Parameter} to which the
	 *            <code>path + {@link Delimiters#pathDelimiter} + QUALIFIED_IDENTIFIER + {@link Delimiters#typeDelimiter} + QUALIFIED_DATATYPE</code>
	 *            should be added.
	 */
	public static void setParametersPaths(Delimiters delimiters, String path,
			Parameter parameter)
	{
		if (parameter != null)
		{
			if (path == null)
			{
				path = "";
			}
			else if (path.length() > 0)
			{
				// omit the path delimiter if Parameters documentations are nor assignable
				// to it (identifier is empty)
				path += delimiters.pathDelimiter;
			}

			path += parameter.getQualifiedIdentifier() + delimiters.typeDelimiter
					+ parameter.getQualifiedDataTypeName();
			parameter.setSignatureElementPath(path);

			for (Parameter param : parameter.getComplexType())
			{
				setParametersPaths(delimiters, path, param);
			}
		}
		else
		{
			logger.log(Level.WARNING, path + "  --  parameter == null");
		}
	}

	/**
	 * Parses the path element and returns the identifier and qualified identifier for the
	 * name and type of the parameter element.
	 * 
	 * @param delimiters
	 *            The {@link Delimiters} that should be use to split the
	 *            <code>pathElement</code>
	 * @param pathElement
	 *            The path element to parse.
	 * @return The {@link ParameterPathElement} for the <code>pathElement</code>.
	 */
	public static ParameterPathElement parsePathElement(Delimiters delimiters,
			String pathElement)
	{
		ParameterPathElement paramPathElem = new ParameterPathElement();

		int typeDelimiterPos = pathElement.indexOf(delimiters.typeDelimiter);
		if (typeDelimiterPos > -1)
		{
			String[] nameAndType = pathElement.split(delimiters.getQuotedTypeDelimiter());
			paramPathElem.setQualifiedIdentifier(nameAndType[0]);
			paramPathElem.setIdentifier(extractIdentifier(delimiters, nameAndType[0]));
			paramPathElem.setQualifiedTypeName(nameAndType[1]);
			paramPathElem.setTypeName(extractIdentifier(delimiters, nameAndType[1]));
		}
		else
		{
			paramPathElem.setQualifiedIdentifier(pathElement);
			paramPathElem.setIdentifier(extractIdentifier(delimiters, pathElement));
		}
		return paramPathElem;
	}

	/**
	 * Extracts the identifier from the qualified identifier. If the qualified identifier
	 * has no name space, it is returned without changes.
	 * 
	 * @param delimiters
	 *            The {@link Delimiters} that should be use to split qualified identifier.
	 * @param qualifiedIdentifier
	 *            The qualified identifier to split.
	 * @return the identifier without name space.
	 */
	private static String extractIdentifier(Delimiters delimiters,
			String qualifiedIdentifier)
	{
		int namespaceDelimiterPos = qualifiedIdentifier
				.lastIndexOf(delimiters.namespaceDelimiter);
		if (namespaceDelimiterPos > -1)
		{
			return qualifiedIdentifier.substring(namespaceDelimiterPos,
					qualifiedIdentifier.length());
		}
		return qualifiedIdentifier;
	}

	/**
	 * Get the {@link ThematicRole} by name. The names are compared with case
	 * insensitivity. If no existing {@link ThematicRole} with the name is found, a new
	 * one is created and returned.
	 * 
	 * @param name
	 *            The name of the searched {@link ThematicRole}.
	 * @return a {@link ThematicRole} with the name <code>name</code>.
	 */
	public static ThematicRole findThematicRole(String name)
	{
		for (ThematicRole role : supportedThematicRoles)
		{
			if (role.getName().equalsIgnoreCase(name))
			{
				return role;
			}
		}
		logger.log(Level.INFO, "Unknown thematic role: " + name);
		return new ThematicRole(name);
	}

	/**
	 * Get the {@link Addressee} by name. The names are compared with case insensitivity.
	 * If no existing {@link Addressee} with the name is found, a new one is created and
	 * returned.
	 * 
	 * @param name
	 *            The name of the searched {@link Addressee}.
	 * @return a {@link Addressee} with the name <code>name</code>.
	 */
	public static Addressee findAddressee(String name)
	{
		for (Addressee a : supportedAddressees)
		{
			if (a.getName().equalsIgnoreCase(name))
			{
				return a;
			}
		}
		logger.log(Level.INFO, "Unknown addressee: " + name);
		return new Addressee(name);
	}
}
