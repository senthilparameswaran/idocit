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
package de.akra.idocit.wsdl.services;

import static de.akra.idocit.wsdl.services.DocumentationGenerator.generateDocumentationElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.wsdl.WSDLElement;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.util.xml.DOMUtils;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.wsdl.structure.WSDLInterface;
import de.akra.idocit.wsdl.structure.WSDLInterfaceArtifact;
import de.akra.idocit.wsdl.structure.WSDLMessage;
import de.akra.idocit.wsdl.structure.WSDLOperation;

/**
 * Generator that updates the {@link Definition} in the
 * {@link WSDLInterfaceArtifact} <code>wsdlIStructure</code> with the new
 * {@link Documentation}s out of the {@link WSDLInterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLInterfaceGenerator {

	/**
	 * The representation of the WSDL artifact.
	 */
	private final WSDLInterfaceArtifact wsdlIStructure;

	/**
	 * The qualified tag name for a documentation element.
	 */
	private String documentationTagName;

	/**
	 * 
	 * @param wsdlInterfaceStructure
	 *            The {@link WSDLInterfaceArtifact} from which a updated
	 *            {@link Definition} should be generated.
	 */
	public WSDLInterfaceGenerator(WSDLInterfaceArtifact wsdlInterfaceStructure) {
		this.wsdlIStructure = wsdlInterfaceStructure;
	}

	/**
	 * Updates the documentation {@link Element}s in the {@link Definition}
	 * <code>wsdlDefinition</code>. Old documentations are removed and all
	 * documentations in the {@link WSDLInterfaceArtifact} structure are added
	 * to the corresponding {@link WSDLElement} in the
	 * <code>wsdlDefinition</code>.
	 * 
	 * @return The updated {@link Definition} that could be written to a file.
	 * @throws WSDLException
	 */
	@SuppressWarnings("unchecked")
	public Definition updateDocumentationInDefinition() throws WSDLException {
		// get tag name for documentation element
		this.documentationTagName = DOMUtils.getQualifiedValue(
				Constants.NS_URI_WSDL, Constants.ELEM_DOCUMENTATION,
				wsdlIStructure.getWsdlDefinition());

		// clean up WSDL definition
		removeAllDocumentationElementsWithDocparts(wsdlIStructure
				.getWsdlDefinition());

		// set for all Interfaces (PortTypes) the documentation into the
		// wsdlDefinition
		List<WSDLInterface> interfaces = (List<WSDLInterface>) wsdlIStructure
				.getInterfaces();
		Iterator<WSDLInterface> it = interfaces.iterator();
		while (it.hasNext()) {
			// get corresponding PortType in wsdlDefinition
			WSDLInterface wsdlInterface = it.next();
			PortType portType = wsdlInterface.getPortType();

			Element newDocElem = generateDocumentationElement(
					documentationTagName, wsdlInterface.getDocumentations(),
					DocumentationParser.THEMATIC_GRID_ATTRIBUTE_NAME_GLOBAL);

			// ... add the new documentation element.
			portType.addDocumentationElement(newDocElem);

			updateOperationsDocumentations((List<WSDLOperation>) wsdlInterface
					.getOperations());
		}
		return wsdlIStructure.getWsdlDefinition();
	}

	/**
	 * Update the documentations in the {@link javax.wsdl.Operation}s hold in
	 * the {@link WSDLOperation}s <code>operations</code> with the current
	 * {@link Documentation}s.
	 * 
	 * @param wsdlOperations
	 *            The {@link List} of {@link WSDLOperation}s with the new
	 *            documentations and containing reference to the corresponding
	 *            {@link javax.wsdl.Operation}.
	 */
	@SuppressWarnings("unchecked")
	private void updateOperationsDocumentations(
			List<WSDLOperation> wsdlOperations) {
		if (wsdlOperations == null)
			return;

		for (WSDLOperation wsdlOperation : wsdlOperations) {
			// get the corresponding operation in the WSDL definition
			javax.wsdl.Operation operation = wsdlOperation.getOperation();
			Element newDocElem = generateDocumentationElement(
					documentationTagName, wsdlOperation.getDocumentations(),
					wsdlOperation.getThematicGridName());
			operation.addDocumentationElement(newDocElem);

			// updateMessagesDocumentations(wsdlOperation);

			// Update input message
			updateMessageDocumentation((WSDLMessage) wsdlOperation
					.getInputParameters());

			// Update output message
			updateMessageDocumentation((WSDLMessage) wsdlOperation
					.getOutputParameters());

			// Update fault messages
			for (WSDLMessage faultMsg : (List<WSDLMessage>) wsdlOperation
					.getExceptions()) {
				updateMessageDocumentation(faultMsg);
			}
		}
	}

	/**
	 * Update the documentations in the {@link Input}, {@link Output} or
	 * {@link Fault} depending on the held reference in the {@link WSDLMessage}
	 * <code>wsdlMessage</code> with the current {@link Documentation}s.
	 * 
	 * @param wsdlMessage
	 *            The {@link WSDLMessage} with the new documentations and
	 *            containing reference to the corresponding {@link Input},
	 *            {@link Output} or {@link Fault}.
	 */
	private void updateMessageDocumentation(WSDLMessage wsdlMessage) {
		List<Documentation> docparts = new ArrayList<Documentation>(
				SignatureElement.DEFAULT_ARRAY_SIZE);

		collectParametersDocparts(docparts, wsdlMessage);
		WSDLElement element = wsdlMessage.getMessageRef();

		Element newDocElem = generateDocumentationElement(documentationTagName,
				docparts, null);
		element.addDocumentationElement(newDocElem);
	}

	/**
	 * Adds all {@link Documentation}s of the {@link Parameters} structure into
	 * the {@link List} <code>docparts</code>.
	 * 
	 * @param docparts
	 *            The {@link List} into which all {@link Documentation}s are
	 *            added.
	 * @param parameters
	 *            The {@link Parameters} whose and whose children's
	 *            {@link Documentation}s are added to <code>docparts</code>.
	 */
	private void collectParametersDocparts(List<Documentation> docparts,
			Parameters parameters) {
		List<Documentation> docList = parameters.getDocumentations();
		docparts.addAll(docList);

		for (Parameter param : parameters.getParameters()) {
			collectParameterDocparts(docparts, param);
		}
	}

	/**
	 * Adds all {@link Documentation}s of the {@link Parameter} structure into
	 * the {@link List} <code>docparts</code>.
	 * 
	 * @param docparts
	 *            The {@link List} into which all {@link Documentation}s are
	 *            added.
	 * @param parameter
	 *            The {@link Parameter} whose and whose children's
	 *            {@link Documentation}s are added to <code>docparts</code>.
	 */
	private void collectParameterDocparts(List<Documentation> docparts,
			Parameter parameter) {
		List<Documentation> docList = parameter.getDocumentations();
		docparts.addAll(docList);

		for (Parameter param : parameter.getComplexType()) {
			collectParameterDocparts(docparts, param);
		}
	}

	/**
	 * Removes all documentation {@link Element}s with docparts from the
	 * <b>whole</b> WSDL {@link Definition} <code>wsdlDefinition</code>.<br>
	 * Before adding new documentation {@link Element}s we should clean up the
	 * {@link Definition} so there are left no old documentations.
	 * 
	 * @param definition
	 *            The {@link Definition} to clean up.
	 */
	private void removeAllDocumentationElementsWithDocparts(
			Definition definition) {
		@SuppressWarnings("unchecked")
		Map<QName, PortType> portTypes = (Map<QName, PortType>) definition
				.getPortTypes();

		if (portTypes != null) {
			for (PortType portType : portTypes.values()) {
				removeAllDocElemsWithDocparts(portType);
			}
		}
	}

	/**
	 * Removes all documentation {@link Element}s with docparts from the
	 * {@link PortType} <code>portType</code> and its subelements.
	 * 
	 * @param portType
	 *            The {@link PortType} to clean up.
	 */
	private void removeAllDocElemsWithDocparts(PortType portType) {
		removeAllDocumentationsWithDocpartsFromElement(portType);

		@SuppressWarnings("unchecked")
		List<javax.wsdl.Operation> operations = (List<javax.wsdl.Operation>) portType
				.getOperations();

		if (operations != null) {
			for (javax.wsdl.Operation op : operations) {
				removeAllDocElemsWithDocparts(op);
			}
		}
	}

	/**
	 * Removes all documentation {@link Element}s with docparts from the
	 * {@link javax.wsdl.Operation} <code>operation</code> and its {@link Input}
	 * , {@link Output} and {@link Fault} messages.
	 * 
	 * @param operation
	 *            The {@link javax.wsdl.Operation} to clean up.
	 */
	private void removeAllDocElemsWithDocparts(javax.wsdl.Operation operation) {
		removeAllDocumentationsWithDocpartsFromElement(operation);

		Input input = operation.getInput();
		if (input != null) {
			removeAllDocumentationsWithDocpartsFromElement(input);
		}

		Output output = operation.getOutput();
		if (output != null) {
			removeAllDocumentationsWithDocpartsFromElement(output);
		}

		@SuppressWarnings("unchecked")
		Map<String, Fault> faults = operation.getFaults();
		for (Fault fault : faults.values()) {
			removeAllDocumentationsWithDocpartsFromElement(fault);
		}
	}

	/**
	 * Removes all documentation {@link Element}s with docparts from this
	 * {@link WSDLElement} <code>wsdlElement</code>.
	 * 
	 * @param wsdlElement
	 *            The {@link WSDLElement} to clean up.
	 */
	private void removeAllDocumentationsWithDocpartsFromElement(
			WSDLElement wsdlElement) {
		Element docElem = null;

		// find all documentation elements with docparts and remove them
		while ((docElem = DocumentationParser
				.findDocElemWithDocpart(wsdlElement.getDocumentationElements())) != null) {
			wsdlElement.removeDocumentationElement(docElem);
		}
	}
}
