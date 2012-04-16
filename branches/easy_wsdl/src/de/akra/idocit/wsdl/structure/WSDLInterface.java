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
import org.ow2.easywsdl.wsdl.api.InterfaceType;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * A representation of a WSDL {@link InterfaceType}. A {@link InterfaceType} has no inner
 * {@link InterfaceType}s, so <code>{@link Interface#getInnerInterfaces()} == null</code>.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class WSDLInterface extends Interface
{
	/**
	 * Reference to the corresponding {@link InterfaceType} in the {@link Description}.
	 */
	private InterfaceType interfaceType;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * 
	 * @param category
	 *            The category of this element.
	 * @param numerus
	 *            The {@link Numerus}
	 */
	public WSDLInterface(SignatureElement parent, String category, Numerus numerus)
	{
		super(parent, category, numerus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLInterface(parent, super.getCategory(), super.getNumerus());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of {@link #interfaceType}.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		WSDLInterface wsdlInterface = (WSDLInterface) signatureElement;
		wsdlInterface.setInterfaceType(interfaceType);
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
		builder.append("WSDLInterface [interfaceType=");
		builder.append(interfaceType);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 
	 * @return interfaceType
	 */
	public InterfaceType getInterfaceType()
	{
		return interfaceType;
	}

	/**
	 * 
	 * @param interfaceType
	 */
	public void setInterfaceType(InterfaceType interfaceType)
	{
		this.interfaceType = interfaceType;
	}
}
