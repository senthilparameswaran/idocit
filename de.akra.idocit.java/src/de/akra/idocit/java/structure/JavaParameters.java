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
package de.akra.idocit.java.structure;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Representation of a collection of Java parameters (input, output or exceptions).
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaParameters extends Parameters
{
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent for this element.
	 * @param category
	 *            The category of this element.
	 * @param numerus
	 *            The {@link Numerus}
	 * @param hasPublicAccessableAttributes
	 *            <code>true</code>, if this fulfills the Java Bean specification
	 */
	public JavaParameters(SignatureElement parent, String category, Numerus numerus,
			boolean hasPublicAccessableAttributes)
	{
		super(parent, category, numerus, hasPublicAccessableAttributes);
		this.setDocumentationAllowed(false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit
	 *      .structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaParameters(parent, super.getCategory(), super.getNumerus(),
				super.hasPublicAccessibleAttributes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.
	 *      SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
