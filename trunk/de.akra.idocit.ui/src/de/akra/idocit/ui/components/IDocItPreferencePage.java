/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.ui.components;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

import de.akra.idocit.core.IDocItActivator;
import de.akra.idocit.core.constants.PreferenceStoreConstants;

/**
 * A {@link PreferencePage} for the iDocIt! settings.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class IDocItPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	// Constants
	private static final String WORDNET_REFERENCE_FILE_NAME = "index.sense";

	// Widgets and field editors
	private Label lblWordNetLink;

	private DirectoryFieldEditor wordnetPathEditor;

	private FileFieldEditor taggerModelEditor;

	// Listeners
	private MouseListener wordnetLinkMouseListener;

	/**
	 * Constructor.
	 */
	public IDocItPreferencePage()
	{
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	@Override
	public void dispose()
	{
		super.dispose();
		lblWordNetLink.removeMouseListener(wordnetLinkMouseListener);

	}

	@Override
	protected Control createContents(Composite parent)
	{
		Label lblWordNet = new Label(parent, SWT.NONE);
		lblWordNet.setText("Please specify where WordNet is installed and the PoS-Tagger Model is stored on your computer.");

		lblWordNetLink = new Label(parent, SWT.NONE);
		lblWordNetLink.setText("If you do not have Wordnet, download it here!");
		lblWordNetLink.setForeground(getShell().getDisplay().getSystemColor(
				SWT.COLOR_BLUE));
		lblWordNetLink.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));

		wordnetLinkMouseListener = new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e)
			{
				int style = IWorkbenchBrowserSupport.AS_EDITOR
						| IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.STATUS;
				try
				{
					IWebBrowser browser = WorkbenchBrowserSupport.getInstance()
							.createBrowser(style, "WordNet-Download", "WordNet-Download",
									"Download WordNet");
					browser.openURL(new URL(
							"http://wordnet.princeton.edu/wordnet/download/"));
				}
				catch (PartInitException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (MalformedURLException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				// Nothing to do!
			}

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				// Nothing to do!
			}
		};

		lblWordNetLink.addMouseListener(wordnetLinkMouseListener);
		new Label(parent, SWT.NONE);

		return super.createContents(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors()
	{
		wordnetPathEditor = new DirectoryFieldEditor(
				PreferenceStoreConstants.WORDNET_PATH, "WordNet Path:",
				getFieldEditorParent());

		taggerModelEditor = new FileFieldEditor(
				PreferenceStoreConstants.TAGGER_MODEL_FILE, "PoS-Tagger Model:",
				getFieldEditorParent());

		addField(wordnetPathEditor);
		addField(taggerModelEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		// Nothing to do!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkState()
	{
		// Changes due to Issue #11
		// the FieldEditorPreferencePage does the check already
		super.checkState();
		updateApplyButton();
		// End changes due to Issue #11
	}

	/**
	 * Makes a {@link File} from the path in the tagger model field.
	 * 
	 * @return {@link File} pointing to the tagger model.
	 */
	public File getChoosenPoSTaggerModelFile()
	{
		return new File(taggerModelEditor.getStringValue());
	}

	/**
	 * Makes a {@link File} from the path in the WordNet dictionary field.
	 * 
	 * @return {@link File} pointing to the WordNet dictionary.
	 */
	public File getChoosenWordnetInstallation()
	{
		String choosenPath = wordnetPathEditor.getStringValue();

		if (choosenPath.endsWith(File.separator))
		{
			choosenPath += WORDNET_REFERENCE_FILE_NAME;
		}
		else
		{
			choosenPath += File.separator + WORDNET_REFERENCE_FILE_NAME;
		}

		File wordNetInstallation = new File(choosenPath);
		return wordNetInstallation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE))
		{
			checkState();
		}
	}

	@Override
	public boolean isValid()
	{
		return getChoosenWordnetInstallation().exists()
				&& getChoosenPoSTaggerModelFile().exists();
	}

//	@Override
//	public boolean okToLeave()
//	{
//		String info = null;
//		if (!getChoosenWordnetInstallation().exists())
//		{
//			info = "The choosen directory does not contain a valid WordNet-Installation.";
//		}
//		else if (!getChoosenPoSTaggerModelFile().exists())
//		{
//			info = "The choosen model file for the Part-of-Speech-tagger does not exist.";
//		}
//		if (info != null)
//		{
//			// return true, if user clicked on No. Then he wants to leave the page.
//			return !MessageBoxUtils.openYesNoWarningDialogBox(getShell(), info+"\n\nWithout correct settings iDocIt! will not work.\nDo you want to stay on the page and correct the settings?" );
//		}
//		return true;
//	}

	@Override
	public boolean performOk()
	{
		boolean saveState = super.performOk();
		IDocItActivator.initializeIDocIt();
		return saveState;
	}
}
