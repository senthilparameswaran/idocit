package de.akra.idocit.ui.composites;

import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.core.structure.ThematicRole;

/**
 * The selection / state for a {@link ManageThematicRoleComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class ManageThematicRoleCompositeSelection implements ISelection
{
	private ThematicRole activeThematicRole = null;

	private List<ThematicRole> thematicRoles = null;

	/**
	 * 
	 * @return The active {@link ThematicRole}.
	 */
	public ThematicRole getActiveThematicRole()
	{
		return activeThematicRole;
	}

	/**
	 * 
	 * @param activeRole
	 *            The active {@link ThematicRole}.
	 */
	public void setActiveThematicRole(ThematicRole activeRole)
	{
		this.activeThematicRole = activeRole;
	}

	/**
	 * 
	 * @return List of {@link ThematicRole}s.
	 */
	public List<ThematicRole> getThematicRoles()
	{
		return thematicRoles;
	}

	/**
	 * 
	 * @param roles
	 *            The {@link ThematicRole}s.
	 */
	public void setThematicRoles(List<ThematicRole> roles)
	{
		this.thematicRoles = roles;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeThematicRole == null) ? 0 : activeThematicRole.hashCode());
		result = prime * result + ((thematicRoles == null) ? 0 : thematicRoles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManageThematicRoleCompositeSelection other = (ManageThematicRoleCompositeSelection) obj;
		if (activeThematicRole == null)
		{
			if (other.activeThematicRole != null)
				return false;
		}
		else if (!activeThematicRole.equals(other.activeThematicRole))
			return false;
		if (thematicRoles == null)
		{
			if (other.thematicRoles != null)
				return false;
		}
		else if (!thematicRoles.equals(other.thematicRoles))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ManageAddresseesCompositeSelection [activeAddressee=");
		builder.append(activeThematicRole);
		builder.append(", addressees=");
		builder.append(thematicRoles);
		builder.append("]");
		return builder.toString();
	}
}
