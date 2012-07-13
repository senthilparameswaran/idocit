package de.akra.idocit.ui.composites;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.services.ThematicGridService;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.impl.ServiceManager;

/**
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendRolesComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>
{
	private static Logger logger = Logger.getLogger(RecommendRolesComposite.class
			.getName());

	private static final Map<String, ThematicGrid> NO_RECOMMANDATIONS_FOUND;
	private static final String TXT_LABEL_TEXT = "Enter Identifier";

	static
	{
		NO_RECOMMANDATIONS_FOUND = new HashMap<String, ThematicGrid>();
		ThematicGrid grid = new ThematicGrid();
		grid.setName("No recommendations found");
		grid.setRoles(Collections.<ThematicRole, Boolean> emptyMap());
		NO_RECOMMANDATIONS_FOUND.put(grid.getName(), grid);
	}

	private Color defaultForeground;
	private Color grayedForeground;
	private Font initializationFont = null;

	/*
	 * Widgets
	 */
	private Composite intializationMessageComposite;
	private Text txtOperationIdentifier;
	private Button btnFindThematicGrid;
	private DisplayRecommendedRolesComposite displayRecommendedRolesComposite;

	/*
	 * Layouts
	 */
	private StackLayout contentCompositeLayout;

	/*
	 * Listener
	 */
	private SelectionListener findThematicGridButtonListener;
	private FocusListener txtOperationIdentifierListener;
	private KeyListener txtOperationIdentifierKeyListener;

	public RecommendRolesComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	@Override
	protected void initGUI(Composite arg0) throws CompositeInitializationException
	{
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);

		txtOperationIdentifier = new Text(this, SWT.BORDER);
		this.defaultForeground = txtOperationIdentifier.getForeground();
		this.grayedForeground = this.getDisplay().getSystemColor(SWT.COLOR_GRAY);

		txtOperationIdentifier.setText(TXT_LABEL_TEXT);
		txtOperationIdentifier.setForeground(grayedForeground);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
				.applyTo(txtOperationIdentifier);

		btnFindThematicGrid = new Button(this, SWT.PUSH);
		btnFindThematicGrid.setText("Find matching grids");

		initializationFont = new Font(Display.getDefault(), new FontData("Arial", 12,
				SWT.BOLD));

		this.contentCompositeLayout = new StackLayout();

		final Composite contentComp = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(contentComp);
		contentComp.setLayout(this.contentCompositeLayout);

		createInitializingComposite(contentComp);
		createDisplayRecommendedRolesComposite(contentComp);

		this.contentCompositeLayout.topControl = this.displayRecommendedRolesComposite;
		this.displayRecommendedRolesComposite.layout();
	}

	private void createDisplayRecommendedRolesComposite(final Composite parent)
	{
		this.displayRecommendedRolesComposite = new DisplayRecommendedRolesComposite(
				parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
				.applyTo(this.displayRecommendedRolesComposite);
	}

	private void createInitializingComposite(final Composite parent)
	{
		this.intializationMessageComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(intializationMessageComposite);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
				.applyTo(intializationMessageComposite);

		intializationMessageComposite.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		createInitializingLabel(intializationMessageComposite);
	}

	/**
	 * Returns a label with an initialization-message
	 * 
	 * @param parent
	 *            The parent composite
	 * @return The message-label
	 */
	private Label createInitializingLabel(final Composite parent)
	{
		final Label initializingMessageLabel = new Label(parent, SWT.WRAP);
		initializingMessageLabel.setFont(initializationFont);
		initializingMessageLabel
				.setText("iDocIt! is initializing at the moment.\n\nPlease try again ...");
		initializingMessageLabel.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true)
				.applyTo(initializingMessageLabel);

		return initializingMessageLabel;
	}

	@Override
	protected void doSetSelection(RecommendRolesCompositeSelection oldInSelection,
			RecommendRolesCompositeSelection newInSelection, Object sourceControl)
	{
		if (newInSelection != null && !newInSelection.equals(oldInSelection))
		{
			final DisplayRecommendedRolesCompositeSelection dispRolesCompSelection = new DisplayRecommendedRolesCompositeSelection(
					newInSelection.getRecommendedThematicGrids(),
					Collections.<ThematicRole> emptySet(), null,
					Collections.<String> emptySet());
			displayRecommendedRolesComposite.setSelection(dispRolesCompSelection);
		}
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		this.findThematicGridButtonListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				RecommendRolesCompositeSelection selection = copySelection(getSelection());
				final String identifier = txtOperationIdentifier.getText();
				selection.setOperationIdentifier(identifier);

				if (!StringUtils.isBlank(identifier)
						&& !TXT_LABEL_TEXT.equals(identifier))
				{
					try
					{
						final List<ThematicGrid> allThematicGrids = ServiceManager
								.getInstance().getPersistenceService()
								.loadThematicGrids();
						Map<String, ThematicGrid> deriveThematicGrid = ThematicGridService
								.deriveThematicGrid(identifier, allThematicGrids);

						if (deriveThematicGrid.isEmpty())
						{
							deriveThematicGrid = NO_RECOMMANDATIONS_FOUND;
						}
						selection.setRecommendedThematicGrids(deriveThematicGrid);

						contentCompositeLayout.topControl = displayRecommendedRolesComposite;
						displayRecommendedRolesComposite.getParent().layout();
					}
					catch (UnitializedIDocItException e1)
					{
						logger.log(Level.SEVERE, e1.toString());
						contentCompositeLayout.topControl = intializationMessageComposite;
						intializationMessageComposite.getParent().layout();
					}
				}
				else
				{
					selection.setRecommendedThematicGrids(Collections
							.<String, ThematicGrid> emptyMap());
				}

				setSelection(selection);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};

		this.txtOperationIdentifierListener = new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e)
			{
				final Text txt = (Text) e.widget;
				if (txt.getText() == null || txt.getText().length() == 0)
				{
					txt.setText(TXT_LABEL_TEXT);
					txt.setForeground(grayedForeground);
				}
			}

			@Override
			public void focusGained(final FocusEvent e)
			{
				final Text txt = (Text) e.widget;
				if (grayedForeground.equals(txt.getForeground()))
				{
					txt.setText(StringUtils.EMPTY);
					txt.setForeground(defaultForeground);
				}
			}
		};

		this.txtOperationIdentifierKeyListener = new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e)
			{
				// check if enter key where pressed
				if (e.keyCode == SWT.CR)
				{
					btnFindThematicGrid.notifyListeners(SWT.Selection, new Event());
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				// nothing to do
			}
		};
	}

	private RecommendRolesCompositeSelection copySelection(
			final RecommendRolesCompositeSelection selection)
	{
		final RecommendRolesCompositeSelection newSelection = new RecommendRolesCompositeSelection();
		newSelection.setOperationIdentifier(selection.getOperationIdentifier());
		newSelection.setRecommendedThematicGrids(selection.getRecommendedThematicGrids());
		return newSelection;
	}

	@Override
	protected void removeAllListener()
	{
		this.btnFindThematicGrid.removeSelectionListener(findThematicGridButtonListener);
		this.txtOperationIdentifier.removeFocusListener(txtOperationIdentifierListener);
		this.txtOperationIdentifier.removeKeyListener(txtOperationIdentifierKeyListener);
	}

	@Override
	protected void addAllListener()
	{
		this.btnFindThematicGrid.addSelectionListener(findThematicGridButtonListener);
		this.txtOperationIdentifier.addFocusListener(txtOperationIdentifierListener);
		this.txtOperationIdentifier.addKeyListener(txtOperationIdentifierKeyListener);
	}

	@Override
	protected void doCleanUp()
	{
		if (initializationFont != null)
		{
			initializationFont.dispose();
		}
	}
}
