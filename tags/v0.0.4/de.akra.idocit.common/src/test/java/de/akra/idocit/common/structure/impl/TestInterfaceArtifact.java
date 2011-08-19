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
package de.akra.idocit.common.structure.impl;

import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Test implementation of {@link InterfaceArtifact}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestInterfaceArtifact extends InterfaceArtifact
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 */
	public TestInterfaceArtifact(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new TestInterfaceArtifact(parent, super.getCategory());
	}

	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
