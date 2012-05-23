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
package de.akra.idocit.common.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A representation of an interface.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class Interface extends SignatureElement
{
	/**
	 * List of operations in the interface.
	 */
	private List<? extends Operation> operations = Collections.emptyList();

	/**
	 * Embedded interfaces.
	 */
	private List<? extends Interface> innerInterfaces = Collections.emptyList();

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 * @param numerus
	 *            The numerus
	 */
	public Interface(SignatureElement parent, String category, Numerus numerus)
	{
		super(parent, category, numerus, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size()
	{
		int size = 0;
		for (Interface i : innerInterfaces)
		{
			size += i.size() + 1;
		}
		for (Operation o : operations)
		{
			size += o.size() + 1;
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#copy(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	public final SignatureElement copy(SignatureElement parent)
			throws IllegalArgumentException
	{
		SignatureElement newElem = super.copy(parent);
		checkIfAssignable(Interface.class, newElem);

		Interface iface = (Interface) newElem;

		List<Operation> newOps = Collections.emptyList();
		if (operations != Collections.EMPTY_LIST)
		{
			newOps = new ArrayList<Operation>(operations.size());
			for (Operation op : operations)
			{
				newOps.add((Operation) op.copy(iface));
			}
		}
		iface.setOperations(newOps);

		List<Interface> newInnerInterfaces = Collections.emptyList();
		if (innerInterfaces != Collections.EMPTY_LIST)
		{
			newInnerInterfaces = new ArrayList<Interface>(innerInterfaces.size());
			for (Interface i : innerInterfaces)
			{
				newInnerInterfaces.add((Interface) i.copy(iface));
			}
		}
		iface.setInnerInterfaces(newInnerInterfaces);

		return iface;
	}

	/**
	 * @return the operations
	 */
	public List<? extends Operation> getOperations()
	{
		return operations;
	}

	/**
	 * @param operations
	 *            the operations to set
	 */
	public void setOperations(List<? extends Operation> operations)
	{
		this.operations = operations;
	}

	/**
	 * @param innerInterfaces
	 *            the innerInterfaces to set
	 */
	public void setInnerInterfaces(List<? extends Interface> innerInterfaces)
	{
		this.innerInterfaces = innerInterfaces;
	}

	/**
	 * @return the innerInterfaces
	 */
	public List<? extends Interface> getInnerInterfaces()
	{
		return innerInterfaces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((innerInterfaces == null) ? 0 : innerInterfaces.hashCode());
		result = prime * result + ((operations == null) ? 0 : operations.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof Interface))
		{
			return false;
		}
		Interface other = (Interface) obj;
		if (innerInterfaces == null)
		{
			if (other.innerInterfaces != null)
			{
				return false;
			}
		}
		else if (!innerInterfaces.equals(other.innerInterfaces))
		{
			return false;
		}
		if (operations == null)
		{
			if (other.operations != null)
			{
				return false;
			}
		}
		else if (!operations.equals(other.operations))
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Interface [operations=");
		builder.append(operations);
		builder.append(", innerInterfaces=");
		builder.append(innerInterfaces);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
