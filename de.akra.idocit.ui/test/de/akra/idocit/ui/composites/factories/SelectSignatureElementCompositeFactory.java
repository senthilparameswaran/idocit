/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.ui.composites.SelectSignatureElementComposite;
import de.akra.idocit.ui.composites.SelectSignatureElementCompositeRC;
import de.akra.idocit.ui.composites.SelectSignatureElementCompositeSelection;

/**
 * Factory to create {@link SelectSignatureElementComposite}
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class SelectSignatureElementCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection> createComposite(
			Composite pvParent)
	{
		SelectSignatureElementComposite comp = new SelectSignatureElementComposite(
				pvParent, SWT.NONE, new SelectSignatureElementCompositeRC());
		SelectSignatureElementCompositeSelection selection = new SelectSignatureElementCompositeSelection();

		// create test data
		InterfaceArtifact interfaceArt = InterfaceArtifactTestFactory
				.createTestStructure();
		selection.setInterfaceArtifact(interfaceArt);

		comp.setSelection(selection);

		return comp;
	}

	/**
	 * {@inheritDoc}
	 */
	public SelectSignatureElementCompositeRC getResourceConfiguration()
	{
		return new SelectSignatureElementCompositeRC();
	}
}
