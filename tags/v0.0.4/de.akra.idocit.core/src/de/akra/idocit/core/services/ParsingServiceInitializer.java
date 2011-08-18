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
package de.akra.idocit.core.services;

import java.util.Map;

import de.akra.idocit.core.extensions.Parser;

/**
 * Represents a reader for parsers registerred somehwere (the registry is not
 * specified).
 * 
 * @author Jan Christian Krause
 * 
 */
public interface ParsingServiceInitializer {

	/**
	 * Returns the registered {@link Parser}s per file-extension (used as
	 * map-key). It is not specified from where the extensions are read.
	 * 
	 * @return The registered {@link Parser}s
	 */
	public Map<String, Parser> readRegisteredParsers();

}
