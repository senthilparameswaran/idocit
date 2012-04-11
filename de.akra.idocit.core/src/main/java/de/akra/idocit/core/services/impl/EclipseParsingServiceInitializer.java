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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.akra.idocit.core.extensions.Parser;

/**
 * Reader for the registerred {@link Parser}s at the Eclipse Extension Point
 * Platform Registry.
 * 
 * @author Jan Christian Krause
 * 
 */
public class EclipseParsingServiceInitializer implements
		ParsingServiceInitializer {

	/**
	 * Logger.
	 */
	private static Logger logger = Logger
			.getLogger(EclipseParsingServiceInitializer.class.getName());

	/**
	 * This the ID from the extension point
	 */
	private static final String PARSER_EXTENSION_POINT_ID = "de.akra.idocit.core.extensions.Parser";

	/**
	 * {@inheritDoc}
	 * 
	 * This operation reads the parsers from the Eclipse Platform Extension
	 * Registry.
	 */
	@Override
	public Map<String, Parser> readRegisteredParsers() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PARSER_EXTENSION_POINT_ID);

		Map<String, Parser> extensions = Collections
				.synchronizedMap(new HashMap<String, Parser>());

		try {
			for (IConfigurationElement e : config) {
				logger.log(Level.INFO, "Evaluating extensions");
				final Object o = e.createExecutableExtension("class");
				if (o instanceof Parser) {
					Parser parser = (Parser) o;
					extensions.put(parser.getSupportedType(), parser);

					logger.log(Level.INFO, "Loaded parser \""
							+ parser.getClass().toString() + "\" supports \""
							+ parser.getSupportedType() + "\".");
				}
			}
		} catch (CoreException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return extensions;
	}

}
