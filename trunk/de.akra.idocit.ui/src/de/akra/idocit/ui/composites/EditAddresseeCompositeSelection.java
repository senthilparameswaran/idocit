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
	 * The original item.
	 */
	private Addressee addressee = null;

	/**
	 * The modified item.
	 */
	private Addressee modifiedAddressee = null;

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

	
}
