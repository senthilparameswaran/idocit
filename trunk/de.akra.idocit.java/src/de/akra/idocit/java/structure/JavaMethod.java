package de.akra.idocit.java.structure;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.akra.idocit.structure.Operation;
import de.akra.idocit.structure.SignatureElement;

/**
 * Representation of a Java method.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class JavaMethod extends Operation
{
	/**
	 * Reference to the corresponding {@link MethodDeclaration}.
	 */
	private MethodDeclaration refToASTNode;

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param category
	 */
	public JavaMethod(SignatureElement parent, String category)
	{
		super(parent, category);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see
	 * de.akra.idocit.structure.SignatureElement#createSignatureElement(de.akra.idocit
	 * .structure.SignatureElement)
	 */
	@Override
	protected SignatureElement createSignatureElement(SignatureElement parent)
	{
		return new JavaMethod(parent, super.getCategory());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#doCopyTo(de.akra.idocit.structure.
	 * SignatureElement)
	 */
	@Override
	protected void doCopyTo(SignatureElement signatureElement)
	{
		JavaMethod jm = (JavaMethod)signatureElement;
		jm.setRefToASTNode(refToASTNode);
	}

	/**
	 * @param refToASTNode the refToASTNode to set
	 */
	public void setRefToASTNode(MethodDeclaration refToASTNode)
	{
		this.refToASTNode = refToASTNode;
	}

	/**
	 * @return the refToASTNode
	 */
	public MethodDeclaration getRefToASTNode()
	{
		return refToASTNode;
	}

	/* (non-Javadoc)
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
		if (!(obj instanceof JavaMethod))
		{
			return false;
		}
		JavaMethod other = (JavaMethod) obj;
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
