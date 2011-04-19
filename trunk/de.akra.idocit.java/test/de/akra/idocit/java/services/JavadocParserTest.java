package de.akra.idocit.java.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.Documentation;
import de.akra.idocit.structure.Scope;
import de.akra.idocit.structure.ThematicRole;
import de.akra.idocit.utils.ObjectStructureUtils;

/**
 * Tests for {@link JavadocParser}.
 * <p>
 * Initialize in {@link ObjectStructureUtils} the private attributes
 * <code>supportedAddressees</code> and <code>supportedThematicRoles</code> with
 * {@link Collections#emptyList()}, because the Eclipse Workspace is not available in
 * JUnit Test.
 * </p>
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class JavadocParserTest
{
	private static Logger logger = Logger.getLogger(JavadocParserTest.class.getName());

	/**
	 * Test for {@link JavadocParser#parse(Javadoc)}.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void test() throws SAXException, IOException, ParserConfigurationException
	{
		@SuppressWarnings("deprecation")
		AST a = new AST();
		Javadoc javadoc = a.newJavadoc();

		List<Documentation> documentations = createDocumentations();
		JavadocGenerator.appendDocsToJavadoc(documentations, null, null, javadoc);
		JavadocGenerator.appendDocsToJavadoc(documentations, TagElement.TAG_PARAM,
				"person", javadoc);
		JavadocGenerator.appendDocsToJavadoc(documentations, TagElement.TAG_RETURN, null,
				javadoc);
		JavadocGenerator.appendDocsToJavadoc(documentations, TagElement.TAG_THROWS,
				"IllegalArgException", javadoc);

		// add the number of used documentations to this list, to make an assertion
		List<Documentation> allUsedDocs = new ArrayList<Documentation>(
				documentations.size() * 4);
		allUsedDocs.addAll(documentations);
		allUsedDocs.addAll(documentations);
		allUsedDocs.addAll(documentations);
		allUsedDocs.addAll(documentations);

		logger.log(Level.INFO, javadoc.toString());

		List<Documentation> convertedDocs = JavadocParser.parse(javadoc);

		logger.log(Level.INFO, allUsedDocs.toString());
		logger.log(Level.INFO, convertedDocs.toString());

		Assert.assertEquals(allUsedDocs, convertedDocs);
	}

	/**
	 * Create a list of {@link Documentation}s for testing.
	 * 
	 * @return List<Documentation>
	 */
	private static List<Documentation> createDocumentations()
	{
		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createDocumentation());
		documentations.add(createDocumentation());
		return documentations;
	}

	/**
	 * Create a test Documentation.
	 * 
	 * @return a new Documentation with some constant values.
	 */
	private static Documentation createDocumentation()
	{
		Documentation newDoc = new Documentation();
		newDoc.setScope(Scope.EXPLICIT);
		newDoc.setThematicRole(new ThematicRole("OBJECT"));

		newDoc.setSignatureElementIdentifier("/person:Person/name:java.lang.String");

		Map<Addressee, String> docMap = new HashMap<Addressee, String>();
		List<Addressee> addresseeSequence = new LinkedList<Addressee>();

		Addressee developer = new Addressee("Developer");
		Addressee manager = new Addressee("Manager");

		docMap.put(developer, "Documenation for developers.");
		addresseeSequence.add(developer);

		docMap.put(manager, "Documenation for managers.");
		addresseeSequence.add(manager);

		newDoc.setDocumentation(docMap);
		newDoc.setAddresseeSequence(addresseeSequence);

		return newDoc;
	}
}
