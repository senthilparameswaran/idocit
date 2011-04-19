package de.akra.idocit.structure;

import java.util.Map;
import java.util.Set;

/**
 * Representation of an thematic grid.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class ThematicGrid implements DescribedItem
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((verbs == null) ? 0 : verbs.hashCode());
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
		if (!(obj instanceof ThematicGrid))
		{
			return false;
		}
		ThematicGrid other = (ThematicGrid) obj;
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (roles == null)
		{
			if (other.roles != null)
			{
				return false;
			}
		}
		else if (!roles.equals(other.roles))
		{
			return false;
		}
		if (verbs == null)
		{
			if (other.verbs != null)
			{
				return false;
			}
		}
		else if (!verbs.equals(other.verbs))
		{
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
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
		builder.append("]");
		return builder.toString();
	}
}
