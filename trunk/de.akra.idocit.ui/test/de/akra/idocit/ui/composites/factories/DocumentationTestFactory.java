package de.akra.idocit.ui.composites.factories;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.akra.idocit.structure.Addressee;
import de.akra.idocit.structure.Documentation;
import de.akra.idocit.structure.Scope;
import de.akra.idocit.structure.ThematicRole;

/**
 * Helper to create a {@link Documentation} for testing.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class DocumentationTestFactory
{
	/**
	 * The addresses who should be considered independently by writing documentations.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * 
	 */
	public static enum AddresseeEnum
	{
		BUILDER, TESTER, DEVELOPER, ANALYST, SYSTEM_BUILDER, INTEGRATOR, ARCHITECT, MANAGER;
	}

	/**
	 * String values for {@link Addressee}s.
	 */
	public static String[] addresseeArray = new String[] { "Builder", "Tester",
			"Developer", "Analyst", "System Builder", "Integrator", "Architect",
			"Manager" };

	/**
	 * The supported thematic roles for the signature elements.
	 * 
	 * @author Dirk Meier-Eickhoff
	 * 
	 */
	public static enum ThematicRoleEnum
	{
		SOURCE, CRITERION, OBJECT, ACTION, AGENT;
	}

	/**
	 * String values for {@link ThematicRole}s.
	 */
	public static String[] thematicRoleArray = new String[] { "SOURCE", "CRITERION",
			"OBJECT", "ACTION", "AGENT" };

	/**
	 * Create a dummy {@link Documentation}.
	 * 
	 * @return a new {@link Documentation} with test data.
	 */
	public static Documentation createDocumentation()
	{
		Documentation doc = new Documentation();

		doc.setScope(Scope.EXPLICIT);
		ThematicRole d = new ThematicRole("CRITERION");
		doc.setThematicRole(d);

		Map<Addressee, String> docs = doc.getDocumentation();
		List<Addressee> addresseeSequence = doc.getAddresseeSequence();

		Addressee developer = new Addressee("Developer");
		docs.put(developer, "Doku für " + developer.getName());
		addresseeSequence.add(developer);

		Addressee manager = new Addressee("Manager");
		docs.put(manager, "Doku für " + manager.getName());
		addresseeSequence.add(manager);

		Addressee tester = new Addressee("Tester");
		docs.put(tester, "Doku für " + tester.getName());
		addresseeSequence.add(tester);
		return doc;
	}

	/**
	 * Create a List of the {@link Addressee}s.
	 * 
	 * @return {@link List} of {@link Addressee}s as strings.
	 */
	public static List<String> createAddresseeList()
	{
		List<String> addrList = new Vector<String>(addresseeArray.length);

		for (int i = 0; i < addresseeArray.length; ++i)
		{
			addrList.add(addresseeArray[i].toString());
		}
		return addrList;
	}

	/**
	 * Create a List of the {@link ThematicRole}s.
	 * 
	 * @return {@link List} of {@link ThematicRole}s as strings.
	 */
	public static List<String> createThematicRoleList()
	{
		List<String> thematicRoleList = new Vector<String>(thematicRoleArray.length);

		for (int i = 0; i < thematicRoleArray.length; ++i)
		{
			thematicRoleList.add(thematicRoleArray[i].toString());
		}
		return thematicRoleList;
	}

}
