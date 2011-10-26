package de.akra.idocit.common.factories;

import com.thoughtworks.xstream.XStream;

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
}
