package de.akra.idocit.ui.composites;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.RolesRecommendations;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.TestUtils;

/**
 * Testcases for {@link DocumentListItemComposite}
 * 
 * @author Jan Christian Krause
 * 
 */
public class DocumentListItemCompositeTest
{

	private Shell mainWindow = null;

	private DocumentItemListComposite compositeUnderTest = null;

	@Before
	public void setUp() throws Exception
	{
		Display display = PlatformUI.createDisplay();
		mainWindow = new Shell(display, SWT.BORDER | SWT.RESIZE);
		FillLayout layout = new FillLayout();
		mainWindow.setLayout(layout);

		compositeUnderTest = new DocumentItemListComposite(mainWindow, SWT.NONE);
	}

	@After
	public void tearDown() throws Exception
	{
		mainWindow.dispose();
	}

	private Documentation createDocumentation(String roleName)
	{
		Documentation doc = new Documentation();
		doc.setAddresseeSequence(TestUtils.createDeveloperSequence());
		doc.setErrorCase(true);
		doc.setThematicRole(new ThematicRole(roleName));

		Map<Addressee, String> documentations = new HashMap<Addressee, String>();
		documentations.put(TestUtils.createDeveloper(),
				"This is the developer documentation text.");
		doc.setDocumentation(documentations);

		return doc;
	}

	/**
	 * Test if the 2nd level role recommendations merged result of the former 2nd level
	 * recommendations and the associated roles.
	 */
	@Test
	public void testIfSecondLevelRecommendationsAreSetCorrectly()
	{
		DocumentItemListCompositeSelection emptySelection = new DocumentItemListCompositeSelection();

		emptySelection.setActiveAddressees(new ArrayList<Integer>());
		emptySelection
				.setDisplayedAddresseesOfDocumentations(new ArrayList<List<Addressee>>());

		List<Documentation> documentations = new ArrayList<Documentation>();
		documentations.add(createDocumentation("SOURCE"));
		documentations.add(createDocumentation("DESTINATION"));
		documentations.add(createDocumentation("AGENT"));

		emptySelection.setDocumentations(documentations);

		List<ThematicRole> old2ndLevelRecommendations = new ArrayList<ThematicRole>();
		old2ndLevelRecommendations.add(new ThematicRole("ACTION"));
		
		RolesRecommendations recommendations = new RolesRecommendations(
				new ArrayList<ThematicRole>(), old2ndLevelRecommendations);

		emptySelection.setRolesRecommendations(recommendations);

		compositeUnderTest.setSelection(emptySelection);

		List<ThematicRole> referenceRoles = new ArrayList<ThematicRole>();
		referenceRoles.add(new ThematicRole("SOURCE"));
		referenceRoles.add(new ThematicRole("DESTINATION"));
		referenceRoles.add(new ThematicRole("AGENT"));
		referenceRoles.add(new ThematicRole("ACTION"));

		List<ThematicRole> actualSecondLevelRecommendations = compositeUnderTest
				.getSelection().getRolesRecommendations().getSecondLevelRecommendations();

		assertTrue(referenceRoles.containsAll(actualSecondLevelRecommendations));
		assertTrue(actualSecondLevelRecommendations.containsAll(referenceRoles));
	}
}
