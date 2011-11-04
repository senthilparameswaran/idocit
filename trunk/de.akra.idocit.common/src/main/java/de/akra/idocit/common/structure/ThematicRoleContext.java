package de.akra.idocit.common.structure;

public class ThematicRoleContext
{

	private ThematicRole role;

	private Numerus numerus;
	
	private boolean publicAccessibleAttributes;

	public ThematicRoleContext(ThematicRole role, Numerus numerus, boolean publicAccessibleAttributes)
	{
		this.role = new ThematicRole();
		this.role.setDescription(role.getDescription());
		this.role.setName(role.getName());
		this.role.setRoleBasedRule(role.getRoleBasedRule());

		this.numerus = numerus;
		this.publicAccessibleAttributes = publicAccessibleAttributes;
	}
	
	public boolean hasPulicAccessableAttributes()
	{
		return publicAccessibleAttributes;
	}

	public ThematicRoleContext setPulicAccessableAttributes(boolean pulicAccessableAttributes)
	{
		return new ThematicRoleContext(role, numerus, pulicAccessableAttributes);
	}

	public ThematicRole getRole()
	{
		ThematicRole role = new ThematicRole();
		role.setDescription(this.role.getDescription());
		role.setName(this.role.getName());
		role.setRoleBasedRule(this.role.getRoleBasedRule());

		return role;
	}

	public ThematicRoleContext setRole(ThematicRole role)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes);
	}

	public Numerus getNumerus()
	{
		return numerus;
	}

	public ThematicRoleContext setNumerus(Numerus numerus)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (publicAccessibleAttributes ? 1231 : 1237);
		result = prime * result + ((numerus == null) ? 0 : numerus.hashCode());
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
		if (publicAccessibleAttributes != other.publicAccessibleAttributes)
			return false;
		if (numerus != other.numerus)
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
		builder.append("]");
		return builder.toString();
	}
}
