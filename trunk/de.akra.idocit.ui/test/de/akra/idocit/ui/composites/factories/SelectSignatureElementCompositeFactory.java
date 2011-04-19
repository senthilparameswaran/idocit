package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.structure.InterfaceArtifact;
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
