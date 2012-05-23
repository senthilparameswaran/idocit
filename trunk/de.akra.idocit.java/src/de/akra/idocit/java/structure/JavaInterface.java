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

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.TagElement;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Representation of a Java interface, class or enumeration (see
 * {@link AbstractTypeDeclaration}).
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class JavaInterface extends Interface
{
	/**
	 * Reference to the corresponding {@link AbstractTypeDeclaration}.
	 */
	private AbstractTypeDeclaration refToASTNode;

	/**
	 * The list of additional Javadoc tags that are not interpreted by iDocIt! (all tags
	 * except param, return, throws and the general description). These tags are appended
	 * at the end to the Javadoc by writing it again. It is only used to keep existing
	 * tags in Javadoc.
	 */
	private List<TagElement> additionalTags = Collections.emptyList();

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param category
	 */
	public JavaInterface(SignatureElement parent, String category, Numerus numerus)
	{
		super(parent, category, numerus);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaInterface(parent, super.getCategory(), super.getNumerus());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> For <code>refToASTNode</code> and <code>additionalTags</code> only the
	 * references are copied. There will created no deep copy of them!
	 * </p>
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		JavaInterface ji = (JavaInterface) signatureElement;
		ji.setRefToASTNode(refToASTNode);
		ji.setAdditionalTags(additionalTags);
	}

	/**
	 * @return the refToASTNode
	 */
	public AbstractTypeDeclaration getRefToASTNode()
	{
		return refToASTNode;
	}

	/**
	 * @param refToASTNode
	 *            the refToASTNode to set
	 */
	public void setRefToASTNode(AbstractTypeDeclaration refToASTNode)
	{
		this.refToASTNode = refToASTNode;
	}

	public void setAdditionalTags(List<TagElement> additionalTags)
	{
		this.additionalTags = additionalTags;
	}

	public List<TagElement> getAdditionalTags()
	{
		return additionalTags;
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
				+ ((additionalTags == null) ? 0 : additionalTags.hashCode());
		result = prime * result + ((refToASTNode == null) ? 0 : refToASTNode.hashCode());
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
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaInterface other = (JavaInterface) obj;
		if (additionalTags == null)
		{
			if (other.additionalTags != null)
				return false;
		}
		else if (!additionalTags.equals(other.additionalTags))
			return false;
		if (refToASTNode == null)
		{
			if (other.refToASTNode != null)
				return false;
		}
		else if (!refToASTNode.equals(other.refToASTNode))
			return false;
		return true;
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
		builder.append("JavaInterface [additionalTags=");
		builder.append(additionalTags);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
