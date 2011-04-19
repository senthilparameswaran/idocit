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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.pocui.core.resources.IResourceConfiguration;

import de.akra.idocit.structure.Interface;
import de.akra.idocit.structure.InterfaceArtifact;
import de.akra.idocit.structure.Operation;
import de.akra.idocit.structure.Parameter;
import de.akra.idocit.structure.ParameterType;
import de.akra.idocit.structure.Parameters;
import de.akra.idocit.structure.SignatureElement;
import de.akra.idocit.ui.Activator;
import de.akra.idocit.utils.ObjectStructureUtils;

/**
 * The image / icon management for the {@link SelectSignatureElementComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 1.0.0
 * @version 1.0.0
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
	 * Get the icon for the <code>sigElemenent</code>
	 * 
	 * @param sigElement
	 *            The {@link SignatureElement} for which the icon should be get.
	 * @return The {@link Image}.
	 */
	public Image getImageForSignatureElement(SignatureElement sigElement)
	{
		if (sigElement instanceof Interface)
		{
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
			ParameterType type = ObjectStructureUtils.findParameterType(sigElement,
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
		if (Activator.getDefault() != null){
			Image image = imageCache.get(path);
			if (image == null)
			{
				try
				{
					InputStream imageStream = FileLocator.openStream(Activator.getDefault()
							.getBundle(), new Path(path), false);
	
					ImageDescriptor.createFromImage(new Image(PlatformUI.getWorkbench()
							.getDisplay(), imageStream));
					image = Activator.getImageDescriptor(path).createImage();
					if (image != null)
					{
						imageCache.put(path, image);
					}
					else
					{
						logger.log(Level.SEVERE, "The image could not be created: " + path);
					}
				}
				catch (IOException ioEx)
				{
					// TODO: Use Workbench Exception Handling
					throw new RuntimeException(ioEx);
				}
			}
			return image;
		} else {
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
