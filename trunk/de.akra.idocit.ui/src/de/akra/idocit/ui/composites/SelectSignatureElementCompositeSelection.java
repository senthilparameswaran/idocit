package de.akra.idocit.ui.composites;

import org.eclipse.swt.widgets.Tree;
import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.SignatureElement;

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
