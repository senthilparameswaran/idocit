package de.akra.idocit.core.structure.impl;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Test implementation of {@link Interface}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestInterface extends Interface
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestInterface(SignatureElement parent, String category)
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
		return new TestInterface(parent, super.getCategory());
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
