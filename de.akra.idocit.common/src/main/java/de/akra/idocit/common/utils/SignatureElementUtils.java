package de.akra.idocit.common.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.ParameterPathElement;
import de.akra.idocit.common.structure.ParameterType;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;

public class SignatureElementUtils
{
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(SignatureElementUtils.class.getName());
	
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

		boolean found = false;
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
	 * Checks if the operation's or at least one of it's sub-elements'
	 * {@link Documentation}s were changed.
	 * 
	 * @param operation
	 *            The {@link Operation} to check.
	 * @return true, if this operation'' {@link Documentation}s or at least one of the
	 *         sub-elements' {@link Documentation}s were changed.
	 * @since 0.0.2
	 * @see SignatureElement#isDocumentationChanged()
	 */
	public static boolean isOperationsDocChanged(Operation operation)
	{
		return operation.isDocumentationChanged()
				|| doIsOperationsDocChanged(operation.getInputParameters())
				|| doIsOperationsDocChanged(operation.getOutputParameters())
				|| doIsOperationsDocChanged(operation.getExceptions());
	}

	/**
	 * Checks for all {@link Parameters} in the list if at least one {@link Documentation}
	 * was changed.
	 * 
	 * @param parametersList
	 *            The list of Parameters to check (in general used for the list of
	 *            exceptions).
	 * @return true, if at least one signature elements's documentation was changed.
	 * @since 0.0.2
	 */
	private static boolean doIsOperationsDocChanged(
			List<? extends Parameters> parametersList)
	{
		Iterator<? extends Parameters> iter = parametersList.iterator();
		boolean docChanged = false;
		while (iter.hasNext() && !docChanged)
		{
			Parameters params = iter.next();
			docChanged = doIsOperationsDocChanged(params);
		}
		return docChanged;
	}

	/**
	 * Checks if the {@link Parameters}' or at least one sub-elements'
	 * {@link Documentation} was changed.
	 * 
	 * @param currentElem
	 *            A {@link Parameters} or {@link Parameter} to check if their
	 *            {@link Documentation}s were changed.
	 * @return true, if this elements's {@link Documentation}(s) or at least one of the
	 *         sub-elements' {@link Documentation} were changed.
	 * @since 0.0.2
	 */
	private static boolean doIsOperationsDocChanged(SignatureElement currentElem)
	{
		if (currentElem == null)
		{
			return false;
		}
		else if (currentElem.isDocumentationChanged())
		{
			return true;
		}

		Iterator<Parameter> itParam;
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

		boolean docChanged = false;
		while (itParam.hasNext() && !docChanged)
		{
			Parameter param = itParam.next();
			docChanged = doIsOperationsDocChanged(param);
		}
		return docChanged;
	}
}
