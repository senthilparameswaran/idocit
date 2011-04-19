/*
 * (c) Copyright IBM Corp 2006 
 */

package com.ibm.wsdl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.WSDLElement;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * Abstract super class for all WSDL Elements, providing some basic common functionality.
 */
public abstract class AbstractWSDLElement implements WSDLElement
{
	protected List<Element> docElList;
	protected List extElements = new Vector();
	protected Map extensionAttributes = new HashMap();

	/**
	 * Adds a documentation {@link Element} to this document. This dependency on
	 * org.w3c.dom.Element should eventually be removed when a more appropriate way of
	 * representing this information is employed.
	 * 
	 * @param docEl
	 *            the documentation element
	 */
	public void addDocumentationElement(Element docEl)
	{
		if (docEl != null)
		{
			if (this.docElList == null)
			{
				this.docElList = new Vector<Element>(2);
			}
			this.docElList.add(docEl);
		}
	}

	/**
	 * Removes the {@link Element} <code>dolEl</code> from {@link List}
	 * <code>dolElList</code>.
	 * 
	 * @param docEl
	 *            The {@link Element} to remove.
	 * @return true if found and removed; false if not found or
	 *         <code>docElList == null</code>.
	 * @see List#remove(Object)
	 */
	public boolean removeDocumentationElement(Element docEl)
	{
		if (this.docElList != null && docEl != null)
		{
			return docElList.remove(docEl);
		}
		return false;
	}

	/**
	 * Get the documentation element. This dependency on org.w3c.dom.Element should
	 * eventually be removed when a more appropriate way of representing this information
	 * is employed.
	 * 
	 * @return the documentation element
	 */
	public List<Element> getDocumentationElements()
	{
		return docElList;
	}

	/**
	 * Add an extensibility element.
	 * 
	 * @param extElement
	 *            the extensibility element to be added
	 */
	public void addExtensibilityElement(ExtensibilityElement extElement)
	{
		extElements.add(extElement);
	}

	/**
	 * Remove an extensibility element.
	 * 
	 * @param extElement
	 *            the extensibility element to be removed.
	 * @return the extensibility element which was removed.
	 */
	public ExtensibilityElement removeExtensibilityElement(ExtensibilityElement extElement)
	{
		if (extElements.remove(extElement))
		{
			return extElement;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get all the extensibility elements defined here.
	 */
	public List getExtensibilityElements()
	{
		return extElements;
	}

	/**
	 * Set an extension attribute on this element. Pass in a null value to remove an
	 * extension attribute.
	 * 
	 * @param name
	 *            the extension attribute name
	 * @param value
	 *            the extension attribute value. Can be a String, a QName, a List of
	 *            Strings, or a List of QNames.
	 * 
	 * @see #getExtensionAttribute
	 * @see #getExtensionAttributes
	 * @see ExtensionRegistry#registerExtensionAttributeType
	 * @see ExtensionRegistry#queryExtensionAttributeType
	 */
	public void setExtensionAttribute(QName name, Object value)
	{
		if (value != null)
		{
			extensionAttributes.put(name, value);
		}
		else
		{
			extensionAttributes.remove(name);
		}
	}

	/**
	 * Retrieve an extension attribute from this element. If the extension attribute is
	 * not defined, null is returned.
	 * 
	 * @param name
	 *            the extension attribute name
	 * 
	 * @return the value of the extension attribute, or null if it is not defined. Can be
	 *         a String, a QName, a List of Strings, or a List of QNames.
	 * 
	 * @see #setExtensionAttribute
	 * @see #getExtensionAttributes
	 * @see ExtensionRegistry#registerExtensionAttributeType
	 * @see ExtensionRegistry#queryExtensionAttributeType
	 */
	public Object getExtensionAttribute(QName name)
	{
		return extensionAttributes.get(name);
	}

	/**
	 * Get the map containing all the extension attributes defined on this element. The
	 * keys are the qnames of the attributes.
	 * 
	 * @return a map containing all the extension attributes defined on this element
	 * 
	 * @see #setExtensionAttribute
	 * @see #getExtensionAttribute
	 */
	public Map getExtensionAttributes()
	{
		return extensionAttributes;
	}

	public String toString()
	{
		StringBuffer strBuf = new StringBuffer();

		if (extElements.size() > 0)
		{
			Iterator extIterator = extElements.iterator();

			if (extIterator.hasNext())
			{
				strBuf.append(extIterator.next());

				while (extIterator.hasNext())
				{
					strBuf.append("\n");
					strBuf.append(extIterator.next());
				}
			}
		}

		if (extensionAttributes.size() > 0)
		{
			Iterator keys = extensionAttributes.keySet().iterator();

			if (keys.hasNext())
			{
				QName name = (QName) keys.next();

				strBuf.append("extension attribute: ");
				strBuf.append(name);
				strBuf.append("=");
				strBuf.append(extensionAttributes.get(name));

				while (keys.hasNext())
				{
					name = (QName) keys.next();

					strBuf.append("\n");
					strBuf.append("extension attribute: ");
					strBuf.append(name);
					strBuf.append("=");
					strBuf.append(extensionAttributes.get(name));
				}
			}
		}

		return strBuf.toString();
	}
}
