/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
package de.akra.idocit.java.structure;

public class StringReplacement
{
	/**
	 * @param javadocUtils
	 */
	public StringReplacement()
	{
		
	}

	private String originalString;
	private String escapedString;

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((escapedString == null) ? 0 : escapedString.hashCode());
		result = prime * result
				+ ((originalString == null) ? 0 : originalString.hashCode());
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
		StringReplacement other = (StringReplacement) obj;
		if (escapedString == null)
		{
			if (other.escapedString != null)
				return false;
		}
		else if (!escapedString.equals(other.escapedString))
			return false;
		if (originalString == null)
		{
			if (other.originalString != null)
				return false;
		}
		else if (!originalString.equals(other.originalString))
			return false;
		return true;
	}

	public String getOriginalString()
	{
		return originalString;
	}

	public void setOriginalString(String originalString)
	{
		this.originalString = originalString;
	}

	public String getEscapedString()
	{
		return escapedString;
	}

	public void setEscapedString(String escapedString)
	{
		this.escapedString = escapedString;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("StringReplacements [originalString=");
		builder.append(originalString);
		builder.append(", escapedString=");
		builder.append(escapedString);
		builder.append("]");
		return builder.toString();
	}
}