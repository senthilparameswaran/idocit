/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
package de.akra.idocit.ui.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.Preconditions;

/**
 * Composite that manages a list of {@link Documentation}s in
 * {@link DocumentItemComposite}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DocumentItemListComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection>
{
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(DocumentItemListComposite.class
			.getName());

	private static final String BTN_ADD_TEXT = "Add Thematic Role documentation";

	private static final String BTN_DELETE_TEXT = "X";

	private static final int MIN_WIDTH = 340;

	private static final int DOCUMENTATION_COMPOSITE_HEIGHT = 135;

	private Composite scrolledRoot;

	private ScrolledComposite scrolledComposite;

	/**
	 * {@link List} of the {@link DocumentItemComposite}s.
	 */
	private List<DocumentItemComposite> docItemCompList;

	/**
	 * The corresponding delete {@link Button} for the {@link DocumentItemComposite}s in
	 * <code>docItemCompList</code>. It is needed to get the line number to delete the
	 * {@link DocumentItemComposite} from the list.
	 */
	private List<Button> docItemCompDeleteBtnList;

	/**
	 * {@link Button} to add new {@link DocumentItemComposite}s.
	 */
	private Button addButton;

	/**
	 * {@link ISelectionListener} for the {@link DocumentItemCompositeSelection}s.
	 */
	private ISelectionListener<DocumentItemCompositeSelection> docItemSelectionListener;

	/**
	 * {@link SelectionListener} for the delete {@link Button}s, to remove
	 * {@link DocumentItemComposite}s from the list <code>docItemCompList</code>.
	 */
	private SelectionListener docItemCompDeleteBtnSelectionListener;

	/**
	 * {@link SelectionListener} for the add {@link Button}, to add a new
	 * {@link DocumentItemComposite} to the list <code>docItemCompList</code>.
	 */
	private SelectionListener addButtonSelectionListener;

	private Listener scrolledCompositeShowListener;

	private Listener scrolledCompositeResizeListener;

	/**
	 * Constructor.
	 * 
	 * @param pvParent
	 *            The parent {@link Composite}.
	 * @param pvStyle
	 *            The style for this {@link Composite}.
	 * @throws CompositeInitializationException
	 */
	public DocumentItemListComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
		docItemCompList = Collections.emptyList();
		docItemCompDeleteBtnList = Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		addButton = new Button(this, SWT.PUSH);
		addButton.setText(BTN_ADD_TEXT);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(addButton);

		scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayoutFactory.fillDefaults().applyTo(scrolledComposite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledComposite);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		scrolledRoot = new Composite(scrolledComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(scrolledRoot);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledRoot);

		scrolledComposite.setContent(scrolledRoot);
		scrolledComposite.setMinSize(scrolledRoot.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		computeLayout();
	}

	/**
	 * Computes the layout.
	 */
	private void computeLayout()
	{
		final Rectangle clientArea = scrolledComposite.getClientArea();
		int minHeight = 0;

		if (docItemCompList != null)
		{
			minHeight += DOCUMENTATION_COMPOSITE_HEIGHT * docItemCompList.size();
		}

		final Point prefferedSize = scrolledRoot.computeSize(
				(clientArea.width >= MIN_WIDTH) ? clientArea.width : MIN_WIDTH,
				(clientArea.height >= minHeight) ? clientArea.height : minHeight);
		scrolledRoot.setSize(prefferedSize);
		scrolledComposite.setMinHeight(prefferedSize.y);
		scrolledComposite.setMinWidth(MIN_WIDTH);

		scrolledRoot.layout();
		scrolledComposite.layout();
		layout();

		scrolledRoot.redraw();
		scrolledComposite.redraw();
		redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initListener() throws CompositeInitializationException
	{
		docItemSelectionListener = new ISelectionListener<DocumentItemCompositeSelection>() {

			@Override
			public void selectionChanged(DocumentItemCompositeSelection selection,
					PocUIComposite<DocumentItemCompositeSelection> comp,
					Object sourceControl)
			{
				Documentation newDoc = selection.getDocumentation();
				List<Documentation> docs = getSelection().getDocumentations();

				// replace the old documentation
				int compIndex = docItemCompList.indexOf(comp);
				docs.set(compIndex, newDoc);

				// update the active addressee index
				getSelection().getActiveAddressees().set(compIndex,
						selection.getActiveAddressee());

				// updated displayed Addressee tabs
				getSelection().getDisplayedAddresseesForDocumentations().set(compIndex,
						selection.getDisplayedAddressees());

				fireChangeEvent(sourceControl);
			}
		};

		docItemCompDeleteBtnSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				logger.log(Level.FINE, e.widget.toString());
				docItemCompDeleteBtnClicked(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				logger.log(Level.FINE, e.widget.toString());
				docItemCompDeleteBtnClicked(e);
			}
		};

		addButtonSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				addButtonClicked();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				addButtonClicked();
			}
		};

		scrolledCompositeShowListener = new Listener() {
			public void handleEvent(final Event e)
			{
				computeLayout();
			}
		};

		scrolledCompositeResizeListener = new Listener() {
			public void handleEvent(final Event e)
			{
				computeLayout();
			}
		};
	}

	/**
	 * The <code>addbutton</code> was clicked and a new documentation should be added.
	 */
	private void addButtonClicked()
	{
		DocumentItemListCompositeSelection selection = getSelection();

		Documentation newDoc = new Documentation();
		newDoc.setSignatureElementIdentifier(selection.getSignatureElementPath());
		selection.getDocumentations().add(newDoc);
		selection.getDisplayedAddresseesForDocumentations().add(
				new ArrayList<Addressee>());

		// init it with the first tab as active.
		selection.getActiveAddressees().add(0);

		fireChangeEvent(null);
	}

	/**
	 * Remove the {@link DocumentItemComposite} associated to the clicked delete button.
	 * 
	 * @param e
	 *            The {@link SelectionEvent}.
	 * @throws IllegalArgumentException
	 */
	private void docItemCompDeleteBtnClicked(SelectionEvent e)
			throws IllegalArgumentException
	{
		int row = docItemCompDeleteBtnList.indexOf(e.widget);

		// throw exception if the delete button is not in the list.
		Preconditions.checkTrue(row >= 0,
				"The delete button has no associated documentation.");

		DocumentItemListCompositeSelection listSelection = getSelection();
		listSelection.getDocumentations().remove(row);
		listSelection.getActiveAddressees().remove(row);
		listSelection.getDisplayedAddresseesForDocumentations().remove(row);

		fireChangeEvent(e.widget);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 */
	@Override
	protected void doSetSelection(DocumentItemListCompositeSelection oldInSelection,
			DocumentItemListCompositeSelection newInSelection, Object sourceControl)
			throws IllegalArgumentException
	{
		if (newInSelection != null
				&& (!newInSelection.equals(oldInSelection) || newInSelection
						.getDocumentations().size() != docItemCompList.size()))
		{
			addButton.setEnabled(newInSelection.isDocumentationAllowed());

			List<Documentation> documentations = newInSelection.getDocumentations();

			if (documentations != null)
			{

				if (newInSelection.getActiveAddressees().size() < documentations.size())
				{
					initActiveAddressees(newInSelection.getActiveAddressees(),
							documentations.size());
				}
				else
				{
					// check length of documentations and activeAddressee list to be equal
					Preconditions
							.checkTrue(
									newInSelection.getActiveAddressees().size() <= documentations
											.size(),
									"Number of active addressees (size="
											+ newInSelection.getActiveAddressees().size()
											+ ") must not be greater than the number of existing documentations (size="
											+ documentations.size() + ").");
				}

				if (newInSelection.getDisplayedAddresseesForDocumentations().size() < documentations
						.size())
				{
					initDisplayedAddresseesForDocumentations(
							newInSelection.getDisplayedAddresseesForDocumentations(),
							documentations.size());
				}

				disposeAllComposites();

				List<Integer> activeAddressees = newInSelection.getActiveAddressees();

				docItemCompList = new ArrayList<DocumentItemComposite>(
						documentations.size());
				docItemCompDeleteBtnList = new ArrayList<Button>(documentations.size());

				// collect associated roles and used addressees
				Set<ThematicRole> associatedThematicRoles = new HashSet<ThematicRole>();
				Set<Addressee> usedAddressees = new HashSet<Addressee>();
				for (Documentation doc : documentations)
				{
					if (doc.getThematicRole() != null)
					{
						associatedThematicRoles.add(doc.getThematicRole());
					}
					usedAddressees.addAll(doc.getDocumentation().keySet());
				}

				List<Addressee> allAvailableAddressees = mergeCollections(
						newInSelection.getAddresseeList(), usedAddressees);

				List<ThematicRole> allAvailableThematicRoles = mergeCollections(
						newInSelection.getRolesRecommendations()
								.getSecondLevelRecommendations(), associatedThematicRoles);

				newInSelection.getRolesRecommendations().setSecondLevelRecommendations(
						allAvailableThematicRoles);

				for (int i = 0; i < documentations.size(); i++)
				{
					List<Addressee> displayedAddressees;
					if (newInSelection.getDisplayedAddresseesForDocumentations() == null
							|| newInSelection.getDisplayedAddresseesForDocumentations()
									.isEmpty())
					{
						displayedAddressees = new ArrayList<Addressee>();
					}
					else
					{
						displayedAddressees = newInSelection
								.getDisplayedAddresseesForDocumentations().get(i);
					}
					addNewDocumentItemComposite(documentations.get(i),
							activeAddressees.get(i), allAvailableAddressees,
							displayedAddressees, newInSelection.getRolesRecommendations());

					addNewDeleteButton();
				}
			}

			computeLayout();
		}
	}

	/**
	 * Merge all items from <code>first</code> and <code>second</code> and return a new
	 * {@link List} containing all items without duplication.
	 * 
	 * @param <T>
	 *            The type of the {@link Collection}s.
	 * @param first
	 *            The first Collection.
	 * @param second
	 *            The second Collection.
	 * @return a new {@link List} without duplicate items.
	 */
	private <T> List<T> mergeCollections(Collection<T> first, Collection<T> second)
	{
		List<T> newList = new ArrayList<T>(first);
		for (T o : second)
		{
			if (!newList.contains(o))
			{
				newList.add(o);
			}
		}
		return newList;
	}

	/**
	 * Initialize the list with the active addressee index with 0 for each documentation.
	 * 
	 * @param activeAddressees
	 *            the active addressees index list.
	 * @param numberOfDocumentations
	 *            the number of available documentations.
	 */
	private void initActiveAddressees(List<Integer> activeAddressees,
			int numberOfDocumentations)
	{
		for (int i = 0; i < numberOfDocumentations; i++)
		{
			activeAddressees.add(Integer.valueOf(-1));
		}
	}

	/**
	 * Initialize the list with the list of displayed addressees with a new List.
	 * 
	 * @param displayedAddresseesForDocumentations
	 *            The List to initialize with new lists, in the number of existing
	 *            {@link Documentation}s.
	 * @param numberOfDocumentations
	 */
	private void initDisplayedAddresseesForDocumentations(
			List<List<Addressee>> displayedAddresseesForDocumentations,
			int numberOfDocumentations)
	{
		for (int i = 0; i < numberOfDocumentations; i++)
		{
			displayedAddresseesForDocumentations.add(new ArrayList<Addressee>());
		}
	}

	/**
	 * Dispose all composites in <code>docItemCompList</code> and
	 * <code>docItemCompDeleteBtnList</code>.
	 */
	private void disposeAllComposites()
	{
		for (DocumentItemComposite docItemComp : docItemCompList)
		{
			docItemComp.dispose();
		}
		for (Button delBtn : docItemCompDeleteBtnList)
		{
			delBtn.dispose();
		}
	}

	/**
	 * Creates a {@link DocumentItemComposite}, sets the
	 * {@link DocumentItemCompositeSelection} with the given values, adds the
	 * <code>docItemSelectionListener</code> and appends the new composite to
	 * <code>docItemCompList</code>.
	 * 
	 * @param documentation
	 * @param activeAddressee
	 * @param addresseeList
	 * @param thematicRoleList
	 */
	private void addNewDocumentItemComposite(Documentation documentation,
			int activeAddressee, List<Addressee> addresseeList,
			List<Addressee> displayedAddressees, RolesRecommendations rolesRecommendations)
	{
		DocumentItemComposite docItemComp = new DocumentItemComposite(scrolledRoot,
				SWT.NONE);

		DocumentItemCompositeSelection selection = new DocumentItemCompositeSelection();
		selection.setDocumentation(documentation);
		selection.setActiveAddressee(activeAddressee);
		selection.setAddresseeList(addresseeList);
		selection.setDisplayedAddressees(displayedAddressees);
		selection.setRolesRecommendations(rolesRecommendations);

		docItemComp.setSelection(selection);
		docItemCompList.add(docItemComp);
	}

	/**
	 * Create a new delete {@link Button}, append it to
	 * <code>docItemCompDeleteBtnList</code> and add the
	 * <code>docItemCompDeleteBtnSelectionListener</code>.
	 */
	private void addNewDeleteButton()
	{
		Button delBtn = new Button(scrolledRoot, SWT.PUSH);
		delBtn.setText(BTN_DELETE_TEXT);
		docItemCompDeleteBtnList.add(delBtn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addAllListener()
	{
		for (DocumentItemComposite comp : docItemCompList)
		{
			comp.addSelectionListener(docItemSelectionListener);
		}
		for (Button btn : docItemCompDeleteBtnList)
		{
			btn.addSelectionListener(docItemCompDeleteBtnSelectionListener);
		}
		addButton.addSelectionListener(addButtonSelectionListener);

		scrolledComposite.addListener(SWT.Show, scrolledCompositeShowListener);
		scrolledComposite.addListener(SWT.Resize, scrolledCompositeResizeListener);

		scrolledRoot.addListener(SWT.Show, scrolledCompositeShowListener);
		scrolledRoot.addListener(SWT.Resize, scrolledCompositeResizeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeAllListener()
	{
		for (DocumentItemComposite comp : docItemCompList)
		{
			comp.removeSelectionListener(docItemSelectionListener);
		}
		for (Button btn : docItemCompDeleteBtnList)
		{
			btn.removeSelectionListener(docItemCompDeleteBtnSelectionListener);
		}
		addButton.removeSelectionListener(addButtonSelectionListener);

		scrolledComposite.removeListener(SWT.Show, scrolledCompositeShowListener);
		scrolledComposite.removeListener(SWT.Resize, scrolledCompositeResizeListener);

		scrolledRoot.removeListener(SWT.Show, scrolledCompositeShowListener);
		scrolledRoot.removeListener(SWT.Resize, scrolledCompositeResizeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doCleanUp()
	{
		for (DocumentItemComposite comp : docItemCompList)
		{
			comp.cleanUp();
		}
	}
}
