/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.DescribedItem;

/**
 * The selection / state for a {@link ManageAddresseeComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageAddresseeCompositeSelection implements ISelection
{
	private Addressee activeAddressee = null;

	private List<Addressee> addressees = null;

	/**
	 * 
	 * @return The active {@link DescribedItem}.
	 */
	public Addressee getActiveAddressee()
	{
		return activeAddressee;
	}

	/**
	 * 
	 * @param activeAddressee
	 *            The active {@link Addressee}.
	 */
	public void setActiveAddressee(Addressee activeAddressee)
	{
		this.activeAddressee = activeAddressee;
	}

	/**
	 * 
	 * @return List of {@link Addressee}s.
	 */
	public List<Addressee> getAddressees()
	{
		return addressees;
	}

	/**
	 * 
	 * @param addressees
	 *            The {@link Addressee}s.
	 */
	public void setAddressees(List<Addressee> addressees)
	{
		this.addressees = addressees;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ManageAddresseeCompositeSelection [activeAddressee=");
		builder.append(activeAddressee);
		builder.append(", addressees=");
		builder.append(addressees);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeAddressee == null) ? 0 : activeAddressee.hashCode());
		result = prime * result + ((addressees == null) ? 0 : addressees.hashCode());
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
		if (!(obj instanceof ManageAddresseeCompositeSelection))
		{
			return false;
		}
		ManageAddresseeCompositeSelection other = (ManageAddresseeCompositeSelection) obj;
		if (activeAddressee == null)
		{
			if (other.activeAddressee != null)
			{
				return false;
			}
		}
		else if (!activeAddressee.equals(other.activeAddressee))
		{
			return false;
		}
		if (addressees == null)
		{
			if (other.addressees != null)
			{
				return false;
			}
		}
		else if (!addressees.equals(other.addressees))
		{
			return false;
		}
		return true;
	}

	
}
