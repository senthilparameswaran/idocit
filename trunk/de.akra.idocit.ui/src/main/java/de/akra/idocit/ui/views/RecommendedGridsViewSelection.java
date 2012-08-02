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
package de.akra.idocit.ui.views;

import java.util.Set;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection for {@link RecommendedGridsView}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendedGridsViewSelection
{
	private String operationIdentifier;

	private Set<ThematicRole> assignedThematicRoles;

	private String referenceThematicGridName;

	public String getOperationIdentifier()
	{
		return operationIdentifier;
	}

	public void setOperationIdentifier(String operationIdentifier)
	{
		this.operationIdentifier = operationIdentifier;
	}

	public Set<ThematicRole> getAssignedThematicRoles()
	{
		return assignedThematicRoles;
	}

	public void setAssignedThematicRoles(Set<ThematicRole> assignedThematicRoles)
	{
		this.assignedThematicRoles = assignedThematicRoles;
	}

	public String getReferenceThematicGridName()
	{
		return referenceThematicGridName;
	}

	public void setReferenceThematicGridName(String referenceThematicGridName)
	{
		this.referenceThematicGridName = referenceThematicGridName;
	}
}
