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

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * A representation of a WSDL {@link org.ow2.easywsdl.wsdl.api.Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class WSDLOperation extends Operation
{
	/**
	 * Reference to the corresponding {@link org.ow2.easywsdl.wsdl.api.Operation} of a {@link InterfaceType}
	 * in the {@link Description}.
	 */
	private org.ow2.easywsdl.wsdl.api.Operation operation;

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
	public WSDLOperation(SignatureElement parent, String category, String thematicGrid,
			Numerus numerus)
	{
		super(parent, category, thematicGrid, numerus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLOperation(parent, super.getCategory(),
				super.getThematicGridName(), super.getNumerus());
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
	public void setOperation(org.ow2.easywsdl.wsdl.api.Operation operation)
	{
		this.operation = operation;
	}

	/**
	 * @return the {@link org.ow2.easywsdl.wsdl.api.Operation} <code>operation</code>.
	 */
	public org.ow2.easywsdl.wsdl.api.Operation getOperation()
	{
		return operation;
	}

}
