/*******************************************************************************
 * Copyright 2011 AKRA GmbH 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.akra.idocit.nlp.stanford.structure;

import java.util.List;

public final class TaggedOperationIdentifier
{

	private List<TaggedToken> taggedTokens = null;

	public List<TaggedToken> getTaggedTokens()
	{
		return taggedTokens;
	}

	public void setTaggedTokens(List<TaggedToken> taggedTokens)
	{
		this.taggedTokens = taggedTokens;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taggedTokens == null) ? 0 : taggedTokens.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaggedOperationIdentifier other = (TaggedOperationIdentifier) obj;
		if (taggedTokens == null)
		{
			if (other.taggedTokens != null)
				return false;
		}
		else if (!taggedTokens.equals(other.taggedTokens))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("TaggedOperationIdentifier [");
		if (taggedTokens != null)
		{
			builder.append("taggedTokens=");
			builder.append(taggedTokens);
		}
		builder.append("]");
		return builder.toString();
	}
}
