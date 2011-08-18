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
package de.akra.idocit.ui.composites;

import org.eclipse.swt.widgets.Tree;
import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Selection for {@link SelectSignatureElementComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class SelectSignatureElementCompositeSelection implements ISelection
{
	/**
	 * The represented structure by the {@link Tree}.
	 */
	private InterfaceArtifact interfaceArtifact;

	/**
	 * The selected {@link SignatureElement} in the {@link Tree}.
	 */
	private SignatureElement selectedSignatureElement;

	/**
	 * @param interfaceArtifact
	 *            the interfaceArtifact to set
	 */
	public void setInterfaceArtifact(InterfaceArtifact interfaceArtifact)
	{
		this.interfaceArtifact = interfaceArtifact;
	}

	/**
	 * @return the interfaceArtifact
	 */
	public InterfaceArtifact getInterfaceArtifact()
	{
		return interfaceArtifact;
	}

	/**
	 * @param selectedSignatureElement
	 *            the selectedSignatureElement to set
	 */
	public void setSelectedSignatureElement(SignatureElement selectedSignatureElement)
	{
		this.selectedSignatureElement = selectedSignatureElement;
	}

	/**
	 * @return the selectedSignatureElement
	 */
	public SignatureElement getSelectedSignatureElement()
	{
		return selectedSignatureElement;
	}

		
}
