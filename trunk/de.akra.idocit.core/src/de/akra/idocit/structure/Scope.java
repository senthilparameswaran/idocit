package de.akra.idocit.structure;

/**
 * A scope for a documentation part of a {@link SignatureElement}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public enum Scope
{
	/**
	 * An explicit element or resource.
	 */
	EXPLICIT,

	/**
	 * An implicit element or resource.
	 */
	IMPLICIT;

	/**
	 * Converts a string to a <code>Scope</code>. Comparison is case insensitive.
	 * 
	 * @param scope
	 *            The scope as string.
	 * @return The matched <code>Scope</code>.
	 * @throws IllegalArgumentException
	 *             If no <code>Scope</code> matches the string.
	 */
	public static Scope fromString(String scope) throws IllegalArgumentException
	{
		return valueOf(scope.toUpperCase());
	}
}
