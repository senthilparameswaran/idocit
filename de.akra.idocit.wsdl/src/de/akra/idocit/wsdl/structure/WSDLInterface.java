package de.akra.idocit.wsdl.structure;

import javax.wsdl.Definition;
import javax.wsdl.PortType;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * A representation of a WSDL {@link PortType}. A {@link PortType} has no inner
 * {@link PortType}s, so <code>{@link Interface#getInnerInterfaces()} == null</code>.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class WSDLInterface extends Interface
{
	/**
	 * Reference to the corresponding {@link PortType} in the {@link Definition}.
	 */
	private PortType portType;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * 
	 * @param category
	 *            The category of this element.
	 */
	public WSDLInterface(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new WSDLInterface(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * HINT: Does not make a deep copy of <code>portType</code>.
	 * </p>
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		WSDLInterface wsdlInterface = (WSDLInterface) signatureElement;
		wsdlInterface.setPortType(portType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("WSDLInterface [portType=");
		builder.append(portType);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param portType
	 *            the portType to set
	 */
	public void setPortType(PortType portType)
	{
		this.portType = portType;
	}

	/**
	 * @return the portType
	 */
	public PortType getPortType()
	{
		return portType;
	}

}
