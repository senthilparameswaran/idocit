/*******************************************************************************
 * Copyright 2011 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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