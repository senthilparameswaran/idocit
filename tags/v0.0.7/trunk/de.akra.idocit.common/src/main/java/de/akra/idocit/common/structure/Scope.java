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
package de.akra.idocit.common.structure;

/**
 * A scope for a documentation part of a {@link SignatureElement}.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public enum Scope
{
	/**
	 * An explicit element or resource.
	 */
	EXPLICIT,

	/**
	 * An implicit element or resource.
	 */
	IMPLICIT;

	/**
	 * Converts a string to a <code>Scope</code>. Comparison is case insensitive.
	 * 
	 * @param scope
	 *            The scope as string.
	 * @return The matched <code>Scope</code>.
	 * @throws IllegalArgumentException
	 *             If no <code>Scope</code> matches the string.
	 */
	public static Scope fromString(String scope) throws IllegalArgumentException
	{
		return valueOf(scope.toUpperCase());
	}
}
