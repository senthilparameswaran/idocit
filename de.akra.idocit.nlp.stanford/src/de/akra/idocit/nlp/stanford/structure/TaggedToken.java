/*******************************************************************************
 * Copyright 2011 AKRA GmbH and Jan Christian Krause
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


/**
 * Represents a tagged token. As tagset the {@link PennTreebankTag}s are used.
 * 
 * @author Jan Christian Krause
 *
 */
public class TaggedToken {

	private String token = null;
	
	private PennTreebankTag tag = null;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public PennTreebankTag getTag() {
		return tag;
	}

	public void setTag(PennTreebankTag tag) {
		this.tag = tag;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		TaggedToken other = (TaggedToken) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaggedToken [");
		if (tag != null) {
			builder.append("tag=");
			builder.append(tag);
			builder.append(", ");
		}
		if (token != null) {
			builder.append("token=");
			builder.append(token);
		}
		builder.append("]");
		return builder.toString();
	}
}
