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
package de.akra.idocit.common.structure.impl;

import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Test implementation of {@link Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestOperation extends Operation
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestOperation(SignatureElement parent, String category, String thematicGridName, Numerus numerus)
	{
		super(parent, category, thematicGridName, numerus);
	}

	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new TestOperation(parent, super.getCategory(), super.getThematicGridName(), super.getNumerus());
	}

	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
