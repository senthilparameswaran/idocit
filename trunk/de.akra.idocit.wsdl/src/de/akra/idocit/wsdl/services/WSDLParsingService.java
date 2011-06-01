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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.akra.idocit.wsdl.structure.WsdlContent;
import de.akra.idocit.wsdl.structure.WsdlMetadata;
import de.akra.idocit.wsdl.structure.WsdlParsingResult;

/**
 * Provides operations for the extraction of information from WSDL-files.
 * 
 * @author Jan Christian Krause
 * 
 */
public final class WSDLParsingService {

	// Constants
	private static final Logger logger = Logger.getLogger(WSDLParsingService.class.getName());
	private static final String ANONYMOUS_TYPE_NAME = "anonymous";
	private static final String[] SIMPLE_XML_TYPES = { "string", "normalizedString", "token", "base64Binary", "hexBinary", "integer",
			"positiveInteger", "negativeInteger", "nonNegativeInteger", "nonPositiveInteger", "long", "unsignedLong", "int", "unsignedInt", "short",
			"unsignedShort", "byte", "unsignedByte", "decimal", "float", "double", "boolean", "duration", "dateTime", "date", "time", "gYear",
			"gYearMonth", "gMonth", "gMonthDay", "gDay", "Name", "QName", "NCName", "anyURI", "language", "ID", "IDREF", "IDREFS", "ENTITY",
			"ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS", "anyType" };

	/**
	 * Extracts all the referenced type structure from the given {@link Message}
	 * <code>wsdlMessage</code> and returns it as {@link List} of paths (flat
	 * structure) in the following format:
	 * 
	 * <p>
	 * &lt;MESSAGE_NAME&gt;"."&lt;PART_NAME&gt;"("&lt;ELEMENT_TYPE&gt;")"["."&lt
	 * ;ELEMENT_NAME&gt;"("&lt;ELEMENT_TYPE&gt;")"]*
	 * </p>
	 * 
	 * <p>
	 * Example:
	 * GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList
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
	 * <li>In case of recursive cycles in the type-definitions, the
	 * {@link String} "recursion" is used as &lt;ELEMENT_TYPE&gt;</li>
	 * <li>In case of an element in the type declaration with no name, its name
	 * is set to "anonymous" (like an anonymous type).</li>
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
	public static List<String> extractRoles(Message wsdlMessage, Types types) {
		List<String> result = new ArrayList<String>();

		// For each Part ...
		if ((wsdlMessage != null) && (wsdlMessage.getParts() != null) && !wsdlMessage.getParts().isEmpty()) {
			for (Object partObject : wsdlMessage.getParts().entrySet()) {
				Entry<QName, Part> partEntry = (Entry<QName, Part>) partObject;
				Part part = partEntry.getValue();

				// 1. Generate the path to the Part ...
				String localPartName = (part.getElementName() != null) ? part.getElementName().getLocalPart() : part.getTypeName() != null ? part
						.getTypeName().getLocalPart() : part.getName();
				String path = wsdlMessage.getQName().getLocalPart() + "." + part.getName() + "(" + localPartName + ")";

				// ... and append a dot, if the Part has a simple type as type.
				if (!isSimpleType(localPartName) && (types != null)) {
					path += ".";
					List<Node> schemata = new ArrayList<Node>();

					// 2. Collect all schema-nodes in one list, ...
					for (Object schemaObj : types.getExtensibilityElements()) {
						if (schemaObj instanceof Schema) {
							Schema schema = (Schema) schemaObj;

							schemata.add(schema.getElement());
						}
					}

					int i = 0;
					Node typeNode = null;

					// ... get the node which declares the type of the current
					// Part
					// ...
					while ((i < schemata.size()) && (typeNode == null)) {
						typeNode = findTypeDeclarationNode(schemata.get(i), localPartName);
						i++;
					}

					// ... and extract its flat structure.
					if (typeNode != null) {
						List<String> childPaths = extractFlatMessageStructure(typeNode, schemata, new HashSet<String>());

						for (String childPpath : childPaths) {
							result.add(path + childPpath);
						}
					}
				} else {
					result.add(path);
				}
			}
		} else {
			String path = wsdlMessage.getQName().getLocalPart();
			result.add(path);
		}

		return result;
	}

	/**
	 * Returns the {@link Node} with localname == element and attribute name ==
	 * <code>localTypeName</code>. It searches for this {@link Node} in the
	 * given <code>tree</code>.
	 * 
	 * @param tree
	 *            The tree where to look for the {@link Node}
	 * @param localTypeName
	 *            The type-name of the {@link Node} to look for
	 * @return The {@link Node} with name <code>localTypeName</code>
	 */
	private static Node findTypeDeclarationNode(Node tree, String localTypeName) {
		// 1. Case: we have an element and have to look for the name.
		if ((isElementOrComplexType(tree) || isSimpleType(tree.getLocalName())) && getNameAttribute(tree).equals(localTypeName)) {
			return tree;
			// 2. Case: we have a one-node-tree
		} else if (!tree.hasChildNodes()) {
			return null;
			// 3. Case: the current node is not the right one, but maybe one of
			// its
			// children.
		} else {
			NodeList children = tree.getChildNodes();

			Node typeNode = null;
			int i = 0;

			while ((i < children.getLength()) && (typeNode == null)) {
				typeNode = findTypeDeclarationNode(children.item(i), localTypeName);
				i++;
			}

			return typeNode;
		}
	}

	private static String getNameAttribute(Node tree) {
		if (tree.getAttributes() != null) {
			// String namespace = (tree.getNamespaceURI() != null) ?
			// tree.getNamespaceURI() + ":": "";
			Node name = tree.getAttributes().getNamedItem("name");

			return (name != null) ? name.getNodeValue() : ANONYMOUS_TYPE_NAME;
		} else {
			return ANONYMOUS_TYPE_NAME;
		}
	}

	private static boolean isElementOrComplexType(Node tree) {
		return "element".equals(String.valueOf(tree.getLocalName()).toLowerCase()) || isNamedComplexType(tree);
	}

	private static boolean isNamedComplexType(Node tree) {
		return "complextype".equals(String.valueOf(tree.getLocalName()).toLowerCase()) && (tree.getAttributes() != null)
				&& (getNameAttribute(tree) != null);
	}

	/**
	 * Returns <code>true</code> if <code>typeName</code> is a simple type
	 * according to the XML-Schema specification:
	 * http://www.w3.org/TR/xmlschema-0/
	 * 
	 * @param typeName
	 *            The type-name to check
	 * 
	 * @return <code>true</code> if <code>typeName</code> is a simple type, else
	 *         <code>false</code>
	 */
	private static boolean isSimpleType(String typeName) {
		String lcTypeName = String.valueOf(typeName).toLowerCase();

		if ("simpletype".equals(lcTypeName)) {
			return true;
		} else {
			for (String simpleType : SIMPLE_XML_TYPES) {
				if (simpleType.toLowerCase().equals(lcTypeName)) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean isSimpleType(Node node) {
		return (node != null) && isSimpleType(node.getLocalName());
	}

	/**
	 * Extracts the flat-message structure as specified in the documentation of
	 * {@link this#extractRoles(Message, Types)}.
	 * 
	 * @param node
	 *            The tree to look in
	 * 
	 * @return The flat-message structure
	 */
	private static List<String> extractFlatMessageStructure(Node node, List<Node> schemata, Set<String> visitedTypes) {
		List<String> result = new ArrayList<String>();
		// At first derive the type name ...
		String typeName = deriveTypeName(node);
		boolean isVisitedType = visitedTypes.contains(typeName);

		visitedTypes.add(typeName);

		// ... and check if we have a simple type. (1st case)
		if (isSimpleType(typeName)) {
			// Ok, we have to add this element to our result.
			String elementName = getNameAttribute(node);
			String namespace = (node.getNamespaceURI() != null) ? node.getNamespaceURI() + ":" : "";

			result.add(namespace + ":" + elementName + "(" + typeName + ")");
			// 2nd case: do we have a recursive type definition?
		} else if (isVisitedType && !ANONYMOUS_TYPE_NAME.equals(typeName)) {
			// Ok, we have to add this element to our result and stop recursion.
			String elementName = getNameAttribute(node);
			String namespace = (node.getNamespaceURI() != null) ? node.getNamespaceURI() + ":" : "";

			result.add(namespace + ":" + elementName + '(' + typeName + "[recursion])");
		} else {
			// No? Well, then we have to analyse all child-nodes.
			List<String> allChildPaths = new ArrayList<String>();
			Node typeDeclarationNode = null;

			if (ANONYMOUS_TYPE_NAME.equals(typeName)) {
				NodeList childNodes = node.getChildNodes();

				// Get the paths of the child-nodes ...
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node childNode = childNodes.item(i);

					allChildPaths.addAll(extractFlatMessageStructure(childNode, schemata, visitedTypes));
				}
			} else {
				int i = 0;

				while ((i < schemata.size()) && (typeDeclarationNode == null)) {
					typeDeclarationNode = findTypeDeclarationNode(schemata.get(i), typeName);
					i++;
				}

				if (typeDeclarationNode != null) {
					allChildPaths.addAll(extractFlatMessageStructure(typeDeclarationNode, schemata, visitedTypes));
				}
			}

			// This node has to be added to the result only if it is an element.
			if (isElementOrComplexType(node)) {
				// Parse only valid elements with a name.
				String elementName = getNameAttribute(node);
				String namespace = (node.getNamespaceURI() != null) ? node.getNamespaceURI() + ":" : "";

				// ... and concat them with the current element- and
				// typename.
				if (!allChildPaths.isEmpty()) {
					for (String childPath : allChildPaths) {
						if (isNamedComplexType(node)) {
							result.add(childPath);
						} else {
							result.add(namespace + ":" + elementName + "(" + typeName + ")" + "." + childPath);
						}
					}
				} else if (isSimpleType(typeDeclarationNode)) {
					result.add(namespace + ":" + elementName + "(" + getNameAttribute(typeDeclarationNode) + ")");
				} else {
					result.add(namespace + ":" + elementName + "(no_definition)");
				}
			} else {
				return allChildPaths;
			}
		}

		return result;
	}

	/**
	 * Returns the value of attribute "type" of the given {@link Node}
	 * <code>node</code>. If this attribute is not declared, the type will be
	 * "anonymous". The result will always be a local name without namespace.
	 * 
	 * @param node
	 *            The {@link Node} to get the type of
	 * 
	 * @return The type of the given node.
	 */
	private static String deriveTypeName(Node node) {
		String typeName = null;
		String nodeName = String.valueOf(node.getLocalName()).toLowerCase();

		if ((node.getAttributes() == null) || (node.getAttributes().getNamedItem("type") == null)) {
			typeName = ANONYMOUS_TYPE_NAME;
		} else if ("simpletype".equals(nodeName)) {
			return nodeName;
		} else {
			typeName = node.getAttributes().getNamedItem("type").getNodeValue();
		}

		if (typeName.indexOf(':') > -1) {
			return typeName.split(":")[1];
		} else {
			return typeName;
		}
	}

	private static Map<String, List<Operation>> extractPortTypes(Definition def) {
		Map<String, List<Operation>> portTypes = new HashMap<String, List<Operation>>();

		if (def.getPortTypes() != null) {
			for (Object portTypeObj : def.getPortTypes().values()) {
				PortType portType = (PortType) portTypeObj;
				List<Operation> operationIdentifiers = new ArrayList<Operation>();

				for (Object operationObj : portType.getOperations()) {
					operationIdentifiers.add((Operation) operationObj);
				}

				portTypes.put(portType.getQName().getLocalPart(), operationIdentifiers);
			}
		}

		return portTypes;
	}

	private static List<WsdlMetadata> convertToMetadata(Map<String, List<Operation>> portTypes, int startId, String wsdlFilename, Types types) {
		List<WsdlMetadata> result = new ArrayList<WsdlMetadata>();
		int id = startId;

		for (Entry<String, List<Operation>> entry : portTypes.entrySet()) {
			for (Operation operation : entry.getValue()) {
				WsdlMetadata metadata = new WsdlMetadata();

				metadata.setId(id);
				metadata.setOperationIdentifier(operation.getName());
				metadata.setPortTypeName(entry.getKey());
				metadata.setWsdlFilename(wsdlFilename);

				if (operation.getInput() != null) {
					metadata.setInputMessagePaths(extractRoles(operation.getInput().getMessage(), types));
				}

				if (operation.getOutput() != null) {
					metadata.setOutputMessagePaths(extractRoles(operation.getOutput().getMessage(), types));
				}
				id++;
				result.add(metadata);
			}
		}

		return result;
	}

	private static WsdlContent createPortType(List<WsdlMetadata> metadata) {
		WsdlContent portType = new WsdlContent();

		for (WsdlMetadata wsdlMetadata : metadata) {
			portType.setPortTypeName(wsdlMetadata.getPortTypeName());
			portType.addOperation(wsdlMetadata.getOperationIdentifier());
			portType.addMessagePaths(wsdlMetadata.getInputMessagePaths());
			portType.addMessagePaths(wsdlMetadata.getOutputMessagePaths());
		}

		return portType;
	}

	/**
	 * Returns the {@link WsdlParsingResult} for the given array of
	 * <code>wsdlFiles</code> .
	 * 
	 * @param wsdlFiles
	 *            The WSDL-files to extract the {@link WsdlMetadata} from
	 * 
	 * @return The {@link WsdlParsingResult}
	 * 
	 * @throws WSDLException
	 * @see In case of an {@link WSDLException} see
	 *      {@link WSDLFactory#newInstance() #newWSDLReader()}
	 */
	public static WsdlParsingResult extractMetadata(File[] wsdlFiles) throws WSDLException {
		WsdlParsingResult result = new WsdlParsingResult();
		List<WsdlMetadata> metadata = new ArrayList<WsdlMetadata>();
		List<String> doublePortTypes = new ArrayList<String>();
		List<String> unparseableWsdlFiles = new ArrayList<String>();
		int id = 0;
		Set<WsdlContent> parsedPortTypes = new HashSet<WsdlContent>();
		int fileNo = 0;
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();

		// For each WSDL-file ...
		for (File wsdlFile : wsdlFiles) {
			try {
				// ... parse it and ...
				System.out.println("Parsing file no. " + fileNo + " of " + wsdlFiles.length + " files.");
				Definition def = reader.readWSDL(wsdlFile.getAbsolutePath());
				Map<String, List<Operation>> portTypes = extractPortTypes(def);
				List<WsdlMetadata> extractedMetadata = convertToMetadata(portTypes, id, wsdlFile.getName(), def.getTypes());
				WsdlContent portType = createPortType(extractedMetadata);

				// Each interface has to be analysed only one time!
				if (!parsedPortTypes.contains(portType)) {
					// ... extract its Porttypes.
					metadata.addAll(extractedMetadata);
					id += extractedMetadata.size();

					parsedPortTypes.add(portType);
				} else {
					doublePortTypes.add(wsdlFile.getAbsolutePath());
				}
			} catch (WSDLException wsdlEx) {
				unparseableWsdlFiles.add(wsdlFile.getAbsolutePath());
				// We catch the NPE, because it could occur in the WSDLReader.
			} catch (NullPointerException npe) {
				unparseableWsdlFiles.add(wsdlFile.getAbsolutePath());
			}

			fileNo++;
		}

		result.setUnparseableWsdlFiles(unparseableWsdlFiles);
		result.setWsdlMetadata(metadata);
		result.setDoublePortTypes(doublePortTypes);
		result.setNumberOfWsdlFiles(wsdlFiles.length);

		return result;
	}

	public static List<String> getOperationLabels(List<File> validWsdls) throws ParserConfigurationException, SAXException, IOException {
		List<String> operationNames = new ArrayList<String>();
		int currentWsdlNumber = 1;
		int numberOfWsdls = validWsdls.size();

		logger.info("Starting extraction of operation labels ...");
		logger.info("Processing " + numberOfWsdls + " files");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		for (File wsdlFile : validWsdls) {
			if (wsdlFile.isFile()) {
				logger.info("Analyzing " + wsdlFile.getAbsolutePath() + "(" + currentWsdlNumber + " of " + numberOfWsdls + ")");
				List<String> operationLabels = getOperationLabelsFromWsdlFile(saxParser, wsdlFile);

				if (operationLabels.size() > 0) {
					logger.info(operationLabels.size() + " operation labels found");
					operationNames.addAll(operationLabels);
				} else {
					logger.info("No operation labels found");
				}

				currentWsdlNumber++;
			}
		}

		logger.info(operationNames.size() + " operation labels have been extracted.");
		logger.info("Finished extraction of operation labels ...");

		return operationNames;
	}

	public static List<String> getPortTypeLabels(List<File> validWsdls) throws ParserConfigurationException, SAXException, IOException {
		List<String> portTypeNames = new ArrayList<String>();
		int currentWsdlNumber = 1;
		int numberOfWsdls = validWsdls.size();

		logger.info("Starting extraction of PortType labels ...");
		logger.info("Processing " + numberOfWsdls + " files");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		for (File wsdlFile : validWsdls) {
			logger.info("Analyzing " + wsdlFile.getAbsolutePath() + "(" + currentWsdlNumber + " of " + numberOfWsdls + ")");
			WsdlDocumentHandler documentHandler = new WsdlDocumentHandler();
			saxParser.parse(wsdlFile, documentHandler);
			List<String> portTypeLabels = documentHandler.getPortTypeLabels();

			if (portTypeLabels.size() > 0) {
				logger.info(portTypeLabels.size() + " PortType labels found");
				portTypeNames.addAll(portTypeLabels);
			} else {
				logger.info("No PortType labels found");
			}

			currentWsdlNumber++;
		}

		logger.info(portTypeNames.size() + " operation labels have been extracted.");
		logger.info("Finished extraction of operation labels ...");

		return portTypeNames;
	}

	public static List<String> getOperationLabelsFromWsdlFile(SAXParser saxParser, File wsdlFile) throws SAXException, IOException {
		WsdlDocumentHandler documentHandler = new WsdlDocumentHandler();
		saxParser.parse(wsdlFile, documentHandler);

		List<String> operationLabels = documentHandler.getOperationLabels();
		return operationLabels;
	}

}
