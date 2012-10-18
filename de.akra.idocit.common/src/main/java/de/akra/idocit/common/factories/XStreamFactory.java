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
package de.akra.idocit.common.factories;

import com.thoughtworks.xstream.XStream;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.ThematicGrid;
import de.akra.idocit.common.structure.ThematicRole;

/**
 * <table border="1" cellspacing="0">
 * <tr>
 * <td>Role:</td>
 * <td>AGENT</td>
 * </tr>
 * <tr>
 * <td><b>Developer</b>:</td>
 * <td>Provides factory-methods for streams handeled with the XStream library.</td>
 * </tr>
 * </table>
 */
public final class XStreamFactory {
	public static final String XML_ALIAS_THEMATIC_GRID = "thematicGrid";

	public static final String XML_ALIAS_THEMATIC_ROLE = "thematicRole";
	
	public static final String XML_ALIAS_ADDRESSEE = "addressee";

	/**
	 * <table border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ACTION</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Returns a XStream which could read and write ThematicGrids.</td>
	 * </tr>
	 * </table>
	 */
	public static XStream configureXStreamForThematicGrid() {
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_THEMATIC_GRID, ThematicGrid.class);
		stream.alias(XML_ALIAS_THEMATIC_ROLE, ThematicRole.class);
		return stream;
	}

	/**
	 * <table border="1" cellspacing="0">
	 * <tr>
	 * <td>Role:</td>
	 * <td>ACTION</td>
	 * </tr>
	 * <tr>
	 * <td><b>Developer</b>:</td>
	 * <td>Returns a XStream which could read and write ThematicRoles.</td>
	 * </tr>
	 * </table>
	 */
	public static XStream configureXStreamForThematicRoles() {
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_THEMATIC_ROLE, ThematicRole.class);
		return stream;
	}
	
	public static XStream configureXStreamForAddressee() {
		XStream stream = new XStream();
		stream.alias(XML_ALIAS_ADDRESSEE, Addressee.class);
		return stream;
	}

}
