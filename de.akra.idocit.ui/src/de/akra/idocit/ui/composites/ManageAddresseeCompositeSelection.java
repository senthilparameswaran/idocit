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

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.DescribedItem;

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
	/**
	 * The original selected Addressee.
	 */
	private Addressee activeAddressee = null;

	/**
	 * The modified addressee (copy of the active addressee with changes).
	 */
	private Addressee modifiedAddressee = null;

	/**
	 * All available addressees.
	 */
	private List<Addressee> addressees = null;

	/**
	 * True, if a Addressee with same name exists. If same name exists, changes can not be
	 * applied.
	 */
	private boolean nameExists = false;

	/**
	 * The last cursor position in the name text field.
	 */
	 private int lastCurserPosition = 0;

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

	/**
	 * @return the modifiedAddressee
	 */
	public Addressee getModifiedAddressee()
	{
		return modifiedAddressee;
	}

	/**
	 * @param modifiedAddressee the modifiedAddressee to set
	 */
	public void setModifiedAddressee(Addressee modifiedAddressee)
	{
		this.modifiedAddressee = modifiedAddressee;
	}

	/**
	 * @return the nameExists
	 */
	public boolean isNameExists()
	{
		return nameExists;
	}

	/**
	 * @param nameExists the nameExists to set
	 */
	public void setNameExists(boolean nameExists)
	{
		this.nameExists = nameExists;
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
		builder.append(", modifiedAddressee=");
		builder.append(modifiedAddressee);
		builder.append(", addressees=");
		builder.append(addressees);
		builder.append(", nameExists=");
		builder.append(nameExists);
		builder.append(", lastCurserPosition=");
		builder.append(lastCurserPosition);
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
		result = prime * result + lastCurserPosition;
		result = prime * result
				+ ((modifiedAddressee == null) ? 0 : modifiedAddressee.hashCode());
		result = prime * result + (nameExists ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
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
		ManageAddresseeCompositeSelection other = (ManageAddresseeCompositeSelection) obj;
		if (activeAddressee == null)
		{
			if (other.activeAddressee != null)
				return false;
		}
		else if (!activeAddressee.equals(other.activeAddressee))
			return false;
		if (addressees == null)
		{
			if (other.addressees != null)
				return false;
		}
		else if (!addressees.equals(other.addressees))
			return false;
		if (lastCurserPosition != other.lastCurserPosition)
			return false;
		if (modifiedAddressee == null)
		{
			if (other.modifiedAddressee != null)
				return false;
		}
		else if (!modifiedAddressee.equals(other.modifiedAddressee))
			return false;
		if (nameExists != other.nameExists)
			return false;
		return true;
	}

	public void setLastCurserPosition(int lastCurserPosition)
	{
		this.lastCurserPosition = lastCurserPosition;
	}

	public int getLastCurserPosition()
	{
		return lastCurserPosition;
	}

}
