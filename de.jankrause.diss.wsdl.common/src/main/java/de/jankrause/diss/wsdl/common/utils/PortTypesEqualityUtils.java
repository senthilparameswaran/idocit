package de.jankrause.diss.wsdl.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

/**
 * Contains an equals-test for two {@link PortType}s.
 * 
 * @author Jan Christian Krause
 */
public class PortTypesEqualityUtils {

	private static Logger logger = Logger
			.getLogger(PortTypesEqualityUtils.class.getName());

	public static boolean areWSDLsEqual(Definition wsdl1, Definition wsdl2)
			throws WSDLException {
		Map<QName, PortType> portTypes1 = mergePortTypes(wsdl1);
		Map<QName, PortType> portTypes2 = mergePortTypes(wsdl2);

		return arePortTypeMapsCompatible(portTypes1, portTypes2);
	}

	private static Map<QName, PortType> mergePortTypesOfImports(
			Map<QName, Vector<Import>> imports) {
		Map<QName, PortType> mergedPortTypes = new HashMap<QName, PortType>();

		if (imports != null) {
			for (Vector<Import> curImportVec : imports.values()) {

				for (Import curImport : curImportVec) {
					Definition definition = curImport.getDefinition();

					if (definition != null) {
						Map<QName, PortType> portTypesOfImport = mergePortTypesOfImports(definition
								.getImports());

						mergedPortTypes.putAll(definition.getPortTypes());
						mergedPortTypes.putAll(portTypesOfImport);
					}
				}
			}
		}

		return mergedPortTypes;
	}

	private static Map<QName, PortType> mergePortTypes(Definition definition) {
		Map<QName, PortType> allPortTypes = new HashMap<QName, PortType>();

		try {
			allPortTypes.putAll(definition.getPortTypes());
			allPortTypes
					.putAll(mergePortTypesOfImports(definition.getImports()));

		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return allPortTypes;
	}

	private static boolean containsOperation(Operation operation,
			Map<QName, PortType> portTypes) {
		for (PortType portType : portTypes.values()) {
			List<Operation> ptOperations = portType.getOperations();

			for (Operation ptOperation : ptOperations) {
				if (areOperationsCompatible(operation, ptOperation)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean arePortTypeMapsCompatible(
			Map<QName, PortType> referencePortTypes,
			Map<QName, PortType> otherPortTypes) {
		for (Entry<QName, PortType> entryPortType1 : referencePortTypes
				.entrySet()) {
			PortType referencePortType = entryPortType1.getValue();
			List<Operation> referenceOperations = referencePortType
					.getOperations();

			for (Operation referenceOperation : referenceOperations) {
				if (!containsOperation(referenceOperation, otherPortTypes)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean arePortTypesCompatible(PortType pt1, PortType pt2) {
		if ((pt1 == null) && (pt2 != null)) {
			return false;
		} else if ((pt1 != null) && (pt2 == null)) {
			return false;
		} else if ((pt1 == null) && (pt2 == null)) {
			return true;
		} else {
			List<Operation> operations1 = pt1.getOperations();
			List<Operation> operations2 = pt2.getOperations();

			return areQNamesCompatible(pt1.getQName(), pt2.getQName())
					&& areOperationListsCompatible(operations1, operations2);
		}
	}

	private static boolean areOperationListsCompatible(
			List<Operation> operations1, List<Operation> operations2) {
		if ((operations1 == null) && (operations2 != null)) {
			return false;
		} else if ((operations1 != null) && (operations2 == null)) {
			return false;
		} else if ((operations1 == null) && (operations2 == null)) {
			return true;
		} else {
			for (Operation operation : operations1) {
				if (containsOperation(operation, operations1)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean containsOperation(Operation operation,
			List<Operation> operations) {
		for (Operation operation2 : operations) {
			if (areOperationsCompatible(operation, operation2)) {
				return true;
			}
		}

		return false;
	}

	private static boolean areOperationsCompatible(Operation operation1,
			Operation operation2) {
		return areFaultCompatible(operation1.getFaults(),
				operation2.getFaults())
				&& areOutputsCompatible(operation1.getOutput(),
						operation2.getOutput())
				&& areInputsCompatible(operation1.getInput(),
						operation2.getInput())
				&& areStyleCompatible(operation1.getStyle(),
						operation2.getStyle())
				&& areNamesCompatible(operation1.getName(),
						operation2.getName());
	}

	private static boolean areFaultCompatible(Map<QName, Fault> faults1,
			Map<QName, Fault> faults2) {
		if ((faults1 == null) && (faults2 != null)) {
			return false;
		} else if ((faults1 != null) && (faults2 == null)) {
			return false;
		} else if ((faults1 == null) && (faults2 == null)) {
			return true;
		} else {
			for (Entry<QName, Fault> entryFault1 : faults1.entrySet()) {
				Fault fault1 = entryFault1.getValue();
				Fault fault2 = faults2.get(entryFault1.getKey());

				if (!areFaultsCompatible(fault1, fault2)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean areFaultsCompatible(Fault fault1, Fault fault2) {
		return (fault1 == fault2)
				|| ((fault1 != null)
						&& (fault2 != null)
						&& areNamesCompatible(fault1.getName(),
								fault2.getName()) && areMessagesCompatible(
						fault1.getMessage(), fault2.getMessage()));
	}

	private static boolean areMessagesCompatible(Message message1,
			Message message2) {
		if ((message1 == null) && (message2 != null)) {
			return false;
		} else if ((message1 != null) && (message2 == null)) {
			return false;
		} else if ((message1 == null) && (message2 == null)) {
			return true;
		} else {
			return areQNamesCompatible(message1.getQName(), message2.getQName())
					&& arePartMapsCompatible(message1.getParts(),
							message2.getParts());
		}
	}

	private static boolean arePartMapsCompatible(Map<QName, Part> parts1,
			Map<QName, Part> parts2) {
		if ((parts1 == null) && (parts2 != null)) {
			return false;
		} else if ((parts1 != null) && (parts2 == null)) {
			return false;
		} else if ((parts1 == null) && (parts2 == null)) {
			return true;
		} else {
			for (Entry<QName, Part> entryFault1 : parts1.entrySet()) {
				Part fault1 = entryFault1.getValue();
				Part fault2 = parts2.get(entryFault1.getKey());

				if (!arePartCompatible(fault1, fault2)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean arePartCompatible(Part part1, Part part2) {
		if ((part1 == null) && (part2 != null)) {
			return false;
		} else if ((part1 != null) && (part2 == null)) {
			return false;
		} else if ((part1 == null) && (part2 == null)) {
			return true;
		} else {
			return areQNamesCompatible(part1.getElementName(),
					part2.getElementName())
					&& areQNamesCompatible(part1.getTypeName(),
							part2.getTypeName())
					&& areNamesCompatible(part1.getName(), part2.getName());
		}
	}

	private static boolean areOutputsCompatible(Output output1, Output output2) {
		if ((output1 == null) && (output2 != null)) {
			return false;
		} else if ((output1 != null) && (output2 == null)) {
			return false;
		} else if ((output1 == null) && (output2 == null)) {
			return true;
		} else {
			return areMessagesCompatible(output1.getMessage(),
					output2.getMessage());
		}
	}

	private static boolean areInputsCompatible(Input input1, Input input2) {
		if ((input1 == null) && (input2 != null)) {
			return false;
		} else if ((input1 != null) && (input2 == null)) {
			return false;
		} else if ((input1 == null) && (input2 == null)) {
			return true;
		} else {
			return areMessagesCompatible(input1.getMessage(),
					input2.getMessage());
		}
	}

	private static boolean areStyleCompatible(OperationType style1,
			OperationType style2) {
		if ((style1 == null) && (style2 != null)) {
			return false;
		} else if ((style1 != null) && (style2 == null)) {
			return false;
		} else if ((style1 == null) && (style2 == null)) {
			return true;
		} else {
			return style1.equals(style2);
		}
	}

	private static boolean areQNamesCompatible(QName name1, QName name2) {
		if ((name1 == null) && (name2 != null)) {
			return false;
		} else if ((name1 != null) && (name2 == null)) {
			return false;
		} else if ((name1 == null) && (name2 == null)) {
			return true;
		} else {
			return name1.getLocalPart().equals(name2.getLocalPart())
					&& name1.getPrefix().equals(name2.getPrefix());
		}
	}

	private static boolean areNamesCompatible(String name1, String name2) {
		if ((name1 == null) && (name2 != null)) {
			return false;
		} else if ((name1 != null) && (name2 == null)) {
			return false;
		} else if ((name1 == null) && (name2 == null)) {
			return true;
		} else {
			return name1.equals(name2);
		}
	}
}
