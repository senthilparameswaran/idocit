package de.akra.idocit.structure;


/**
 * Types for {@link Operation}'s {@link Parameter}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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