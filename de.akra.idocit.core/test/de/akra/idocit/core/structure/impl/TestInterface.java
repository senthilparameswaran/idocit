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
package de.akra.idocit.core.structure.impl;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Test implementation of {@link Interface}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestInterface extends Interface
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestInterface(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit
	 * .structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new TestInterface(parent, super.getCategory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.
	 * SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
