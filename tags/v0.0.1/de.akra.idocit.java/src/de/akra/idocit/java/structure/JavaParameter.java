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

import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Representation of a single Java parameter, return type or thrown exception.<br />
 * If it is a parameter or return type and has further internal structures, e.g. is an
 * object and not a simple type, this parameter describes the whole structure. If it is a
 * thrown exception only the <code>identifier</code> is used.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaParameter extends Parameter
{
	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public JavaParameter(SignatureElement parent)
	{
		super(parent, "");
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
		return new JavaParameter(parent);
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
