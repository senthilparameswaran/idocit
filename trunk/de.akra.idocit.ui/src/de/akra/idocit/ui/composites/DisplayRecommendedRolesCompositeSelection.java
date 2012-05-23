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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pocui.core.composites.ISelection;

import de.akra.idocit.common.structure.ThematicRole;

/**
 * Selection for {@link DisplayRecommendedRolesComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class DisplayRecommendedRolesCompositeSelection implements ISelection {
	private Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles;

	private Set<ThematicRole> assignedThematicRoles;

	private String referenceThematicGridName;

	/**
	 * The names of the collapsed grids / verb classes.
	 */
	private Set<String> collapsedThematicGridNames = Collections.emptySet();

	/**
	 * The default constructor
	 */
	public DisplayRecommendedRolesCompositeSelection() {
		assignedThematicRoles = new HashSet<ThematicRole>();
		referenceThematicGridName = "";
		recommendedThematicRoles = new HashMap<String, Map<ThematicRole, Boolean>>();
		collapsedThematicGridNames = new HashSet<String>();
	}

	/**
	 * Complete Constructor
	 * 
	 * Intention: this constructor has been implemented for convenience within
	 * internal setters. But it could be used from outside as well.
	 * 
	 * @param recommendedThematicRoles
	 *            The recommended thematic roles (key: name of thematic grid,
	 *            value: Map of thematic role to boolean (key: role, value: role
	 *            is mandatory))
	 * @param assignedThematicRoles
	 *            The set of documented roles
	 * @param referenceThematicGridName
	 *            The name of the reference thematic grid
	 * @param collapsedThematicGridNames
	 *            The set of names of collapsed thematic grids
	 */
	public DisplayRecommendedRolesCompositeSelection(
			Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles,
			Set<ThematicRole> assignedThematicRoles,
			String referenceThematicGridName,
			Set<String> collapsedThematicGridNames) {

		this.recommendedThematicRoles = new HashMap<String, Map<ThematicRole, Boolean>>(
				recommendedThematicRoles);
		this.assignedThematicRoles = new HashSet<ThematicRole>(
				assignedThematicRoles);
		this.referenceThematicGridName = referenceThematicGridName;
		this.collapsedThematicGridNames = new HashSet<String>(
				collapsedThematicGridNames);
	}

	/**
	 * @return the assignedThematicRoles
	 */
	public Set<ThematicRole> getAssignedThematicRoles() {
		return new HashSet<ThematicRole>(assignedThematicRoles);
	}

	/**
	 * @param assignedThematicRoles
	 *            the assignedThematicRoles to set
	 */
	public DisplayRecommendedRolesCompositeSelection setAssignedThematicRoles(
			Set<ThematicRole> assignedThematicRoles) {
		return new DisplayRecommendedRolesCompositeSelection(
				recommendedThematicRoles, assignedThematicRoles,
				referenceThematicGridName, collapsedThematicGridNames);
	}

	/**
	 * @param recommendedThematicRoles
	 *            the recommendedThematicRoles to set
	 */
	public DisplayRecommendedRolesCompositeSelection setRecommendedThematicRoles(
			Map<String, Map<ThematicRole, Boolean>> recommendedThematicRoles) {
		return new DisplayRecommendedRolesCompositeSelection(
				recommendedThematicRoles, assignedThematicRoles,
				referenceThematicGridName, collapsedThematicGridNames);
	}

	/**
	 * @return the recommendedThematicRoles
	 */
	public Map<String, Map<ThematicRole, Boolean>> getRecommendedThematicRoles() {
		return new HashMap<String, Map<ThematicRole, Boolean>>(
				recommendedThematicRoles);
	}

	/**
	 * @param collapsedThematicGridNames
	 *            the collapsedThematicGridNames to set
	 */
	public DisplayRecommendedRolesCompositeSelection setCollapsedThematicGridNames(
			Set<String> collapsedThematicGridNames) {
		return new DisplayRecommendedRolesCompositeSelection(
				recommendedThematicRoles, assignedThematicRoles,
				referenceThematicGridName, collapsedThematicGridNames);
	}

	/**
	 * @return the collapsedThematicGridNames
	 */
	public Set<String> getCollapsedThematicGridNames() {
		return new HashSet<String>(collapsedThematicGridNames);
	}

	public String getReferenceThematicGridName() {
		return referenceThematicGridName;
	}

	public DisplayRecommendedRolesCompositeSelection setReferenceThematicGridName(
			String referenceThematicGridName) {
		return new DisplayRecommendedRolesCompositeSelection(
				recommendedThematicRoles, assignedThematicRoles,
				referenceThematicGridName, collapsedThematicGridNames);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((assignedThematicRoles == null) ? 0 : assignedThematicRoles
						.hashCode());
		result = prime
				* result
				+ ((collapsedThematicGridNames == null) ? 0
						: collapsedThematicGridNames.hashCode());
		result = prime
				* result
				+ ((recommendedThematicRoles == null) ? 0
						: recommendedThematicRoles.hashCode());
		result = prime
				* result
				+ ((referenceThematicGridName == null) ? 0
						: referenceThematicGridName.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisplayRecommendedRolesCompositeSelection other = (DisplayRecommendedRolesCompositeSelection) obj;
		if (assignedThematicRoles == null) {
			if (other.assignedThematicRoles != null)
				return false;
		} else if (!assignedThematicRoles.equals(other.assignedThematicRoles))
			return false;
		if (collapsedThematicGridNames == null) {
			if (other.collapsedThematicGridNames != null)
				return false;
		} else if (!collapsedThematicGridNames
				.equals(other.collapsedThematicGridNames))
			return false;
		if (recommendedThematicRoles == null) {
			if (other.recommendedThematicRoles != null)
				return false;
		} else if (!recommendedThematicRoles
				.equals(other.recommendedThematicRoles))
			return false;
		if (referenceThematicGridName == null) {
			if (other.referenceThematicGridName != null)
				return false;
		} else if (!referenceThematicGridName
				.equals(other.referenceThematicGridName))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DisplayRecommendedRolesCompositeSelection [recommendedThematicRoles="
				+ recommendedThematicRoles
				+ ", assignedThematicRoles="
				+ assignedThematicRoles
				+ ", referenceThematicGridName="
				+ referenceThematicGridName
				+ ", collapsedThematicGridNames="
				+ collapsedThematicGridNames + "]";
	}
}
