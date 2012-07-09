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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.xml.sax.SAXException;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.java.structure.JavaMethod;

/**
 * Parser for {@link Javadoc}. It converts a Javadoc to a list of {@link Documentation}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.3
 * 
 */
public final class JavadocParser extends AbsJavadocParser
{
	/**
	 * Logger.
	 */
	static Logger log = Logger.getLogger(JavadocParser.class.getName());

	static final String JAVADOC_TAG_PARAM = TagElement.TAG_PARAM + "\\s*";
	static final String JAVADOC_TAG_RETURN = TagElement.TAG_RETURN + "\\s*";
	static final String JAVADOC_TAG_THROWS = TagElement.TAG_THROWS + "\\s*";
	static final String JAVADOC_TAG_THEMATICGRID = "@thematicgrid";
	static final String JAVADOC_TAG_THEMATICGRID_PATTERN = JAVADOC_TAG_THEMATICGRID
			+ "\\s*";

	public static final AbsJavadocParser INSTANCE;

	static
	{
		INSTANCE = new JavadocParser();
	}

	/**
	 * Private default constructor due to Singleton Pattern.
	 */
	private JavadocParser()
	{

	}

	/**
	 * Converts the {@link Javadoc} to a list of {@link Documentation}s. The generated
	 * Documentations can then be used to attach them to the corresponding
	 * {@link SignatureElement} in the object structure of the method parameters.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to parse.
	 * @return List of {@link Documentation}s. If <code>javadoc == null</code> then
	 *         {@link Collections#EMPTY_LIST} is returned.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @since 0.0.2
	 */
	public List<Documentation> parseIDocItJavadoc(Javadoc javadoc,
			List<Addressee> addressees, List<ThematicRole> thematicRoles,
			JavaMethod method) throws SAXException, IOException,
			ParserConfigurationException
	{
		if (javadoc == null)
		{
			return Collections.emptyList();
		}
		String html = extractPlainText(javadoc);
		return HTMLTableParser.convertJavadocToDocumentations(html);
	}
}
