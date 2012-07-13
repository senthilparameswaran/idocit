package de.akra.idocit.ui.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.ui.composites.RecommendRolesComposite;
import de.akra.idocit.ui.composites.RecommendRolesCompositeSelection;

/**
 * Eclipse {@link ViewPart} to show recommended {@link ThematicGrid}s for an identifier (
 * {@link RecommendRolesComposite}).
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class RecommendedGridsView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.akra.idocit.ui.views.RecommendedGridsView";

	private RecommendRolesComposite recommendRolesComposite;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public RecommendedGridsView()
	{
		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		this.recommendRolesComposite = new RecommendRolesComposite(parent, SWT.NONE);
		this.recommendRolesComposite.setSelection(new RecommendRolesCompositeSelection());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		this.recommendRolesComposite.setFocus();
	}
}