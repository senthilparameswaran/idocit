package de.akra.idocit.ui.components;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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

import de.akra.idocit.services.PersistenceService;
import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.structure.SignatureElement;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.ui.composites.EditArtifactDocumentationComposite;
import de.akra.idocit.ui.composites.EditArtifactDocumentationCompositeSelection;
import de.akra.idocit.ui.constants.DialogConstants;
import de.akra.idocit.ui.utils.MessageBoxUtils;

/**
 * An {@link EditorPart} to document software-interfaces by using thematic grids and
 * concerning different groups of adressees.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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

	// The root composite
	private InterfaceArtifact initialInterfaceArtifact = null;

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
				PersistenceService.writeInterface(artifact, interfaceFile);
				initialInterfaceArtifact = (InterfaceArtifact) artifact
						.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
				firePropertyChange(PROP_DIRTY);
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
		List<Addressee> addressees = PersistenceService.loadConfiguredAddressees();
		List<ThematicRole> thematicRoles = PersistenceService.loadThematicRoles();

		setSite(site);
		setInput(input);

		EditArtifactDocumentationCompositeSelection selection = new EditArtifactDocumentationCompositeSelection();
		selection.setSelectedSignatureElement(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
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
					InterfaceArtifact interfaceArtifact = PersistenceService
							.loadInterface(interfaceIFile);
					selection.setInterfaceArtifact(interfaceArtifact);
					selection.setArtifactFile(interfaceIFile);
					logger.log(Level.INFO, "End parsing");
					logger.log(Level.INFO,
							"InterfaceArtifact.size=" + interfaceArtifact.size());

					logger.log(Level.INFO, "Start copy");
					initialInterfaceArtifact = (InterfaceArtifact) interfaceArtifact
							.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
					logger.log(Level.INFO, "End copy");

					setPartName(interfaceIFile.getName() + " - "
							+ DialogConstants.DIALOG_TITLE);

					if (!interfaceFile.canWrite())
					{
						MessageDialog.openInformation(site.getShell(),
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty()
	{
		return (initialInterfaceArtifact != null)
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
					PocUIComposite<EditArtifactDocumentationCompositeSelection> changedComposite)
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
		return new EditArtifactDocumentationComposite(parent, SWT.NONE);
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
}
