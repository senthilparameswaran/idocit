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


import org.ow2.easywsdl.wsdl.api.Description;

import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * The interface structure (artifact) for an WSDL file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class WSDLInterfaceArtifact extends InterfaceArtifact
{
	/**
	 * The definition of the WSDL file.
	 */
	private Description wsdlDescription;

	

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 * 
	 * @param wsdlDescription
	 *            The WSDL {@link Description} structure of the WSDL file.
	 * @param name
	 *            The artifact name.
	 * @param numerus
	 *            The {@link Numerus}
	 */
	public WSDLInterfaceArtifact(SignatureElement parent, String category,
			Description wsdlDescription, String name, Numerus numerus)
	{
		super(parent, category, numerus);
		this.wsdlDescription = wsdlDescription;
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
		return new WSDLInterfaceArtifact(parent, super.getCategory(), wsdlDescription,
				parent.getIdentifier(), super.getNumerus());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of {@link #wsdlDescription}.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}

	/**
	 * 
	 * @return wsdlDescription
	 */
	public Description getWsdlDescription()
	{
		return wsdlDescription;
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
				+ ((wsdlDescription == null) ? 0 : wsdlDescription.hashCode());
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
		if (wsdlDescription == null)
		{
			if (other.wsdlDescription != null)
			{
				return false;
			}
		}
		else if (!wsdlDescription.equals(other.wsdlDescription))
		{
			return false;
		}
		return true;
	}
}
