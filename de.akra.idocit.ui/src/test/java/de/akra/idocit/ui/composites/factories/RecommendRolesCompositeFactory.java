package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;
import de.akra.idocit.ui.services.CompositeTestPersistenceService;

public class RecommendRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection> createComposite(
			Composite parent, int style)
	{
		ServiceManager.getInstance().setPersistenceService(
				new CompositeTestPersistenceService());

		final RecommendRolesComposite composite = new RecommendRolesComposite(parent,
				style);
		composite.setSelection(new RecommendRolesCompositeSelection());
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
