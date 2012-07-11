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
package de.akra.idocit.ui.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.containers.dialogs.CompositeViewer;

import de.akra.idocit.ui.composites.factories.DisplayRecommendedRolesCompositeFactory;
import de.akra.idocit.ui.composites.factories.DocumentItemCompositeFactory;
import de.akra.idocit.ui.composites.factories.DocumentItemListCompositeFactory;
import de.akra.idocit.ui.composites.factories.EditAddresseeListCompositeFactory;
import de.akra.idocit.ui.composites.factories.EditArtifactDocumentationCompositeFactory;
import de.akra.idocit.ui.composites.factories.EditThematicGridCompositeFactory;
import de.akra.idocit.ui.composites.factories.EditThematicRoleCompositeFactory;
import de.akra.idocit.ui.composites.factories.ManageAddresseesCompositeFactory;
import de.akra.idocit.ui.composites.factories.ManageThematicGridsCompositeFactory;
import de.akra.idocit.ui.composites.factories.ManageThematicRoleCompositeFactory;
import de.akra.idocit.ui.composites.factories.RecommendRolesCompositeFactory;
import de.akra.idocit.ui.composites.factories.SelectSignatureElementCompositeFactory;

/**
 * This class is the main class for Composites Tests You can start the tests by passing an
 * int Argument (0 to 9) to the main method. Every Composite is mapped to a integer number
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class CompositeTester
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Display display = new Display();
			Shell shell = new Shell(display, SWT.BORDER | SWT.RESIZE);
			FillLayout layout = new FillLayout();
			shell.setLayout(layout);

			// show the dialogs depending on the needed testcase
			if (args.length > 0)
			{

				for (String argument : args)
				{
					CompositeViewer<?, ?, ?> viewer;

					int testcase = Integer.parseInt(argument);
					switch (testcase)
					{
					case 0:
						viewer = new CompositeViewer<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection>(
								shell, new SelectSignatureElementCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					case 1:
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemCompositeSelection>(
								shell, new DocumentItemCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					case 2:
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, DocumentItemListCompositeSelection>(
								shell, new DocumentItemListCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					case 3:
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, EditArtifactDocumentationCompositeSelection>(
								shell, new EditArtifactDocumentationCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					case 4:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, EditAddresseeListCompositeSelection>(
								shell, new EditAddresseeListCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 5:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, DisplayRecommendedRolesCompositeSelection>(
								shell, new DisplayRecommendedRolesCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 6:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicRoleCompositeSelection>(
								shell, new EditThematicRoleCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 7:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageAddresseeCompositeSelection>(
								shell, new ManageAddresseesCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 8:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicRoleCompositeSelection>(
								shell, new ManageThematicRoleCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 9:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, EditThematicGridCompositeSelection>(
								shell, new EditThematicGridCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 10:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageThematicGridsCompositeSelection>(
								shell, new ManageThematicGridsCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					case 11:
					{
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, RecommendRolesCompositeSelection>(
								shell, new RecommendRolesCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Please specify a test id to run.");
					}
				}
			}
			else
			{
				CompositeViewer<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection> viewer = new CompositeViewer<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection>(
						shell, new SelectSignatureElementCompositeFactory());
				viewer.setBlockOnOpen(true);
				viewer.open();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
