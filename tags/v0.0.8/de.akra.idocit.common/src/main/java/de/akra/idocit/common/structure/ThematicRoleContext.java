/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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

public class ThematicRoleContext
{

	private ThematicRole role;

	private Numerus numerus;

	private boolean publicAccessibleAttributes;

	private boolean interfaceLevel;

	private String predicate;

	public ThematicRoleContext(ThematicRole role, Numerus numerus,
			boolean publicAccessibleAttributes, boolean interfaceLevel, String predicate)
	{
		if (role != null)
		{
			this.role = new ThematicRole();
			this.role.setDescription(role.getDescription());
			this.role.setName(role.getName());
		}

		this.predicate = predicate;

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
				interfaceLevel, predicate);
	}

	public boolean isInterfaceLevel()
	{
		return interfaceLevel;
	}

	public ThematicRoleContext setInterfaceLevel(boolean interfaceLevel)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel, predicate);
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
				interfaceLevel, predicate);
	}

	public Numerus getNumerus()
	{
		return numerus;
	}

	public ThematicRoleContext setNumerus(Numerus numerus)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel, predicate);
	}

	public String getPredicate()
	{
		return predicate;
	}

	public ThematicRoleContext setPredicate(String predicate)
	{
		return new ThematicRoleContext(role, numerus, publicAccessibleAttributes,
				interfaceLevel, predicate);
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
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
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
		if (predicate == null)
		{
			if (other.predicate != null)
				return false;
		}
		else if (!predicate.equals(other.predicate))
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
		builder.append(", predicate=");
		builder.append(predicate);
		builder.append("]");
		return builder.toString();
	}
}
