package de.akra.idocit.core.structure;

/**
 * Represents an Addressee.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class Addressee implements DescribedItem, Comparable<Addressee>
{
	private String name = "";

	private String description = "";

	private boolean isDefault = true;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the addressee group.
	 */
	public Addressee(String name)
	{
		this.name = name;
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
	 * @param isDefault
	 *            the isDefault to set
	 */
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	/**
	 * @return the isDefault
	 */
	public boolean isDefault()
	{
		return isDefault;
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Addressee))
		{
			return false;
		}
		Addressee other = (Addressee) obj;
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
		if (isDefault != other.isDefault)
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
		builder.append("Addressee [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", isDefault=");
		builder.append(isDefault);
		builder.append("]");
		return builder.toString();
	}

	public Addressee clone()
	{
		Addressee clone = new Addressee(getName());
		clone.setDescription(getDescription());
		clone.setDefault(isDefault());
		return clone;
	}

	/**
	 * Sort by name.
	 */
	@Override
	public int compareTo(Addressee a)
	{
		if (this.equals(a))
		{
			return 0;
		}

		int nameCmp = name.compareTo(a.getName());
		if (nameCmp != 0)
		{
			return nameCmp;
		}

		return 0;
	}
}
