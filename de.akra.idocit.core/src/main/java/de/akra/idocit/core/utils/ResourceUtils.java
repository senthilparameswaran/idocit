/*******************************************************************************
 * Copyright 2012 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import de.akra.idocit.core.IDocItActivator;

/**
 * @author Dirk Meier-Eickhoff
 * @since 0.0.9
 * @version 0.0.1
 */
public class ResourceUtils
{
	private static Logger logger = Logger.getLogger(ResourceUtils.class.getName());
	
	/**
	 * If you run Eclipse from Eclipse the resource path differs from the path if it were
	 * packaged into a jar.
	 */
	public static final String RSSOURCE_FOLDER = "src/main/resources/";

	/**
	 * Path in the resource file for the stored thematic roles.
	 */
	public static final String ROLE_RESOURCE_FILE = "roles.properties";
	
	/**
	 * Path in the resource file for the stored addresses.
	 */
	public static final String ADDRESSEE_RESOURCE_FILE = "addressees.properties";

	public static final String THEMATIC_GRIDS_RESOURCE_FILE = "thematicgrids.xml";

	public static InputStream getResourceInputStream(String resourceName)
	{
		InputStream resourceInputStream = null;
		Path path = new Path(resourceName);
		try
		{
			logger.log(Level.FINE, "Open resource: {0}", path.toString());
			resourceInputStream = FileLocator.openStream(IDocItActivator.getDefault().getBundle(), path, false);
		}
		catch (IOException e)
		{
			logger.log(Level.FINE, "Cannot find resource: {0}", path.toString());
			try
			{
				path = new Path(RSSOURCE_FOLDER + resourceName);
				logger.log(Level.FINE, "Open resource: {0}", path.toString());
				resourceInputStream = FileLocator.openStream(IDocItActivator.getDefault().getBundle(), path,
						false);
			}
			catch (IOException e1)
			{
				logger.log(Level.SEVERE, "Cannot find resource: {0}", path.toString());
				// TODO: Handle exception via Eclipse Workbench
				throw new RuntimeException(e1);
			}
		}
		return resourceInputStream;
	}
}
