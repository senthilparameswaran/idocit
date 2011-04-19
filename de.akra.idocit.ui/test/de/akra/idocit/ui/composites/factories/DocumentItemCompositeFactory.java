package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.Documentation;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.composites.DocumentItemComposite;
import de.akra.idocit.ui.composites.DocumentItemCompositeSelection;

/**
 * Factory to create a {@link DocumentItemComposite} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentItemCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection> createComposite(
			Composite pvParent)
	{
		DocumentItemComposite docItemComp = new DocumentItemComposite(pvParent, SWT.NONE);
		DocumentItemCompositeSelection selection = new DocumentItemCompositeSelection();

		List<Addressee> addresseeList = new ArrayList<Addressee>();
		// Here we have to use the same roles as returned by DocumentationTestFactory.createDocumentation().
		Addressee addresseeDeveloper = new Addressee("Developer");
		Addressee addresseeArchitect = new Addressee("Tester");
		Addressee addresseeProjectManager = new Addressee("Manager");
		
		addresseeList.add(addresseeDeveloper);
		addresseeList.add(addresseeArchitect);
		addresseeList.add(addresseeProjectManager);
		
		List<ThematicRole> thematicRoleList = new ArrayList<ThematicRole>();
		ThematicRole roleAGENT = new ThematicRole("AGENT");
		ThematicRole roleACTION = new ThematicRole("ACTION");
		
		thematicRoleList.add(roleAGENT);
		thematicRoleList.add(roleACTION);
		
		selection.setAddresseeList(addresseeList);
		selection.setThematicRoleList(thematicRoleList);

		// create documentation
		Documentation doc = DocumentationTestFactory.createDocumentation();

		// set test documentation to the composite
		selection.setDocumentation(doc);
		docItemComp.setSelection(selection);

		pvParent.pack();
		pvParent.layout();
		return docItemComp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
