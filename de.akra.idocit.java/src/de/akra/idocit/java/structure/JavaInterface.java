/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.java.structure;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Representation of a Java interface, class or enumeration (see
 * {@link AbstractTypeDeclaration}).
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaInterface extends Interface
{
	/**
	 * Reference to the corresponding {@link AbstractTypeDeclaration}.
	 */
	private AbstractTypeDeclaration refToASTNode;

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param category
	 */
	public JavaInterface(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaInterface(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		JavaInterface ji = (JavaInterface) signatureElement;
		ji.setRefToASTNode(refToASTNode);
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
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof JavaInterface))
		{
			return false;
		}
		JavaInterface other = (JavaInterface) obj;
		if (refToASTNode == null)
		{
			if (other.refToASTNode != null)
			{
				return false;
			}
		}
		else if (!refToASTNode.equals(other.refToASTNode))
		{
			return false;
		}
		return true;
	}
}
