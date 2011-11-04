package de.akra.idocit.common.structure;

public class ThematicRoleContext
{

	private ThematicRole role;

	private Numerus numerus;

	private boolean publicAccessibleAttributes;

	private boolean interfaceLevel;

	public ThematicRoleContext(ThematicRole role, Numerus numerus,
			boolean publicAccessibleAttributes, boolean interfaceLevel)
	{
		this.role = new ThematicRole();
		this.role.setDescription(role.getDescription());
		this.role.setName(role.getName());

		this.numerus = numerus;
		this.publicAccessibleAttributes = publicAccessibleAttributes;
		this.interfaceLevel = interfaceLevel;
	}

	public boolean hasPulicAccessableAttributes()
	{
		return publicAccessibleAttributes;
	}

	public ThematicRoleContext setPulicAccessableAttributes(
			boolean pulicAccessableAttributes)
	{
		return new ThematicRoleContext(role, numerus, pulicAccessableAttributes,
				interfaceLevel);
	}

	public boolean isInterfaceLevel()
	{
		return interfaceLevel;
	}

	public ThematicRoleContext setInterfaceLevel(boolean interfaceLevel)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel);
	}

	public ThematicRole getRole()
	{
		ThematicRole role = new ThematicRole();
		role.setDescription(this.role.getDescription());
		role.setName(this.role.getName());

		return role;
	}

	public ThematicRoleContext setRole(ThematicRole role)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel);
	}

	public Numerus getNumerus()
	{
		return numerus;
	}

	public ThematicRoleContext setNumerus(Numerus numerus)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (interfaceLevel ? 1231 : 1237);
		result = prime * result + ((numerus == null) ? 0 : numerus.hashCode());
		result = prime * result + (publicAccessibleAttributes ? 1231 : 1237);
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		ThematicRoleContext other = (ThematicRoleContext) obj;
		if (interfaceLevel != other.interfaceLevel)
			return false;
		if (numerus != other.numerus)
			return false;
		if (publicAccessibleAttributes != other.publicAccessibleAttributes)
			return false;
		if (role == null)
		{
			if (other.role != null)
				return false;
		}
		else if (!role.equals(other.role))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ThematicRoleContext [role=");
		builder.append(role);
		builder.append(", numerus=");
		builder.append(numerus);
		builder.append(", publicAccessibleAttributes=");
		builder.append(publicAccessibleAttributes);
		builder.append(", interfaceLevel=");
		builder.append(interfaceLevel);
		builder.append("]");
		return builder.toString();
	}
}
