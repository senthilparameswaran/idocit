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
