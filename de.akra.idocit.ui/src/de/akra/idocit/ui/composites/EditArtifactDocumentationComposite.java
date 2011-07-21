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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.ThematicGridService;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.core.utils.ObjectStructureUtils;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * Composite that manages the Composites to edit the {@link Documentation}s of
 * {@link SignatureElement}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class EditArtifactDocumentationComposite
		extends
		AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditArtifactDocumentationCompositeSelection>
{
	private static final String GROUP_TITLE_SELECT_SIGNATURE_ELEMENT = "Select Signature Element:";

	private static final String NO_SIGNATURE_ELEMENT_SELECTED = "[No Signature Element selected]";

	private static final String OPERATION_NAME_NOT_AVAILABLE = "...";

	private static final int MIN_HEIGHT = 450;

	private static final int MIN_WIDTH = 450;

	private static final int MIN_HEIGHT_SCROLLED_COMPOSITE = 350;

	private static final int MIN_WIDTH_SCROLLED_COMPOSITE = 450;

	/**
	 * The place holder is replaced with name of the {@link SignatureElement} that is
	 * opened for documentation.
	 */
	private static final String GROUP_TITLE_DOCUMENT_SIGNATURE_ELEMENT = "Document Signature Element %s:";

	private static final String GROUP_TITLE_OVERVIEW_RECOMMENDED_ROLES = "Overview of recommended roles for %s:";

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger
			.getLogger(EditArtifactDocumentationComposite.class.getName());

	private SelectSignatureElementComposite selectSignatureElementComposite;
	private DocumentItemListComposite documentItemListComposite;
	private DisplayRecommendedRolesComposite displayRecommendedRolesComposite;

	private Group groupDocumentItemListComposite;
	private Group groupDisplayRecommendedRolesComposite;

	private ISelectionListener<SelectSignatureElementCompositeSelection> selectSignatureElementCompositeSelectionListener;
	private ISelectionListener<DocumentItemListCompositeSelection> documentItemListCompositeSelectionListener;
	private ISelectionListener<DisplayRecommendedRolesCompositeSelection> displayRecommendedRolesCompositeSelectionListener;

	/**
	 * Constructor.
	 * 
	 * @param pvParent
	 * @param pvStyle
	 * @throws CompositeInitializationException
	 */
	public EditArtifactDocumentationComposite(Composite pvParent, int pvStyle)
			throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(),
				EmptyResourceConfiguration.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).minSize(MIN_WIDTH, MIN_HEIGHT)
				.applyTo(this);

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL
				| SWT.H_SCROLL);
		GridLayoutFactory.fillDefaults().applyTo(scrolledComposite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinHeight(MIN_HEIGHT_SCROLLED_COMPOSITE);
		scrolledComposite.setMinWidth(MIN_WIDTH_SCROLLED_COMPOSITE);

		Composite scrolledRoot = new Composite(scrolledComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(scrolledRoot);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledRoot);
		scrolledComposite.setContent(scrolledRoot);

		SashForm rootForm = new SashForm(scrolledRoot, SWT.HORIZONTAL);
		GridLayoutFactory.fillDefaults().applyTo(rootForm);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(rootForm);

		SashForm leftForm = new SashForm(rootForm, SWT.VERTICAL);
		GridLayoutFactory.fillDefaults().applyTo(leftForm);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(leftForm);

		Group grpSelectSignatureElement = new Group(leftForm, SWT.NONE);
		grpSelectSignatureElement.setText(GROUP_TITLE_SELECT_SIGNATURE_ELEMENT);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(grpSelectSignatureElement);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(grpSelectSignatureElement);
		selectSignatureElementComposite = new SelectSignatureElementComposite(
				grpSelectSignatureElement, SWT.BORDER,
				new SelectSignatureElementCompositeRC());

		groupDocumentItemListComposite = new Group(rootForm, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(5, 5)
				.applyTo(groupDocumentItemListComposite);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(groupDocumentItemListComposite);

		documentItemListComposite = new DocumentItemListComposite(
				groupDocumentItemListComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(documentItemListComposite);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(documentItemListComposite);

		groupDisplayRecommendedRolesComposite = new Group(leftForm, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(5, 5)
				.applyTo(groupDisplayRecommendedRolesComposite);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(groupDisplayRecommendedRolesComposite);

		displayRecommendedRolesComposite = new DisplayRecommendedRolesComposite(
				groupDisplayRecommendedRolesComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(displayRecommendedRolesComposite);

		rootForm.setWeights(new int[] { 40, 60 });
		leftForm.setWeights(new int[] { 82, 18 });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initListener() throws CompositeInitializationException
	{
		selectSignatureElementCompositeSelectionListener = new ISelectionListener<SelectSignatureElementCompositeSelection>() {

			@Override
			public void selectionChanged(
					SelectSignatureElementCompositeSelection selection,
					PocUIComposite<SelectSignatureElementCompositeSelection> comp)
			{
				logger.log(Level.FINEST, comp.toString());

				EditArtifactDocumentationCompositeSelection editArtSelection = makeEditArtifactDocumentationCompositeSelectionFrom(getSelection());

				SignatureElement selectedSigElem = selection
						.getSelectedSignatureElement();
				editArtSelection.setSelectedSignatureElement(selectedSigElem);
				// Changes due to Issue #21
				editArtSelection.setOriginalDocumentations(selectedSigElem
						.getDocumentations());
				// End changes due to Issue #21
				setSelection(editArtSelection);

				fireChangeEvent();
			}
		};

		documentItemListCompositeSelectionListener = new ISelectionListener<DocumentItemListCompositeSelection>() {

			@Override
			public void selectionChanged(DocumentItemListCompositeSelection selection,
					PocUIComposite<DocumentItemListCompositeSelection> comp)
			{
				logger.log(Level.FINEST, comp.toString());

				EditArtifactDocumentationCompositeSelection editArtSelection = makeEditArtifactDocumentationCompositeSelectionFrom(getSelection());
				SignatureElement selectedSigElem = editArtSelection
						.getSelectedSignatureElement();
				selectedSigElem.setDocumentations(selection.getDocumentations());
				// Changes due to Issue #21
				editArtSelection.setOriginalDocumentations(selectedSigElem
						.getDocumentations());
				// End changes due to Issue #21

				// update map with new activeAddressees
				editArtSelection.putActiveAddressees(selectedSigElem.getId(),
						selection.getActiveAddressees());

				// update map with list of list of displayed Addressees
				editArtSelection.putDisplayedAddresseesForSigElemsDocumentations(
						selectedSigElem.getId(),
						selection.getDisplayedAddresseesForDocumentations());

				setSelection(editArtSelection);

				fireChangeEvent();
			}
		};

		displayRecommendedRolesCompositeSelectionListener = new ISelectionListener<DisplayRecommendedRolesCompositeSelection>() {

			@Override
			public void selectionChanged(
					DisplayRecommendedRolesCompositeSelection selection,
					PocUIComposite<DisplayRecommendedRolesCompositeSelection> comp)
			{
				SignatureElement operation = ObjectStructureUtils
						.findOperationForParameter(getSelection()
								.getSelectedSignatureElement());

				if (operation != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
				{
					// save collapsed grids, if the selected item is an element
					// of an
					// operation
					getSelection().putCollapsedThematicGrids(operation.getId(),
							selection.getCollapsedThematicGridNames());

					// no setSelection() needed, because the state does not
					// affect other
					// composites
					// fireChangeEvent();
				}
			}
		};
	}

	/**
	 * Create a new {@link EditArtifactDocumentationCompositeSelection} with the values
	 * from <code>selection</code>.
	 * 
	 * @param selection
	 *            The selection whose values should be copied.
	 * @return a new {@link EditArtifactDocumentationCompositeSelection} with the values
	 *         from <code>selection</code>.
	 */
	private EditArtifactDocumentationCompositeSelection makeEditArtifactDocumentationCompositeSelectionFrom(
			EditArtifactDocumentationCompositeSelection selection)
	{
		EditArtifactDocumentationCompositeSelection newSelection = new EditArtifactDocumentationCompositeSelection();

		newSelection.setActiveAddresseesMap(selection.getActiveAddresseesMap());
		newSelection.setAddresseeList(selection.getAddresseeList());
		newSelection.setArtifactFile(selection.getArtifactFile());
		newSelection.setInterfaceArtifact(selection.getInterfaceArtifact());
		newSelection.setSelectedSignatureElement(selection.getSelectedSignatureElement());
		newSelection.setThematicRoleList(selection.getThematicRoleList());
		newSelection.setCollapsedThematicGrids(selection.getCollapsedThematicGrids());
		newSelection.setDisplayedAddresseesForSigElemsDocumentations(selection
				.getDisplayedAddresseesForSigElemsDocumentations());

		return newSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetSelection(
			EditArtifactDocumentationCompositeSelection oldInSelection,
			EditArtifactDocumentationCompositeSelection newInSelection)
	{
		if (newInSelection != null && !newInSelection.equals(oldInSelection))
		{
			updateDocumentItemListComposite(newInSelection);
			updateSelectSignatureElementComposite(newInSelection);
			updateDisplayRecommendedRolesComposite(newInSelection);
		}
	}

	/**
	 * Update the selection for {@link DocumentItemListComposite}.
	 * 
	 * @param newInSelection
	 *            the new {@link EditArtifactDocumentationCompositeSelection}.
	 */
	private void updateDocumentItemListComposite(
			EditArtifactDocumentationCompositeSelection newInSelection)
	{
		SignatureElement selectedSigElem = newInSelection.getSelectedSignatureElement();
		DocumentItemListCompositeSelection docItemListSelection = new DocumentItemListCompositeSelection();
		docItemListSelection.setAddresseeList(newInSelection.getAddresseeList());
		docItemListSelection.setThematicRoleList(newInSelection.getThematicRoleList());
		docItemListSelection.setDocumentationAllowed(selectedSigElem != null
				&& selectedSigElem != SignatureElement.EMPTY_SIGNATURE_ELEMENT
				&& selectedSigElem != InterfaceArtifact.NOT_SUPPORTED_ARTIFACT
				&& selectedSigElem.isDocumentationAllowed());

		if (selectedSigElem != null
				&& selectedSigElem != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
		{
			logger.log(Level.FINE, selectedSigElem.getDisplayName());

			groupDocumentItemListComposite.setText(String.format(
					GROUP_TITLE_DOCUMENT_SIGNATURE_ELEMENT,
					selectedSigElem.getDisplayName()));

			docItemListSelection.setActiveAddressees(newInSelection
					.getActiveAddressees(selectedSigElem.getId()));
			docItemListSelection.setDocumentations(selectedSigElem.getDocumentations());
			docItemListSelection.setDisplayedAddresseesOfDocumentations(newInSelection
					.getDisplayedAddresseesForSigElemsDocumentations(selectedSigElem
							.getId()));

			// the path is only needed for Parameter and Parameters
			if (selectedSigElem instanceof Parameter)
			{
				String sigElemPath = ((Parameter) selectedSigElem)
						.getSignatureElementPath();
				docItemListSelection.setSignatureElementPath(sigElemPath);
			}
			else if (selectedSigElem instanceof Parameters)
			{
				String sigElemPath = selectedSigElem.getIdentifier();
				docItemListSelection.setSignatureElementPath(sigElemPath);
			}
		}
		else
		{
			groupDocumentItemListComposite.setText(String
					.format(GROUP_TITLE_DOCUMENT_SIGNATURE_ELEMENT,
							NO_SIGNATURE_ELEMENT_SELECTED));
			
			docItemListSelection.setDocumentations(new ArrayList<Documentation>());
			docItemListSelection.setActiveAddressees(new ArrayList<Integer>());
			docItemListSelection.setDisplayedAddresseesOfDocumentations(new ArrayList<List<Addressee>>());
		}

		documentItemListComposite.setSelection(docItemListSelection);
	}

	/**
	 * Update the selection for {@link SelectSignatureElementComposite}.
	 * 
	 * @param newInSelection
	 *            the new {@link EditArtifactDocumentationCompositeSelection}.
	 */
	private void updateSelectSignatureElementComposite(
			EditArtifactDocumentationCompositeSelection newInSelection)
	{
		SelectSignatureElementCompositeSelection sigElemSelection = new SelectSignatureElementCompositeSelection();
		sigElemSelection.setInterfaceArtifact(newInSelection.getInterfaceArtifact());
		sigElemSelection.setSelectedSignatureElement(newInSelection
				.getSelectedSignatureElement());

		selectSignatureElementComposite.setSelection(sigElemSelection);
	}

	/**
	 * Update the selection for {@link DisplayRecommendedRolesComposite}.
	 * 
	 * @param newInSelection
	 *            the new {@link EditArtifactDocumentationCompositeSelection}.
	 */
	private void updateDisplayRecommendedRolesComposite(
			EditArtifactDocumentationCompositeSelection newInSelection)
	{
		SignatureElement selectedSigElem = newInSelection.getSelectedSignatureElement();
		DisplayRecommendedRolesCompositeSelection recRolesCompSelection = new DisplayRecommendedRolesCompositeSelection();

		Map<String, Map<ThematicRole, Boolean>> roles = new HashMap<String, Map<ThematicRole, Boolean>>();
		Set<ThematicRole> associatedThematicRoles = Collections.emptySet();

		SignatureElement operation = ObjectStructureUtils
				.findOperationForParameter(selectedSigElem);

		// recommended roles are only provided for operations
		if (operation != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
		{
			groupDisplayRecommendedRolesComposite.setText(String.format(
					GROUP_TITLE_OVERVIEW_RECOMMENDED_ROLES, operation.getDisplayName()));

			recRolesCompSelection.setCollapsedThematicGridNames(newInSelection
					.getCollapsedThematicGrids(operation.getId()));

			// Changes due to Issue #23
			try
			{
				roles = ThematicGridService.deriveThematicGrid(operation.getIdentifier());
			}
			catch (UnitializedIDocItException unEx)
			{
				logger.log(Level.WARNING, "WSDLTaggingService is not initialized.", unEx);

				MessageBoxUtils
						.openErrorBox(
								getShell(),
								"The thematic grid deriving service is not initialized,\nplease check the configurations under \"Window\" -> \"Preferences\" -> \"iDocIt!\".");
			}
			// End changes due to Issue #23

			associatedThematicRoles = new TreeSet<ThematicRole>();
			ObjectStructureUtils.collectAssociatedThematicRoles(associatedThematicRoles,
					selectedSigElem);
		}
		else
		{
			groupDisplayRecommendedRolesComposite
					.setText(String.format(GROUP_TITLE_OVERVIEW_RECOMMENDED_ROLES,
							OPERATION_NAME_NOT_AVAILABLE));
		}

		recRolesCompSelection.setRecommendedThematicRoles(roles);
		recRolesCompSelection.setAssignedThematicRoles(associatedThematicRoles);

		displayRecommendedRolesComposite.setSelection(recRolesCompSelection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addAllListener()
	{
		selectSignatureElementComposite
				.addSelectionListener(selectSignatureElementCompositeSelectionListener);
		documentItemListComposite
				.addSelectionListener(documentItemListCompositeSelectionListener);
		displayRecommendedRolesComposite
				.addSelectionListener(displayRecommendedRolesCompositeSelectionListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeAllListener()
	{
		if (getSelection() != null)
		{
			selectSignatureElementComposite
					.removeSelectionListener(selectSignatureElementCompositeSelectionListener);
			documentItemListComposite
					.removeSelectionListener(documentItemListCompositeSelectionListener);
			displayRecommendedRolesComposite
					.removeSelectionListener(displayRecommendedRolesCompositeSelectionListener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doCleanUp()
	{
		documentItemListComposite.cleanUp();
		selectSignatureElementComposite.cleanUp();
	}

}
