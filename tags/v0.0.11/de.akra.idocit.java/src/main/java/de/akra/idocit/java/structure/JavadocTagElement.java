/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package de.akra.idocit.java.structure;

import java.util.List;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * A container for information that should be added to a {@link Javadoc} comment.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavadocTagElement
{
	private String tagName;
	private String parameterName;
	private List<Documentation> documentations;
	private SignatureElement signatureElement;

	/**
	 * Constructor.
	 * 
	 * @param documentations
	 *            The {@link Documentation}s that should be added to the {@link Javadoc}.
	 */
	public JavadocTagElement(List<Documentation> documentations,
			SignatureElement signatureElement)
	{
		this(null, null, documentations, signatureElement);
	}

	/**
	 * Constructor.
	 * 
	 * @param tagName
	 *            The tag name to which the documentations should be added in the
	 *            {@link Javadoc}.
	 * @see TagElement
	 * @param documentations
	 *            The {@link Documentation}s that should be added to the {@link Javadoc}.
	 */
	public JavadocTagElement(String tagName, List<Documentation> documentations,
			SignatureElement signatureElement)
	{
		this(tagName, null, documentations, signatureElement);
	}

	/**
	 * Constructor.
	 * 
	 * @param tagName
	 *            The tag name to which the documentations should be added in the
	 *            {@link Javadoc}.
	 * @see TagElement
	 * @param parameterName
	 *            The name of the parameter that should be added, e.g. for a
	 *            {@link TagElement#TAG_PARAM}.
	 * @param documentations
	 *            The {@link Documentation}s that should be added to the {@link Javadoc}.
	 */
	public JavadocTagElement(String tagName, String parameterName,
			List<Documentation> documentations, SignatureElement signatureElement)
	{
		this.tagName = tagName;
		this.parameterName = parameterName;
		this.documentations = documentations;
		this.signatureElement = signatureElement;
	}

	/**
	 * Check the {@link JavadocTagElement} before writing it, if it really contains some
	 * information that can be written. If you do not check, empty tags might be added to
	 * Javadoc.
	 * 
	 * @return true, if no value is set that should be written to Javadoc.
	 */
	public boolean isEmpty()
	{
		return tagName == null && parameterName == null
				&& (documentations == null || documentations.isEmpty());
	}

	/**
	 * @return the tagName
	 */
	public String getTagName()
	{
		return tagName;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName()
	{
		return parameterName;
	}

	/**
	 * @return the documentations
	 */
	public List<Documentation> getDocumentations()
	{
		return documentations;
	}

	public SignatureElement getSignatureElement()
	{
		return signatureElement;
	}

	public void setSignatureElement(SignatureElement signatureElement)
	{
		this.signatureElement = signatureElement;
	}
}
