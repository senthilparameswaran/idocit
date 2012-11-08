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
import java.util.List;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Selection for {@link DocumentItemListComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DocumentItemListCompositeSelection implements ISelection
{

	/**
	 * The path to the signature element, that have to set to new {@link Documentation}s.
	 */
	private String signatureElementPath;

	/**
	 * The displayed {@link Documentation}.
	 */
	private List<Documentation> documentations;

	/**
	 * List of indexes for the active addressee of the <code>documentations</code>.
	 */
	private List<Integer> activeAddressees;

	/**
	 * List of all available addressees.
	 */
	private List<Addressee> addresseeList;

	/**
	 * List of all available thematic roles.
	 */
	private RolesRecommendations rolesRecommendations;

	/**
	 * List of Addressees that are displayed with tabs for each {@link Documentation} in
	 * <code>documentations</code>. This list has the same order as the
	 * <code>documentations</code>.
	 */
	private List<List<Addressee>> displayedAddresseesForDocumentations;

	/**
	 * True, if it is allowed to add a documentation part to this signature element. Is
	 * false, if not allowed or no element was selected.
	 * 
	 * @see SignatureElement#isDocumentationAllowed()
	 */
	private boolean documentationAllowed = true;

	/**
	 * @return the documentations
	 */
	public List<Documentation> getDocumentations()
	{
		if (documentations == Collections.EMPTY_LIST)
		{
			documentations = new ArrayList<Documentation>(
					SignatureElement.DEFAULT_ARRAY_SIZE);
		}
		return documentations;
	}

	/**
	 * @param documentations
	 *            the documentations to set
	 */
	public void setDocumentations(List<Documentation> documentations)
	{
		this.documentations = documentations;
	}

	/**
	 * @return the activaAddressees
	 */
	public List<Integer> getActiveAddressees()
	{
		return activeAddressees;
	}

	/**
	 * @param activeAddressees
	 *            the activaAddressees to set
	 */
	public void setActiveAddressees(List<Integer> activeAddressees)
	{
		this.activeAddressees = activeAddressees;
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
	public RolesRecommendations getRolesRecommendations()
	{
		return rolesRecommendations;
	}

	/**
	 * @param thematicRoleList
	 *            the thematicRoleList to set
	 */
	public void setRolesRecommendations(RolesRecommendations rolesRecommendations)
	{
		this.rolesRecommendations = rolesRecommendations;
	}

	/**
	 * @param signatureElementPath
	 *            the signatureElementPath to set
	 */
	public void setSignatureElementPath(String signatureElementPath)
	{
		this.signatureElementPath = signatureElementPath;
	}

	/**
	 * @return the signatureElementPath
	 */
	public String getSignatureElementPath()
	{
		return signatureElementPath;
	}

	/**
	 * @param documentationAllowed
	 *            the documentationAllowed to set
	 */
	public void setDocumentationAllowed(boolean documentationAllowed)
	{
		this.documentationAllowed = documentationAllowed;
	}

	/**
	 * @return the documentationAllowed
	 */
	public boolean isDocumentationAllowed()
	{
		return documentationAllowed;
	}

	/**
	 * @param displayedAddresseesForDocumentations
	 *            the displayedAddresseesOfDocumentations to set
	 */
	public void setDisplayedAddresseesOfDocumentations(
			List<List<Addressee>> displayedAddresseesForDocumentations)
	{
		this.displayedAddresseesForDocumentations = displayedAddresseesForDocumentations;
	}

	/**
	 * @return the displayedAddresseesForDocumentations
	 */
	public List<List<Addressee>> getDisplayedAddresseesForDocumentations()
	{
		return displayedAddresseesForDocumentations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activeAddressees == null) ? 0 : activeAddressees.hashCode());
		result = prime * result
				+ ((addresseeList == null) ? 0 : addresseeList.hashCode());
		result = prime * result + (documentationAllowed ? 1231 : 1237);
		result = prime * result
				+ ((documentations == null) ? 0 : documentations.hashCode());
		result = prime * result
				+ ((signatureElementPath == null) ? 0 : signatureElementPath.hashCode());
		result = prime * result
				+ ((rolesRecommendations == null) ? 0 : rolesRecommendations.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof DocumentItemListCompositeSelection))
		{
			return false;
		}
		DocumentItemListCompositeSelection other = (DocumentItemListCompositeSelection) obj;
		if (activeAddressees == null)
		{
			if (other.activeAddressees != null)
			{
				return false;
			}
		}
		else if (!activeAddressees.equals(other.activeAddressees))
		{
			return false;
		}
		if (addresseeList == null)
		{
			if (other.addresseeList != null)
			{
				return false;
			}
		}
		else if (!addresseeList.equals(other.addresseeList))
		{
			return false;
		}
		if (documentationAllowed != other.documentationAllowed)
		{
			return false;
		}
		if (documentations == null)
		{
			if (other.documentations != null)
			{
				return false;
			}
		}
		else if (!documentations.equals(other.documentations))
		{
			return false;
		}
		if (signatureElementPath == null)
		{
			if (other.signatureElementPath != null)
			{
				return false;
			}
		}
		else if (!signatureElementPath.equals(other.signatureElementPath))
		{
			return false;
		}
		if (rolesRecommendations == null)
		{
			if (other.rolesRecommendations != null)
			{
				return false;
			}
		}
		else if (!rolesRecommendations.equals(other.rolesRecommendations))
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DocumentItemListCompositeSelection [signatureElementPath=");
		builder.append(signatureElementPath);
		builder.append(", documentations=");
		builder.append(documentations);
		builder.append(", activeAddressees=");
		builder.append(activeAddressees);
		builder.append(", addresseeList=");
		builder.append(addresseeList);
		builder.append(", thematicRoleList=");
		builder.append(rolesRecommendations);
		builder.append(", displayedAddresseesOfDocumentations=");
		builder.append(displayedAddresseesForDocumentations);
		builder.append(", documentationAllowed=");
		builder.append(documentationAllowed);
		builder.append("]");
		return builder.toString();
	}
}
