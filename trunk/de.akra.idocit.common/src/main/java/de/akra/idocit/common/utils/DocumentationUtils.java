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
package de.akra.idocit.common.utils;

import java.util.Collection;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;

public final class DocumentationUtils
{
	/**
	 * Returns the {@link Documentation} with the {@link ThematicRole} having
	 * the given rolename.
	 * 
	 * Please note:
	 *  <ul>
	 *  	<li>If the rolename or the documentations are <code>null</code>, 
	 *  	the result will be also <code>null</code>.</li>
	 *  	<li>If no documentation is found, <code>null</code> is returned.</li>
	 *  	<li>If the rolename appears more than one time in the collection, 
	 *  	the first found documentation is returned.</li>
	 *  </ul>
	 * 
	 * @param rolename [COMPARISON] Nullable
	 * @param documentations [SOURCE] Nullable
	 * @return [OBJECT] Nullable
	 */
	public static Documentation findDocumentationByRoleName(String rolename,
			Collection<Documentation> documentations)
	{
		if ((rolename != null) && (documentations != null))
		{
			for (Documentation documentation : documentations)
			{
				ThematicRole role = documentation.getThematicRole();

				if (role != null)
				{
					String name = role.getName();
					if ((name != null) && name.equals(rolename))
					{
						return documentation;
					}
				}
			}
		}

		return null;
	}
}
