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
package de.akra.idocit.common.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * It represents a parameter. One parameter can be a complex type with further inner
 * structures. Therefore there exists the {@link List} of {@link Parameter} objects.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class Parameter extends SignatureElement
{
	/**
	 * Only the identifier of the data type for the parameter.
	 */
	private String dataTypeName;

	/**
	 * The qualified name for the data type.
	 */
	private String qualifiedDataTypeName;

	/**
	 * Path from the {@link Parameters} to this parameter. This is the identifier for a
	 * {@link Documentation}.
	 * <p style="white-space: nowrap;">
	 * Example: path = &lt;MESSAGE_NAME&gt; '.' &lt;QUALIFIED_IDENTIFIER&gt; ':'
	 * &lt;QUALIFIED_DATA_TYPE_NAME&gt; [ '.' &lt; QUALIFIED_IDENTIFIER&gt; ':'
	 * &lt;QUALIFIED_DATA_TYPE_NAME&gt; ]*
	 * </p>
	 * The Delimiters in the path depends on the programming language of the
	 * {@link Parser} implementation.
	 * 
	 * @see Parser
	 * @see Delimiters
	 */
	private String signatureElementPath;

	/**
	 * Further inner structure of a complex type.
	 */
	private List<Parameter> complexType;
	
	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 */
	public Parameter(SignatureElement parent, String category, Numerus numerus)
	{
		super(parent, category, numerus);
		this.complexType = Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size()
	{
		int size = 0;
		for (Parameter param : complexType)
		{
			size += param.size() + 1;
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#copy(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	public final SignatureElement copy(SignatureElement parent)
			throws IllegalArgumentException
	{
		SignatureElement newElem = super.copy(parent);
		checkIfAssignable(Parameter.class, newElem);

		Parameter param = (Parameter) newElem;
		param.setDataTypeName(dataTypeName);
		param.setQualifiedDataTypeName(qualifiedDataTypeName);
		param.setSignatureElementPath(signatureElementPath);

		// is not needed to be set again, because it is static
		// setDelimiters(delimiters);

		for (Parameter p : complexType)
		{
			param.addParameter((Parameter) p.copy(param));
		}

		return param;
	}

	/**
	 * Builds a name string for displaying like:
	 * <p>
	 * name = &lt;IDENTIFIER&gt; [ "(Type: " &lt;TYPE&gt; ")" ] [ "[" &lt;CATEGORY&gt; "]"
	 * ]
	 * </p>
	 * 
	 * @return the display name of this element.
	 */
	public String getDisplayName()
	{
		String category = getCategory();
		return getIdentifier()
				+ (dataTypeName != null ? " (Type: " + dataTypeName + ")" : "")
				+ (category != null && !category.isEmpty() ? " [" + category + "]" : "");
	}

	/**
	 * Builds a name string with the qualified names for displaying like:
	 * <p>
	 * name = &lt;QUALIFIED_IDENTIFIER&gt; [ "(Type: " &lt;QUALIFIED_TYPE&gt; ")" ] [ "["
	 * &lt;CATEGORY&gt; "]" ]
	 * </p>
	 * 
	 * @return the qualified display name of this element.
	 */
	public String getQualifiedDisplayName()
	{
		String category = getCategory();
		return getQualifiedIdentifier()
				+ (qualifiedDataTypeName != null ? " (Type: " + qualifiedDataTypeName
						+ ")" : "")
				+ (category != null && !category.isEmpty() ? " [" + category + "]" : "");
	}

	/**
	 * Adds the {@link Documentation} <code>documentation</code> to this {@link Parameter}
	 * if
	 * <p>
	 * <code>parameterPath + {@link Delimiters#pathDelimiter} + {@link SignatureElement#getQualifiedIdentifier()}
	 * + {@link Delimiters#typeDelimiter} + {@link #qualifiedDataTypeName}</code>
	 * </p>
	 * equals {@link Documentation#getSignatureElementIdentifier()} of
	 * <code>documentation</code> . If it is not equal, the search is continued.<br>
	 * If <code>{@link Documentation#getSignatureElementIdentifier()} == null</code> the
	 * {@link Documentation} is not assignable and <code>false</code> is returned.
	 * 
	 * @param delimiters
	 *            The {@link Delimiters} for path building.
	 * 
	 * @param documentation
	 *            The documentation that should be added to a {@link Parameter}.
	 * @param parameterPath
	 *            The path to this {@link Parameter}. It is extended by each element.
	 * @return True, if matching {@link Parameter} found.
	 */
	public boolean addMatchingDocumentation(Delimiters delimiters,
			Documentation documentation, String parameterPath)
	{
		// if there is no assigned signature element, the documentation is not assignable.
		if (documentation.getSignatureElementIdentifier() == null)
		{
			return false;
		}

		boolean itemFound = false;

		// if there are previous parameters, add a dot
		if (parameterPath.length() > 0)
		{
			parameterPath += delimiters.pathDelimiter;
		}

		// append own data
		parameterPath += super.getQualifiedIdentifier() + delimiters.typeDelimiter
				+ qualifiedDataTypeName;

		// if searched Parameter found, add the documentation
		if (documentation.getSignatureElementIdentifier()
				.equals(parameterPath.toString()))
		{
			super.addDocpart(documentation);
			itemFound = true;
		}
		else
		{
			// if not found, then continue search in all sub Parameters
			Iterator<Parameter> it = complexType.iterator();
			while (it.hasNext() && !itemFound)
			{
				Parameter p = it.next();
				itemFound = p.addMatchingDocumentation(delimiters, documentation,
						parameterPath);
			}
		}
		return itemFound;
	}

	/**
	 * @param complexType
	 *            the complexType to set
	 */
	public void setComplexType(List<Parameter> complexType)
	{
		this.complexType = complexType;
	}

	/**
	 * Adds a {@link Parameter} to {@link Parameter#complexType}. It does nothing if
	 * <code>parameter == null</code>.
	 * 
	 * @param parameter
	 *            The {@link Parameter} to add to {@link Parameter#complexType}.
	 */
	public void addParameter(Parameter parameter)
	{
		if (parameter != null)
		{
			if (complexType == Collections.EMPTY_LIST)
			{
				complexType = new ArrayList<Parameter>(DEFAULT_ARRAY_SIZE);
			}
			this.complexType.add(parameter);
		}
	}

	/**
	 * @return the complexType
	 */
	public List<Parameter> getComplexType()
	{
		return complexType;
	}

	/**
	 * @param signatureElementPath
	 *            the signatureElementPath to set
	 */
	public void setSignatureElementPath(String signatureElementPath)
	{
		this.signatureElementPath = signatureElementPath;
	}

	/**
	 * @return the signatureElementPath
	 */
	public String getSignatureElementPath()
	{
		return signatureElementPath;
	}

	/**
	 * @param qualifiedDataTypeName
	 *            the qualifiedDataTypeName to set
	 */
	public void setQualifiedDataTypeName(String qualifiedDataTypeName)
	{
		this.qualifiedDataTypeName = qualifiedDataTypeName;
	}

	/**
	 * @return the qualifiedDataTypeName
	 */
	public String getQualifiedDataTypeName()
	{
		return qualifiedDataTypeName;
	}

	/**
	 * @return the dataTypeName
	 */
	public String getDataTypeName()
	{
		return dataTypeName;
	}

	/**
	 * @param dataTypeName
	 *            the dataTypeName to set
	 */
	public void setDataTypeName(String dataTypeName)
	{
		this.dataTypeName = dataTypeName;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((complexType == null) ? 0 : complexType.hashCode());
		result = prime * result + ((dataTypeName == null) ? 0 : dataTypeName.hashCode());
		result = prime
				* result
				+ ((qualifiedDataTypeName == null) ? 0 : qualifiedDataTypeName.hashCode());
		result = prime * result
				+ ((signatureElementPath == null) ? 0 : signatureElementPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (complexType == null)
		{
			if (other.complexType != null)
				return false;
		}
		else if (!complexType.equals(other.complexType))
			return false;
		if (dataTypeName == null)
		{
			if (other.dataTypeName != null)
				return false;
		}
		else if (!dataTypeName.equals(other.dataTypeName))
			return false;
		if (qualifiedDataTypeName == null)
		{
			if (other.qualifiedDataTypeName != null)
				return false;
		}
		else if (!qualifiedDataTypeName.equals(other.qualifiedDataTypeName))
			return false;
		if (signatureElementPath == null)
		{
			if (other.signatureElementPath != null)
				return false;
		}
		else if (!signatureElementPath.equals(other.signatureElementPath))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Parameter [dataTypeName=" + dataTypeName + ", qualifiedDataTypeName="
				+ qualifiedDataTypeName + ", signatureElementPath="
				+ signatureElementPath + ", complexType=" + complexType + "]";
	}

}
