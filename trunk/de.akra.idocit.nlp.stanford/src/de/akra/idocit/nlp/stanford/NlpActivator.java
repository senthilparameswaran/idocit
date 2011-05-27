package de.akra.idocit.nlp.stanford;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.akra.idocit.nlp.stanford.constants.NlpConstans;
import de.akra.idocit.nlp.stanford.services.WSDLTaggingService;

public class NlpActivator implements BundleActivator
{

	private ServiceReference taggingServiceReference = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		ServiceRegistration registration = context.registerService(
				NlpConstans.TAGGING_SERVICE_NAME, WSDLTaggingService.getInstance(), null);

		taggingServiceReference = registration.getReference();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		if (taggingServiceReference != null)
		{
			context.ungetService(taggingServiceReference);
		}
	}
}
