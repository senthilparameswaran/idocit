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
package de.akra.idocit.common.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A representation of an interface file. It must contain all information about it so it
 * can be stored as valid code into a file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class InterfaceArtifact extends SignatureElement
{
	/**
	 * This {@link InterfaceArtifact} is used to represent not supported artifacts.
	 */
	public static final InterfaceArtifact NOT_SUPPORTED_ARTIFACT = new NotSupportedArtifact();

	/**
	 * List of the interfaces in the file.
	 */
	protected List<Interface> interfaces = Collections.emptyList();

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 */
	public InterfaceArtifact(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size()
	{
		int size = 0;
		for (Interface i : interfaces)
		{
			size += i.size() + 1;
		}
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#copy(de.akra.idocit.structure.
	 * SignatureElement)
	 */
	@Override
	public final SignatureElement copy(SignatureElement parent)
			throws IllegalArgumentException
	{
		SignatureElement newElem = super.copy(parent);
		checkIfAssignable(InterfaceArtifact.class, newElem);
		InterfaceArtifact artifact = (InterfaceArtifact) newElem;
		List<Interface> newInterfaceList = Collections.emptyList();
		if (interfaces != Collections.EMPTY_LIST)
		{
			newInterfaceList = new ArrayList<Interface>(interfaces.size());
			for (Interface i : interfaces)
			{
				newInterfaceList.add((Interface) i.copy(artifact));
			}
		}
		artifact.setInterfaces(newInterfaceList);
		return artifact;
	}

	/**
	 * @return the interfaceList
	 */
	public List<? extends Interface> getInterfaces()
	{
		return interfaces;
	}

	/**
	 * @param interfaceList
	 *            the interfaceList to set
	 */
	public void setInterfaces(List<Interface> interfaceList)
	{
		this.interfaces = interfaceList;
	}

	/**
	 * Adds an {@link Interface} to the structure.
	 * 
	 * @param i
	 *            the interface to add
	 */
	public void addInterface(Interface i)
	{
		if (interfaces == Collections.EMPTY_LIST)
		{
			interfaces = new ArrayList<Interface>(DEFAULT_ARRAY_SIZE);
		}
		this.interfaces.add(i);
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
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
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
		if (!(obj instanceof InterfaceArtifact))
		{
			return false;
		}
		InterfaceArtifact other = (InterfaceArtifact) obj;
		if (interfaces == null)
		{
			if (other.interfaces != null)
			{
				return false;
			}
		}
		else if (!interfaces.equals(other.interfaces))
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
		builder.append("InterfaceArtifact [interfaceList=");
		builder.append(interfaces);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * This {@link InterfaceArtifact} is used to represent not supported artifacts.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * @since 0.0.1
	 * @version 0.0.2
	 * 
	 */
	private static class NotSupportedArtifact extends InterfaceArtifact
	{
		private static final String msg = "[Artifact is not supported]";

		public NotSupportedArtifact()
		{
			super(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "");
			setIdentifier(msg);
			setDocumentationAllowed(false);
			
			// Changes due to Issue #59
			Interface i = new Interface(this, "") {
				
				@Override
				protected void doCopyTo(SignatureElement signatureElement)
				{
					// do nothing
				}
				
				@Override
				protected SignatureElement createSignatureElement(SignatureElement parent)
				{
					return this;
				}
			};
			i.setIdentifier(msg);
			i.setDocumentationAllowed(false);
			this.addInterface(i);
			// End changes due to Issue #59
		}

		@Override
		protected final SignatureElement createSignatureElement(SignatureElement parent)
		{
			return this;
		}

		@Override
		protected final void doCopyTo(SignatureElement signatureElement)
		{
			// do nothing
		}
	}
}
