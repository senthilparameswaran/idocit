/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.composites.CompositeInitializationException;
import org.pocui.swt.composites.AbsComposite;

import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * The composite with a {@link Tree} in which the {@link SignatureElement} can be
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
	private static Logger logger = Logger.getLogger(SelectSignatureElementComposite.class
			.getName());

	private Tree selectionTree;
	private SelectionListener selectionListener;

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
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		selectionTree = new Tree(pvParent, SWT.VIRTUAL | SWT.HORIZONTAL | SWT.VERTICAL
				| SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(selectionTree);
	}

	@Override
	protected void initListener() throws CompositeInitializationException
	{
		selectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				treeSelectionChanged(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				treeSelectionChanged(e);
			}
		};
	}

	/**
	 * Gets the new selected item and invokes
	 * {@link SelectSignatureElementComposite#fireChangeEvent()}.
	 * 
	 * @param evt
	 *            The {@link SelectionEvent}.
	 */
	private void treeSelectionChanged(SelectionEvent evt)
	{
		SelectSignatureElementCompositeSelection selection = getSelection();

		TreeItem item = (TreeItem) evt.item;
		SignatureElement attachedSigElem = (SignatureElement) item
				.getData(item.getText());

		// fire only if another item is selected
		if (selection.getSelectedSignatureElement() != attachedSigElem)
		{
			selection.setSelectedSignatureElement(attachedSigElem);
			fireChangeEvent();
		}
	}

	@Override
	protected void doSetSelection(
			SelectSignatureElementCompositeSelection oldInSelection,
			SelectSignatureElementCompositeSelection newInSelection)
	{
		if (newInSelection != null && !newInSelection.equals(oldInSelection))
		{
			// create the tree only if the structure was changed.
			// This should not be happen during one session.
			if (oldInSelection == null
					|| (oldInSelection != null && newInSelection.getInterfaceArtifact() != oldInSelection
							.getInterfaceArtifact()))
			{
				TreeItem[] items = selectionTree.getItems();
				for (int i = 0; i < items.length; i++)
				{
					items[i].dispose();
				}

				// init tree by first use
				buildTree(selectionTree, newInSelection.getInterfaceArtifact());
			}
		}
	}

	/**
	 * Builds a {@link Tree} from the structure of {@link InterfaceArtifact}.
	 * 
	 * @param parent
	 *            The parent {@link Tree} or {@link TreeItem} for new {@link TreeItem}s.
	 * @param sigElem
	 *            The {@link SignatureElement} that should be added to the {@link Tree}.
	 * @throws IllegalArgumentException
	 *             if the <code>parent</code> is not a {@link Tree} or {@link TreeItem}.
	 */
	private void buildTree(Widget parent, SignatureElement sigElem)
			throws IllegalArgumentException
	{
		if (sigElem != null)
		{
			TreeItem item = addItemToTree(parent, sigElem);

			// build further tree structure
			if (sigElem instanceof Parameter)
			{
				Parameter param = (Parameter) sigElem;
				if (param.getComplexType() != null)
				{
					for (Parameter p : param.getComplexType())
					{
						buildTree(item, p);
					}
				}
			}
			else if (sigElem instanceof Operation)
			{
				Operation op = (Operation) sigElem;
				buildTree(item, op.getInputParameters());
				buildTree(item, op.getOutputParameters());

				List<? extends Parameters> exceptions = op.getExceptions();
				for (Parameters exception : exceptions)
				{
					buildTree(item, exception);
				}
			}
			else if (sigElem instanceof Parameters)
			{
				Parameters paramList = (Parameters) sigElem;
				if (paramList.getParameters() != null)
				{
					for (Parameter p : paramList.getParameters())
					{
						buildTree(item, p);
					}
				}
			}
			else if (sigElem instanceof Interface)
			{
				Interface interf = (Interface) sigElem;

				if (interf.getOperations() != null)
				{
					for (Operation o : interf.getOperations())
					{
						buildTree(item, o);
					}
				}
				if (interf.getInnerInterfaces() != null)
				{
					for (Interface i : interf.getInnerInterfaces())
					{
						buildTree(item, i);
					}
				}
			}
			else if (sigElem instanceof InterfaceArtifact)
			{
				InterfaceArtifact iArtifact = (InterfaceArtifact) sigElem;
				if (iArtifact.getInterfaces() != null)
				{
					for (Interface i : iArtifact.getInterfaces())
					{
						buildTree(item, i);
					}
				}
			}
		}
	}

	/**
	 * Make a {@link TreeItem} from <code>sigElem</code>, add it to the
	 * <code>parent</code> and return the new TreeItem.
	 * 
	 * @param parent
	 *            The parent {@link Tree} or {@link TreeItem}.
	 * @param sigElem
	 *            The {@link SignatureElement} to add to the tree.
	 * @return the created TreeItem.
	 * @throws IllegalArgumentException
	 *             if <code>parent</code> is no {@link Tree} or {@link TreeItem}.
	 */
	private TreeItem addItemToTree(Widget parent, SignatureElement sigElem)
			throws IllegalArgumentException
	{
		// cast parent to create new TreeItem
		TreeItem item;
		if (parent instanceof TreeItem)
		{
			item = new TreeItem((TreeItem) parent, SWT.NONE);
		}
		else if (parent instanceof Tree)
		{
			item = new TreeItem((Tree) parent, SWT.NONE);
		}
		else
		{
			String errMsg = "Not supported Widget for Tree: " + parent;
			logger.log(Level.SEVERE, errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		item.setText(sigElem.getDisplayName());
		item.setData(sigElem.getDisplayName(), sigElem);

		Image icon = getResourceConfiguration().getImageForSignatureElement(sigElem);
		
		if(icon != null){
			item.setImage(icon);
		}
		
		return item;
	}

	@Override
	protected void addAllListener()
	{
		selectionTree.addSelectionListener(selectionListener);
	}

	@Override
	protected void removeAllListener()
	{
		if (selectionTree.getListeners(SWT.Selection).length > 0)
		{
			selectionTree.removeSelectionListener(selectionListener);
		}
	}

	@Override
	public void doCleanUp()
	{
		selectionTree.removeAll();
		SelectSignatureElementCompositeRC rc = getResourceConfiguration();
		rc.disposeImageCache();
	}
}
