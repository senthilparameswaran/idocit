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



import org.ow2.easywsdl.wsdl.api.Fault;
import org.ow2.easywsdl.wsdl.api.Input;
import org.ow2.easywsdl.wsdl.api.Output;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfParam;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * This is a representation of an {@link Input}, {@link Output} or {@link Fault} element
 * embedded in an {@link org.ow2.easywsdl.wsdl.api.Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class WSDLMessage extends Parameters
{
	/**
	 * Reference to the ({@link Input}, {@link Output} or {@link Fault}) of an
	 * {@link org.ow2.easywsdl.wsdl.api.Operation}.
	 */
	private AbsItfParam messageRef;

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
	 * @param hasPublicAccessableAttributes
	 *            <code>true</code>, if this message has internal attributes which could
	 *            be modified from outside
	 */
	public WSDLMessage(SignatureElement parent, String category, Numerus numerus,
			boolean hasPublicAccessableAttributes)
	{
		super(parent, category, numerus, hasPublicAccessableAttributes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLMessage(parent, super.getCategory(), super.getNumerus(),
				super.hasPublicAccessibleAttributes());
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
	 *            set the reference to the ({@link Input}, {@link Output} or {@link Fault}
	 *            ) of an {@link org.ow2.easywsdl.wsdl.api.Operation}.
	 */
	public void setMessageRef(AbsItfParam messageRef)
	{
		this.messageRef = messageRef;
	}

	/**
	 * @return the reference to the ({@link Input}, {@link Output} or {@link Fault}) of an
	 *         {@link org.ow2.easywsdl.wsdl.api.Operation}.
	 */
	public AbsItfParam getMessageRef()
	{
		return messageRef;
	}
}
