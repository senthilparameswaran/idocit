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
package de.akra.idocit.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * The selection / state for a {@link DocumentItemComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DocumentItemCompositeSelection implements ISelection
{
	/**
	 * The {@link Documentation} that should be displayed.
	 */
	private Documentation documentation;

	/**
	 * Active addressee in tab control.
	 */
	private int activeAddressee = -1;

	/**
	 * List of all available addressees.
	 */
	private List<Addressee> addresseeList;

	/**
	 * List of Addressees that are displayed with tabs.
	 */
	private List<Addressee> displayedAddressees = new ArrayList<Addressee>();
	
	/**
	 * List of all available thematic roles.
	 */
	private List<ThematicRole> thematicRoleList;

	/**
	 * @param documentation
	 *            the documentation to set
	 */
	public void setDocumentation(Documentation documentation)
	{
		this.documentation = documentation;
	}

	/**
	 * @return the documentation
	 */
	public Documentation getDocumentation()
	{
		return documentation;
	}

	/**
	 * @return the activeAddressee
	 */
	public int getActiveAddressee()
	{
		return activeAddressee;
	}

	/**
	 * @param activeAddressee
	 *            the activeAddressee to set
	 */
	public void setActiveAddressee(int activeAddressee)
	{
		this.activeAddressee = activeAddressee;
	}

	/**
	 * @return the addresseeList
	 */
	public List<Addressee> getAddresseeList()
	{
		return addresseeList;
	}

	/**
	 * @param addresseeList
	 *            the addresseeList to set
	 */
	public void setAddresseeList(List<Addressee> addresseeList)
	{
		this.addresseeList = addresseeList;
	}

	/**
	 * @return the thematicRoleList
	 */
	public List<ThematicRole> getThematicRoleList()
	{
		return thematicRoleList;
	}

	/**
	 * @param thematicRoleList
	 *            the thematicRoleList to set
	 */
	public void setThematicRoleList(List<ThematicRole> thematicRoleList)
	{
		this.thematicRoleList = thematicRoleList;
	}

	/**
	 * @param displayedAddressees the displayedAddressees to set
	 */
	public void setDisplayedAddressees(List<Addressee> displayedAddressees)
	{
		this.displayedAddressees = displayedAddressees;
	}

	/**
	 * @return the displayedAddressees
	 */
	public List<Addressee> getDisplayedAddressees()
	{
		return displayedAddressees;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + activeAddressee;
		result = prime * result
				+ ((addresseeList == null) ? 0 : addresseeList.hashCode());
		result = prime * result
				+ ((displayedAddressees == null) ? 0 : displayedAddressees.hashCode());
		result = prime * result
				+ ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result
				+ ((thematicRoleList == null) ? 0 : thematicRoleList.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof DocumentItemCompositeSelection))
		{
			return false;
		}
		DocumentItemCompositeSelection other = (DocumentItemCompositeSelection) obj;
		if (activeAddressee != other.activeAddressee)
		{
			return false;
		}
		if (addresseeList == null)
		{
			if (other.addresseeList != null)
			{
				return false;
			}
		}
		else if (!addresseeList.equals(other.addresseeList))
		{
			return false;
		}
		if (displayedAddressees == null)
		{
			if (other.displayedAddressees != null)
			{
				return false;
			}
		}
		else if (!displayedAddressees.equals(other.displayedAddressees))
		{
			return false;
		}
		if (documentation == null)
		{
			if (other.documentation != null)
			{
				return false;
			}
		}
		else if (!documentation.equals(other.documentation))
		{
			return false;
		}
		if (thematicRoleList == null)
		{
			if (other.thematicRoleList != null)
			{
				return false;
			}
		}
		else if (!thematicRoleList.equals(other.thematicRoleList))
		{
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DocumentItemCompositeSelection [documentation=");
		builder.append(documentation);
		builder.append(", activeAddressee=");
		builder.append(activeAddressee);
		builder.append(", addresseeList=");
		builder.append(addresseeList);
		builder.append(", displayedAddressees=");
		builder.append(displayedAddressees);
		builder.append(", thematicRoleList=");
		builder.append(thematicRoleList);
		builder.append("]");
		return builder.toString();
	}
}
