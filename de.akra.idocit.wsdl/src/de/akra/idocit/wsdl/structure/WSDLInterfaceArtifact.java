package de.akra.idocit.wsdl.structure;

import javax.wsdl.Definition;

import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * The interface structure (artifact) for an WSDL file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLInterfaceArtifact extends InterfaceArtifact
{
	/**
	 * The definition of the WSDL file.
	 */
	private Definition wsdlDefinition;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 * 
	 * @param wsdlDefinition
	 *            The WSDL {@link Definition} structure of the WSDL file.
	 * @param name
	 *            The artifact name.
	 */
	public WSDLInterfaceArtifact(SignatureElement parent, String category,
			Definition wsdlDefinition, String name)
	{
		super(parent, category);
		this.wsdlDefinition = wsdlDefinition;
		super.setIdentifier(name);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>wsdlDefinition</code>.
	 * </p>
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLInterfaceArtifact(parent, super.getCategory(), wsdlDefinition,
				parent.getIdentifier());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>wsdlDefinition</code>.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing
	}

	/**
	 * @return the wsdlDefinition
	 */
	public Definition getWsdlDefinition()
	{
		return wsdlDefinition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((wsdlDefinition == null) ? 0 : wsdlDefinition.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof WSDLInterfaceArtifact))
		{
			return false;
		}
		WSDLInterfaceArtifact other = (WSDLInterfaceArtifact) obj;
		if (wsdlDefinition == null)
		{
			if (other.wsdlDefinition != null)
			{
				return false;
			}
		}
		else if (!wsdlDefinition.equals(other.wsdlDefinition))
		{
			return false;
		}
		return true;
	}
}
