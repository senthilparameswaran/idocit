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
package de.akra.idocit.core.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.akra.idocit.core.services.Preconditions;

/**
 * A signature element is an abstract element of an interface, a part of the interface,
 * the interface itself or even the whole file (or other resource) of the interface
 * definition.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class SignatureElement
{

	/**
	 * Error message if an not expected (assignable) object is insert.
	 */
	protected static final String ILLEGAL_ARGUMENT_ERR_MSG = "\"%s\" is not assignable to \"%s\".";

	/**
	 * Use this identifier if it is undefined in the parsed structure.
	 */
	public static final String ANONYMOUS_IDENTIFIER = "anonymous";

	/**
	 * Use this smaller default size for initializing e.g. {@link ArrayList}s to save some
	 * memory.
	 */
	public static final int DEFAULT_ARRAY_SIZE = 5;

	/**
	 * The empty SignatureElement represents the parent of the root SignatureElement.
	 */
	public static final SignatureElement EMPTY_SIGNATURE_ELEMENT = new SignatureElement(
			null, "") {

		@Override
		public int size()
		{
			return 0;
		}

		@Override
		protected SignatureElement createSignatureElement(SignatureElement parent)
		{
			// return the EMPTY_SIGNATURE_ELEMENT
			return this;
		}

		@Override
		protected void doCopyTo(SignatureElement signatureElement)
		{
			// do nothing
		}
	};

	/**
	 * Counter for the unique IDs, starts with {@link Integer#MIN_VALUE}.
	 */
	private static int ID_COUNTER = Integer.MIN_VALUE;

	/**
	 * The parent SignatureElement.
	 */
	private SignatureElement parent;

	/**
	 * The unique id for this element in the current structure.
	 */
	private int id;

	/**
	 * A list of documentation parts. It is initialized with
	 * {@link Collections#emptyList()}.
	 */
	private List<Documentation> documentations;

	/**
	 * Identifier of the signature element.
	 */
	private String identifier;

	/**
	 * Fully qualified identifier.
	 */
	private String qualifiedIdentifier;

	/**
	 * The category/type of the signature element. It is shown in the UI to better
	 * distinguish the elements.<br>
	 * It should be set by the derived classes with a meaningful name.
	 */
	private String category;

	/**
	 * True, if it is allowed to add a documentation part to this signature element. E.g.
	 * it is not always possible or wise to add a documentation on file level. The UI has
	 * to hide the possibility if it is not allowed.<br>
	 * By default <code>documentationAllowed == true</code>.
	 */
	private boolean documentationAllowed = true;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * 
	 * @param category
	 *            The category of this element.
	 */
	public SignatureElement(SignatureElement parent, String category)
	{
		this.id = ID_COUNTER++;
		this.parent = parent;
		this.category = category;
		this.documentations = Collections.emptyList();
	}

	/**
	 * Sum the number of elements in this structure up to the leaves.
	 * 
	 * @return number of elements in this structure up to the leaves.
	 */
	public abstract int size();

	/**
	 * Make a deep copy of this instance. It calls the abstract methods
	 * {@link #createSignatureElement(SignatureElement)} and
	 * {@link #doCopyTo(SignatureElement)}.
	 * 
	 * @param parent
	 *            The parent for the copy.
	 * @return The deep copy of this instance.
	 * @throws IllegalArgumentException
	 *             If <code>parent</code> is not an instance of {@link SignatureElement}
	 *             or the new created SignatureElement from
	 *             {@link SignatureElement#createSignatureElement(SignatureElement)} is
	 *             not assignable to the current sub class.
	 */
	public SignatureElement copy(SignatureElement parent) throws IllegalArgumentException
	{
		checkIfAssignable(SignatureElement.class, parent);
		SignatureElement newSigElem = createSignatureElement(parent);
		copyAttributesTo(newSigElem);
		doCopyTo(newSigElem);
		return newSigElem;
	}

	/**
	 * Create a new instance of the current sub class of {@link SignatureElement}.
	 * 
	 * @param parent
	 *            The parent for the new instance.
	 * @return the new instance.
	 */
	protected abstract SignatureElement createSignatureElement(SignatureElement parent);

	/**
	 * Copy all attributes from this object to <code>signatureElement</code>.
	 * 
	 * @param signatureElement
	 *            The signature element to which all values should be copied.
	 */
	protected abstract void doCopyTo(SignatureElement signatureElement);

	/**
	 * Copies all attributes except the <code>parent</code> attribute to
	 * <code>signatureElement</code> and returns <code>signatureElement</code>.
	 * 
	 * @param signatureElement
	 *            A {@link SignatureElement} to which all attributes except the
	 *            <code>parent</code> are copied to.
	 */
	private void copyAttributesTo(SignatureElement signatureElement)
	{
		// category was set via constructor, need not to be set here
		// signatureElement.setCategory(category);

		signatureElement.setIdentifier(identifier);
		signatureElement.setQualifiedIdentifier(qualifiedIdentifier);
		signatureElement.setDocumentationAllowed(documentationAllowed);
		signatureElement.setId(id);

		List<Documentation> newDocparts = Collections.emptyList();
		if (documentations != Collections.EMPTY_LIST)
		{
			newDocparts = new ArrayList<Documentation>(documentations.size());
			for (Documentation doc : documentations)
			{
				newDocparts.add(doc.copy());
			}
		}
		signatureElement.setDocumentations(newDocparts);
	}

	/**
	 * Checks if the <code>actual</code> SignatureElement can be casted to the
	 * <code>expected</code> class.
	 * 
	 * @param expected
	 *            The expected {@link Class}.
	 * @param actual
	 *            The SignatureElement to test for assignability.
	 * @throws IllegalArgumentException
	 *             if <code>actual</code> is not assignable to the expected class.
	 */
	protected void checkIfAssignable(Class<? extends SignatureElement> expected,
			SignatureElement actual) throws IllegalArgumentException
	{
		Preconditions.checkTrue(expected.isInstance(actual), String.format(
				ILLEGAL_ARGUMENT_ERR_MSG, SignatureElement.class.getName(), actual
						.getClass().getName()));
	}

	/**
	 * @return the documentations
	 */
	public List<Documentation> getDocumentations()
	{
		return documentations;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Only used by {@link SignatureElement#copyAttributesTo(SignatureElement)}.
	 * 
	 * @param id
	 *            the id to set
	 */
	private final void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Builds a name string for displaying like:
	 * <p>
	 * name = &lt;IDENTIFIER&gt; [ "[" &lt;CATEGORY&gt; "]" ]
	 * </p>
	 * 
	 * 
	 * @return the display name of this element.
	 */
	public String getDisplayName()
	{
		return identifier
				+ (category != null && !category.isEmpty() ? " [" + category + "]" : "");
	}

	/**
	 * @param documentations
	 *            the documentations to set
	 */
	public void setDocumentations(List<Documentation> documentations)
	{
		this.documentations = documentations;
	}

	/**
	 * Add a {@link Documentation} to this signature element.
	 * 
	 * @param documentation
	 *            The {@link Documentation} to add.
	 */
	public void addDocpart(Documentation documentation)
	{
		if (documentations == Collections.EMPTY_LIST)
		{
			// replace the dummy list by a working one
			documentations = new ArrayList<Documentation>(DEFAULT_ARRAY_SIZE);
		}
		this.documentations.add(documentation);
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier()
	{
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * @return the documentationAllowed
	 */
	public boolean isDocumentationAllowed()
	{
		return documentationAllowed;
	}

	/**
	 * @param documentationAllowed
	 *            the documentationAllowed to set
	 */
	public void setDocumentationAllowed(boolean documentationAllowed)
	{
		this.documentationAllowed = documentationAllowed;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * @return the category
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * @return the parent
	 */
	public SignatureElement getParent()
	{
		return parent;
	}

	/**
	 * @param qualifiedIdentifier
	 *            the qualifiedIdentifier to set
	 */
	public void setQualifiedIdentifier(String qualifiedIdentifier)
	{
		this.qualifiedIdentifier = qualifiedIdentifier;
	}

	/**
	 * @return the qualifiedIdentifier
	 */
	public String getQualifiedIdentifier()
	{
		return qualifiedIdentifier;
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
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((documentations == null) ? 0 : documentations.hashCode());
		result = prime * result + (documentationAllowed ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((qualifiedIdentifier == null) ? 0 : qualifiedIdentifier.hashCode());
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
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof SignatureElement))
		{
			return false;
		}
		SignatureElement other = (SignatureElement) obj;
		if (category == null)
		{
			if (other.category != null)
			{
				return false;
			}
		}
		else if (!category.equals(other.category))
		{
			return false;
		}
		if (documentations == null)
		{
			if (other.documentations != null)
			{
				return false;
			}
		}
		else if (!documentations.equals(other.documentations))
		{
			return false;
		}
		if (documentationAllowed != other.documentationAllowed)
		{
			return false;
		}
		if (id != other.id)
		{
			return false;
		}
		if (identifier == null)
		{
			if (other.identifier != null)
			{
				return false;
			}
		}
		else if (!identifier.equals(other.identifier))
		{
			return false;
		}
		if (qualifiedIdentifier == null)
		{
			if (other.qualifiedIdentifier != null)
			{
				return false;
			}
		}
		else if (!qualifiedIdentifier.equals(other.qualifiedIdentifier))
		{
			return false;
		}
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
		builder.append("SignatureElement [id=");
		builder.append(id);
		builder.append(", docparts=");
		builder.append(documentations);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", qualifiedIdentifier=");
		builder.append(qualifiedIdentifier);
		builder.append(", category=");
		builder.append(category);
		builder.append(", documentationAllowed=");
		builder.append(documentationAllowed);
		builder.append("]");
		return builder.toString();
	}
}
