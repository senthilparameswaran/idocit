package de.akra.idocit.structure.impl;

import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.structure.SignatureElement;

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
