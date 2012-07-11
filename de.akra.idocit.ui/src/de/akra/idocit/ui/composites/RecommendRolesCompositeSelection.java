package de.akra.idocit.ui.composites;

import java.util.Map;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicRole;

public class RecommendRolesCompositeSelection implements ISelection
{
	
	private Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles;

	private String operationIdentifier;

	public Map<String, Map<ThematicRole, Boolean>> getRecommendedThematicRoles()
	{
		return recommendedThematicRoles;
	}

	public void setRecommendedThematicRoles(
			Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles)
	{
		this.recommendedThematicRoles = recommendedThematicRoles;
	}

	public String getOperationIdentifier()
	{
		return operationIdentifier;
	}

	public void setOperationIdentifier(String operationIdentifier)
	{
		this.operationIdentifier = operationIdentifier;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operationIdentifier == null) ? 0 : operationIdentifier.hashCode());
		result = prime
				* result
				+ ((recommendedThematicRoles == null) ? 0 : recommendedThematicRoles
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
		if (operationIdentifier == null)
		{
			if (other.operationIdentifier != null)
				return false;
		}
		else if (!operationIdentifier.equals(other.operationIdentifier))
			return false;
		if (recommendedThematicRoles == null)
		{
			if (other.recommendedThematicRoles != null)
				return false;
		}
		else if (!recommendedThematicRoles.equals(other.recommendedThematicRoles))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RecommendRolesCompositeSelection [recommendedThematicRoles=");
		builder.append(recommendedThematicRoles);
		builder.append(", operationIdentifier=");
		builder.append(operationIdentifier);
		builder.append("]");
		return builder.toString();
	}
}
