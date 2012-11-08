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
package de.akra.idocit.java.structure;

import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Container for Java attribute information.
 * 
 * @author Dirk Meier-Eickhoff
 * @version 0.0.1
 * @since 0.0.5
 */
public class JavaAttribute implements Comparable<JavaAttribute> {
	private String name;
	private ITypeBinding type;

	public JavaAttribute() {
		this(null, null);
	}

	public JavaAttribute(final String name) {
		this(name, null);
	}

	public JavaAttribute(final String name, final ITypeBinding type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ITypeBinding getType() {
		return type;
	}

	public void setType(ITypeBinding type) {
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "JavaAttribute [name=" + name + ", type=" 
				+ ((type != null) ? type.getName() : "null") 
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JavaAttribute other = (JavaAttribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		return true;
	}

	/**
	 * Compares the attribute {@code name}.
	 */
	@Override
	public int compareTo(final JavaAttribute other) {
		if (other == null)
		{
			return -1;
		}
		else if (name != null && other.getName() == null) 
		{
			return -1;
		}
		else if (name == null && other.getName() != null) 
		{
			return 1;
		} 
		else if (name == null && other.getName() == null) 
		{
			return 0;
		}
		else if (name != null && other.getName() != null) 
		{
			return name.compareTo(other.getName());
		}
		
		return -1;
	}
}
