package de.akra.idocit.core.structure.impl;

import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Test implementation of {@link Parameter}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestParameter extends Parameter
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestParameter(SignatureElement parent, String category)
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
		return new TestParameter(parent, super.getCategory());
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
