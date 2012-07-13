package de.akra.idocit.ui.composites;

import java.util.Collections;
import java.util.Map;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicGrid;

/**
 * Selection for {@link RecommendRolesComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendRolesCompositeSelection implements ISelection
{
	private Map<String, ThematicGrid> recommendedThematicGrids = Collections.emptyMap();

	private String operationIdentifier;

	public Map<String, ThematicGrid> getRecommendedThematicGrids()
	{
		return recommendedThematicGrids;
	}

	public void setRecommendedThematicGrids(
			Map<String, ThematicGrid> recommendedThematicGrids)
	{
		this.recommendedThematicGrids = recommendedThematicGrids;
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
				+ ((recommendedThematicGrids == null) ? 0 : recommendedThematicGrids
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
		if (recommendedThematicGrids == null)
		{
			if (other.recommendedThematicGrids != null)
				return false;
		}
		else if (!recommendedThematicGrids.equals(other.recommendedThematicGrids))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "RecommendRolesCompositeSelection [recommendedThematicGrids="
				+ recommendedThematicGrids + ", operationIdentifier="
				+ operationIdentifier + "]";
	}
}
