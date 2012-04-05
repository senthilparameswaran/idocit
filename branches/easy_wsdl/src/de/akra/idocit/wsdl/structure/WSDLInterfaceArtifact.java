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
package de.akra.idocit.wsdl.structure;

import javax.wsdl.Definition;

import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * The interface structure (artifact) for an WSDL file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLInterfaceArtifact extends InterfaceArtifact
{
	/**
	 * The definition of the WSDL file.
	 */
	private Definition wsdlDefinition;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 * 
	 * @param wsdlDefinition
	 *            The WSDL {@link Definition} structure of the WSDL file.
	 * @param name
	 *            The artifact name.
	 * @param numerus
	 *            The {@link Numerus}
	 */
	public WSDLInterfaceArtifact(SignatureElement parent, String category,
			Definition wsdlDefinition, String name, Numerus numerus)
	{
		super(parent, category, numerus);
		this.wsdlDefinition = wsdlDefinition;
		super.setIdentifier(name);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>wsdlDefinition</code>.
	 * </p>
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLInterfaceArtifact(parent, super.getCategory(), wsdlDefinition,
				parent.getIdentifier(), super.getNumerus());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>wsdlDefinition</code>.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}

	/**
	 * @return the wsdlDefinition
	 */
	public Definition getWsdlDefinition()
	{
		return wsdlDefinition;
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
				+ ((wsdlDefinition == null) ? 0 : wsdlDefinition.hashCode());
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
		if (!(obj instanceof WSDLInterfaceArtifact))
		{
			return false;
		}
		WSDLInterfaceArtifact other = (WSDLInterfaceArtifact) obj;
		if (wsdlDefinition == null)
		{
			if (other.wsdlDefinition != null)
			{
				return false;
			}
		}
		else if (!wsdlDefinition.equals(other.wsdlDefinition))
		{
			return false;
		}
		return true;
	}
}
