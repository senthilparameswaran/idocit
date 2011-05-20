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

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Representation of a Java source file.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavaInterfaceArtifact extends InterfaceArtifact
{
	/**
	 * The {@link CompilationUnit} of the Java source.
	 */
	private CompilationUnit compilationUnit;
	
	private String originalDocument;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent should be {@link SignatureElement#EMPTY_SIGNATURE_ELEMENT}.
	 * @param category
	 *            The category of this artifact.
	 * @param compilationUnit
	 *            The {@link CompilationUnit} of the Java source.
	 */
	public JavaInterfaceArtifact(SignatureElement parent, String category,
			CompilationUnit compilationUnit)
	{
		super(parent, category);
		this.setDocumentationAllowed(false);
		this.compilationUnit = compilationUnit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit
	 *      .structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaInterfaceArtifact(parent, super.getCategory(),
				this.compilationUnit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.
	 *      SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		// do nothing, as the CompilationUnit was set via the constructor
	}

	/**
	 * @return the compilationUnit
	 */
	public CompilationUnit getCompilationUnit()
	{
		return compilationUnit;
	}

	/**
	 * @return the originalDocument
	 */
	public String getOriginalDocument()
	{
		return originalDocument;
	}

	/**
	 * @param originalDocument the originalDocument to set
	 */
	public void setOriginalDocument(String originalDocument)
	{
		this.originalDocument = originalDocument;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((compilationUnit == null) ? 0 : compilationUnit.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		if (!(obj instanceof JavaInterfaceArtifact))
		{
			return false;
		}
		JavaInterfaceArtifact other = (JavaInterfaceArtifact) obj;
		if (compilationUnit == null)
		{
			if (other.compilationUnit != null)
			{
				return false;
			}
		}
		else if (!compilationUnit.equals(other.compilationUnit))
		{
			return false;
		}
		return true;
	}
}
