package de.akra.idocit.java.services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.structure.Documentation;
import de.akra.idocit.utils.ObjectStructureUtils;

/**
 * Tests for {@link HTMLTableParser}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class HTMLTableParserTest {

	/**
	 * Test for {@link HTMLTableParser#convertJavadocToDocumentations(String)}.
	 * <p>
	 * Initialize in {@link ObjectStructureUtils} the private attributes 
	 * <code>supportedAddressees</code> and <code>supportedThematicRoles</code>
	 * with {@link Collections#emptyList()}, because the Eclipse Workspace is
	 * not available in JUnit Test.
	 * </p>
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testConvertJavadocToDocumentations() throws SAXException,
			IOException, ParserConfigurationException {
		List<Documentation> docs = HTMLTableParser
				.convertJavadocToDocumentations("<table border=\"1\"><tr><td>Element:</td><td>filter.id</td></tr><tr><td>Role:</td><td>Criterion</td></tr><tr><td><b>Developer</b>:</td><td>My extisting documentation: this is a really good filter ;)</td></tr><tr><td><b>Manager</b>:</td><td>sdfgsgjse gjgijwejj</td></tr></table>");

		// to get rid of the warning
		docs.size();
	}
}
