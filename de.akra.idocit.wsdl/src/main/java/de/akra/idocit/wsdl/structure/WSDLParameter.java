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

import javax.wsdl.Message;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Representation for a parameter in a {@link Message}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLParameter extends Parameter
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 * @param numerus
	 *            The {@link Numerus}
	 * @param hasPublicAccessableAttributes
	 *            <code>true</code>, if this parameters has internal attributes which
	 *            could be modified from outside
	 */
	public WSDLParameter(SignatureElement parent, String category, Numerus numerus,
			boolean hasPublicAccessableAttributes)
	{
		super(parent, category, numerus, hasPublicAccessableAttributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLParameter(parent, super.getCategory(), super.getNumerus(),
				super.hasPublicAccessibleAttributes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
