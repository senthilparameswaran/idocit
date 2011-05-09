package de.akra.idocit.wsdl.structure;

import javax.wsdl.Message;

import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Representation for a parameter in a {@link Message}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLParameter extends Parameter
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 */
	public WSDLParameter(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLParameter(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
