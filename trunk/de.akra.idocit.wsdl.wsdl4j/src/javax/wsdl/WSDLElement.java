/*
 * (c) Copyright IBM Corp 2006 
 */

package javax.wsdl;

import java.util.List;

import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ElementExtensible;

import org.w3c.dom.Element;

/**
 * This interface represents all WSDL Elements
 */
public interface WSDLElement extends java.io.Serializable, AttributeExtensible,
		ElementExtensible
{
	/**
	 * Adds a documentation element to this document.
	 * 
	 * @param docEl
	 *            the documentation element
	 */
	public void addDocumentationElement(Element docEl);

	/**
	 * Removes the {@link Element} <code>dolEl</code> from this element.
	 * 
	 * @param docEl
	 *            The {@link Element} to remove.
	 * @return true if found and removed; false if not found or
	 *         <code>docElList == null</code>.
	 */
	public boolean removeDocumentationElement(Element docEl);

	/**
	 * Get the documentation elements.
	 * 
	 * @return the documentation elements
	 */
	public List<Element> getDocumentationElements();
}