/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package de.akra.idocit.ui.composites;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.resources.IResourceConfiguration;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.ParameterType;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.utils.SignatureElementUtils;
import de.akra.idocit.ui.Activator;

/**
 * The image / icon management for the {@link SelectSignatureElementComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 */
public class SelectSignatureElementCompositeRC implements IResourceConfiguration
{
	private static final String IMAGE_PATH = "resources/images/";

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(SelectSignatureElementComposite.class
			.getName());

	/**
	 * Cache for the used images.
	 */
	private Map<String, Image> imageCache = new HashMap<String, Image>();

	@Override
	public boolean isComplete()
	{
		return true;
	}

	/**
	 * Get the icon for the <code>sigElemenent</code>. If the element is
	 * {@link InterfaceArtifact#NOT_SUPPORTED_ARTIFACT} or has
	 * {@link InterfaceArtifact#NOT_SUPPORTED_ARTIFACT} as parent the icon with a red
	 * cross (rem_co.gif) is returned.
	 * 
	 * @param sigElement
	 *            The {@link SignatureElement} for which the icon should be get.
	 * @return The {@link Image}.
	 */
	public Image getImageForSignatureElement(SignatureElement sigElement)
	{
		if (sigElement instanceof Interface)
		{
			// Changes due to Issue #59
			if (sigElement.getParent() == InterfaceArtifact.NOT_SUPPORTED_ARTIFACT)
			{
				return getImageFromCache(IMAGE_PATH + "rem_co.gif");
			}
			// End changes due to Issue #59
			return getImageFromCache(IMAGE_PATH + "int_obj.gif");
		}
		else if (sigElement instanceof InterfaceArtifact)
		{
			if (sigElement == InterfaceArtifact.NOT_SUPPORTED_ARTIFACT)
			{
				return getImageFromCache(IMAGE_PATH + "rem_co.gif");
			}
			return getImageFromCache(IMAGE_PATH + "package_obj.gif");
		}
		else if (sigElement instanceof Operation)
		{
			return getImageFromCache(IMAGE_PATH + "public_co.gif");
		}
		else if (sigElement instanceof Parameter)
		{
			ParameterType type = SignatureElementUtils.findParameterType(sigElement,
					sigElement);

			switch (type)
			{
			case EXCEPTION:
				return getImageFromCache(IMAGE_PATH + "rem_co.gif");
			case INPUT:
				return getImageFromCache(IMAGE_PATH + "incom_stat.gif");
			case OUTPUT:
				return getImageFromCache(IMAGE_PATH + "outgo_stat.gif");
			case NONE:
			default:
				throw new RuntimeException("The Parameter " + String.valueOf(sigElement)
						+ " could not be found in any Operation-Declaration.");
			}
		}
		else if (sigElement instanceof Parameters)
		{
			return getImageFromCache(IMAGE_PATH + "datasheet.gif");
		}
		else
		{
			throw new RuntimeException("The type of SignatureElement "
					+ String.valueOf(sigElement) + " is unknown.");
		}
	}

	/**
	 * Return the {@link Image} from the image cache. If it does not exist, it is created
	 * and returned.
	 * 
	 * @param path
	 *            The path of the image resource.
	 * @return The image.
	 */
	private Image getImageFromCache(String path)
	{
		if (Activator.getDefault() != null)
		{
			Image image = imageCache.get(path);
			if (image == null)
			{
				try
				{
					InputStream imageStream = FileLocator.openStream(Activator
							.getDefault().getBundle(), new Path(path), false);

					ImageDescriptor.createFromImage(new Image(PlatformUI.getWorkbench()
							.getDisplay(), imageStream));
					image = Activator.getImageDescriptor(path).createImage();
					if (image != null)
					{
						imageCache.put(path, image);
					}
					else
					{
						logger.log(Level.SEVERE, "The image could not be created: "
								+ path);
					}
				}
				catch (IOException ioEx)
				{
					// TODO: Use Workbench Exception Handling
					throw new RuntimeException(ioEx);
				}
			}
			return image;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Dispose all {@link Image}s in the cache.
	 */
	public void disposeImageCache()
	{
		for (Entry<String, Image> entry : imageCache.entrySet())
		{
			entry.getValue().dispose();
		}
	}
}
