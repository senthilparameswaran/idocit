package de.akra.idocit.ui.composites.factories;

import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;

public class RecommendRolesCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>
{

	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection> createComposite(
			Composite parent, int style)
	{
		RecommendRolesComposite composite = new RecommendRolesComposite(parent, style);
		
		
		
		return composite;
	}

	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}
