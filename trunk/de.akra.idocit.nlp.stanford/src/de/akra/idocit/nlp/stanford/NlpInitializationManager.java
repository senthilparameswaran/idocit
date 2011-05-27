package de.akra.idocit.nlp.stanford;

import org.eclipse.ui.IStartup;

public class NlpInitializationManager implements IStartup
{

	@Override
	public void earlyStartup()
	{
		// Nothing to do! This IStartup is just used to ensure that
		// this plugin is loaded at startup-time of the workbench!

	}

}
