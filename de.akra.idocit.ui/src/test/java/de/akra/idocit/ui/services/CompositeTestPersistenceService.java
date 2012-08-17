/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.ui.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.services.PersistenceService;

public class CompositeTestPersistenceService implements PersistenceService
{

	@Override
	public void init()
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public InterfaceArtifact loadInterface(IFile iFile) throws Exception
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void writeInterface(InterfaceArtifact interfaceArtifact, IFile iFile)
			throws Exception
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean areAddresseesInitialized()
	{
		return true;
	}

	@Override
	public boolean areThematicRolesInitialized()
	{
		return true;
	}

	@Override
	public void persistAddressees(List<Addressee> addressees)
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void persistThematicRoles(List<ThematicRole> roles)
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<ThematicRole> readInitialThematicRoles()
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<Addressee> readInitialAddressees()
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<Addressee> loadConfiguredAddressees()
	{
		Addressee addressee0 = new Addressee("Developer");
		Addressee addressee1 = new Addressee("Tester");
		Addressee addressee2 = new Addressee("Manager");

		List<Addressee> addressees = new ArrayList<Addressee>();
		addressees.add(addressee0);
		addressees.add(addressee1);
		addressees.add(addressee2);

		return addressees;
	}

	@Override
	public List<ThematicRole> loadThematicRoles()
	{
		ThematicRole role0 = new ThematicRole("ACTION");
		ThematicRole role1 = new ThematicRole("AGENT");
		ThematicRole role2 = new ThematicRole("OBJECT");
		ThematicRole role3 = new ThematicRole("COMPARISON");

		List<ThematicRole> roles = new ArrayList<ThematicRole>();
		roles.add(role0);
		roles.add(role1);
		roles.add(role2);
		roles.add(role3);

		return roles;
	}

	@Override
	public List<ThematicGrid> loadThematicGrids() throws UnitializedIDocItException
	{
		ThematicRole role0 = new ThematicRole("ACTION");
		ThematicRole role1 = new ThematicRole("AGENT");
		ThematicRole role2 = new ThematicRole("DESTINATION");

		Map<String, String> gridBasedRules = new HashMap<String, String>();
		gridBasedRules.put("ACTION", "always();");
		gridBasedRules.put("AGENT", "always();");
		gridBasedRules.put("DESTINATION", "always();");
		
		List<ThematicGrid> grids = new ArrayList<ThematicGrid>();
		{
			ThematicGrid grid0 = new ThematicGrid();
			grid0.setName("Searching Operations");
			grid0.setRefernceVerb("find");
			grid0.setGridBasedRules(gridBasedRules);

			Set<String> grid0Verbs = new HashSet<String>();
			grid0Verbs.add("find");
			grid0Verbs.add("search");
			grid0Verbs.add("get");
			grid0.setVerbs(grid0Verbs);

			Map<ThematicRole, Boolean> grid0Roles = new HashMap<ThematicRole, Boolean>();
			grid0Roles.put(role0, Boolean.TRUE);
			grid0Roles.put(role1, Boolean.TRUE);
			grid0Roles.put(role2, Boolean.FALSE);
			grid0.setRoles(grid0Roles);

			grids.add(grid0);
		}
		{
			ThematicGrid grid1 = new ThematicGrid();
			grid1.setName("Converting Operations");
			grid1.setRefernceVerb("convert");

			Set<String> grid0Verbs = new HashSet<String>();
			grid0Verbs.add("duplicate");
			grid0Verbs.add("copy");
			grid0Verbs.add("convert");
			grid0Verbs.add("get");
			grid1.setVerbs(grid0Verbs);

			Map<ThematicRole, Boolean> grid0Roles = new HashMap<ThematicRole, Boolean>();
			grid0Roles.put(role0, Boolean.TRUE);
			grid0Roles.put(role1, Boolean.TRUE);
			grid0Roles.put(role2, Boolean.FALSE);
			grid1.setRoles(grid0Roles);
			grid1.setGridBasedRules(gridBasedRules);
			
			grids.add(grid1);
		}

		return grids;
	}

	@Override
	public void persistThematicGrids(List<ThematicGrid> verbClassRoleAssociations)
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void exportThematicGridsAsXml(File destination, List<ThematicGrid> grids)
			throws IOException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<ThematicGrid> importThematicGrids(File source) throws IOException
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void exportThematicGridsAsHtml(File destination, List<ThematicGrid> grids)
			throws IOException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public long getLastSaveTimeOfThematicGrids()
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public long getLastSaveTimeOfThematicRoles()
	{
		return new Date().getTime();
	}

	@Override
	public long getLastSaveTimeOfAddressees()
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public ValidationReport validateInterfaceArtifact(InterfaceArtifact artifact,
			IFile ifile) throws Exception
	{
		throw new RuntimeException("Not implemented!");
	}
}
