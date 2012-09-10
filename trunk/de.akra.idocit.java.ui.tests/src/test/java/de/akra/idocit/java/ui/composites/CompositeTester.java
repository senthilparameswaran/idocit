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
package de.akra.idocit.java.ui.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.containers.dialogs.CompositeViewer;

import de.akra.idocit.java.ui.composites.factories.ManageJavadocGeneratorCompositeFactory;

/**
 * This class is the main class for Composites Tests. You can start the tests by passing
 * an int Argument to the main method. Every Composite is mapped to a integer number
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
						viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection>(
								shell, new ManageJavadocGeneratorCompositeFactory());
						viewer.setBlockOnOpen(true);
						viewer.open();
						break;
					default:
						throw new IllegalArgumentException(
								"Please specify a test id to run.");
					}
				}
			}
			else
			{
				CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection> viewer = new CompositeViewer<EmptyActionConfiguration, EmptyResourceConfiguration, ManageJavadocGeneratorCompositeSelection>(
						shell, new ManageJavadocGeneratorCompositeFactory());
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
