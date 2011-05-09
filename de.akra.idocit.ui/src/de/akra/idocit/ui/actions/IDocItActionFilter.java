package de.akra.idocit.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IResourceActionFilter;

import de.akra.idocit.core.services.ParsingService;

/**
 * This should be a filter to disable the Action if the file is not supported.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class IDocItActionFilter implements IResourceActionFilter
{
	private static IDocItActionFilter instance;

	/**
	 * @return the singleton instance of IDocItActionFilter.
	 */
	public IDocItActionFilter getInstance()
	{
		if (instance == null)
		{
			instance = new IDocItActionFilter();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean testAttribute(Object target, String name, String value)
	{
		if (target instanceof IFile)
		{
			IFile file = (IFile) target;
			String type = file.getFileExtension();
			return (type != null) && ParsingService.isSupported(type);
		}
		return false;
	}

}
