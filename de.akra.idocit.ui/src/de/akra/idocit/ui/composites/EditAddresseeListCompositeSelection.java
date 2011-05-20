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

/**
 * Selection / state for {@link EditAddresseeListComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class EditAddresseeListCompositeSelection implements ISelection
{
	private List<Addressee> activeAddressees = null;

	private List<Addressee> addressees = null;

	private int minNumberOfItems = 0;

	/**
	 * 
	 * @return the List of active {@link Addressee}s.
	 */
	public List<Addressee> getActiveAddressees()
	{
		return activeAddressees;
	}

	/**
	 * 
	 * @param activeAddressees
	 *            List of active {@link Addressee}s.
	 */
	public void setActiveAddressees(List<Addressee> activeAddressees)
	{
		this.activeAddressees = activeAddressees;
	}

	/**
	 * 
	 * @return the List of all {@link Addressee}s.
	 */
	public List<Addressee> getAddressees()
	{
		return addressees;
	}

	/**
	 * 
	 * @param addressees
	 *            List of all {@link Addressee}s.
	 */
	public void setAddressees(List<Addressee> addressees)
	{
		this.addressees = addressees;
	}

	/**
	 * 
	 * @return the minimum number of items in a list.
	 */
	public int getMinNumberOfItems()
	{
		return minNumberOfItems;
	}

	/**
	 * 
	 * @param minNumberOfItems
	 *            The minimum number of items in a list.
	 */
	public void setMinNumberOfItems(int minNumberOfItems)
	{
		this.minNumberOfItems = minNumberOfItems;
	}
}
