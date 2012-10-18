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
