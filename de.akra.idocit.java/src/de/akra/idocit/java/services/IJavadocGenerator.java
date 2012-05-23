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

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;

/**
 * Represents generators for Javadoc from {@link Documentation}s of
 * {@link SignatureElement}s.
 * 
 * @author Jan Christian Krause
 */
public interface IJavadocGenerator
{
	/**
	 * Creates Javadoc-representation for all {@link Documentation}s in
	 * <code>signatureElement</code> and it's children.
	 * 
	 * @param javadocTags
	 *            [SOURCE]
	 * @param referenceGridName
	 *            [ATTRIBUTE] Added to the generated Javadoc
	 * @param javadoc
	 *            [DESTINATION] This object is modified.
	 * 
	 * @format The format of the generated Javadoc depends on the implementation.
	 * 
	 * @thematicgrid Putting Operations
	 */
	public void appendDocsToJavadoc(List<Documentation> documentations, String tagName,
			String paramName, String thematicGridName, Javadoc javadoc);
}