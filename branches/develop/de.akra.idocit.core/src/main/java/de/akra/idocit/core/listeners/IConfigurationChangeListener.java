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
