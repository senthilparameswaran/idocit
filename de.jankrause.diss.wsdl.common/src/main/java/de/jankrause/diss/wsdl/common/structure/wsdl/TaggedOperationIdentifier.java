package de.jankrause.diss.wsdl.common.structure.wsdl;

import java.util.List;

public final class TaggedOperationIdentifier {

	private List<TaggedToken> taggedTokens = null;

	public List<TaggedToken> getTaggedTokens() {
		return taggedTokens;
	}

	public void setTaggedTokens(List<TaggedToken> taggedTokens) {
		this.taggedTokens = taggedTokens;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((taggedTokens == null) ? 0 : taggedTokens.hashCode());
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
		TaggedOperationIdentifier other = (TaggedOperationIdentifier) obj;
		if (taggedTokens == null) {
			if (other.taggedTokens != null)
				return false;
		} else if (!taggedTokens.equals(other.taggedTokens))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaggedOperationIdentifier [");
		if (taggedTokens != null) {
			builder.append("taggedTokens=");
			builder.append(taggedTokens);
		}
		builder.append("]");
		return builder.toString();
	}
}
