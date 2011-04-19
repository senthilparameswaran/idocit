package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.ui.composites.ManageAddresseeComposite;
import de.akra.idocit.ui.composites.ManageAddresseeCompositeSelection;
import de.akra.idocit.ui.composites.ManageThematicRoleComposite;

/**
 * Factory for {@link ManageThematicRoleComposite} for managing {@link Addressee}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 */
public class ManageAddresseesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, ManageAddresseeCompositeSelection>
{

	@Override
	public ManageAddresseeComposite createComposite(Composite parent)
	{
		ManageAddresseeComposite composite = new ManageAddresseeComposite(parent);

		ManageAddresseeCompositeSelection selection = new ManageAddresseeCompositeSelection();

		List<Addressee> addressees = new ArrayList<Addressee>();
		Addressee addressee1 = new Addressee("Developer");
		addressee1.setName("Developer");
		addressee1
				.setDescription("Builder of an element, who needs the most comprehensive documentation of an interface. The builder needs to see any assertions about the interface that other stakeholders will see and perhaps depend on, so that he or she can make them true. A special kind of builder is the maintainer, who makes assigned changes to the element.");

		Addressee addressee2 = new Addressee("A Manager manages");
		addressee2.setName("Manager");

		addressees.add(addressee1);
		addressees.add(addressee2);

		selection.setAddressees(addressees);
		selection.setActiveAddressee(addressee1);

		composite.setSelection(selection);
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}

}
