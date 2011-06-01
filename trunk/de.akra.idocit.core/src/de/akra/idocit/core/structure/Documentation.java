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
package de.akra.idocit.core.structure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * It represents a documentation part of a {@link SignatureElement}.<br>
 * The documentation map contains mappings from {@link Addressee}s to the documentation as
 * String.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class Documentation
{
	/**
	 * The documentation; mapping from an {@link Addressee} to the documentation as
	 * String. The sequence in which the addressees were loaded has to be stored in
	 * {@link Documentation#addresseeSequence}.
	 */
	private Map<Addressee, String> documentation;

	/**
	 * The sequence in which the addressees are read out of the source code and added to
	 * {@link Documentation#documentation}. The read sequence should be kept, because
	 * there should be no change by only reading and writing the documentation. New
	 * addressees must be append to the end.
	 */
	private List<Addressee> addresseeSequence;

	/**
	 * The assigned thematic role.
	 */
	private ThematicRole thematicRole;

	/**
	 * The selected scope.
	 */
	private Scope scope;

	/**
	 * This represents the unique identifier (complete path) of a signature element.
	 * <p style="white-space: nowrap;">
	 * Format for such a path:<br>
	 * &lt;MESSAGE_NAME&gt;"."&lt;PART_NAME&gt;"("&lt;ELEMENT_TYPE&gt;")"["."
	 * &lt;ELEMENT_NAME&gt;"("&lt;ELEMENT_TYPE&gt;")"]*
	 * </p>
	 */
	private String signatureElementIdentifier;

	/**
	 * Constructor. Initializes <code>documentation</code> and
	 * <code>addresseeSequence</code>.
	 */
	public Documentation()
	{
		documentation = new HashMap<Addressee, String>();
		addresseeSequence = new LinkedList<Addressee>();
	}

	/**
	 * Make a deep copy of the Documentation.
	 * 
	 * @return A clone of this Documentation.
	 */
	public final Documentation copy()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(scope);
		newDoc.setThematicRole(thematicRole);
		newDoc.setSignatureElementIdentifier(signatureElementIdentifier);

		Map<Addressee, String> newDocMap = new HashMap<Addressee, String>();
		Set<Addressee> keys = documentation.keySet();
		for (Addressee key : keys)
		{
			newDocMap.put(key, documentation.get(key));
		}
		newDoc.setDocumentation(newDocMap);

		List<Addressee> newAddresseeSequence = new LinkedList<Addressee>();
		for (Addressee addressee : addresseeSequence)
		{
			newAddresseeSequence.add(addressee);
		}
		newDoc.setAddresseeSequence(newAddresseeSequence);

		return newDoc;
	}

	/**
	 * @return the documentation
	 */
	public Map<Addressee, String> getDocumentation()
	{
		return documentation;
	}

	/**
	 * @param documentation
	 *            the documentation to set
	 */
	public void setDocumentation(Map<Addressee, String> documentation)
	{
		this.documentation = documentation;
	}

	/**
	 * @return the addresseeSequence
	 */
	public List<Addressee> getAddresseeSequence()
	{
		return addresseeSequence;
	}

	/**
	 * @param addresseeSequence
	 *            the addresseeSequence to set
	 */
	public void setAddresseeSequence(List<Addressee> addresseeSequence)
	{
		this.addresseeSequence = addresseeSequence;
	}

	/**
	 * @return the thematicRole
	 */
	public ThematicRole getThematicRole()
	{
		return thematicRole;
	}

	/**
	 * @param thematicRole
	 *            the thematicRole to set
	 */
	public void setThematicRole(ThematicRole thematicRole)
	{
		this.thematicRole = thematicRole;
	}

	/**
	 * @return the scope
	 */
	public Scope getScope()
	{
		return scope;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(Scope scope)
	{
		this.scope = scope;
	}

	/**
	 * @param signatureElementIdentifier
	 *            the signatureElementIdentifier to set
	 */
	public void setSignatureElementIdentifier(String signatureElementIdentifier)
	{
		this.signatureElementIdentifier = signatureElementIdentifier;
	}

	/**
	 * @return the signatureElementIdentifier
	 */
	public String getSignatureElementIdentifier()
	{
		return signatureElementIdentifier;
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
				+ ((addresseeSequence == null) ? 0 : addresseeSequence.hashCode());
		result = prime * result
				+ ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime
				* result
				+ ((signatureElementIdentifier == null) ? 0 : signatureElementIdentifier
						.hashCode());
		result = prime * result + ((thematicRole == null) ? 0 : thematicRole.hashCode());
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
		if (!(obj instanceof Documentation))
		{
			return false;
		}
		Documentation other = (Documentation) obj;
		if (addresseeSequence == null)
		{
			if (other.addresseeSequence != null)
			{
				return false;
			}
		}
		else if (!addresseeSequence.equals(other.addresseeSequence))
		{
			return false;
		}
		if (documentation == null)
		{
			if (other.documentation != null)
			{
				return false;
			}
		}
		else if (!documentation.equals(other.documentation))
		{
			return false;
		}
		if (scope != other.scope)
		{
			return false;
		}
		if (signatureElementIdentifier == null)
		{
			if (other.signatureElementIdentifier != null)
			{
				return false;
			}
		}
		else if (!signatureElementIdentifier.equals(other.signatureElementIdentifier))
		{
			return false;
		}
		if (thematicRole == null)
		{
			if (other.thematicRole != null)
			{
				return false;
			}
		}
		else if (!thematicRole.equals(other.thematicRole))
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
		builder.append("Documentation [documentation=");
		builder.append(documentation);
		builder.append(", addresseeSequence=");
		builder.append(addresseeSequence);
		builder.append(", thematicRole=");
		builder.append(thematicRole);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", signatureElementName=");
		builder.append(signatureElementIdentifier);
		builder.append("]");
		return builder.toString();
	}
}
