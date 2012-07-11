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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * The composite with a {@link TreeViewer} in which the {@link SignatureElement} can be
 * selected, which the user want to document.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class SelectSignatureElementComposite
		extends
		AbsComposite<EmptyActionConfiguration, SelectSignatureElementCompositeRC, SelectSignatureElementCompositeSelection>
{
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(SelectSignatureElementComposite.class
			.getName());

	private TreeViewer selectionTreeViewer;
	private ISelectionChangedListener selectionChangedListener;

	private Menu treePopUpMenu;
	private MenuItem menuItemExpandTree;
	private MenuItem menuItemCollapseTree;

	private SelectionListener menuItemExpandTreeSelectionListener;
	private SelectionListener menuItemCollapseTreeSelectionListener;

	/**
	 * @param pvParent
	 * @param pvStyle
	 * @param rc
	 * @throws CompositeInitializationException
	 */
	public SelectSignatureElementComposite(Composite pvParent, int pvStyle,
			SelectSignatureElementCompositeRC rc) throws CompositeInitializationException
	{
		super(pvParent, pvStyle, EmptyActionConfiguration.getInstance(), rc);
	}

	@Override
	protected void initGUI(Composite pvParent) throws CompositeInitializationException
	{
		// both layouts are needed to maximize the TreeViewer in the GUI;
		// maybe check if it works in a better way.
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		this.setLayout(new FillLayout(SWT.VERTICAL));

		selectionTreeViewer = new TreeViewer(this, SWT.VIRTUAL | SWT.HORIZONTAL
				| SWT.VERTICAL | SWT.SINGLE);
		selectionTreeViewer.setContentProvider(new SignatureElementContentProvider());
		selectionTreeViewer.setLabelProvider(new SignatureElementLabelProvider());
		// expand up to Operations as default
		selectionTreeViewer.setAutoExpandLevel(2);

		// add context menu to TreeViewer
		treePopUpMenu = new Menu(this.getShell(), SWT.POP_UP);
		menuItemExpandTree = new MenuItem(treePopUpMenu, SWT.PUSH);
		menuItemExpandTree.setText("Expand subitems");
		menuItemCollapseTree = new MenuItem(treePopUpMenu, SWT.CASCADE);
		menuItemCollapseTree.setText("Collapse subitems");

		selectionTreeViewer.getTree().setMenu(treePopUpMenu);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		selectionChangedListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				treeSelectionChanged(e);
			}
		};

		menuItemExpandTreeSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Object selObj = ((TreeSelection) selectionTreeViewer.getSelection())
						.getFirstElement();
				if (selObj != null)
				{
					selectionTreeViewer.expandToLevel(selObj, TreeViewer.ALL_LEVELS);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};

		menuItemCollapseTreeSelectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Object selObj = ((TreeSelection) selectionTreeViewer.getSelection())
						.getFirstElement();
				if (selObj != null)
				{
					selectionTreeViewer.collapseToLevel(selObj, TreeViewer.ALL_LEVELS);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		};
	}

	/**
	 * Gets the new selected item and invokes
	 * {@link SelectSignatureElementComposite#fireChangeEvent()}.
	 * 
	 * @param evt
	 *            The {@link SelectionChangedEvent}.
	 */
	private void treeSelectionChanged(SelectionChangedEvent evt)
	{
		SelectSignatureElementCompositeSelection selection = getSelection();

		TreeSelection sel = (TreeSelection) evt.getSelection();
		SignatureElement attachedSigElem = (SignatureElement) sel.getFirstElement();

		// fire only if another item is selected
		if (selection.getSelectedSignatureElement() != attachedSigElem)
		{
			selection.setSelectedSignatureElement(attachedSigElem);
			fireChangeEvent(selectionTreeViewer);
		}
	}

	@Override
	protected void doSetSelection(
			SelectSignatureElementCompositeSelection oldInSelection,
			SelectSignatureElementCompositeSelection newInSelection, Object sourceControl)
	{
		if (newInSelection != null && !newInSelection.equals(oldInSelection))
		{
			// create the tree only if the structure was changed.
			// This should not be happen during one session.
			if (oldInSelection == null
					|| newInSelection.getInterfaceArtifact() != oldInSelection
							.getInterfaceArtifact())
			{
				log.log(Level.FINE, "Build tree of SignatureElements.");
				selectionTreeViewer.setInput(newInSelection.getInterfaceArtifact());
			}
		}
	}

	@Override
	protected void addAllListener()
	{
		selectionTreeViewer.addSelectionChangedListener(selectionChangedListener);
		menuItemCollapseTree.addSelectionListener(menuItemCollapseTreeSelectionListener);
		menuItemExpandTree.addSelectionListener(menuItemExpandTreeSelectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		selectionTreeViewer.removeSelectionChangedListener(selectionChangedListener);
		menuItemCollapseTree
				.removeSelectionListener(menuItemCollapseTreeSelectionListener);
		menuItemExpandTree.removeSelectionListener(menuItemExpandTreeSelectionListener);
	}

	@Override
	public void doCleanUp()
	{
		SelectSignatureElementCompositeRC rc = getResourceConfiguration();
		rc.disposeImageCache();
	}

	/**
	 * The {@link ITreeContentProvider} for the
	 * {@link SelectSignatureElementComposite#selectionTreeViewer}.
	 * 
	 * @author Dirk Meier-Eickhoff * @since 0.0.1
	 * @version 0.0.1
	 */
	private static class SignatureElementContentProvider implements ITreeContentProvider
	{
		private static final Object[] EMPTY_ARRAY = new Object[0];

		@Override
		public void dispose()
		{}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{}

		@Override
		public Object[] getElements(Object inputElement)
		{
			return getChildren(inputElement);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getChildren(Object parentElement)
		{
			Object[] children = EMPTY_ARRAY;
			if (parentElement instanceof Parameter)
			{
				Parameter param = (Parameter) parentElement;
				if (param.getComplexType() != null)
				{
					children = param.getComplexType().toArray();
				}
			}
			else if (parentElement instanceof Operation)
			{
				Operation op = (Operation) parentElement;
				List<SignatureElement> list = new ArrayList<SignatureElement>(2);
				if (op.getInputParameters() != null)
				{
					list.add(op.getInputParameters());
				}
				if (op.getOutputParameters() != null)
				{
					list.add(op.getOutputParameters());
				}
				children = concat(list, op.getExceptions());
			}
			else if (parentElement instanceof Parameters)
			{
				Parameters paramList = (Parameters) parentElement;
				children = paramList.getParameters().toArray();
			}
			else if (parentElement instanceof Interface)
			{
				Interface interf = (Interface) parentElement;
				children = concat(interf.getOperations(), interf.getInnerInterfaces());
			}
			else if (parentElement instanceof InterfaceArtifact)
			{
				InterfaceArtifact iArtifact = (InterfaceArtifact) parentElement;
				children = iArtifact.getInterfaces().toArray();
			}
			return children;
		}

		/**
		 * Concatenates the <code>lists</code> of {@link SignatureElement}s in an array of
		 * Objects.
		 * 
		 * @param lists
		 *            The lists to concatenate. If a <code>lists</code>' item is
		 *            <code>null</code> it is ignored.
		 * @return the concatenated lists as new Object[].
		 */
		private Object[] concat(List<? extends SignatureElement>... lists)
		{
			Object[] result = EMPTY_ARRAY;
			if (lists.length > 0)
			{
				// calculate total array size
				int totalLength = 0;
				for (int i = 0; i < lists.length; ++i)
				{
					List<? extends SignatureElement> item = lists[i];
					if (item != null)
					{
						totalLength += item.size();
					}
				}

				// copy elements to new list
				ArrayList<SignatureElement> concatList = new ArrayList<SignatureElement>(
						totalLength);
				for (int i = 0; i < lists.length; ++i)
				{
					List<? extends SignatureElement> item = lists[i];
					if (item != null)
					{
						concatList.addAll(item);
					}
				}

				// convert list to array
				if (!concatList.isEmpty())
				{
					result = concatList.toArray();
				}
			}
			return result;
		}

		@Override
		public Object getParent(Object element)
		{
			if (element instanceof SignatureElement
					&& element != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
			{
				return ((SignatureElement) element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element)
		{
			return getChildren(element).length > 0;
		}

	}

	/**
	 * The {@link ILabelProvider} for the
	 * {@link SelectSignatureElementComposite#selectionTreeViewer}.
	 * 
	 * @author Dirk Meier-Eickhoff * @since 0.0.1
	 * @version 0.0.1
	 */
	private class SignatureElementLabelProvider implements ILabelProvider
	{

		@Override
		public void addListener(ILabelProviderListener listener)
		{}

		@Override
		public void dispose()
		{
			// do nothing. The images are managed in getResourceConfiguration()
		}

		@Override
		public boolean isLabelProperty(Object element, String property)
		{
			// changes in the SignatureElements are never changes in their display names
			// (only in their documentations)
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener)
		{}

		@Override
		public Image getImage(Object element)
		{
			Image img = null;
			if (element instanceof SignatureElement)
			{
				img = getResourceConfiguration().getImageForSignatureElement(
						(SignatureElement) element);
			}
			return img;
		}

		@Override
		public String getText(Object element)
		{
			String name = null;
			if (element instanceof SignatureElement)
			{
				name = ((SignatureElement) element).getDisplayName();
			}
			return name;
		}
	}
}
