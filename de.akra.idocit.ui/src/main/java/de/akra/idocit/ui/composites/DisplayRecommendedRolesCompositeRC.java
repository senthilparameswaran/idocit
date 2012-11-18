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
package de.akra.idocit.ui.composites;

import org.eclipse.swt.graphics.Image;
import org.pocui.core.resources.IResourceConfiguration;

public class DisplayRecommendedRolesCompositeRC implements IResourceConfiguration
{
	private Image roleWithoutErrorDocsWarningIcon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete()
	{
		return roleWithoutErrorDocsWarningIcon != null;
	}

	public Image getRoleWithoutErrorDocsWarningIcon()
	{
		return roleWithoutErrorDocsWarningIcon;
	}

	public void setRoleWithoutErrorDocsWarningIcon(Image roleWithoutErrorDocsWarningIcon)
	{
		this.roleWithoutErrorDocsWarningIcon = roleWithoutErrorDocsWarningIcon;
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
				+ ((roleWithoutErrorDocsWarningIcon == null) ? 0
						: roleWithoutErrorDocsWarningIcon.hashCode());
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
		DisplayRecommendedRolesCompositeRC other = (DisplayRecommendedRolesCompositeRC) obj;
		if (roleWithoutErrorDocsWarningIcon == null)
		{
			if (other.roleWithoutErrorDocsWarningIcon != null)
				return false;
		}
		else if (!roleWithoutErrorDocsWarningIcon
				.equals(other.roleWithoutErrorDocsWarningIcon))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DisplayRecommendedRolesCompositeRC [roleWithoutErrorDocsWarningIcon=");
		builder.append(roleWithoutErrorDocsWarningIcon);
		builder.append("]");
		return builder.toString();
	}
}
