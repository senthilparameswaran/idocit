package de.akra.idocit.nlp.stanford;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.akra.idocit.nlp.stanford.constants.NlpConstans;
import de.akra.idocit.nlp.stanford.services.WSDLTaggingService;

public class NlpActivator implements BundleActivator
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		context.registerService(NlpConstans.TAGGING_SERVICE_NAME,
				WSDLTaggingService.getInstance(), null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		context.registerService(NlpConstans.TAGGING_SERVICE_NAME, null, null);
	}
}
