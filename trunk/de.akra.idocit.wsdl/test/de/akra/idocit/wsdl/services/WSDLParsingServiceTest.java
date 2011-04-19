package de.akra.idocit.wsdl.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.junit.Test;

import de.jankrause.diss.wsdl.common.services.WSDLParsingService;

/**
 * Tests for {@link WSDLParsingService}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class WSDLParsingServiceTest
{

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(WSDLParsingServiceTest.class
			.getName());

	private List<String> getReferenceRoles3()
	{
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapInComplex.parameters(GetCompletionListComplex).GetCompletionListComplex(MyComplexType).prefixText(string)");
		roles.add("GetCompletionListSoapInComplex.parameters(GetCompletionListComplex).GetCompletionListComplex(MyComplexType).count(int)");

		return roles;
	}

	private List<String> getReferenceRoles2()
	{
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapInSimple.id(int)");

		return roles;
	}

	private List<String> getReferenceRoles()
	{
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList(anonymous).prefixText(string)");
		roles.add("GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList(anonymous).count(int)");

		return roles;
	}

	private void logList(List<String> roles)
	{
		for (String role : roles)
		{
			logger.log(Level.INFO, role);
		}
	}

	/**
	 * Tests {@link WSDLParsingService#extractRoles(Message, javax.wsdl.Types)}
	 * 
	 * @throws WSDLException
	 */
	@Test
	public void testExtractRoles() throws WSDLException
	{
		/**
		 * Positive tests
		 */

		// Test case #1: extract the composite-role of an input-message
		// correctly.
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		Definition def = reader.readWSDL("test/source/wsdl_46001");

		{
			Message message = def.getMessage(new QName("http://tempuri.org/",
					"GetCompletionListSoapIn"));
			List<String> roles = WSDLParsingService.extractRoles(message, def.getTypes());
			logList(roles);
			assertEquals(getReferenceRoles(), roles);
		}
		{
			Message message = def.getMessage(new QName("http://tempuri.org/",
					"GetCompletionListSoapInSimple"));
			List<String> roles = WSDLParsingService.extractRoles(message, def.getTypes());
			logList(roles);
			assertEquals(getReferenceRoles2(), roles);
		}
		{
			Message message = def.getMessage(new QName("http://tempuri.org/",
					"GetCompletionListSoapInComplex"));
			List<String> roles = WSDLParsingService.extractRoles(message, def.getTypes());
			logList(roles);
			assertEquals(getReferenceRoles3(), roles);
		}
		/**
		 * Negative tests
		 */
	}
}
