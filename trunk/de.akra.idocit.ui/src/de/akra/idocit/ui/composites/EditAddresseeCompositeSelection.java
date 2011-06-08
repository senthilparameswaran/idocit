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

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.DescribedItem;

/**
 * Selection / state for {@link EditAddresseeComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditAddresseeCompositeSelection implements ISelection
{
	/**
	 * The original (selected) item.
	 */
	private Addressee addressee = null;

	/**
	 * The modified item (copy of the selected item).
	 */
	private Addressee modifiedAddressee = null;
	
	/**
	 * The last cursor position in the name text field.
	 */
	private int lastCurserPosition = 0;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressee == null) ? 0 : addressee.hashCode());
		result = prime * result + lastCurserPosition;
		result = prime * result
				+ ((modifiedAddressee == null) ? 0 : modifiedAddressee.hashCode());
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
		EditAddresseeCompositeSelection other = (EditAddresseeCompositeSelection) obj;
		if (addressee == null)
		{
			if (other.addressee != null)
				return false;
		}
		else if (!addressee.equals(other.addressee))
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditAddresseeCompositeSelection [addressee=");
		builder.append(addressee);
		builder.append(", modifiedAddressee=");
		builder.append(modifiedAddressee);
		builder.append(", lastCurserPosition=");
		builder.append(lastCurserPosition);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 
	 * @return the modified {@link DescribedItem}.
	 */
	public Addressee getModifiedAddressee()
	{
		return modifiedAddressee;
	}

	/**
	 * 
	 * @param modifiedAddressee
	 *            the modified {@link Addressee}.
	 */
	public void setModifiedAddressee(Addressee modifiedAddressee)
	{
		this.modifiedAddressee = modifiedAddressee;
	}

	/**
	 * 
	 * @return the original {@link Addressee}.
	 */
	public Addressee getAddressee()
	{
		return addressee;
	}

	/**
	 * 
	 * @param addressee
	 *            the original {@link Addressee}.
	 */
	public void setAddressee(Addressee addressee)
	{
		this.addressee = addressee;
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
