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
package de.akra.idocit.wsdl.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.akra.idocit.common.structure.Delimiters;

/**
 * Provides operations for the extraction of information from WSDL-files.
 * 
 * @author Jan Christian Krause
 * 
 */
public final class WSDLParsingService
{
	// Constants
	private static final String TYPE_NAME_NO_DEFINITION = "no_definition";
	private static final String TYPE_NAME_RECURSION = "[recursion]";
	private static final String TYPE_NAME_ANONYMOUS = "anonymous";

	private static final String XML_NS_DELIMITER = ":";
	private static final String XML_ATTRIBUTE_TYPE = "type";
	private static final String XML_SIMPLETYPE = "simpletype";
	private static final String XML_COMPLEXTYPE = "complextype";
	private static final String XML_ELEMENT = "element";
	private static final String XML_ATTRIBUTE_NAME = "name";
	private static final String[] SIMPLE_XML_TYPES = { "string", "normalizedString",
			"token", "base64Binary", "hexBinary", "integer", "positiveInteger",
			"negativeInteger", "nonNegativeInteger", "nonPositiveInteger", "long",
			"unsignedLong", "int", "unsignedInt", "short", "unsignedShort", "byte",
			"unsignedByte", "decimal", "float", "double", "boolean", "duration",
			"dateTime", "date", "time", "gYear", "gYearMonth", "gMonth", "gMonthDay",
			"gDay", "Name", "QName", "NCName", "anyURI", "language", "ID", "IDREF",
			"IDREFS", "ENTITY", "ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS", "anyType" };

	/**
	 * Extracts all the referenced type structure from the given {@link Message}
	 * <code>wsdlMessage</code> and returns it as {@link List} of paths (flat structure)
	 * in the following format:
	 * 
	 * <p>
	 * &lt;MESSAGE_NAME&gt;{@link Delimiters#pathDelimiter}&lt;PART_NAME&gt;
	 * {@link Delimiters#typeDelimiter}&lt;ELEMENT_TYPE&gt;[
	 * {@link Delimiters#pathDelimiter}&lt;ELEMENT_NAME&gt;
	 * {@link Delimiters#typeDelimiter}&lt;ELEMENT_TYPE&gt;]*
	 * </p>
	 * 
	 * <p>
	 * Example: GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList
	 * (anonymous). prefixText(string)
	 * </p>
	 * 
	 * for this WSDL-snipplet:
	 * 
	 * <pre>
	 * &lt;wsdl:types&gt;
	 *  &lt;s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/"&gt;
	 *   &lt;s:element name="GetCompletionList"&gt;
	 *    &lt;s:complexType&gt;
	 *     &lt;s:sequence&gt;
	 *      &lt;s:element minOccurs="0" maxOccurs="1" name="prefixText" type="s:string" /&gt;
	 *     &lt;/s:sequence&gt;
	 *    &lt;/s:complexType&gt;
	 *   &lt;/s:element&gt;
	 *  &lt;/s:schema&gt;
	 * &lt;/wsdl:types&gt;
	 * 
	 * &lt;wsdl:message name="GetCompletionListSoapIn"&gt;
	 *  &lt;wsdl:part name="parameters" element="tns:GetCompletionList" /&gt;
	 * &lt;/wsdl:message&gt;
	 * </pre>
	 * 
	 * <p>
	 * <u>Note:</u>
	 * <ul>
	 * <li>If no type is specified, the {@link String} "anonymous" is used as
	 * &lt;ELEMENT_TYPE&gt;.</li>
	 * <li>In case of recursive cycles in the type-definitions, the {@link String}
	 * "recursion" is used as &lt;ELEMENT_TYPE&gt;</li>
	 * <li>In case of an element in the type declaration with no name, its name is set to
	 * "anonymous" (like an anonymous type).</li>
	 * <li>If a complex type contains no elements, it is labeled with the type
	 * "no_definition"</li>
	 * </ul>
	 * </p>
	 * 
	 * @param wsdlMessage
	 *            The {@link Message} to extract the type structure from
	 * @param types
	 *            The {@link Types} in the WSDL file.
	 * 
	 * @return The {@link List} of type paths
	 */
	public static List<String> extractRoles(Message wsdlMessage, Types types,
			Delimiters delimiters)
	{
		List<String> result = new ArrayList<String>();

		// For each Part ...
		if ((wsdlMessage != null) && (wsdlMessage.getParts() != null)
				&& !wsdlMessage.getParts().isEmpty())
		{
			for (Object partObject : wsdlMessage.getParts().entrySet())
			{
				@SuppressWarnings("unchecked")
				Entry<QName, Part> partEntry = (Entry<QName, Part>) partObject;
				Part part = partEntry.getValue();

				// 1. Generate the path to the Part ...
				String localPartName = (part.getElementName() != null) ? part
						.getElementName().getLocalPart()
						: part.getTypeName() != null ? part.getTypeName().getLocalPart()
								: part.getName();
				String path = wsdlMessage.getQName().getLocalPart()
						+ delimiters.pathDelimiter + part.getName()
						+ delimiters.typeDelimiter + localPartName;

				// ... and append a dot, if the Part has a simple type as type.
				if (!isSimpleType(localPartName) && (types != null))
				{
					path += delimiters.pathDelimiter;
					List<Node> schemata = new ArrayList<Node>();

					// 2. Collect all schema-nodes in one list, ...
					for (Object schemaObj : types.getExtensibilityElements())
					{
						if (schemaObj instanceof Schema)
						{
							Schema schema = (Schema) schemaObj;

							schemata.add(schema.getElement());
						}
					}

					int i = 0;
					Node typeNode = null;

					// ... get the node which declares the type of the current
					// Part
					// ...
					while ((i < schemata.size()) && (typeNode == null))
					{
						typeNode = findTypeDeclarationNode(schemata.get(i), localPartName);
						i++;
					}

					// ... and extract its flat structure.
					if (typeNode != null)
					{
						List<String> childPaths = extractFlatMessageStructure(typeNode,
								schemata, new HashSet<String>(), delimiters);

						for (String childPpath : childPaths)
						{
							result.add(path + childPpath);
						}
					}
				}
				else
				{
					result.add(path);
				}
			}
		}
		else
		{
			String path = wsdlMessage.getQName().getLocalPart();
			result.add(path);
		}

		return result;
	}

	/**
	 * Returns the {@link Node} with localname == element and attribute name ==
	 * <code>localTypeName</code>. It searches for this {@link Node} in the given
	 * <code>tree</code>.
	 * 
	 * @param tree
	 *            The tree where to look for the {@link Node}
	 * @param localTypeName
	 *            The type-name of the {@link Node} to look for
	 * @return The {@link Node} with name <code>localTypeName</code>
	 */
	private static Node findTypeDeclarationNode(Node tree, String localTypeName)
	{
		// 1. Case: we have an element and have to look for the name.
		if ((isElementOrComplexType(tree) || isSimpleType(tree.getLocalName()))
				&& getNameAttribute(tree).equals(localTypeName))
		{
			return tree;
			// 2. Case: we have a one-node-tree
		}
		else if (!tree.hasChildNodes())
		{
			return null;
			// 3. Case: the current node is not the right one, but maybe one of
			// its
			// children.
		}
		else
		{
			NodeList children = tree.getChildNodes();

			Node typeNode = null;
			int i = 0;

			while ((i < children.getLength()) && (typeNode == null))
			{
				typeNode = findTypeDeclarationNode(children.item(i), localTypeName);
				i++;
			}

			return typeNode;
		}
	}

	private static String getNameAttribute(Node tree)
	{
		if (tree.getAttributes() != null)
		{
			Node name = tree.getAttributes().getNamedItem(XML_ATTRIBUTE_NAME);

			return (name != null) ? name.getNodeValue() : TYPE_NAME_ANONYMOUS;
		}
		else
		{
			return TYPE_NAME_ANONYMOUS;
		}
	}

	private static boolean isElementOrComplexType(Node tree)
	{
		return XML_ELEMENT.equalsIgnoreCase(String.valueOf(tree.getLocalName()))
				|| isNamedComplexType(tree);
	}

	private static boolean isNamedComplexType(Node tree)
	{
		return XML_COMPLEXTYPE.equalsIgnoreCase(String.valueOf(tree.getLocalName()))
				&& (tree.getAttributes() != null) && (getNameAttribute(tree) != null);
	}

	/**
	 * Returns <code>true</code> if <code>typeName</code> is a simple type according to
	 * the XML-Schema specification: http://www.w3.org/TR/xmlschema-0/
	 * 
	 * @param typeName
	 *            The type-name to check
	 * 
	 * @return <code>true</code> if <code>typeName</code> is a simple type, else
	 *         <code>false</code>
	 */
	private static boolean isSimpleType(String typeName)
	{
		String lcTypeName = String.valueOf(typeName);

		if (XML_SIMPLETYPE.equalsIgnoreCase(lcTypeName))
		{
			return true;
		}
		else
		{
			for (String simpleType : SIMPLE_XML_TYPES)
			{
				if (simpleType.equalsIgnoreCase(lcTypeName))
				{
					return true;
				}
			}

			return false;
		}
	}

	private static boolean isSimpleType(Node node)
	{
		return (node != null) && isSimpleType(node.getLocalName());
	}

	/**
	 * Extracts the flat-message structure as specified in the documentation of {@link
	 * this#extractRoles(Message, Types)}.
	 * 
	 * @param node
	 *            The tree to look in
	 * 
	 * @return The flat-message structure
	 */
	private static List<String> extractFlatMessageStructure(Node node,
			List<Node> schemata, Set<String> visitedTypes, Delimiters delimiters)
	{
		List<String> result = new ArrayList<String>();
		// At first derive the type name ...
		String typeName = deriveTypeName(node, delimiters);
		boolean isSimpleType = isSimpleType(typeName);
		boolean isVisitedType = visitedTypes.contains(typeName);

		visitedTypes.add(typeName);

		// ... and check if we have a simple type. (1st case)
		if (isSimpleType)
		{
			// Ok, we have to add this element to our result.
			String elementName = getNameAttribute(node);

			result.add(elementName + delimiters.typeDelimiter + typeName);
			// 2nd case: do we have a recursive type definition?
		}
		else if (isVisitedType && !TYPE_NAME_ANONYMOUS.equals(typeName))
		{
			// Ok, we have to add this element to our result and stop recursion.
			String elementName = getNameAttribute(node);

			result.add(elementName + delimiters.typeDelimiter + typeName
					+ TYPE_NAME_RECURSION);
		}
		else
		{
			// No? Well, then we have to analyse all child-nodes.
			List<String> allChildPaths = new ArrayList<String>();
			Node typeDeclarationNode = null;

			if (TYPE_NAME_ANONYMOUS.equals(typeName))
			{
				NodeList childNodes = node.getChildNodes();

				// Get the paths of the child-nodes ...
				for (int i = 0; i < childNodes.getLength(); i++)
				{
					Node childNode = childNodes.item(i);

					allChildPaths.addAll(extractFlatMessageStructure(childNode, schemata,
							visitedTypes, delimiters));
				}
			}
			else
			{
				int i = 0;

				while ((i < schemata.size()) && (typeDeclarationNode == null))
				{
					typeDeclarationNode = findTypeDeclarationNode(schemata.get(i),
							typeName);
					i++;
				}

				if (typeDeclarationNode != null)
				{
					allChildPaths.addAll(extractFlatMessageStructure(typeDeclarationNode,
							schemata, visitedTypes, delimiters));
				}
			}

			// This node has to be added to the result only if it is an element.
			if (isElementOrComplexType(node))
			{
				// Parse only valid elements with a name.
				String elementName = getNameAttribute(node);

				// ... and concat them with the current element- and
				// typename.
				if (!allChildPaths.isEmpty())
				{
					for (String childPath : allChildPaths)
					{
						if (isNamedComplexType(node))
						{
							result.add(childPath);
						}
						else
						{
							result.add(elementName + delimiters.typeDelimiter + typeName
									+ delimiters.pathDelimiter + childPath);
						}
					}
				}
				else if (isSimpleType(typeDeclarationNode))
				{
					result.add(elementName + delimiters.typeDelimiter
							+ getNameAttribute(typeDeclarationNode));
				}
				else
				{
					result.add(elementName + delimiters.typeDelimiter
							+ TYPE_NAME_NO_DEFINITION);
				}
			}
			else
			{
				return allChildPaths;
			}
		}

		return result;
	}

	/**
	 * Returns the value of attribute "type" of the given {@link Node} <code>node</code>.
	 * If this attribute is not declared, the type will be "anonymous". The result will
	 * always be a local name without namespace.
	 * 
	 * @param node
	 *            The {@link Node} to get the type of
	 * 
	 * @return The type of the given node.
	 */
	private static String deriveTypeName(Node node, Delimiters delimiters)
	{
		String typeName = null;
		String nodeName = String.valueOf(node.getLocalName());

		if ((node.getAttributes() == null)
				|| (node.getAttributes().getNamedItem(XML_ATTRIBUTE_TYPE) == null))
		{
			typeName = TYPE_NAME_ANONYMOUS;
		}
		else if (XML_SIMPLETYPE.equalsIgnoreCase(nodeName))
		{
			return nodeName;
		}
		else
		{
			typeName = node.getAttributes().getNamedItem(XML_ATTRIBUTE_TYPE)
					.getNodeValue();
		}

		if (typeName.indexOf(XML_NS_DELIMITER) > -1)
		{
			return typeName.split(XML_NS_DELIMITER)[1];
		}
		else
		{
			return typeName;
		}
	}

}
