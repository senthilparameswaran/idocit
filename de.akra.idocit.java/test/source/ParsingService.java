package source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.akra.idocit.extensions.Parser;


public final class ParsingService
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(ParsingService.class.getName());

	/**
	 * This the ID from the extension point
	 */
	private static final String PARSER_EXTENSION_POINT_ID = "de.akra.idocit.extensions.Parser";

	/**
	 * Contains the loaded extensions. It is initialized by the first use. The key is the
	 * type of the supported file.
	 */
	private static Map<String, Parser> extensions;

	/**
	 * Constructor. No need for instantiation, because all methods are static.
	 */
	private ParsingService()
	{}

	public static Parser getParser(String type)
	{
		if (extensions == null)
		{
			loadParserExtensions();
		}
		return extensions.get(type);
	}


	public static boolean isSupported(String type)
	{
		if (extensions == null)
		{
			loadParserExtensions();
		}
		return extensions.get(type) != null;
	}


	private static void loadParserExtensions()
	{
		// TODO register somewhere to get notifications for installed and uninstalled
		// extensions

		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PARSER_EXTENSION_POINT_ID);

		extensions = Collections.synchronizedMap(new HashMap<String, Parser>());

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
	}
}
