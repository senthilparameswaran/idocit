package de.jankrause.diss.wsdl.common.structure.wsdl;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of the WSDL parsing process. It contains the prepared
 * {@link WsdlMetadata}s and the {@link List} of filenames of those WSDL-files
 * which could not be parsed.
 * 
 * @author Jan Christian Krause
 * 
 */
public class WsdlParsingResult {

	private List<WsdlMetadata> wsdlMetadata = null;

	private List<String> unparseableWsdlFiles = null;

	private List<String> doublePortTypes = null;
	
	private int numberOfWsdlFiles = 0;

	public int getNumberOfWsdlFiles()
	{
		return numberOfWsdlFiles;
	}

	public void setNumberOfWsdlFiles(int numberOfWsdlFiles)
	{
		this.numberOfWsdlFiles = numberOfWsdlFiles;
	}

	public List<String> getDoublePortTypes() {
		return doublePortTypes;
	}

	public void setDoublePortTypes(List<String> doublePortTypes) {
		this.doublePortTypes = doublePortTypes;
	}

	public List<WsdlMetadata> getWsdlMetadata() {
		return wsdlMetadata;
	}

	public void setWsdlMetadata(List<WsdlMetadata> wsdlMetadata) {
		this.wsdlMetadata = wsdlMetadata;
	}

	public List<String> getUnparseableWsdlFiles() {
		return unparseableWsdlFiles;
	}

	public void setUnparseableWsdlFiles(List<String> unparseableWsdlFiles) {
		this.unparseableWsdlFiles = unparseableWsdlFiles;
	}

	public List<String> getOperationIdentifiers() {
		List<String> operationIdentifiers = new ArrayList<String>();

		if (wsdlMetadata != null) {
			for (WsdlMetadata metadata : wsdlMetadata) {
				operationIdentifiers.add(metadata.getOperationIdentifier());
			}
		}

		return operationIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unparseableWsdlFiles == null) ? 0 : unparseableWsdlFiles.hashCode());
		result = prime * result + ((wsdlMetadata == null) ? 0 : wsdlMetadata.hashCode());
		result = prime * result + ((doublePortTypes == null) ? 0 : doublePortTypes.hashCode());
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
		WsdlParsingResult other = (WsdlParsingResult) obj;
		if (unparseableWsdlFiles == null) {
			if (other.unparseableWsdlFiles != null)
				return false;
		} else if (!unparseableWsdlFiles.equals(other.unparseableWsdlFiles))
			return false;
		if (wsdlMetadata == null) {
			if (other.wsdlMetadata != null)
				return false;
		} else if (!wsdlMetadata.equals(other.wsdlMetadata))
			return false;
		if (doublePortTypes == null) {
			if (other.doublePortTypes != null)
				return false;
		} else if (!doublePortTypes.equals(other.doublePortTypes))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WsdlParsingResult [");
		if (unparseableWsdlFiles != null) {
			builder.append("unparseableWsdlFiles=");
			builder.append(unparseableWsdlFiles);
			builder.append(", ");
		}
		if (wsdlMetadata != null) {
			builder.append("wsdlMetadata=");
			builder.append(wsdlMetadata);
		}
		if (doublePortTypes != null) {
			builder.append("doublePortTypes=");
			builder.append(doublePortTypes);
		}
		builder.append("]");
		return builder.toString();
	}
}
