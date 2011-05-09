package de.akra.idocit.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.containers.workbench.AbsPreferencePage;

import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.ui.composites.ManageAddresseeComposite;
import de.akra.idocit.ui.composites.ManageAddresseeCompositeSelection;

/**
 * A {@link PreferencePage} for {@link Addressee}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class AddresseePreferencePage
		extends
		AbsPreferencePage<EmptyActionConfiguration, EmptyResourceConfiguration, ManageAddresseeCompositeSelection>
{
	@Override
	protected void initListener() throws CompositeInitializationException
	{
		// Nothing to do!
	}

	@Override
	protected void addAllListener()
	{
		// Nothing to do!
	}

	@Override
	protected void removeAllListener()
	{
		// Nothing to do!
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		IPreferenceStore prefStore = getPreferenceStore();

		if (prefStore == null)
		{
			setPreferenceStore(PlatformUI.getPreferenceStore());
			prefStore = getPreferenceStore();
		}

		loadPreferences();
	}

	private void loadPreferences()
	{
		List<Addressee> addressees = PersistenceService.loadConfiguredAddressees();

		ManageAddresseeCompositeSelection selection = new ManageAddresseeCompositeSelection();
		selection
				.setActiveAddressee(!addressees.isEmpty() ? addressees.get(0) : null);
		selection.setAddressees(addressees);

		setSelection(selection);
	}

	@Override
	protected void performDefaults()
	{
		loadPreferences();
	}

	@Override
	protected void performApply()
	{
		List<Addressee> items = new ArrayList<Addressee>();

		for (Addressee addressee : getSelection().getAddressees())
		{
			items.add((Addressee) addressee);
		}

		PersistenceService.persistAddressees(items);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		performApply();
		boolean saveState = super.performOk();
		return saveState;
	}

	@Override
	protected AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, ManageAddresseeCompositeSelection> instanciateMask(
			Composite comp)
	{
		return new ManageAddresseeComposite(comp);
	}

}
