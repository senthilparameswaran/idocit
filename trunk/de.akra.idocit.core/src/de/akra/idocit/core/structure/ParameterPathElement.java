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
package de.akra.idocit.core.structure;

/**
 * This class contains the name and type of a path element from the parameter path.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ParameterPathElement
{
	private String qualifiedIdentifier;
	private String identifier;
	private String qualifiedTypeName;
	private String typeName;

	/**
	 * @return the qualifiedIdentifier
	 */
	public String getQualifiedIdentifier()
	{
		return qualifiedIdentifier;
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
	 * @return the qualifiedTypeName
	 */
	public String getQualifiedTypeName()
	{
		return qualifiedTypeName;
	}

	/**
	 * @param qualifiedTypeName
	 *            the qualifiedTypeName to set
	 */
	public void setQualifiedTypeName(String qualifiedTypeName)
	{
		this.qualifiedTypeName = qualifiedTypeName;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName()
	{
		return typeName;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterPathElement [qualifiedIdentifier=");
		builder.append(qualifiedIdentifier);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", qualifiedTypeName=");
		builder.append(qualifiedTypeName);
		builder.append(", typeName=");
		builder.append(typeName);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((qualifiedIdentifier == null) ? 0 : qualifiedIdentifier.hashCode());
		result = prime * result
				+ ((qualifiedTypeName == null) ? 0 : qualifiedTypeName.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ParameterPathElement))
		{
			return false;
		}
		ParameterPathElement other = (ParameterPathElement) obj;
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
		if (qualifiedTypeName == null)
		{
			if (other.qualifiedTypeName != null)
			{
				return false;
			}
		}
		else if (!qualifiedTypeName.equals(other.qualifiedTypeName))
		{
			return false;
		}
		if (typeName == null)
		{
			if (other.typeName != null)
			{
				return false;
			}
		}
		else if (!typeName.equals(other.typeName))
		{
			return false;
		}
		return true;
	}

}