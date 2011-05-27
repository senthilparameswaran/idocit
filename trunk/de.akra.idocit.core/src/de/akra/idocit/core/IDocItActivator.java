/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.akra.idocit.core.constants.PreferenceStoreConstants;
import de.akra.idocit.core.listeners.IDocItInitializationListener;
import de.akra.idocit.core.services.PersistenceService;
import de.akra.idocit.core.services.ServiceManager;
import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.ThematicGrid;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.nlp.stanford.constants.NlpConstans;
import de.akra.idocit.nlp.stanford.services.WSDLTaggingService;

/**
 * The {@link IStartup} of iDocIt!.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class IDocItActivator extends AbstractUIPlugin implements IStartup
{
	private static final Logger logger = Logger
			.getLogger(IDocItActivator.class.getName());

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.akra.idocit.ui"; //$NON-NLS-1$

	/**
	 * Path in the resource file for the stored thematic roles.
	 */
	private static final String ROLE_RESOURCE_FILE = "resources/roles.properties";

	/**
	 * Path in the resource file for the stored addresses.
	 */
	private static final String ADDRESSEE_RESOURCE_FILE = "resources/addressees.properties";

	private static final Set<IDocItInitializationListener> CONFIGURATION_LISTENERS = new HashSet<IDocItInitializationListener>();

	private static boolean initializedAtStartup = false;

	private static IDocItActivator plugin;

	/**
	 * This reference is required to unregister the service tracker at shutdown time.
	 */
	private ServiceTracker serviceTracker = null;

	/**
	 * This Service Tracker-Implementation listens for the registration of an
	 * {@link WSDLTaggingService}, registers it at the service manager of iDocIt and then
	 * calls the method initializeIDocIt().
	 */
	private class TaggingServiceCustomizer implements ServiceTrackerCustomizer
	{

		private BundleContext context = null;

		public TaggingServiceCustomizer(BundleContext context)
		{
			this.context = context;
		}

		@Override
		public Object addingService(ServiceReference reference)
		{
			WSDLTaggingService service = (WSDLTaggingService) context
					.getService(reference);

			ServiceManager.getInstance().setWsdlTaggingService(service);

			initializeIDocIt();

			return service;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service)
		{
			removedService(reference, service);
			addingService(reference);
		}

		@Override
		public void removedService(ServiceReference reference, Object service)
		{
			context.ungetService(reference);
			ServiceManager.getInstance().setWsdlTaggingService(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void earlyStartup()
	{
		// Initialize the Preference Store.
		if (!PersistenceService.areAddresseesInitialized())
		{
			List<Addressee> addressees = PersistenceService.readInitialAddressees();
			PersistenceService.persistAddressees(addressees);
		}

		if (!PersistenceService.areThematicRolesInitialized())
		{
			List<ThematicRole> roles = PersistenceService.readInitialThematicRoles();
			PersistenceService.persistThematicRoles(roles);
		}

		if (!PersistenceService.areThematicGridsInitialized())
		{
			List<ThematicGrid> grids = new ArrayList<ThematicGrid>();

			ThematicGrid grid = new ThematicGrid();
			grid.setDescription("Description");
			grid.setName("Grid 1");
			grid.setRoles(new HashMap<ThematicRole, Boolean>());
			grid.setVerbs(new HashSet<String>());

			grids.add(grid);
			// TODO: Init Thematic Grids with Property File
			PersistenceService.persistThematicGrids(grids);
		}

		initializeIDocIt();
	}

	/**
	 * Registers a new {@link IDocItInitializationListener} at this activator. The
	 * registere service will be informed about changed configurations of iDocIt!, e.g.
	 * via the preference pages.
	 * 
	 * Please note: each listener will be registered and informed exactly one time!
	 * 
	 * @param listener
	 *            The {@link IDocItInitializationListener} to register
	 */
	public static void addConfigurationListener(IDocItInitializationListener listener)
	{
		CONFIGURATION_LISTENERS.add(listener);

		if (!initializedAtStartup)
		{
			listener.initializationStarted();
		}
		else
		{
			listener.initializationFinished();
		}
	}

	/**
	 * Unregisters the given listener.
	 * 
	 * @param listener
	 *            The listener to unregister
	 * 
	 * @see {@link this#addConfigurationListener(IDocItInitializationListener)}
	 */
	public static void removeConfigurationListener(IDocItInitializationListener listener)
	{
		CONFIGURATION_LISTENERS.add(listener);
	}

	/**
	 * Informs all registered listeners about changes on the iDocIt! configuration.
	 * 
	 * @param initializationStarted
	 *  <ul>	
	 *   <li>If <code>true</code> then the method initializationStarted() will be called on each registered listener</li>
	 *   <li>If <code>false</code> then the method initializationFinished() will be called on each registered listener</li></li>
	 * 	</ul>
	 */
	private static void fireChangeEvent(boolean initializationStarted)
	{
		for (IDocItInitializationListener listener : CONFIGURATION_LISTENERS)
		{
			if (initializationStarted)
			{
				listener.initializationStarted();
			}
			else
			{
				listener.initializationFinished();
			}
		}
	}

	/**
	 * Initialize the used services for deriving the thematic grids.
	 */
	public static void initializeIDocIt()
	{
		Thread initializer = new Thread() {
			@Override
			public void run()
			{
				WSDLTaggingService taggingService = ServiceManager.getInstance()
						.getWsdlTaggingService();

				if (taggingService != null)
				{
					fireChangeEvent(true);

					IPreferenceStore store = PlatformUI.getPreferenceStore();
					System.setProperty("wordnet.database.dir",
							store.getString(PreferenceStoreConstants.WORDNET_PATH));
					try
					{
						taggingService.init(store
								.getString(PreferenceStoreConstants.TAGGER_MODEL_FILE));
					}
					catch (FileNotFoundException e)
					{
						logger.log(Level.WARNING, e.getMessage(), e);
					}
					catch (IOException ioEx)
					{
						// TODO: Route exception to Eclipse Platform
						throw new RuntimeException(ioEx);
					}

					initializedAtStartup = true;

					fireChangeEvent(false);
				}
			}
		};

		initializer.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		TaggingServiceCustomizer taggingCustomizer = new TaggingServiceCustomizer(context);
		serviceTracker = new ServiceTracker(context, NlpConstans.TAGGING_SERVICE_NAME,
				taggingCustomizer);

		serviceTracker.open();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);

		serviceTracker.close();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IDocItActivator getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Creates the {@link PropertyResourceBundle} for the path
	 * <code>resourceBundleFile</code> in the plugin's bundle.
	 * 
	 * @param resourceBundleFile
	 *            The resource bundle.
	 * @return {@link PropertyResourceBundle} for this plugin and the given
	 *         resourceBundleFile.
	 */
	private PropertyResourceBundle getResourceBundle(String resourceBundleFile)
	{
		try
		{
			InputStream resourceInputStream = FileLocator.openStream(getBundle(),
					new Path(resourceBundleFile), false);
			return new PropertyResourceBundle(resourceInputStream);
		}
		catch (IOException ioEx)
		{
			// TODO: Handle exception via Eclipse Workbench
			throw new RuntimeException(ioEx);
		}
	}

	/**
	 * 
	 * @return the {@link PropertyResourceBundle} for the thematic roles.
	 */
	public PropertyResourceBundle getThematicRoleResourceBundle()
	{
		return getResourceBundle(ROLE_RESOURCE_FILE);
	}

	/**
	 * 
	 * @return the {@link PropertyResourceBundle} for the addresses.
	 */
	public PropertyResourceBundle getAddresseeResourceBundle()
	{
		return getResourceBundle(ADDRESSEE_RESOURCE_FILE);
	}
}
