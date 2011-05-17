package de.jankrause.diss.wsdl.common.services;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.junit.Test;

import de.jankrause.diss.wsdl.common.structure.wsdl.WsdlMetadata;
import de.jankrause.diss.wsdl.common.structure.wsdl.WsdlParsingResult;

/**
 * Test Cases for {@link WSDLParsingService}.
 * 
 * @author Jan Christian Krause
 * 
 */
public class WSDLParsingServiceTest {

	/**
	 * Returns the {@link List} of reference-roles for Test Case 1.3.
	 * 
	 * @return The {@link List} of reference-roles for Test Case 1.3
	 */
	private List<String> getReferenceRolesTC1_3() {
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapInComplex.parameters(GetCompletionListComplex).GetCompletionListComplex(MyComplexType).prefixText(string)");
		roles.add("GetCompletionListSoapInComplex.parameters(GetCompletionListComplex).GetCompletionListComplex(MyComplexType).count(int)");
		roles.add("GetCompletionListSoapInComplex.parameters(GetCompletionListComplex).GetCompletionListComplex(MyComplexType).countRecursive(MyComplexType[recursion])");

		return roles;
	}

	/**
	 * Returns the {@link List} of reference-roles for Test Case 1.2.
	 * 
	 * @return The {@link List} of reference-roles for Test Case 1.2
	 */
	private List<String> getReferenceRolesTC1_2() {
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapInSimple.id(int)");

		return roles;
	}

	/**
	 * Returns the {@link List} of reference-roles for Test Case 1.1.
	 * 
	 * @return The {@link List} of reference-roles for Test Case 1.1
	 */
	private List<String> getReferenceRolesTC1_1() {
		List<String> roles = new ArrayList<String>();

		roles.add("GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList(anonymous).prefixText(string)");
		roles.add("GetCompletionListSoapIn.parameters(GetCompletionList).GetCompletionList(anonymous).count(int)");

		return roles;
	}

	/**
	 * Returns the {@link List} of reference-roles for Test Case 2.
	 * 
	 * @return The {@link List} of reference-roles for Test Case 2
	 */
	private List<String> getReferenceRolesTC2() {
		List<String> roles = new ArrayList<String>();

		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).strUserName(string)");
		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).strUserPass(string)");
		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).Name(string)");
		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).SendType(string)");
		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).SendList(anonymous).anonymous(no_definition)");
		roles.add("addSendListSoapIn.parameters(addSendList).addSendList(anonymous).iQty(int)");

		return roles;
	}

	/**
	 * Tests {@link WSDLParsingService#extractRoles(Message, javax.wsdl.Types)}.
	 * 
	 * @throws WSDLException
	 *             In case of an error
	 */
	@Test
	public void testExtractRoles() throws WSDLException {
		/**
		 * Positive tests
		 */
		// Test case #1: extract the composite-role of an input-message
		// correctly.
		{
			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
			Definition def = reader.readWSDL("resources/wsdls/wsdl_46001");

			// TC 1.1
			{
				Message message = def.getMessage(new QName(
						"http://tempuri.org/", "GetCompletionListSoapIn"));
				List<String> roles = WSDLParsingService.extractRoles(message,
						def.getTypes());
				assertEquals(getReferenceRolesTC1_1(), roles);
			}

			// TC 1.2
			{
				Message message = def
						.getMessage(new QName("http://tempuri.org/",
								"GetCompletionListSoapInSimple"));
				List<String> roles = WSDLParsingService.extractRoles(message,
						def.getTypes());
				assertEquals(getReferenceRolesTC1_2(), roles);
			}

			// TC 1.3
			{
				Message message = def
						.getMessage(new QName("http://tempuri.org/",
								"GetCompletionListSoapInComplex"));
				List<String> roles = WSDLParsingService.extractRoles(message,
						def.getTypes());
				assertEquals(getReferenceRolesTC1_3(), roles);
			}
		}
		// Test case #2: if an element in the type-declaration has no name, it
		// should be handeled as "anonymous"-element (like anonymous types). If
		// it contains no further definition, its type should be labeled as
		// "no_definition".
		{
			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
			Definition def = reader.readWSDL("resources/wsdls/wsdl_100101");
			// TC 2
			{
				Message message = def.getMessage(new QName(
						"http://tempuri.org/", "addSendListSoapIn"));
				List<String> roles = WSDLParsingService.extractRoles(message,
						def.getTypes());
				assertEquals(getReferenceRolesTC2(), roles);
			}
		}
		// Test case #3:
		{
			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
			Definition def = reader.readWSDL("resources/wsdls/wsdl_1106201");
			{
				Message message = def
						.getMessage(new QName(
								"http://phoebus.cs.man.ac.uk:1977/axis/services/enzymes.enzyme.derived",
								"createEmptyJobRequest"));
				List<String> roles = WSDLParsingService.extractRoles(message,
						def.getTypes());
				System.out.println(roles);
			}
		}
		/**
		 * Negative tests
		 */
		{
			// None
		}
	}

	/**
	 * Returns the {@link List} of reference-roles for TC 1 of {@link
	 * this#testExtractMetadata()}.
	 * 
	 * @return The {@link List} of reference-roles for TC 1 of {@link
	 *         this#testExtractMetadata()}
	 */
	private List<String> getReferenceOutputMessageRolesTC1() {
		List<String> roles = new ArrayList<String>();

		roles.add("addSendListSoapOut.parameters(addSendListResponse).addSendListResponse(anonymous).addSendListResult(SendState)");
		roles.add("addSendListSoapOut.parameters(addSendListResponse).addSendListResponse(anonymous).iQty(int)");

		return roles;
	}

	/**
	 * Returns the reference-{@link WsdlParsingResult} for TC 1 of {@link
	 * this#testExtractMetadata()}.
	 * 
	 * @return The reference-{@link WsdlParsingResult} for TC 1 of {@link
	 *         this#testExtractMetadata()}
	 */
	private WsdlParsingResult getReferenceParsingResultTC1() {
		WsdlParsingResult result = new WsdlParsingResult();

		result.setDoublePortTypes(new ArrayList<String>());
		result.setUnparseableWsdlFiles(new ArrayList<String>());

		List<WsdlMetadata> metadatas = new ArrayList<WsdlMetadata>();

		WsdlMetadata metadata1 = new WsdlMetadata();
		metadata1.setId(0);
		metadata1.setInputMessagePaths(getReferenceRolesTC2());
		metadata1.setOutputMessagePaths(getReferenceOutputMessageRolesTC1());
		metadata1.setOperationIdentifier("addSendList");
		metadata1.setPortTypeName("WebServiceBOQSoap");
		metadata1.setWsdlFilename("wsdl_100101");

		metadatas.add(metadata1);
		result.setWsdlMetadata(metadatas);

		return result;
	}

	/**
	 * Tests {@link WSDLParsingService#extractMetadata(java.io.File[])}.
	 * 
	 * @throws WSDLException
	 *             In case of an error
	 */
	@Test
	public void testExtractMetadata() throws WSDLException {
		/**
		 * Positive tests
		 */
		{
			// TC 1: metadata of given WSDL-file should be extracted. Self
			// defined simple
			// types should be handeled like build-in simple-types (e.g. int,
			// string, etc.).
			{
				File[] wsdlFiles = new File[1];
				wsdlFiles[0] = new File("resources/wsdls/wsdl_100101");
				WsdlParsingResult parsingResult = WSDLParsingService
						.extractMetadata(wsdlFiles);

				assertEquals(getReferenceParsingResultTC1(), parsingResult);
			}
			
			// TC 2: the service should parse two equal wsdls only once.
			{
				// TODO: Write test case
			}
		}

		/**
		 * Negative tests
		 */
		{
			// TC 1
			{

			}
		}
	}
}
