package de.akra.idocit.wsdl.structure;

import java.util.List;

/**
 * Represents an attribute-quartuple of a WSDL-interface. It contains the
 * 
 * <ul>
 * <li>Identifier of the operation</li>
 * <li>Name of the surrounding Porttype</li>
 * <li>Name of the file containing the Porttype</li>
 * <li>Generated technical id</li>
 * </ul>
 * 
 * @author Jan Christian Krause
 * 
 */
public class WsdlMetadata {

	private String operationIdentifier = null;

	private String portTypeName = null;

	private String wsdlFilename = null;

	private int id = 0;
	
	private List<String> inputMessagePaths = null;
	
	private List<String> outputMessagePaths = null;

	public String getOperationIdentifier() {
		return operationIdentifier;
	}

	public void setOperationIdentifier(String operationIdentifier) {
		this.operationIdentifier = operationIdentifier;
	}

	public String getPortTypeName() {
		return portTypeName;
	}

	public void setPortTypeName(String portTypeName) {
		this.portTypeName = portTypeName;
	}

	public String getWsdlFilename() {
		return wsdlFilename;
	}

	public void setWsdlFilename(String wsdlFilename) {
		this.wsdlFilename = wsdlFilename;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getInputMessagePaths() {
		return inputMessagePaths;
	}

	public void setInputMessagePaths(List<String> inputMessagePaths) {
		this.inputMessagePaths = inputMessagePaths;
	}

	public List<String> getOutputMessagePaths() {
		return outputMessagePaths;
	}

	public void setOutputMessagePaths(List<String> outputMessagePaths) {
		this.outputMessagePaths = outputMessagePaths;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime
				* result
				+ ((inputMessagePaths == null) ? 0 : inputMessagePaths
						.hashCode());
		result = prime
				* result
				+ ((operationIdentifier == null) ? 0 : operationIdentifier
						.hashCode());
		result = prime
				* result
				+ ((outputMessagePaths == null) ? 0 : outputMessagePaths
						.hashCode());
		result = prime * result
				+ ((portTypeName == null) ? 0 : portTypeName.hashCode());
		result = prime * result
				+ ((wsdlFilename == null) ? 0 : wsdlFilename.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WsdlMetadata other = (WsdlMetadata) obj;
		if (id != other.id)
			return false;
		if (inputMessagePaths == null) {
			if (other.inputMessagePaths != null)
				return false;
		} else if (!inputMessagePaths.equals(other.inputMessagePaths))
			return false;
		if (operationIdentifier == null) {
			if (other.operationIdentifier != null)
				return false;
		} else if (!operationIdentifier.equals(other.operationIdentifier))
			return false;
		if (outputMessagePaths == null) {
			if (other.outputMessagePaths != null)
				return false;
		} else if (!outputMessagePaths.equals(other.outputMessagePaths))
			return false;
		if (portTypeName == null) {
			if (other.portTypeName != null)
				return false;
		} else if (!portTypeName.equals(other.portTypeName))
			return false;
		if (wsdlFilename == null) {
			if (other.wsdlFilename != null)
				return false;
		} else if (!wsdlFilename.equals(other.wsdlFilename))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WsdlMetadata [id=");
		builder.append(id);
		builder.append(", ");
		if (inputMessagePaths != null) {
			builder.append("inputMessagePaths=");
			builder.append(inputMessagePaths);
			builder.append(", ");
		}
		if (operationIdentifier != null) {
			builder.append("operationIdentifier=");
			builder.append(operationIdentifier);
			builder.append(", ");
		}
		if (outputMessagePaths != null) {
			builder.append("outputMessagePaths=");
			builder.append(outputMessagePaths);
			builder.append(", ");
		}
		if (portTypeName != null) {
			builder.append("portTypeName=");
			builder.append(portTypeName);
			builder.append(", ");
		}
		if (wsdlFilename != null) {
			builder.append("wsdlFilename=");
			builder.append(wsdlFilename);
		}
		builder.append("]");
		return builder.toString();
	}
}
