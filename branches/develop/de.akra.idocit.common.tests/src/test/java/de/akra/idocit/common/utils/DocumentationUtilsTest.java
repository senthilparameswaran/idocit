package de.akra.idocit.common.utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.ThematicRole;

public final class DocumentationUtilsTest
{
	private static final int IND_SOURCE = 0;

	private Documentation createDocumentation(String rolename, boolean isErrorCase)
	{
		Documentation doc = new Documentation();
		doc.setThematicRole(new ThematicRole(rolename));
		doc.setErrorCase(isErrorCase);

		return doc;
	}

	@Test
	public void testFindDocumentationByRoleName()
	{
		List<Documentation> refDocumentations = new ArrayList<Documentation>(4);

		refDocumentations.add(createDocumentation("SOURCE", false));
		refDocumentations.add(createDocumentation("DESTINATION", false));
		refDocumentations.add(createDocumentation(null, false));
		refDocumentations.add(createDocumentation("AGENT", false));
		refDocumentations.add(createDocumentation("SOURCE", true));

		assertEquals(DocumentationUtils.findDocumentationByRoleName("SOURCE",
				refDocumentations), refDocumentations.get(IND_SOURCE));
		assertEquals(
				DocumentationUtils.findDocumentationByRoleName(null, refDocumentations),
				null);
	}
}
