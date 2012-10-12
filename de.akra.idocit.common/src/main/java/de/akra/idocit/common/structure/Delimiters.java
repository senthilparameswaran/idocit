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
package de.akra.idocit.common.structure;

import java.util.regex.Pattern;

/**
 * This class holds the delimiters for building paths of the structure. Each Parser
 * implementation must provide these three different delimiter chars or Strings for the
 * used programming language. The delimiters must be something forbidden in the
 * programming language so it can be used without trouble.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class Delimiters
{
	/**
	 * Delimiter for the type information.
	 */
	private String typeDelimiter;

	/**
	 * Delimiter for the namespace information.
	 */
	private String namespaceDelimiter;

	/**
	 * Delimiter to separate the path elements.
	 */
	private String pathDelimiter;

	/**
	 * Quotes and returns the <code>typeDelimiter</code> for the use in regular
	 * expressions.
	 * 
	 * If the typeDelimiter is <code>null</code>, an empty string is returned.
	 * 
	 * @return the quoted string of the delimiter.
	 * @see Pattern#quote(String)
	 */
	public String getQuotedTypeDelimiter()
	{
		if (typeDelimiter != null)
		{
			return Pattern.quote(typeDelimiter);
		}
		else
		{
			return "";
		}
	}

	/**
	 * Quotes and returns the <code>namespaceDelimiter</code> for the use in regular
	 * expressions.
	 * 
	 * If the namespaceDelimiter is <code>null</code>, an empty string is returned.
	 * 
	 * @return the quoted string of the delimiter.
	 * @see Pattern#quote(String)
	 */
	public String getQuotedNamespaceDelimiter()
	{
		if (namespaceDelimiter != null)
		{
			return Pattern.quote(namespaceDelimiter);
		}
		else
		{
			return "";
		}
	}

	/**
	 * Quotes and returns the <code>pathDelimiter</code> for the use in regular
	 * expressions.
	 * 
	 * If the pathDelimiter is <code>null</code>, an empty string is returned.
	 * 
	 * @return the quoted string of the delimiter.
	 * @see Pattern#quote(String)
	 */
	public String getQuotedPathDelimiter()
	{
		if (pathDelimiter != null)
		{
			return Pattern.quote(pathDelimiter);
		}
		else
		{
			return "";
		}
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
				+ ((namespaceDelimiter == null) ? 0 : namespaceDelimiter.hashCode());
		result = prime * result
				+ ((pathDelimiter == null) ? 0 : pathDelimiter.hashCode());
		result = prime * result
				+ ((typeDelimiter == null) ? 0 : typeDelimiter.hashCode());
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
		if (!(obj instanceof Delimiters))
		{
			return false;
		}
		Delimiters other = (Delimiters) obj;
		if (namespaceDelimiter == null)
		{
			if (other.namespaceDelimiter != null)
			{
				return false;
			}
		}
		else if (!namespaceDelimiter.equals(other.namespaceDelimiter))
		{
			return false;
		}
		if (pathDelimiter == null)
		{
			if (other.pathDelimiter != null)
			{
				return false;
			}
		}
		else if (!pathDelimiter.equals(other.pathDelimiter))
		{
			return false;
		}
		if (typeDelimiter == null)
		{
			if (other.typeDelimiter != null)
			{
				return false;
			}
		}
		else if (!typeDelimiter.equals(other.typeDelimiter))
		{
			return false;
		}
		return true;
	}

	/**
	 * String representation for debugging purpose.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Delimiters [typeDelimiter=");
		builder.append(typeDelimiter);
		builder.append(", namespaceDelimiter=");
		builder.append(namespaceDelimiter);
		builder.append(", pathDelimiter=");
		builder.append(pathDelimiter);
		builder.append("]");
		return builder.toString();
	}

	public String getTypeDelimiter() {
		return typeDelimiter;
	}

	public void setTypeDelimiter(String typeDelimiter) {
		this.typeDelimiter = typeDelimiter;
	}

	public String getNamespaceDelimiter() {
		return namespaceDelimiter;
	}

	public void setNamespaceDelimiter(String namespaceDelimiter) {
		this.namespaceDelimiter = namespaceDelimiter;
	}

	public String getPathDelimiter() {
		return pathDelimiter;
	}

	public void setPathDelimiter(String pathDelimiter) {
		this.pathDelimiter = pathDelimiter;
	}
	
}
