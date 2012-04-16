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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.ComplexContent;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Extension;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaElement;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.Types;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfParam;
import org.w3c.dom.Node;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Provides operations for the extraction of information from WSDL-files.
 * 
 * @author Jan Christian Krause
 * 
 */
public final class WSDLParsingService
{
	private static Logger logger = Logger.getLogger(WSDLParsingService.class.getName());
	
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
	 *            The {@link AbsItfParam} (input, output or fault element) to extract the 
	 *            type structure from.
	 * @param types
	 *            The {@link Types} in the WSDL file.
	 * 
	 * @return The {@link List} of type paths
	 */
	public static List<String> extractRoles(AbsItfParam wsdlMessage, Types types,
			Delimiters delimiters)
	{
		List<String> result = new ArrayList<String>();

		// For each Part ...
		if ((wsdlMessage != null) && (wsdlMessage.getParts() != null)
				&& !wsdlMessage.getParts().isEmpty())
		{
			for (Part part : wsdlMessage.getParts())
			{

				// 1. Generate the path to the Part ...
				String localPartName = (part.getPartQName() != null) ? part
						.getPartQName().getLocalPart() : part.getType() != null
						&& part.getType().getQName() != null ? part.getType().getQName()
						.getLocalPart() : part.getElement() != null
						&& part.getElement().getQName() != null ? part.getElement()
						.getQName().getLocalPart()
						: SignatureElement.ANONYMOUS_IDENTIFIER;
				logger.fine("localPartName: " + localPartName);
								
				String path = wsdlMessage.getMessageName().getLocalPart()
						+ delimiters.pathDelimiter + localPartName
						+ delimiters.typeDelimiter + (part.getType() != null
								&& part.getType().getQName() != null ? part.getType().getQName()
										.getLocalPart() : part.getElement() != null
										&& part.getElement().getQName() != null ? part.getElement()
										.getQName().getLocalPart() : SignatureElement.ANONYMOUS_IDENTIFIER);
				logger.fine("path: " + path);

				Type typeNode = null;
				if (part.getElement() != null) {
					Element elem = part.getElement();
					path = path + delimiters.pathDelimiter
							+ elem.getQName().getLocalPart() + delimiters.typeDelimiter;
					
					typeNode = elem.getType();

					if (typeNode != null)
					{
						path += elem.getType().getQName().getLocalPart();
					}
					else
					{
						path += TYPE_NAME_ANONYMOUS;
					}
				}
				
				// ... and append a dot, if the Part has not a simple type as type.
				if (typeNode != null && ComplexType.class.isAssignableFrom(typeNode.getClass()))
				{
					path += delimiters.pathDelimiter;

					// ... and extract its flat structure.
					List<String> childPaths = extractFlatMessageStructure(typeNode,
							new HashSet<String>(), delimiters);

					for (String childPpath : childPaths)
					{
						result.add(path + childPpath);
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
			String path = wsdlMessage.getName();
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
	private static Element findTypeDeclarationNode(Element tree, String localTypeName)
	{
		
		Type type = tree.getType();
		
//		 1. Case: we have an element and have to look for the name.
		if(tree.getQName().getLocalPart().equals(localTypeName)) {
			return tree;
		} else if (SimpleType.class.isAssignableFrom(type.getClass())) {
			SimpleType sType = (SimpleType)type;
			
			sType.getQName();
			
			// 2. Case: we have a one-node-tree
			return null;
		}
		else if (ComplexType.class.isAssignableFrom(type.getClass())) {
			// 3. Case: the current node is not the right one, but maybe one of
			// its children.
			
			ComplexType cType = (ComplexType)type;
			logger.info(""+cType.hasChoice());
			logger.info(""+cType.hasComplexContent());
			logger.info(""+cType.hasSequence());
			logger.info(""+cType.hasSimpleContent());
			Element typeNode = null;
			
			if(cType.hasChoice()) {
				Choice choice = cType.getChoice();
				
				Iterator<Element> elemIter = choice.getElements().iterator(); 
				while(elemIter.hasNext() && typeNode == null) {
					Element elem = elemIter.next();
					typeNode = findTypeDeclarationNode(elem, localTypeName);
				}
				return typeNode;
				
			} else if(cType.hasSequence()) {
				Sequence sequence = cType.getSequence();
				
				Iterator<Element> elemIter = sequence.getElements().iterator(); 
				while(elemIter.hasNext() && typeNode == null) {
					Element elem = elemIter.next();
					typeNode = findTypeDeclarationNode(elem, localTypeName);
				}
				return typeNode;
				
			}else if(cType.hasComplexContent()) {
				ComplexContent complexContent = cType.getComplexContent();
				Extension ex = complexContent.getExtension();
				logger.info(ex.getBase().getQName().toString());
				ex.getSequence();
				
			}else if(cType.hasSimpleContent()) {
				Extension ex = cType.getSimpleContent().getExtension();
				logger.info(ex.getBase().getQName().toString());
			}
		}
		
//		 1. Case: we have an element and have to look for the name.
//		if ((isElementOrComplexType(tree) || isSimpleType(tree.getQName().getLocalPart()))
//				&& getNameAttribute(tree).equals(localTypeName))
//		{
//			return tree;
//		}
//		else if (!tree.hasChildNodes())
//		{
//			// 2. Case: we have a one-node-tree
//			return null;
//		}
//		else
//		{
//			// 3. Case: the current node is not the right one, but maybe one of
//			// its children.
//			NodeList children = tree.getChildNodes();
//
//			Node typeNode = null;
//			int i = 0;
//
//			while ((i < children.getLength()) && (typeNode == null))
//			{
//				typeNode = findTypeDeclarationNode(children.item(i), localTypeName);
//				i++;
//			}
//
//			return typeNode;
//		}
		
		return null;
	}

	private static String getNameAttribute(SchemaElement tree)
	{
		String name = TYPE_NAME_ANONYMOUS;
		try
		{
			// TODO check if we get here the correct name
			String tmpName;
			if ((tmpName = tree.getOtherAttributes().get(new QName(XML_ATTRIBUTE_NAME))) != null)
			{
				name = tmpName;
			}
			return name;
		}
		catch (XmlException e)
		{
			logger.log(Level.WARNING, "Failed to retrieve element's atttributes.", e);
			return null;
		}
	}

	private static boolean isElementOrComplexType(Element tree)
	{
		return XML_ELEMENT.equalsIgnoreCase(String.valueOf(tree.getQName().getLocalPart()))
				|| isNamedComplexType(tree);
	}

	private static boolean isNamedComplexType(Element tree)
	{
		try
		{
			return XML_COMPLEXTYPE.equalsIgnoreCase(String.valueOf(tree.getQName().getLocalPart()))
					&& (tree.getOtherAttributes() != null) && (getNameAttribute(tree) != null);
		}
		catch (XmlException e)
		{
			logger.log(Level.WARNING, "Failed to retrieve element's atttributes.", e);
			return false;
		}
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

	private static boolean isSimpleType(Type node)
	{
		return (node != null) && isSimpleType(node.getQName().getLocalPart());
	}

	/**
	 * Extracts the flat-message structure as specified in the documentation of {@link
	 * this#extractRoles(AbsItfParam, Types, Delimiters)}.
	 * 
	 * @param node
	 *            The tree to look in
	 * 
	 * @return The flat-message structure
	 */
	private static List<String> extractFlatMessageStructure(Type type,
			Set<String> visitedTypes, Delimiters delimiters)
	{
		// TODO extract flat structure dependent on the instantiated class...
		List<String> result = new ArrayList<String>();
		
		if (SimpleType.class.isAssignableFrom(type.getClass())) {
			
			result.add(elementName + delimiters.typeDelimiter
//					+ getNameAttribute(typeDeclarationNode));
			
			SimpleType sType = (SimpleType)type;
			
			sType.getQName();
			
			// 2. Case: we have a one-node-tree
			return null;
		}
		else if (ComplexType.class.isAssignableFrom(type.getClass())) {
			// 3. Case: the current node is not the right one, but maybe one of
			// its children.
			
			ComplexType cType = (ComplexType)type;
			logger.info(""+cType.hasChoice());
			logger.info(""+cType.hasComplexContent());
			logger.info(""+cType.hasSequence());
			logger.info(""+cType.hasSimpleContent());
			Element typeNode = null;
			
			if(cType.hasChoice()) {
				Choice choice = cType.getChoice();
				
				Iterator<Element> elemIter = choice.getElements().iterator(); 
				while(elemIter.hasNext() && typeNode == null) {
					Element elem = elemIter.next();
					typeNode = findTypeDeclarationNode(elem, localTypeName);
				}
				return typeNode;
				
			} else if(cType.hasSequence()) {
				Sequence sequence = cType.getSequence();
				
				Iterator<Element> elemIter = sequence.getElements().iterator(); 
				while(elemIter.hasNext() && typeNode == null) {
					Element elem = elemIter.next();
					typeNode = findTypeDeclarationNode(elem, localTypeName);
				}
				return typeNode;
				
			}else if(cType.hasComplexContent()) {
				ComplexContent complexContent = cType.getComplexContent();
				Extension ex = complexContent.getExtension();
				logger.info(ex.getBase().getQName().toString());
				ex.getSequence();
				
			}else if(cType.hasSimpleContent()) {
				Extension ex = cType.getSimpleContent().getExtension();
				logger.info(ex.getBase().getQName().toString());
			}
		
		
//		List<String> result = new ArrayList<String>();
//		// At first derive the type name ...
//		String typeName = node.getQName().getLocalPart();
//		boolean isVisitedType = visitedTypes.contains(typeName);
//		visitedTypes.add(typeName);
//
//		// ... and check if we have a simple type. (1st case)
//		if(SimpleType.class.isAssignableFrom(node.getClass())) {
//			// Ok, we have to add this element to our result.
//			String elementName = node.getQName().getLocalPart();
//
//			result.add(elementName + delimiters.typeDelimiter + typeName);
//		} 
//		else if (isVisitedType && !TYPE_NAME_ANONYMOUS.equals(typeName))
//		{
//			// 2nd case: do we have a recursive type definition?
//			// Ok, we have to add this element to our result and stop recursion.
//			String elementName = getNameAttribute(node);
//			result.add(elementName + delimiters.typeDelimiter + typeName
//					+ TYPE_NAME_RECURSION);
//		}
//		else
//		{
//			// No? Well, then we have to analyse all child-nodes.
//			List<String> allChildPaths = new ArrayList<String>();
//			Type typeDeclarationNode = node;
//
//			if (TYPE_NAME_ANONYMOUS.equals(typeName))
//			{
//				
//				logger.info(node.getQName().toString());
////				NodeList childNodes = node.getChildNodes();
////
////				// Get the paths of the child-nodes ...
////				for (int i = 0; i < childNodes.getLength(); i++)
////				{
////					Node childNode = childNodes.item(i);
////
////					allChildPaths.addAll(extractFlatMessageStructure(childNode, schemata,
////							visitedTypes, delimiters));
////				}
//			}
//			else
//			{
//				int i = 0;
//
////				while ((i < schemata.size()) && (typeDeclarationNode == null))
////				{
////					typeDeclarationNode = findTypeDeclarationNode(schemata.get(i),
////							typeName);
////					i++;
////				}
//
//				if (typeDeclarationNode != null)
//				{
//					allChildPaths.addAll(extractFlatMessageStructure(typeDeclarationNode,
//							visitedTypes, delimiters));
//				}
//			}
//
//			// This node has to be added to the result only if it is an element.
//			if (ComplexType.class.isAssignableFrom(node.getClass())) {
//				ComplexType cType = (ComplexType)node;
//				
//				
//				
//
//				// Parse only valid elements with a name.
//				String elementName = cType.getQName().getLocalPart();
//
//				// ... and concat them with the current element- and
//				// typename.
//				if (!allChildPaths.isEmpty())
//				{
//					for (String childPath : allChildPaths)
//					{
////						if (isNamedComplexType(node))
////						{
////							result.add(childPath);
////						}
////						else
////						{
////							result.add(elementName + delimiters.typeDelimiter + typeName
////									+ delimiters.pathDelimiter + childPath);
////						}
//					}
//				}
//				else if (SimpleType.class.isAssignableFrom(typeDeclarationNode.getClass()))
//				{
//					result.add(elementName + delimiters.typeDelimiter
//							+ getNameAttribute(typeDeclarationNode));
//				}
//				else
//				{
//					result.add(elementName + delimiters.typeDelimiter
//							+ TYPE_NAME_NO_DEFINITION);
//				}
//			}
//			else
//			{
//				return allChildPaths;
//			}
//		}
		
		
//		if (isSimpleType)
//		{
//		}
//		else if (isVisitedType && !TYPE_NAME_ANONYMOUS.equals(typeName))
//		{
//		}
//		else
//		{
//			// No? Well, then we have to analyse all child-nodes.
//			List<String> allChildPaths = new ArrayList<String>();
//			Type typeDeclarationNode = null;
//
//			if (TYPE_NAME_ANONYMOUS.equals(typeName))
//			{
//				
//				logger.info(node.getQName().toString());
////				NodeList childNodes = node.getChildNodes();
////
////				// Get the paths of the child-nodes ...
////				for (int i = 0; i < childNodes.getLength(); i++)
////				{
////					Node childNode = childNodes.item(i);
////
////					allChildPaths.addAll(extractFlatMessageStructure(childNode, schemata,
////							visitedTypes, delimiters));
////				}
//			}
//			else
//			{
//				int i = 0;
//
//				while ((i < schemata.size()) && (typeDeclarationNode == null))
//				{
//					typeDeclarationNode = findTypeDeclarationNode(schemata.get(i),
//							typeName);
//					i++;
//				}
//
//				if (typeDeclarationNode != null)
//				{
//					allChildPaths.addAll(extractFlatMessageStructure(typeDeclarationNode,
//							schemata, visitedTypes, delimiters));
//				}
//			}
//
//			// This node has to be added to the result only if it is an element.
//			if (isElementOrComplexType(node))
//			{
//				// Parse only valid elements with a name.
//				String elementName = getNameAttribute(node);
//
//				// ... and concat them with the current element- and
//				// typename.
//				if (!allChildPaths.isEmpty())
//				{
//					for (String childPath : allChildPaths)
//					{
//						if (isNamedComplexType(node))
//						{
//							result.add(childPath);
//						}
//						else
//						{
//							result.add(elementName + delimiters.typeDelimiter + typeName
//									+ delimiters.pathDelimiter + childPath);
//						}
//					}
//				}
//				else if (isSimpleType(typeDeclarationNode))
//				{
//					result.add(elementName + delimiters.typeDelimiter
//							+ getNameAttribute(typeDeclarationNode));
//				}
//				else
//				{
//					result.add(elementName + delimiters.typeDelimiter
//							+ TYPE_NAME_NO_DEFINITION);
//				}
//			}
//			else
//			{
//				return allChildPaths;
//			}
//		}

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
	private static String deriveTypeName(Element node, Delimiters delimiters)
	{
		String typeName = null;
		String nodeName = String.valueOf(node.getQName().getLocalPart());
		
		if (node.getType() == null)
		{
			typeName = TYPE_NAME_ANONYMOUS;
		}
		else if (XML_SIMPLETYPE.equalsIgnoreCase(nodeName))
		{
			return nodeName;
		}
		else
		{
			// TODO get maybe whole QName
			typeName = node.getType().getQName().getLocalPart();
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
