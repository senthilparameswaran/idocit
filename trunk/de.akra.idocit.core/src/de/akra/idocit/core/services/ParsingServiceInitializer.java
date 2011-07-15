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
