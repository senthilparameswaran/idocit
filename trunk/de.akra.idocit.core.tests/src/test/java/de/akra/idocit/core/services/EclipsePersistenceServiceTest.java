package de.akra.idocit.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.core.constants.AddresseeConstants;
import de.akra.idocit.core.services.impl.EclipsePersistenceService;

/**
 * Contains Test cases for {@link EclipsePersistenceService}.
 * 
 * @author Jan Christian Krause
 * 
 */
public class EclipsePersistenceServiceTest
{
	/**
	 * Only the addressee "Developer" (stored as constant at
	 * {@link AddresseeConstants#MOST_IMPORTANT_ADDRESSEE}) is default - all other
	 * addressees not!
	 */
	@Test
	public void testOnlyDeveloperIsDefaultAddressee()
	{
		List<Addressee> addressees = new EclipsePersistenceService()
				.loadConfiguredAddressees();

		for (Addressee addressee : addressees)
		{
			if (AddresseeConstants.MOST_IMPORTANT_ADDRESSEE.equals(addressee.getName()))
			{
				assertTrue(addressee.isDefault());
			}
			else
			{
				assertFalse(addressee.isDefault());
			}
		}
	}
}
