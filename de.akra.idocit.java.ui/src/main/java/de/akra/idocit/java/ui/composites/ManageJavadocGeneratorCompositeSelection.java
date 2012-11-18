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
package de.akra.idocit.java.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * The selection / state for a {@link ManageJavadocGeneratorComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageJavadocGeneratorCompositeSelection implements ISelection
{

	/**
	 * All available ThematicRoles.
	 */
	private List<ThematicRole> thematicRoles = null;

	/**
	 * The radio button value of the selected Javadoc type.
	 */
	private String selectedJavadocType = null;

	/**
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	public List<ThematicRole> getThematicRoles()
	{
		return thematicRoles;
	}

	/**
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s.
	 */
	public void setThematicRoles(List<ThematicRole> roles)
	{
		this.thematicRoles = roles;
	}

	/**
	 * @return the selectedJavadocType
	 */
	public String getSelectedJavadocType()
	{
		return selectedJavadocType;
	}

	/**
	 * @param selectedJavadocType
	 *            the selectedJavadocType to set
	 */
	public void setSelectedJavadocType(String selectedJavadocType)
	{
		this.selectedJavadocType = selectedJavadocType;
	}

	@Override
	public String toString()
	{
		return "ManageJavadocGeneratorCompositeSelection [thematicRoles=" + thematicRoles
				+ ", selectedJavadocType=" + selectedJavadocType + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((selectedJavadocType == null) ? 0 : selectedJavadocType.hashCode());
		result = prime * result
				+ ((thematicRoles == null) ? 0 : thematicRoles.hashCode());
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
		ManageJavadocGeneratorCompositeSelection other = (ManageJavadocGeneratorCompositeSelection) obj;
		if (selectedJavadocType == null)
		{
			if (other.selectedJavadocType != null)
				return false;
		}
		else if (!selectedJavadocType.equals(other.selectedJavadocType))
			return false;
		if (thematicRoles == null)
		{
			if (other.thematicRoles != null)
				return false;
		}
		else if (!thematicRoles.equals(other.thematicRoles))
			return false;
		return true;
	}
}
