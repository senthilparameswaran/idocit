/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package de.akra.idocit.ui.components;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.core.composites.ISelectionListener;
import org.pocui.core.composites.PocUIComposite;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.containers.workbench.AbsEditorPart;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.listeners.IDocItInitializationListener;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.composites.EditArtifactDocumentationComposite;
import de.akra.idocit.ui.composites.EditArtifactDocumentationCompositeSelection;
import de.akra.idocit.ui.constants.DialogConstants;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * An {@link EditorPart} to document software-interfaces by using thematic grids and
 * concerning different groups of adressees.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DocumentationEditor
		extends
		AbsEditorPart<EmptyActionConfiguration, EmptyResourceConfiguration, EditArtifactDocumentationCompositeSelection>
{
	private static final String ERR_FILE_CAN_NOT_BE_SAVED = "File can not be saved";

	private static final String ERR_FILE_NOT_SUPPORTED = "File is not supported.";

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(DocumentationEditor.class
			.getName());

	/**
	 * Configuration Listener
	 */
	private final class DocumentationEditorConfigListener implements
			IDocItInitializationListener
	{

		@Override
		public void initializationStarted()
		{
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run()
				{
					if ((rootComposite != null) && !rootComposite.isDisposed())
					{
						editorParentLayout.topControl = intializationMessageComposite;
						rootComposite.getParent().layout();
					}
				}
			});
		}

		@Override
		public void initializationFinished()
		{
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						if ((rootComposite != null) && !rootComposite.isDisposed())
						{
							editorParentLayout.topControl = rootComposite;

							init(getEditorSite(), getEditorInput());

							rootComposite.getParent().layout();
						}
					}
					catch (PartInitException e)
					{
						logger.log(
								Level.SEVERE,
								"The iDocIt! editor could not be initialized du to the following exception:",
								e);
					}
				}
			});
		}

	}

	// The root composites
	private EditArtifactDocumentationComposite rootComposite = null;

	private Composite intializationMessageComposite = null;

	// Resources
	private StackLayout editorParentLayout = null;

	private InterfaceArtifact initialInterfaceArtifact = null;

	private DocumentationEditorConfigListener listener = new DocumentationEditorConfigListener();

	private Font initializationFont = null;

	// Listeners
	private ISelectionListener<EditArtifactDocumentationCompositeSelection> rootCompositeSelectionListener = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		IFile interfaceFile = getMask().getSelection().getArtifactFile();
		InterfaceArtifact artifact = getMask().getSelection().getInterfaceArtifact();

		String type = interfaceFile.getFileExtension();

		if ((type != null))
		{
			try
			{
				ValidationReport report = ServiceManager.getInstance()
						.getPersistenceService()
						.validateInterfaceArtifact(artifact, interfaceFile);

				switch (report.getReturnCode())
				{
				case OK:
				{
					ServiceManager.getInstance().getPersistenceService()
							.writeInterface(artifact, interfaceFile);

					initialInterfaceArtifact = (InterfaceArtifact) artifact
							.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

					getMask().getSelection().resetOriginalDocumentations();
					firePropertyChange(PROP_DIRTY);
					break;
				}
				case ERROR:
				{
					MessageDialog.openError(getSite().getShell(),
							DialogConstants.DIALOG_TITLE, report.getMessage());
					break;
				}
				case WARNING:
				{
					MessageDialog.openWarning(getSite().getShell(),
							DialogConstants.DIALOG_TITLE, report.getMessage());
					break;
				}
				default:
					throw new RuntimeException("The validation code "
							+ report.getReturnCode() + " is unknown.");
				}
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, e.getMessage(), e);
				MessageDialog.openError(getSite().getShell(),
						DialogConstants.DIALOG_TITLE, ERR_FILE_CAN_NOT_BE_SAVED + ":\n"
								+ e.getMessage());
			}
		}
		else
		{
			logger.log(Level.SEVERE, ERR_FILE_NOT_SUPPORTED);
			MessageDialog.openError(getSite().getShell(), DialogConstants.DIALOG_TITLE,
					ERR_FILE_NOT_SUPPORTED);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);

		if (IDocItActivator.isInitializedAtStartup())
		{
			List<Addressee> addressees = ServiceManager.getInstance()
					.getPersistenceService().loadConfiguredAddressees();
			List<ThematicRole> thematicRoles = ServiceManager.getInstance()
					.getPersistenceService().loadThematicRoles();

			EditArtifactDocumentationCompositeSelection selection = new EditArtifactDocumentationCompositeSelection();
			selection
					.setSelectedSignatureElement(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			selection.setInterfaceArtifact(InterfaceArtifact.NOT_SUPPORTED_ARTIFACT);
			selection.setAddresseeList(addressees);
			selection.setThematicRoleList(thematicRoles);

			if (input instanceof FileEditorInput)
			{
				// Get the interface as file ...
				IFile interfaceIFile = ((FileEditorInput) input).getFile();
				File interfaceFile = interfaceIFile.getLocation().toFile();

				setSelection(null);

				if (interfaceFile.exists())
				{
					// ... and load it.
					try
					{
						logger.log(Level.INFO, "Start parsing");
						InterfaceArtifact interfaceArtifact = ServiceManager
								.getInstance().getPersistenceService()
								.loadInterface(interfaceIFile);
						selection.setInterfaceArtifact(interfaceArtifact);
						selection.setArtifactFile(interfaceIFile);
						logger.log(Level.INFO, "End parsing");
						logger.log(Level.INFO, "InterfaceArtifact.size="
								+ interfaceArtifact.size());

						logger.log(Level.INFO, "Start copy");
						initialInterfaceArtifact = (InterfaceArtifact) interfaceArtifact
								.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
						logger.log(Level.INFO, "End copy");

						setPartName(interfaceIFile.getName() + " - "
								+ DialogConstants.DIALOG_TITLE);

						if (!interfaceFile.canWrite())
						{
							MessageDialog
									.openInformation(site.getShell(),
											DialogConstants.DIALOG_TITLE,
											"The file is not writable, changes can not be saved.");
						}
					}
					catch (Exception ex)
					{
						String msg = "Could not parse the interface "
								+ interfaceIFile.getFullPath();
						logger.log(Level.SEVERE, msg, ex);
						throw new PartInitException(msg, ex);
					}
				}
				else
				{
					String msg = "File is no longer available: "
							+ interfaceFile.getAbsolutePath();
					logger.log(Level.WARNING, msg);
					MessageBoxUtils.openErrorBox(site.getShell(), msg);
				}
			}
			else
			{
				logger.log(Level.SEVERE, "Not an instance of FileEditorInput.");
			}

			logger.log(Level.INFO, "Start setSelection");
			setSelection(selection);
			logger.log(Level.INFO, "End setSelection");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty()
	{
		return (initialInterfaceArtifact != null)
				&& (getSelection() != null)
				&& !initialInterfaceArtifact
						.equals(getSelection().getInterfaceArtifact());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus()
	{
		// Nothing to do!

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addAllListener()
	{
		addSelectionListener(rootCompositeSelectionListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initListener() throws CompositeInitializationException
	{
		rootCompositeSelectionListener = new ISelectionListener<EditArtifactDocumentationCompositeSelection>() {
			@Override
			public void selectionChanged(
					EditArtifactDocumentationCompositeSelection selection,
					PocUIComposite<EditArtifactDocumentationCompositeSelection> changedComposite,
					Object sourceControl)
			{
				firePropertyChange(PROP_DIRTY);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EditArtifactDocumentationComposite instanciateMask(Composite parent)
	{
		rootComposite = new EditArtifactDocumentationComposite(parent, SWT.NONE);
		rootComposite.setVisible(false);

		return rootComposite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeAllListener()
	{
		removeSelectionListener(rootCompositeSelectionListener);
	}

	@Override
	public void doSaveAs()
	{
		// Nothing to do!

	}

	/**
	 * Returns a label with an initialization-message
	 * 
	 * @param arg0
	 *            The parent composite
	 * @return The message-label
	 */
	private Label createInitializingLabel(Composite arg0)
	{
		Label initializingMessageLabel = new Label(arg0, SWT.NONE);
		initializationFont = new Font(Display.getDefault(), new FontData("Arial", 20,
				SWT.BOLD));
		initializingMessageLabel.setFont(initializationFont);
		initializingMessageLabel
				.setText("iDocIt! is initializing at the moment. Please wait ...");
		initializingMessageLabel.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true)
				.applyTo(initializingMessageLabel);

		return initializingMessageLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite arg0)
	{
		editorParentLayout = new StackLayout();
		arg0.setLayout(editorParentLayout);

		intializationMessageComposite = new Composite(arg0, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(intializationMessageComposite);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
				.applyTo(intializationMessageComposite);

		intializationMessageComposite.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		createInitializingLabel(intializationMessageComposite);

		super.createPartControl(arg0);
		editorParentLayout.topControl = intializationMessageComposite;
		arg0.layout();

		IDocItActivator.addConfigurationListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose()
	{
		super.dispose();

		if (initializationFont != null)
		{
			initializationFont.dispose();
		}

		IDocItActivator.removeConfigurationListener(listener);
	}

}
