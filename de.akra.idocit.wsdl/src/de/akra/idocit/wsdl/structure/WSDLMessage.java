/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.wsdl.structure;

import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.WSDLElement;

import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * This is a representation of an {@link Input}, {@link Output} or {@link Fault} element
 * embedded in an {@link javax.wsdl.Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLMessage extends Parameters
{
	/**
	 * Reference to an embedded element for a message ({@link Input}, {@link Output} or
	 * {@link Fault}) of an {@link javax.wsdl.Operation}.
	 */
	private WSDLElement messageRef;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * 
	 * @param category
	 *            The category of this element.
	 */
	public WSDLMessage(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLMessage(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>messageRef</code>.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		WSDLMessage msg = (WSDLMessage) signatureElement;
		msg.setMessageRef(messageRef);
	}

	/**
	 * @param messageRef
	 *            set the reference to an embedded element for a message ({@link Input},
	 *            {@link Output} or {@link Fault}) of an {@link javax.wsdl.Operation}.
	 */
	public void setMessageRef(WSDLElement messageRef)
	{
		this.messageRef = messageRef;
	}

	/**
	 * @return the reference to an embedded message ({@link Input}, {@link Output} or
	 *         {@link Fault}) of an {@link javax.wsdl.Operation}.
	 */
	public WSDLElement getMessageRef()
	{
		return messageRef;
	}
}
