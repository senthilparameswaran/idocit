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
package de.akra.idocit.core.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.structure.Delimiters;

/**
 * The ParsingService is a "factory" for supported {@link Parser}s. It fetches the
 * registered Parser Extensions from Eclipse and uses that objects.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public final class ParsingService
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(ParsingService.class.getName());

	/**
	 * This the ID from the extension point
	 */
	private static final String PARSER_EXTENSION_POINT_ID = "de.akra.idocit.core.extensions.Parser";

	/**
	 * Constructor. No need for instantiation, because all methods are static.
	 */
	private ParsingService()
	{}

	/**
	 * Get the right {@link Parser} depending on the type (file extension) of the source
	 * file.
	 * 
	 * @param type
	 *            The file type.
	 * @return The right {@link Parser} for that file type. <code>null</code> if no
	 *         {@link Parser} for the type is found.
	 */
	public static Parser getParser(String type)
	{
		return loadParserExtensions().get(type);
	}

	/**
	 * Checks if the file type is supported.
	 * 
	 * @param type
	 *            The file type.
	 * @return true, if supported.
	 */
	public static boolean isSupported(String type)
	{
		return loadParserExtensions().get(type) != null;
	}

	/**
	 * Checks if in <code>extensions</code> is a {@link Parser} that supports the file
	 * <code>type</code>.
	 * 
	 * @param extensions
	 *            The {@link Parser} extensions.
	 * @param type
	 *            The type that should be checked if a {@link Parser} in
	 *            <code>extensions</code> supports it.
	 * @return true, if there is a {@link Parser} in <code>extensions</code> supporting
	 *         the <code>type</code>.
	 */
	private static boolean isSupported(Map<String, Parser> extensions, String type)
	{
		return extensions.get(type) != null;
	}

	/**
	 * Returns the {@link Delimiters} for the {@link Parser} implementation that supports
	 * the given <code>type</code>.
	 * 
	 * @param type
	 *            The file type.
	 * @return {@link Delimiters} of the corresponding {@link Parser}.
	 */
	public static Delimiters getDelimiters(String type)
	{
		Map<String, Parser> extensions = loadParserExtensions();
		if (isSupported(extensions, type))
		{
			return extensions.get(type).getDelimiters();
		}
		return null;
	}

	/**
	 * Loads all extensions for the {@link Parser}. They are loaded by first use. If the
	 * list should be updated Eclipse must be restarted.
	 */
	private static Map<String, Parser> loadParserExtensions()
	{
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PARSER_EXTENSION_POINT_ID);

		Map<String, Parser> extensions = Collections
				.synchronizedMap(new HashMap<String, Parser>());

		try
		{
			for (IConfigurationElement e : config)
			{
				logger.log(Level.INFO, "Evaluating extensions");
				final Object o = e.createExecutableExtension("class");
				if (o instanceof Parser)
				{
					Parser parser = (Parser) o;
					extensions.put(parser.getSupportedType(), parser);

					logger.log(Level.INFO,
							"Loaded parser \"" + parser.getClass().toString()
									+ "\" supports \"" + parser.getSupportedType()
									+ "\".");
				}
			}
		}
		catch (CoreException ex)
		{
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return extensions;
	}
}
