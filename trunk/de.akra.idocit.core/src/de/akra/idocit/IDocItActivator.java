package de.akra.idocit;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator to get the plugin's resource bundles.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
 * 
 */
public class IDocItActivator extends AbstractUIPlugin
{

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

	/**
	 * The shared instance
	 */
	private static IDocItActivator plugin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
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
