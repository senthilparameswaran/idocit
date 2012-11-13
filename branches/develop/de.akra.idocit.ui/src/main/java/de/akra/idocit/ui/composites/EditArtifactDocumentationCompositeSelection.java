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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection for {@link EditArtifactDocumentationComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.2
 * 
 */
public class EditArtifactDocumentationCompositeSelection implements ISelection, Cloneable
{
	private static final int DEFAULT_NUMBER_OF_DOCS_PER_SIGNATURE_ELEMENT = 2;

	private static final double MAP_LOAD_FACTOR = 1.3;

	private static final int DEFAULT_MAP_SIZE = 40;

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger
			.getLogger(EditArtifactDocumentationCompositeSelection.class.getName());

	/**
	 * The structure that should be represented.
	 */
	private InterfaceArtifact interfaceArtifact;

	/**
	 * The active addressees for all {@link SignatureElement}s. Mapping from
	 * {@link SignatureElement}'s id to the list of active addressee tab index.
	 */
	private Map<Integer, List<Integer>> activeAddresseesMap;

	/**
	 * {@link List} of predefined {@link Addressee}s.
	 */
	private List<Addressee> addresseeList;

	/**
	 * {@link List} of predefined {@link ThematicRole}s.
	 */
	private List<ThematicRole> thematicRoleList;

	/**
	 * The currently selected {@link SignatureElement}.
	 */
	private SignatureElement selectedSignatureElement;

	/**
	 * Contains copies of the original {@link Documentation}s for all (at least one time)
	 * selected {@link SignatureElement}s. It is internally used to find out changes in
	 * the documentations of the currently selected {@link SignatureElement}. Mapping from
	 * the SignatureElement's id to it's initially (at startup or after saving) present
	 * Documentations. The cached lists of Documentations are unmodifiable.
	 * 
	 * @since 0.0.2
	 * @see #resetOriginalDocumentations()
	 * @see Collections#unmodifiableList(List)
	 */
	private Map<Integer, List<Documentation>> originalDocumentations = Collections
			.emptyMap();

	/**
	 * Copy of the {@link Documentation}s of the currently selected SignatureElement (
	 * {@link #selectedSignatureElement}). It is internally used (in
	 * {@link #equals(Object)}) to find out changes in the documentations of the selected
	 * {@link SignatureElement}.
	 * 
	 * @since 0.0.2
	 */
	private List<Documentation> currentDocumentations = Collections.emptyList();

	/**
	 * The collapsed {@link ThematicGrid}s in the {@link DisplayRecommendedRolesComposite}
	 * for each {@link Operation}. The map contains the mapping from the id of the
	 * operation to the set of collapsed grid's names.
	 */
	private Map<Integer, Set<String>> collapsedThematicGrids = Collections.emptyMap();

	/**
	 * Mapping of {@link SignatureElement} id to the List of Addressee-tabs that are
	 * displayed for each {@link SignatureElement}'s {@link Documentation}.
	 */
	private Map<Integer, List<List<Addressee>>> displayedAddresseesForSigElemsDocumentations = Collections
			.emptyMap();

	/**
	 * The file which stores the interface artifact.
	 */
	private IFile artifactFile = null;

	/**
	 * Set it to <code>true</code> so that the recommended thematic grids are new derived.
	 */
	private boolean refreshRecommendations = false;

	/**
	 * @return the displayedAddresseesForSigElemsDocumentations
	 */
	public Map<Integer, List<List<Addressee>>> getDisplayedAddresseesForSigElemsDocumentations()
	{
		return displayedAddresseesForSigElemsDocumentations;
	}

	/**
	 * @param displayedAddresseesForSigElemsDocumentations
	 *            the displayedAddresseesForSigElemsDocumentations to set
	 */
	public void setDisplayedAddresseesForSigElemsDocumentations(
			Map<Integer, List<List<Addressee>>> displayedAddresseesForSigElemsDocumentations)
	{
		this.displayedAddresseesForSigElemsDocumentations = displayedAddresseesForSigElemsDocumentations;
	}

	/**
	 * Saves the list of active addressees for the {@link SignatureElement}
	 * <code>signatureElement</code>.
	 * <p>
	 * HINT: Set {@link EditArtifactDocumentationCompositeSelection#interfaceArtifact}
	 * before invoking this method the first time. That ensures an appropriate
	 * initialization size for the
	 * {@link EditArtifactDocumentationCompositeSelection#activeAddresseesMap}, otherwise
	 * {@link EditArtifactDocumentationCompositeSelection#DEFAULT_MAP_SIZE} is used.
	 * </p>
	 * 
	 * @param signatureElementId
	 *            The {@link SignatureElement} id (used as key) to which the
	 *            <code>activeAddressees</code> belongs.
	 * @param activeAddressees
	 *            The {@link List} of active addressees for the
	 *            <code>signatureElement</code>.
	 */
	public void putActiveAddressees(int signatureElementId, List<Integer> activeAddressees)
	{
		if (activeAddressees != null)
		{
			logger.log(Level.FINEST, "signatureElement.id=" + signatureElementId
					+ ", activeAddressees=" + activeAddressees);

			if (this.activeAddresseesMap == null)
			{
				int initSize = DEFAULT_MAP_SIZE;
				if (interfaceArtifact != null)
				{
					// initialize the map with little more space than needed, so
					// that the map need not to grow
					initSize = (int) (interfaceArtifact.size() * MAP_LOAD_FACTOR);
				}
				this.activeAddresseesMap = new ConcurrentHashMap<Integer, List<Integer>>(
						initSize);
			}
			this.activeAddresseesMap.put(signatureElementId, activeAddressees);
		}
		else
		{
			logger.log(Level.WARNING, "signatureElement.id=" + signatureElementId
					+ ", activeAddressees = null");
		}
	}

	/**
	 * Get the {@link List} of active addressees for the <code>signatureElement</code>. If
	 * there is no List for the <code>signatureElement</code>, a new one with default
	 * values (indexes == 0) is returned.
	 * 
	 * @param signatureElementId
	 *            The {@link SignatureElement#getId()} whose active addressees should be
	 *            received.
	 * @return {@link List} of indexes of the active addressees.
	 */
	public List<Integer> getActiveAddressees(int signatureElementId)
	{
		List<Integer> result;

		// if the map not exists or the signature element is not in it ...
		if (activeAddresseesMap == null
				|| (result = activeAddresseesMap.get(signatureElementId)) == null)
		{
			// ... create a new list of active addressee-tabs for an
			// SignatureElement and put them to the map
			result = new ArrayList<Integer>(DEFAULT_NUMBER_OF_DOCS_PER_SIGNATURE_ELEMENT);

			putActiveAddressees(signatureElementId, result);
		}

		logger.log(Level.FINEST, "signatureElement.id=" + signatureElementId
				+ ", result=" + result + ", activeAddresseesMap.size="
				+ activeAddresseesMap.size());

		return result;
	}

	/**
	 * @return the addresseeList
	 */
	public List<Addressee> getAddresseeList()
	{
		return addresseeList;
	}

	/**
	 * @param addresseeList
	 *            the addresseeList to set
	 */
	public void setAddresseeList(List<Addressee> addresseeList)
	{
		this.addresseeList = addresseeList;
	}

	/**
	 * @return the thematicRoleList
	 */
	public List<ThematicRole> getThematicRoleList()
	{
		return thematicRoleList;
	}

	/**
	 * @param thematicRoleList
	 *            the thematicRoleList to set
	 */
	public void setThematicRoleList(List<ThematicRole> thematicRoleList)
	{
		this.thematicRoleList = thematicRoleList;
	}

	/**
	 * @return the selectedSignatureElement
	 */
	public SignatureElement getSelectedSignatureElement()
	{
		return selectedSignatureElement;
	}

	/**
	 * @param selectedSignatureElement
	 *            the selectedSignatureElement to set
	 */
	public void setSelectedSignatureElement(SignatureElement selectedSignatureElement)
	{
		this.selectedSignatureElement = selectedSignatureElement;
	}

	/**
	 * @return the interfaceArtifact
	 */
	public InterfaceArtifact getInterfaceArtifact()
	{
		return interfaceArtifact;
	}

	/**
	 * @param interfaceArtifact
	 *            the interfaceArtifact to set
	 */
	public void setInterfaceArtifact(InterfaceArtifact interfaceArtifact)
	{
		this.interfaceArtifact = interfaceArtifact;
	}

	/**
	 * @return the activeAddresseesMap
	 */
	public Map<Integer, List<Integer>> getActiveAddresseesMap()
	{
		return activeAddresseesMap;
	}

	/**
	 * @param activeAddresseesMap
	 *            the activeAddresseesMap to set
	 */
	public void setActiveAddresseesMap(Map<Integer, List<Integer>> activeAddresseesMap)
	{
		this.activeAddresseesMap = activeAddresseesMap;
	}

	/**
	 * @param artifactFile
	 *            the artifactFile to set
	 */
	public void setArtifactFile(IFile artifactFile)
	{
		this.artifactFile = artifactFile;
	}

	/**
	 * @return the artifactFile
	 */
	public IFile getArtifactFile()
	{
		return artifactFile;
	}

	/**
	 * @param collapsedThematicGrids
	 *            the expandedThematicGrids to set
	 */
	public void setCollapsedThematicGrids(Map<Integer, Set<String>> collapsedThematicGrids)
	{
		this.collapsedThematicGrids = collapsedThematicGrids;
	}

	/**
	 * @return the collapsedThematicGrids
	 */
	public Map<Integer, Set<String>> getCollapsedThematicGrids()
	{
		return collapsedThematicGrids;
	}

	/**
	 * Get the names of the collapsed {@link ThematicGrid}s for the {@link Operation}
	 * <code>key</code>.
	 * 
	 * @param key
	 *            The {@link Operation} as key to get the collapsed grids.
	 * @return The set of {@link ThematicGrid} names that are collapsed.
	 */
	public Set<String> getCollapsedThematicGrids(int key)
	{
		Set<String> res = collapsedThematicGrids.get(Integer.valueOf(key));
		if (res == null)
		{
			res = new HashSet<String>();
		}
		return res;
	}

	/**
	 * Put the collapsed {@link ThematicGrid} names for an {@link Operation}.
	 * 
	 * @param key
	 *            The {@link Operation}'s id.
	 * @param collapsedThematicGridNames
	 *            Set of the grid names, that are collapsed.
	 */
	public void putCollapsedThematicGrids(int key, Set<String> collapsedThematicGridNames)
	{
		if (collapsedThematicGrids == Collections.EMPTY_MAP)
		{
			collapsedThematicGrids = new HashMap<Integer, Set<String>>();
		}
		this.collapsedThematicGrids.put(Integer.valueOf(key), collapsedThematicGridNames);
	}

	/**
	 * Get the List of the list of Addressees that are displayed for each
	 * {@link Documentation} of the {@link SignatureElement}. <code>key</code>.
	 * 
	 * @param key
	 *            The {@link SignatureElement}' id as key.
	 * @return List of the list of Addressees that are displayed as tabs.
	 */
	public List<List<Addressee>> getDisplayedAddresseesForSigElemsDocumentations(int key)
	{
		List<List<Addressee>> res = displayedAddresseesForSigElemsDocumentations
				.get(Integer.valueOf(key));
		if (res == null)
		{
			res = new ArrayList<List<Addressee>>();
		}
		return res;
	}

	/**
	 * Put the list of lists of {@link Addressee}s that are displayed for each
	 * {@link Documentation} of the {@link SignatureElement}. The list
	 * <code>displayedAddresseesForSigElemsDocumentations</code> has the same order as the
	 * {@link Documentation}s of the {@link SignatureElement}.
	 * 
	 * @param key
	 *            The {@link SignatureElement}'s id.
	 * @param displayedAddresseesForSigElemsDocumentations
	 *            List of lists of {@link Addressee}s that are displayed.
	 * 
	 */
	public void putDisplayedAddresseesForSigElemsDocumentations(int key,
			List<List<Addressee>> displayedAddresseesForSigElemsDocumentations)
	{
		if (this.displayedAddresseesForSigElemsDocumentations == Collections.EMPTY_MAP)
		{
			this.displayedAddresseesForSigElemsDocumentations = new HashMap<Integer, List<List<Addressee>>>();
		}
		this.displayedAddresseesForSigElemsDocumentations.put(Integer.valueOf(key),
				displayedAddresseesForSigElemsDocumentations);
	}

	/**
	 * Copies and caches the {@link Documentation}s. It is needed to find out changes in
	 * the documentations of selected {@link SignatureElement}.
	 * 
	 * @param signatureElementId
	 *            The {@link SignatureElement} id (used as key) to which the
	 *            <code>documentations</code> belongs.
	 * @param documentations
	 *            The original documentations from the {@link SignatureElement} to cache.
	 */
	// Changes due to Issue #62
	public void putOriginalDocumentations(int signatureElementId,
			List<Documentation> documentations)
	{
		if (originalDocumentations == null
				|| originalDocumentations == Collections.EMPTY_MAP)
		{
			this.originalDocumentations = new HashMap<Integer, List<Documentation>>(
					DEFAULT_MAP_SIZE);
		}

		Integer id = Integer.valueOf(signatureElementId);
		if (originalDocumentations.get(id) == null)
		{
			List<Documentation> origDocs = new ArrayList<Documentation>(
					documentations.size());
			for (Documentation doc : documentations)
			{
				origDocs.add(doc.copy());
			}
			originalDocumentations.put(id, Collections.unmodifiableList(origDocs));
		}
	}

	// End changes due to Issue #62

	/**
	 * Use only {@link #putOriginalDocumentations(int, List)} to add original
	 * documentations.
	 * 
	 * @param originalDocumentations
	 *            the originalDocumentations to set
	 */
	// Changes due to Issue #62
	public void setOriginalDocumentations(
			Map<Integer, List<Documentation>> originalDocumentations)
	{
		this.originalDocumentations = originalDocumentations;
	}

	// End changes due to Issue #62

	/**
	 * Clears the map {@link #originalDocumentations}. Use this method after saving an
	 * artifact.
	 */
	// Changes due to Issue #62
	public void resetOriginalDocumentations()
	{
		if (this.originalDocumentations != null)
		{
			this.originalDocumentations.clear();
		}
	}

	// End changes due to Issue #62

	/**
	 * @return the original Documentations for the given SignatureElement in an
	 *         unmodifiable list. If the documentations are not cached <code>null</code>
	 *         is returned (must not happen).
	 */
	// Changes due to Issue #62
	public List<Documentation> getOriginalDocumentations(int signatureElementId)
	{
		return originalDocumentations.get(Integer.valueOf(signatureElementId));
	}

	// End changes due to Issue #62

	/**
	 * @return the originalDocumentations
	 */
	// Changes due to Issue #62
	public Map<Integer, List<Documentation>> getOriginalDocumentations()
	{
		return originalDocumentations;
	}

	/**
	 * Copies and caches the {@link Documentation}s. It is needed to find out changes in
	 * the documentations of the selected {@link SignatureElement}.
	 * 
	 * @param documentations
	 *            the current documentations from the current {@link SignatureElement} to
	 *            cache.
	 */
	// Changes due to Issue #21
	public void setCurrentDocumentations(List<Documentation> documentations)
	{
		this.currentDocumentations = new ArrayList<Documentation>();
		for (Documentation doc : documentations)
		{
			this.currentDocumentations.add(doc.copy());
		}
	}

	// End changes due to Issue #21

	// End changes due to Issue #62

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("EditArtifactDocumentationCompositeSelection [interfaceArtifact=")
				.append(interfaceArtifact).append(", activeAddresseesMap=")
				.append(activeAddresseesMap).append(", addresseeList=")
				.append(addresseeList).append(", thematicRoleList=")
				.append(thematicRoleList).append(", selectedSignatureElement=")
				.append(selectedSignatureElement).append(", originalDocumentations=")
				.append(originalDocumentations).append(", currentDocumentations=")
				.append(currentDocumentations).append(", collapsedThematicGrids=")
				.append(collapsedThematicGrids)
				.append(", displayedAddresseesForSigElemsDocumentations=")
				.append(displayedAddresseesForSigElemsDocumentations)
				.append(", artifactFile=").append(artifactFile)
				.append(", refreshRecommendations=").append(refreshRecommendations)
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeAddresseesMap == null) ? 0 : activeAddresseesMap.hashCode());
		result = prime * result
				+ ((addresseeList == null) ? 0 : addresseeList.hashCode());
		result = prime
				* result
				+ ((collapsedThematicGrids == null) ? 0 : collapsedThematicGrids
						.hashCode());
		result = prime
				* result
				+ ((currentDocumentations == null) ? 0 : currentDocumentations.hashCode());
		result = prime
				* result
				+ ((displayedAddresseesForSigElemsDocumentations == null) ? 0
						: displayedAddresseesForSigElemsDocumentations.hashCode());
		result = prime * result
				+ ((interfaceArtifact == null) ? 0 : interfaceArtifact.hashCode());
		result = prime
				* result
				+ ((originalDocumentations == null) ? 0 : originalDocumentations
						.hashCode());
		result = prime * result + (refreshRecommendations ? 1231 : 1237);
		result = prime
				* result
				+ ((selectedSignatureElement == null) ? 0 : selectedSignatureElement
						.hashCode());
		result = prime * result
				+ ((thematicRoleList == null) ? 0 : thematicRoleList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EditArtifactDocumentationCompositeSelection other = (EditArtifactDocumentationCompositeSelection) obj;
		if (activeAddresseesMap == null)
		{
			if (other.activeAddresseesMap != null)
				return false;
		}
		else if (!activeAddresseesMap.equals(other.activeAddresseesMap))
			return false;
		if (addresseeList == null)
		{
			if (other.addresseeList != null)
				return false;
		}
		else if (!addresseeList.equals(other.addresseeList))
			return false;
		if (collapsedThematicGrids == null)
		{
			if (other.collapsedThematicGrids != null)
				return false;
		}
		else if (!collapsedThematicGrids.equals(other.collapsedThematicGrids))
			return false;
		if (currentDocumentations == null)
		{
			if (other.currentDocumentations != null)
				return false;
		}
		else if (!currentDocumentations.equals(other.currentDocumentations))
			return false;
		if (displayedAddresseesForSigElemsDocumentations == null)
		{
			if (other.displayedAddresseesForSigElemsDocumentations != null)
				return false;
		}
		else if (!displayedAddresseesForSigElemsDocumentations
				.equals(other.displayedAddresseesForSigElemsDocumentations))
			return false;
		if (interfaceArtifact == null)
		{
			if (other.interfaceArtifact != null)
				return false;
		}
		else if (!interfaceArtifact.equals(other.interfaceArtifact))
			return false;
		if (originalDocumentations == null)
		{
			if (other.originalDocumentations != null)
				return false;
		}
		else if (!originalDocumentations.equals(other.originalDocumentations))
			return false;
		if (refreshRecommendations != other.refreshRecommendations)
			return false;
		if (selectedSignatureElement == null)
		{
			if (other.selectedSignatureElement != null)
				return false;
		}
		else if (!selectedSignatureElement.equals(other.selectedSignatureElement))
			return false;
		if (thematicRoleList == null)
		{
			if (other.thematicRoleList != null)
				return false;
		}
		else if (!thematicRoleList.equals(other.thematicRoleList))
			return false;
		return true;
	}

	public boolean isRefreshRecommendations()
	{
		return refreshRecommendations;
	}

	public void setRefreshRecommendations(boolean refreshRecommendations)
	{
		this.refreshRecommendations = refreshRecommendations;
	}

	/**
	 * Clone the flat structure of this selection.
	 */
	@Override
	public EditArtifactDocumentationCompositeSelection clone()
	{
		final EditArtifactDocumentationCompositeSelection newSelection = new EditArtifactDocumentationCompositeSelection();

		newSelection.setActiveAddresseesMap(activeAddresseesMap);
		newSelection.setAddresseeList(addresseeList);
		newSelection.setArtifactFile(artifactFile);
		newSelection.setInterfaceArtifact(interfaceArtifact);
		newSelection.setSelectedSignatureElement(selectedSignatureElement);
		newSelection.setThematicRoleList(thematicRoleList);
		newSelection.setCollapsedThematicGrids(collapsedThematicGrids);
		newSelection
				.setDisplayedAddresseesForSigElemsDocumentations(displayedAddresseesForSigElemsDocumentations);
		newSelection.setOriginalDocumentations(originalDocumentations);
		newSelection.setRefreshRecommendations(refreshRecommendations);
		return newSelection;
	}
}
