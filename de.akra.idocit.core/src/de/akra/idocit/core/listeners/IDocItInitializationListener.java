package de.akra.idocit.core.listeners;

/**
 * Represents a listener for change-events concerning the IDocIt-Plattform (Core)
 * Configuration.
 * 
 * @author Jan Christian Krause
 * 
 */
public interface IDocItInitializationListener
{
	/**
	 * Is invoked before the initialization-method of IDocIt-Core is started.
	 */
	public void initializationStarted();

	/**
	 * Is invoked after the initialization-method of IDocIt-Core has been finished.
	 */
	public void initializationFinished();

}
