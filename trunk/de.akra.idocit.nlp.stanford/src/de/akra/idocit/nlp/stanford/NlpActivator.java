/*******************************************************************************
 * Copyright 2011 AKRA GmbH 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
