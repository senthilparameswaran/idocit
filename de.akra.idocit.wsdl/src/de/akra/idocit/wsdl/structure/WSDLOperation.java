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
import javax.wsdl.PortType;

import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * A representation of a WSDL {@link javax.wsdl.Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLOperation extends Operation
{
	/**
	 * Reference to the corresponding {@link javax.wsdl.Operation} of a {@link PortType}
	 * in the {@link Definition}.
	 */
	private javax.wsdl.Operation operation;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * 
	 * @param category
	 *            The category of this element.
	 */
	public WSDLOperation(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLOperation(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>operation</code>.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		WSDLOperation wsdlOp = (WSDLOperation) signatureElement;
		wsdlOp.setOperation(operation);
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(javax.wsdl.Operation operation)
	{
		this.operation = operation;
	}

	/**
	 * @return the {@link javax.wsdl.Operation} <code>operation</code>.
	 */
	public javax.wsdl.Operation getOperation()
	{
		return operation;
	}

}
