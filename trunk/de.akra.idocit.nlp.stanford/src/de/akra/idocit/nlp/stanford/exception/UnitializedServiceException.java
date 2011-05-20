package de.akra.idocit.nlp.stanford.exception;

public class UnitializedServiceException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3855010499735220326L;

	/**
	 * Constructor with message
	 * 
	 * @param message
	 *            The error-description
	 */
	public UnitializedServiceException(String message)
	{
		super(message);
	}
}
