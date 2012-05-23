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
package de.akra.idocit.java.utils;

import org.eclipse.jdt.core.dom.TagElement;

import de.akra.idocit.java.constants.CustomTaglets;

public class JavadocUtils
{
	public static boolean isSubParam(String tagName)
	{
		return CustomTaglets.SUB_PARAM.equals(tagName);
	}

	public static boolean isParam(String tagName)
	{
		return TagElement.TAG_PARAM.equals(tagName);
	}

	public static boolean isThrows(String tagName)
	{
		return TagElement.TAG_THROWS.equals(tagName);
	}

	public static boolean isReturn(String tagName)
	{
		return TagElement.TAG_RETURN.equals(tagName);
	}

	public static boolean isStandardJavadocTaglet(String tagName)
	{
		boolean isReturn = isReturn(tagName);
		boolean isThrows = TagElement.TAG_THROWS.equals(tagName);

		return isParam(tagName) || isReturn || isThrows;
	}

	public static boolean isSubReturn(String tagName)
	{
		return CustomTaglets.SUB_RETURN.equals(tagName);
	}

	public static boolean isIdocitJavadocTaglet(String tagName)
	{
		boolean isSubParam = isSubParam(tagName);
		boolean isSubReturn = isSubReturn(tagName);

		return isSubParam || isSubReturn;
	}

}
