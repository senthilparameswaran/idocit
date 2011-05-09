package de.akra.idocit.core.structure;


/**
 * Types for {@link Operation}'s {@link Parameter}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public enum ParameterType
{
	/**
	 * Input Parameter
	 */
	INPUT,

	/**
	 * Output Parameter
	 */
	OUTPUT,

	/**
	 * Exception Parameter
	 */
	EXCEPTION,

	/**
	 * Is no Parameter
	 */
	NONE;
}