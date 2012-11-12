/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.akra.idocit.java.ui;

import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.core.listeners.IConfigurationChangeListener;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.ui.components.RecommendedGridsView;
import de.akra.idocit.ui.components.RecommendedGridsViewSelection;

/**
 * Handler to manage the state of the {@link RecommendedGridsView}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendedGridsViewHandler
{
	// Listeners
	private ISelectionListener editorSelectionListener;
	private IWindowListener windowListener;
	private IConfigurationChangeListener thematicGridConfigChangeListener;
	private IConfigurationChangeListener thematicRoleConfigChangeListener;

	/**
	 * Initialize and start needed listeners to handle the {@link RecommendedGridsView}.
	 */
	public void start()
	{
		initListeners();
		addAllListener();
	}

	/**
	 * Stop the handler and all it's listeners.
	 */
	public void stop()
	{
		removeAllListeners();
	}

	private void removeAllListeners()
	{

		for (final IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows())
		{
			window.getSelectionService().removePostSelectionListener(
					editorSelectionListener);
		}

		PlatformUI.getWorkbench().removeWindowListener(windowListener);

		ServiceManager.getInstance().getPersistenceService()
				.removeThematicGridChangeListener(thematicGridConfigChangeListener);
		ServiceManager.getInstance().getPersistenceService()
				.removeThematicRoleChangeListener(thematicRoleConfigChangeListener);
	}

	private void addAllListener()
	{
		// add listener to existing windows, because if there is only one window, the
		// "windowActivated" event will not fire if the window gets the focus.
		for (final IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows())
		{
			window.getSelectionService()
					.addPostSelectionListener(editorSelectionListener);
		}

		// add listener to notice also new windows
		PlatformUI.getWorkbench().addWindowListener(this.windowListener);

		ServiceManager.getInstance().getPersistenceService()
				.addThematicGridChangeListener(thematicGridConfigChangeListener);
		ServiceManager.getInstance().getPersistenceService()
				.addThematicRoleChangeListener(thematicRoleConfigChangeListener);
	}

	private void initListeners()
	{
		this.editorSelectionListener = new JavaEditorSelectionListener();

		this.windowListener = new IWindowListener() {

			@Override
			public void windowOpened(IWorkbenchWindow window)
			{}

			@Override
			public void windowDeactivated(IWorkbenchWindow window)
			{
				window.getSelectionService().removePostSelectionListener(
						editorSelectionListener);
			}

			@Override
			public void windowClosed(IWorkbenchWindow window)
			{}

			@Override
			public void windowActivated(IWorkbenchWindow window)
			{
				window.getSelectionService().addPostSelectionListener(
						editorSelectionListener);
			}
		};

		thematicGridConfigChangeListener = new IConfigurationChangeListener() {

			@Override
			public void configurationChange()
			{
				updateAllViews();
			}
		};

		thematicRoleConfigChangeListener = new IConfigurationChangeListener() {

			@Override
			public void configurationChange()
			{
				updateAllViews();
			}
		};
	}

	/**
	 * Refresh all {@link RecommendedGridsView} (derive recommended thematic grids again).
	 */
	private void updateAllViews()
	{
		for (final IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows())
		{
			final IViewPart findView = window.getActivePage().findView(
					RecommendedGridsView.ID);
			if (findView != null && findView instanceof RecommendedGridsView)
			{
				final RecommendedGridsView view = (RecommendedGridsView) findView;
				final RecommendedGridsViewSelection selection = view.getSelection();
				if (selection != null)
				{
					view.setSelection(null);
					view.setSelection(selection);
				}
			}
		}
	}
}
