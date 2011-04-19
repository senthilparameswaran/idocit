package de.akra.idocit.structure.impl;

import de.akra.idocit.structure.Parameters;
import de.akra.idocit.structure.SignatureElement;

/**
 * Test implementation of {@link Parameters}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestParameters extends Parameters
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestParameters(SignatureElement parent, String category)
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
		return new TestParameters(parent, super.getCategory());
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
