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
 * Representation for a thematic role.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ThematicRole implements DescribedItem, Comparable<ThematicRole>
{
	/**
	 * Role representation if there aren't any.
	 */
	public static final ThematicRole EMPTY_ROLE = new ThematicRole("No Roles found"); 
	
	private String name;

	private String description;

	/**
	 * Constructor.
	 */
	public ThematicRole()
	{
		this(SignatureElement.ANONYMOUS_IDENTIFIER, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Name of the thematic role.
	 */
	public ThematicRole(String name)
	{
		this(name, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Name of the thematic role.
	 * @param description
	 *            Description for the thematic role.
	 */
	public ThematicRole(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThematicRole other = (ThematicRole) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ThematicRole [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Sort by name.
	 */
	@Override
	public int compareTo(ThematicRole role)
	{
		if (this.equals(role))
		{
			return 0;
		}

		int nameCmp = name.compareTo(role.getName());
		if (nameCmp != 0)
		{
			return nameCmp;
		}

		return 0;
	}
}
