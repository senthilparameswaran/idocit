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
package de.akra.idocit.common.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Representation of an thematic grid.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ThematicGrid implements DescribedItem, Cloneable
{
	private String name;

	private String description;

	/**
	 * Verbs using this grid.
	 */
	private Set<String> verbs;

	/**
	 * All associated roles for this grid with the flag if it is mandatory or optional.
	 * The keys are the {@link ThematicRole}s and the value describes if it is mandatory (
	 * <code>true</code>) or optional (<code>false</code>).<br/>
	 * By default, roles are mandatory!
	 * 
	 */
	private Map<ThematicRole, Boolean> roles;

	/**
	 * The main verb of the grid which generalizes all other associated verbs.
	 * 
	 * Invariant: the reference verb must be an element of the set of associated verbs!
	 */
	private String referenceVerb;

	/**
	 * Map of Thematic Grid Name > Rule: rule answers whether the corresponding role
	 * shosuld be shown or not?
	 */
	private Map<String, String> gridBasedRules;

	/**
	 * @return the roles
	 */
	public Map<ThematicRole, Boolean> getRoles()
	{
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(Map<ThematicRole, Boolean> roles)
	{
		this.roles = roles;
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

	/**
	 * @return the set of verbs
	 */
	public Set<String> getVerbs()
	{
		return verbs;
	}

	/**
	 * @param verbs
	 *            Set of verbs.
	 */
	public void setVerbs(Set<String> verbs)
	{
		this.verbs = verbs;
	}

	public String getRefernceVerb()
	{
		return referenceVerb;
	}

	public void setRefernceVerb(String refernceVerb)
	{
		this.referenceVerb = refernceVerb;
	}

	public Map<String, String> getGridBasedRules()
	{
		return gridBasedRules;
	}

	public void setGridBasedRules(Map<String, String> gridBasedRules)
	{
		this.gridBasedRules = gridBasedRules;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((gridBasedRules == null) ? 0 : gridBasedRules.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((referenceVerb == null) ? 0 : referenceVerb.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((verbs == null) ? 0 : verbs.hashCode());
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
		ThematicGrid other = (ThematicGrid) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (gridBasedRules == null)
		{
			if (other.gridBasedRules != null)
				return false;
		}
		else if (!gridBasedRules.equals(other.gridBasedRules))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (referenceVerb == null)
		{
			if (other.referenceVerb != null)
				return false;
		}
		else if (!referenceVerb.equals(other.referenceVerb))
			return false;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
			return false;
		if (verbs == null)
		{
			if (other.verbs != null)
				return false;
		}
		else if (!verbs.equals(other.verbs))
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
		builder.append("ThematicGrid [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", verbs=");
		builder.append(verbs);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", rerferenceVerb=");
		builder.append(referenceVerb);
		builder.append(", gridBasedRules=");
		builder.append(gridBasedRules);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ThematicGrid clone()
	{
		try
		{
			final ThematicGrid clone = (ThematicGrid) super.clone();

			clone.setDescription(description);
			clone.setName(name);
			clone.setRefernceVerb(referenceVerb);

			final Set<String> cloneVerbs = new HashSet<String>(verbs);
			clone.setVerbs(cloneVerbs);

			if (roles != null)
			{
				final Map<ThematicRole, Boolean> cloneRoles = new HashMap<ThematicRole, Boolean>();
				cloneRoles.putAll(roles);
				clone.setRoles(cloneRoles);
			}

			if (gridBasedRules != null)
			{
				final Map<String, String> cloneGridBasedRules = new HashMap<String, String>();
				cloneGridBasedRules.putAll(gridBasedRules);
				clone.setGridBasedRules(cloneGridBasedRules);
			}

			return clone;
		}
		catch (CloneNotSupportedException cnsEx)
		{
			throw new RuntimeException(cnsEx);
		}
	}
}
