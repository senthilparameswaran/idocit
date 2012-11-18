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
package de.akra.idocit.ui.composites;

import java.util.Set;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection for {@link RecommendRolesComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendRolesCompositeSelection implements ISelection
{
	private String operationIdentifier;

	private Set<ThematicRole> assignedThematicRoles;

	private Set<ThematicRole> assignedThematicRolesWithErrorDocs;

	private String referenceThematicGridName;

	public RecommendRolesCompositeSelection()
	{}

	public RecommendRolesCompositeSelection(final String operationIdentifier)
	{
		this(operationIdentifier, null, null, null);
	}

	public RecommendRolesCompositeSelection(final String operationIdentifier,
			final Set<ThematicRole> assignedThematicRoles,
			final String referenceThematicGridName,
			final Set<ThematicRole> assignedThematicRolesWithErrorDocs)
	{
		this.operationIdentifier = operationIdentifier;
		this.assignedThematicRoles = assignedThematicRoles;
		this.referenceThematicGridName = referenceThematicGridName;
		this.assignedThematicRolesWithErrorDocs = assignedThematicRolesWithErrorDocs;
	}

	public String getOperationIdentifier()
	{
		return operationIdentifier;
	}

	public void setOperationIdentifier(String operationIdentifier)
	{
		this.operationIdentifier = operationIdentifier;
	}

	/**
	 * @return the assignedThematicRoles
	 */
	public Set<ThematicRole> getAssignedThematicRoles()
	{
		return assignedThematicRoles;
	}

	/**
	 * @param assignedThematicRoles
	 *            the assignedThematicRoles to set
	 */
	public void setAssignedThematicRoles(Set<ThematicRole> assignedThematicRoles)
	{
		this.assignedThematicRoles = assignedThematicRoles;
	}

	/**
	 * @return the referenceThematicGridName
	 */
	public String getReferenceThematicGridName()
	{
		return referenceThematicGridName;
	}

	/**
	 * @param referenceThematicGridName
	 *            the referenceThematicGridName to set
	 */
	public void setReferenceThematicGridName(String referenceThematicGridName)
	{
		this.referenceThematicGridName = referenceThematicGridName;
	}

	public Set<ThematicRole> getAssignedThematicRolesWithErrorDocs()
	{
		return assignedThematicRolesWithErrorDocs;
	}

	public void setAssignedThematicRolesWithErrorDocs(
			Set<ThematicRole> assignedThematicRolesWithErrorDocs)
	{
		this.assignedThematicRolesWithErrorDocs = assignedThematicRolesWithErrorDocs;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((assignedThematicRoles == null) ? 0 : assignedThematicRoles.hashCode());
		result = prime
				* result
				+ ((assignedThematicRolesWithErrorDocs == null) ? 0
						: assignedThematicRolesWithErrorDocs.hashCode());
		result = prime * result
				+ ((operationIdentifier == null) ? 0 : operationIdentifier.hashCode());
		result = prime
				* result
				+ ((referenceThematicGridName == null) ? 0 : referenceThematicGridName
						.hashCode());
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
		RecommendRolesCompositeSelection other = (RecommendRolesCompositeSelection) obj;
		if (assignedThematicRoles == null)
		{
			if (other.assignedThematicRoles != null)
				return false;
		}
		else if (!assignedThematicRoles.equals(other.assignedThematicRoles))
			return false;
		if (assignedThematicRolesWithErrorDocs == null)
		{
			if (other.assignedThematicRolesWithErrorDocs != null)
				return false;
		}
		else if (!assignedThematicRolesWithErrorDocs
				.equals(other.assignedThematicRolesWithErrorDocs))
			return false;
		if (operationIdentifier == null)
		{
			if (other.operationIdentifier != null)
				return false;
		}
		else if (!operationIdentifier.equals(other.operationIdentifier))
			return false;
		if (referenceThematicGridName == null)
		{
			if (other.referenceThematicGridName != null)
				return false;
		}
		else if (!referenceThematicGridName.equals(other.referenceThematicGridName))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RecommendRolesCompositeSelection [operationIdentifier=");
		builder.append(operationIdentifier);
		builder.append(", assignedThematicRoles=");
		builder.append(assignedThematicRoles);
		builder.append(", assignedThematicRolesWithErrorDocs=");
		builder.append(assignedThematicRolesWithErrorDocs);
		builder.append(", referenceThematicGridName=");
		builder.append(referenceThematicGridName);
		builder.append("]");
		return builder.toString();
	}
}
