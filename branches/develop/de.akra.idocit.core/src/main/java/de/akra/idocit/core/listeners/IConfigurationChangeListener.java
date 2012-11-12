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
package de.akra.idocit.core.listeners;

import java.util.EventListener;

import de.akra.idocit.core.services.PersistenceService;

/**
 * Listener for configuration changes (if ThematicGrids, ThematicRoles and Addresses
 * changes).
 * 
 * @author Dirk Meier-Eickhoff
 */
public interface IConfigurationChangeListener extends EventListener
{
	/**
	 * <p>
	 * Notification that a configuration has changed. Load the configuration again from
	 * {@link PersistenceService}.
	 * </p>
	 * <p>
	 * This method gets called when the observed object fires a configuration change
	 * event.
	 * </p>
	 */
	public void configurationChange();
}
