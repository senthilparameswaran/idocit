package de.akra.idocit.ui.composites;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.services.ThematicGridService;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.services.impl.ServiceManager;

public class RecommendRolesComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>
{

	public RecommendRolesComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	private Text txtOperationIdentifier;
	private Button btnFindThematicGrid;
	private DisplayRecommendedRolesComposite displayRecommendedRolesComposite;

	private SelectionListener findThematicGridButtonListener;

	@Override
	protected void addAllListener()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void doCleanUp()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void doSetSelection(RecommendRolesCompositeSelection arg0,
			RecommendRolesCompositeSelection arg1, Object arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void initGUI(Composite arg0) throws CompositeInitializationException
	{
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);

		Group grpQuery = new Group(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grpQuery);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(grpQuery);
		grpQuery.setText("Enter Identifier:");

		txtOperationIdentifier = new Text(grpQuery, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtOperationIdentifier);
		btnFindThematicGrid = new Button(grpQuery, SWT.PUSH);
		btnFindThematicGrid.setText("Find matching grids");

		Group grpThematicGrids = new Group(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grpThematicGrids);
		GridLayoutFactory.fillDefaults().applyTo(grpThematicGrids);

		displayRecommendedRolesComposite = new DisplayRecommendedRolesComposite(
				grpThematicGrids, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(displayRecommendedRolesComposite);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		findThematicGridButtonListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				RecommendRolesCompositeSelection selection = getSelection();
				String identifier = selection.getOperationIdentifier();

				if ((identifier != null) && !identifier.isEmpty())
				{
					PersistenceService persistenceService = ServiceManager.getInstance().getPersistenceService();
					// TODO: Hier weitermachen
					//List<ThematicGrid> thematicGrids = persistenceService.loadThematicGrids(); 
					
					//Map<String,ThematicGridService.deriveThematicGrid(identifier, thematicGrids);
				}
				else
				{
					selection
							.setRecommendedThematicRoles(new HashMap<String, Map<ThematicRole, Boolean>>());
				}

				setSelection(selection);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};
	}

	@Override
	protected void removeAllListener()
	{
		// TODO Auto-generated method stub

	}

}
