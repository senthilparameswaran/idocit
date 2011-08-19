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

/**
 * A DescribedItem is an item that has a name and a description.
 * 
 * @author Dirk Meier-Eickchoff
 * 
 */
public interface DescribedItem
{
	/**
	 * 
	 * @return the description for this item.
	 */
	public String getDescription();

	/**
	 * 
	 * @param description
	 *            The description for this item.
	 */
	public void setDescription(String description);

	/**
	 * 
	 * @return the name for this item.
	 */
	public String getName();

	/**
	 * 
	 * @param name
	 *            The name for this item.
	 */
	public void setName(String name);
}
