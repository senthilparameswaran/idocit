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
package de.akra.idocit.wsdl.structure;

import java.util.ArrayList;
import java.util.List;

public final class WsdlContent {

	private String portTypeName = null;

	private List<String> operations = new ArrayList<String>();

	private List<String> messagePaths = new ArrayList<String>();

	public String getPortTypeName() {
		return portTypeName;
	}

	public void setPortTypeName(String portTypeName) {
		this.portTypeName = portTypeName;
	}

	public void addMessagePaths(List<String> messagePaths) {
		this.messagePaths.addAll(messagePaths);
	}

	public void addOperation(String operation) {
		operations.add(operation);
	}

	public List<String> getOperations() {
		return operations;
	}

	public List<String> getMessagePaths() {
		return messagePaths;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messagePaths == null) ? 0 : messagePaths.hashCode());
		result = prime * result
				+ ((operations == null) ? 0 : operations.hashCode());
		result = prime * result
				+ ((portTypeName == null) ? 0 : portTypeName.hashCode());
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
		WsdlContent other = (WsdlContent) obj;
		if (messagePaths == null) {
			if (other.messagePaths != null)
				return false;
		} else if (!messagePaths.equals(other.messagePaths))
			return false;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		if (portTypeName == null) {
			if (other.portTypeName != null)
				return false;
		} else if (!portTypeName.equals(other.portTypeName))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PortType [portTypeName=");
		builder.append(portTypeName);
		builder.append(", operations=");
		builder.append(operations);
		builder.append(", messagePaths=");
		builder.append(messagePaths);
		builder.append("]");
		return builder.toString();
	}
}
