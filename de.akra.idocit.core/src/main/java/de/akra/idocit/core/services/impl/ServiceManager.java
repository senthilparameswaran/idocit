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
package de.akra.idocit.core.services.impl;

import de.akra.idocit.common.services.ThematicGridService;
import de.akra.idocit.core.services.PersistenceService;


public class ServiceManager {
	private static final ServiceManager instance = new ServiceManager();

	private ParsingService parsingService = null;

	private PersistenceService persistenceService = null;

	private ThematicGridService thematicGridService = null;

	private ServiceManager() {

	}

	public static ServiceManager getInstance() {
		return instance;
	}

	public ParsingService getParsingService() {
		return parsingService;
	}

	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	public ThematicGridService getThematicGridService() {
		return thematicGridService;
	}

	public void setParsingService(ParsingService parsingService) {
		this.parsingService = parsingService;
	}

	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void setThematicGridService(ThematicGridService thematicGridService) {
		this.thematicGridService = thematicGridService;
	}
}
