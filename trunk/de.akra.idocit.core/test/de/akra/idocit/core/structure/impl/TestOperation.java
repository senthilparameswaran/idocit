package de.akra.idocit.core.structure.impl;

import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Test implementation of {@link Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class TestOperation extends Operation
{

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param category
	 */
	public TestOperation(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new TestOperation(parent, super.getCategory());
	}

	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}
}
