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
package de.akra.idocit.java.services;

import java.util.List;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.java.exceptions.ParsingException;
import de.akra.idocit.java.structure.JavaMethod;

/**
 * Represents generators for Javadoc from {@link Documentation}s of
 * {@link SignatureElement}s.
 * 
 * @author Jan Christian Krause
 */
public interface IJavadocGenerator
{
	/**
	 * Append the information out of <code>documentations</code> to the {@link Javadoc}
	 * block comment. If <code>tagName != null</code> the documentations are added to a
	 * new {@link TagElement} with that name. Add first the general description text with
	 * <code>tagName == null</code>. After that all other wished tags.
	 * 
	 * @param documentations
	 *            [SOURCE] The list of {@link Documentation}s which should be converted to
	 *            {@link Javadoc}.
	 * @param tagName
	 *            [ATTRIBUTE] The name of the tag element, or <code>null</code> (see
	 *            {@link TagElement#setTagName(String)}).
	 * @param paramName
	 *            [ATTRIBUTE] The name of the described parameter, or <code>null</code> if
	 *            no name is needed.
	 * @param thematicGridName
	 *            [ATTRIBUTE] Reference-grid of the given Java method. This parameter is
	 *            nullable.
	 * @param javadoc
	 *            [DESTINATION] This object is modified.
	 * @param additionalTagElements
	 *            [ATTRIBUTE] Existing Javadoc tags which are kept and not changed by
	 *            iDocIt!.
	 * @param method
	 *            [DESTINATION] The given Javadoc belongs to this method.
	 * @throws ParsingException
	 *             [NONE]
	 * @format The format of the generated Javadoc depends on the implementation.
	 * 
	 * @thematicgrid Putting Operations
	 */
	public void appendDocsToJavadoc(List<Documentation> documentations, String tagName,
			String paramName, String thematicGridName, Javadoc javadoc,
			List<TagElement> additionalTagElements, JavaMethod method)
			throws ParsingException;
}