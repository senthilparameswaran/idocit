/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

import java.util.Collections;
import java.util.List;

public final class RolesRecommendations
{

	private final List<ThematicRole> firstLevelRecommendations;
	private final List<ThematicRole> secondLevelRecommendations;
	
	public RolesRecommendations(final List<ThematicRole> firstLevelRecommendations,
			final List<ThematicRole> secondLevelRecommendations)
	{
		this.firstLevelRecommendations = firstLevelRecommendations;
		this.secondLevelRecommendations = secondLevelRecommendations;
	}
	
	public List<ThematicRole> getFirstLevelRecommendations()
	{
		return Collections.unmodifiableList(this.firstLevelRecommendations);
	}

	public RolesRecommendations setFirstLevelRecommendations(List<ThematicRole> firstLevelRecommendations)
	{
		return new RolesRecommendations(firstLevelRecommendations, this.secondLevelRecommendations);
	}
	
	public List<ThematicRole> getSecondLevelRecommendations()
	{
		return Collections.unmodifiableList(this.secondLevelRecommendations);
	}

	public RolesRecommendations setSecondLevelRecommendations(List<ThematicRole> secondLevelRecommendations)
	{
		return new RolesRecommendations(this.firstLevelRecommendations, secondLevelRecommendations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((firstLevelRecommendations == null) ? 0 : firstLevelRecommendations
						.hashCode());
		result = prime
				* result
				+ ((secondLevelRecommendations == null) ? 0 : secondLevelRecommendations
						.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RolesRecommendations other = (RolesRecommendations) obj;
		if (firstLevelRecommendations == null)
		{
			if (other.firstLevelRecommendations != null)
				return false;
		}
		else if (!firstLevelRecommendations.equals(other.firstLevelRecommendations))
			return false;
		if (secondLevelRecommendations == null)
		{
			if (other.secondLevelRecommendations != null)
				return false;
		}
		else if (!secondLevelRecommendations.equals(other.secondLevelRecommendations))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "RolesRecommendations [firstLevelRecommendations="
				+ firstLevelRecommendations + ", secondLevelRecommendations="
				+ secondLevelRecommendations + "]";
	}
}
