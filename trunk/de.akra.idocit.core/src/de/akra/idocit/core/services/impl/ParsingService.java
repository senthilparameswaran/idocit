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
package de.akra.idocit.core.services.impl;

import java.util.Map;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.Parser;

/**
 * The ParsingService is a "factory" for supported {@link Parser}s. It fetches
 * the registered Parser Extensions from Eclipse and uses that objects.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public final class ParsingService {

	private ParsingServiceInitializer parserReader = null;

	public void init(ParsingServiceInitializer parserReader) {
		this.parserReader = parserReader;
	}

	/**
	 * Get the right {@link Parser} depending on the type (file extension) of
	 * the source file.
	 * 
	 * @param type
	 *            The file type.
	 * @return The right {@link Parser} for that file type. <code>null</code> if
	 *         no {@link Parser} for the type is found.
	 * 
	 * @throws UnitializedIDocItException
	 *             If no {@link ParsingServiceInitializer} is set to this
	 *             service
	 */
	public Parser getParser(String type)
			throws UnitializedIDocItException {
		if (parserReader != null) {
			return loadParserExtensions().get(type);
		}

		throw new UnitializedIDocItException(
				"No ParsingServiceInitializer is set to read registered parsers");
	}

	/**
	 * Checks if the file type is supported.
	 * 
	 * @param type
	 *            The file type.
	 * @return true, if supported.
	 * 
	 * @throws UnitializedIDocItException
	 *             If no {@link ParsingServiceInitializer} is set to this
	 *             service
	 */
	public boolean isSupported(String type)
			throws UnitializedIDocItException {
		if (parserReader != null) {
			return loadParserExtensions().get(type) != null;
		}

		throw new UnitializedIDocItException(
				"No ParsingServiceInitializer is set to read registered parsers");
	}

	/**
	 * Checks if in <code>extensions</code> is a {@link Parser} that supports
	 * the file <code>type</code>.
	 * 
	 * @param extensions
	 *            The {@link Parser} extensions.
	 * @param type
	 *            The type that should be checked if a {@link Parser} in
	 *            <code>extensions</code> supports it.
	 * @return true, if there is a {@link Parser} in <code>extensions</code>
	 *         supporting the <code>type</code>.
	 */
	private boolean isSupported(Map<String, Parser> extensions,
			String type) {
		return extensions.get(type) != null;
	}

	/**
	 * Returns the {@link Delimiters} for the {@link Parser} implementation that
	 * supports the given <code>type</code>.
	 * 
	 * @param type
	 *            The file type.
	 * @return {@link Delimiters} of the corresponding {@link Parser}, if the
	 *         type is not supported <code>null</code> is returned.
	 */
	public Delimiters getDelimiters(String type) {
		Map<String, Parser> extensions = loadParserExtensions();
		if (isSupported(extensions, type)) {
			return extensions.get(type).getDelimiters();
		}
		return null;
	}

	/**
	 * Loads all extensions for the {@link Parser}. They are loaded by first
	 * use. If the list should be updated Eclipse must be restarted.
	 */
	private Map<String, Parser> loadParserExtensions() {
		return parserReader.readRegisteredParsers();
	}
}
