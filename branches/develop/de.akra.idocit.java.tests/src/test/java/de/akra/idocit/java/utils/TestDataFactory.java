/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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
package de.akra.idocit.java.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import source.CustomerService;
import de.akra.idocit.common.constants.ThematicRoleConstants;
import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Numerus;
import de.akra.idocit.common.structure.Parameter;
import de.akra.idocit.common.structure.Parameters;
import de.akra.idocit.common.structure.SignatureElement;
import de.akra.idocit.common.structure.ThematicRole;
import de.akra.idocit.common.utils.StringUtils;
import de.akra.idocit.common.utils.TestUtils;
import de.akra.idocit.common.utils.ThematicRoleUtils;
import de.akra.idocit.core.services.impl.ServiceManager;
import de.akra.idocit.core.utils.DescribedItemUtils;
import de.akra.idocit.java.services.AddresseeUtils;
import de.akra.idocit.java.structure.JavaInterface;
import de.akra.idocit.java.structure.JavaInterfaceArtifact;
import de.akra.idocit.java.structure.JavaMethod;
import de.akra.idocit.java.structure.JavaParameter;
import de.akra.idocit.java.structure.JavaParameters;

/**
 * Contains utility-methods for the construction of {@link JavaInterfaceArtifact}s for
 * testing purposes.
 * 
 * @author Jan Christian Krause
 * 
 */
public class TestDataFactory
{
	/**
	 * Returns a list of one {@link Addressee} with the name <code>addresseeName</code>.
	 * 
	 * @param addresseeName
	 *            [ATTRIBUTE]
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	private static List<Addressee> createReferenceAddresseeList(String addresseeName)
	{
		Addressee addressee = DescribedItemUtils.findAddressee(addresseeName);
		List<Addressee> addressees = new ArrayList<Addressee>();
		addressees.add(addressee);
		return addressees;
	}

	/**
	 * Returns a map with only one element: key is an {@link Addressee} with the name
	 * <code>addresseeName</code>, value is an empty string.
	 * 
	 * @param addresseeName
	 *            [ATTRIBUTE]
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	private static Map<Addressee, String> createComparisonDocumentation(
			final String addresseeName)
	{
		final Map<Addressee, String> comparisonDocumentation = new HashMap<Addressee, String>();
		final Addressee addressee = DescribedItemUtils.findAddressee(addresseeName);

		comparisonDocumentation.put(addressee, StringUtils.EMPTY);

		return comparisonDocumentation;
	}

	public static JavaMethod createDocumentedCheckInvariant(String addresseeName,
			boolean documentationChanged, TypeDeclaration invariantServiceIntfc)
	{
		JavaMethod methodCheckInvariant = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method",
				"Checking Operations", Numerus.SINGULAR);
		MethodDeclaration checkInvariantMeth = (MethodDeclaration) invariantServiceIntfc
				.bodyDeclarations().get(0);
		methodCheckInvariant.setRefToASTNode(checkInvariantMeth);
		methodCheckInvariant.setIdentifier("checkInvariant");
		methodCheckInvariant.setDocumentationChanged(documentationChanged);

		addDocumentation(methodCheckInvariant, addresseeName, "RULE",
				"Maximum length of an address are 40 chars.",
				methodCheckInvariant.getIdentifier(), false);
		addDocumentation(methodCheckInvariant, addresseeName, "ORDERING", "",
				methodCheckInvariant.getIdentifier(), true);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodCheckInvariant,
				"Parameters", Numerus.SINGULAR, false);
		methodCheckInvariant.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("String");
		parameterParameters.setIdentifier("mailAddress");
		parameterParameters.setSignatureElementPath("mailAddress:java.lang.String");
		parameterParameters.setQualifiedDataTypeName("java.lang.String");
		parameterParameters.setDocumentationChanged(documentationChanged);
		inputParameters.addParameter(parameterParameters);

		addDocumentation(parameterParameters, addresseeName, "OBJECT", StringUtils.EMPTY,
				parameterParameters.getSignatureElementPath(), false);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodCheckInvariant,
				"ReturnType", Numerus.SINGULAR, false);
		methodCheckInvariant.setOutputParameters(outputParameters);

		JavaParameter report = new JavaParameter(outputParameters, Numerus.PLURAL, false);
		report.setDataTypeName("boolean");
		report.setQualifiedDataTypeName("boolean");
		report.setDocumentationChanged(documentationChanged);
		outputParameters.addParameter(report);

		addDocumentation(report, addresseeName, "REPORT",
				"<code>false</code> if the rule is violated",
				report.getSignatureElementPath(), false);

		return methodCheckInvariant;
	}

	/**
	 * Returns the method
	 * {@link CustomerService#findCustomersByName(source.NameParameters)} as
	 * iDocIt!-structure.
	 * 
	 * @param addresseeName
	 *            [ATTRIBUTE] All added {@link Documentation}s contain one
	 *            {@link Addressee} with this name
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static JavaMethod createDocumentedFindCustomerByName(String addresseeName,
			boolean documentationChanged, TypeDeclaration customerServiceIntf)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method",
				"Searching Operations", Numerus.SINGULAR);
		MethodDeclaration findCustomerByIdMeth = (MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(1);
		methodFindCustomerById.setRefToASTNode(findCustomerByIdMeth);
		methodFindCustomerById.setIdentifier("findCustomersByName");
		methodFindCustomerById.setQualifiedIdentifier("findCustomersByName");
		methodFindCustomerById.setDocumentationChanged(documentationChanged);

		addDocumentation(methodFindCustomerById, addresseeName, "ORDERING",
				"Alphabetically by lastname", null, false);
		addDocumentation(methodFindCustomerById, addresseeName, "SOURCE", "CRM System",
				null, false);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		inputParameters.setIdentifier(StringUtils.EMPTY);
		inputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("NameParameters");
		parameterParameters.setIdentifier("parameters");
		parameterParameters.setQualifiedIdentifier("parameters");
		parameterParameters.setQualifiedDataTypeName("source.NameParameters");
		parameterParameters.setDocumentationChanged(documentationChanged);
		parameterParameters.setSignatureElementPath("parameters:source.NameParameters");
		inputParameters.addParameter(parameterParameters);
		addDocumentation(parameterParameters, addresseeName, "COMPARISON",
				"This is the customer.", parameterParameters.getSignatureElementPath(),
				false);
		addDocumentation(parameterParameters, addresseeName, "SOURCE",
				"This is the source.", parameterParameters.getSignatureElementPath(),
				false);

		// Attributes of NameParameters
		JavaParameter attrFirstName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrFirstName.setDataTypeName("String");
		attrFirstName.setQualifiedDataTypeName("java.lang.String");
		attrFirstName.setIdentifier("firstName");
		attrFirstName.setQualifiedIdentifier("source.NameParameters.firstName");
		attrFirstName.setDocumentationChanged(documentationChanged);
		attrFirstName
				.setSignatureElementPath("parameters:source.NameParameters/source.NameParameters.firstName:java.lang.String");
		parameterParameters.addParameter(attrFirstName);

		addDocumentation(attrFirstName, addresseeName, "COMPARISON", StringUtils.EMPTY,
				attrFirstName.getSignatureElementPath(), false);

		JavaParameter attrLastName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrLastName.setDataTypeName("String");
		attrLastName.setQualifiedDataTypeName("java.lang.String");
		attrLastName.setIdentifier("lastName");
		attrLastName.setQualifiedIdentifier("source.NameParameters.lastName");
		attrLastName
				.setSignatureElementPath("parameters:source.NameParameters/source.NameParameters.lastName:java.lang.String");
		attrLastName.setDocumentationChanged(documentationChanged);
		parameterParameters.addParameter(attrLastName);

		addDocumentation(attrLastName, addresseeName, "COMPARISON", StringUtils.EMPTY,
				attrLastName.getSignatureElementPath(), false);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		outputParameters.setIdentifier(StringUtils.EMPTY);
		outputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomerList = new JavaParameter(outputParameters,
				Numerus.PLURAL, false);
		returnCustomerList.setDataTypeName("List<Customer>");
		returnCustomerList.setQualifiedDataTypeName("java.util.List<source.Customer>");
		returnCustomerList.setIdentifier("List<Customer>");
		returnCustomerList.setQualifiedIdentifier("java.util.List<source.Customer>");
		returnCustomerList.setDocumentationChanged(documentationChanged);
		returnCustomerList
				.setSignatureElementPath("java.util.List<source.Customer>:java.util.List<source.Customer>");
		outputParameters.addParameter(returnCustomerList);

		addDocumentation(returnCustomerList, addresseeName, "OBJECT",
				"This is the object.", returnCustomerList.getSignatureElementPath(),
				false);
		addDocumentation(returnCustomerList, addresseeName, "SOURCE",
				"This is the source.", returnCustomerList.getSignatureElementPath(),
				false);

		// Exception
		List<JavaParameters> exceptions = new ArrayList<JavaParameters>();

		JavaParameters ioExceptions = new JavaParameters(methodFindCustomerById,
				"Throws", Numerus.SINGULAR, false);
		ioExceptions.setQualifiedIdentifier(StringUtils.EMPTY);
		ioExceptions.setIdentifier(StringUtils.EMPTY);

		JavaParameter ioException = new JavaParameter(ioExceptions, Numerus.SINGULAR,
				false);
		ioException.setDataTypeName("IOException");
		ioException.setQualifiedDataTypeName("java.io.IOException");
		ioException.setSignatureElementPath("java.io.IOException:java.io.IOException");
		ioException.setIdentifier("IOException");
		ioException.setQualifiedIdentifier("java.io.IOException");

		addDocumentation(ioException, addresseeName, null, "In case of an error",
				ioException.getSignatureElementPath(), false);

		addDocumentation(ioException, addresseeName, "ATTRIBUTE",
				"This is also an attribute.", ioException.getSignatureElementPath(),
				false);

		ioExceptions.addParameter(ioException);
		exceptions.add(ioExceptions);
		methodFindCustomerById.setExceptions(exceptions);

		return methodFindCustomerById;
	}

	public static JavaMethod createDocumentedFindCustomerByNameWithCustomerNames(
			String addresseeName, TypeDeclaration customerServiceIntf,
			boolean documentationChanged)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method",
				"Searching Operations", Numerus.SINGULAR);
		MethodDeclaration findCustomerByIdMeth = (MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(2);
		methodFindCustomerById.setRefToASTNode(findCustomerByIdMeth);
		methodFindCustomerById.setIdentifier("findCustomerByName");
		methodFindCustomerById.setQualifiedIdentifier("findCustomerByName");
		methodFindCustomerById.setDocumentationChanged(documentationChanged);

		addDocumentation(
				methodFindCustomerById,
				addresseeName,
				"ACTION",
				"Only customers who placed an order within the last year are considered.",
				null, false);
		addDocumentation(methodFindCustomerById, addresseeName, "ORDERING",
				"Alphabetically by lastname", null, false);
		addDocumentation(methodFindCustomerById, addresseeName, "SOURCE", "CRM System",
				null, false);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		inputParameters.setIdentifier(StringUtils.EMPTY);
		inputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("CustomerNameParameters");
		parameterParameters.setIdentifier("parameters");
		parameterParameters.setQualifiedIdentifier("parameters");
		parameterParameters.setQualifiedDataTypeName("source.CustomerNameParameters");
		parameterParameters.setDocumentationChanged(documentationChanged);
		parameterParameters
				.setSignatureElementPath("parameters:source.CustomerNameParameters");
		inputParameters.addParameter(parameterParameters);

		JavaParameter customerParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		customerParameters.setDataTypeName("Customer");
		customerParameters.setIdentifier("customer");
		customerParameters
				.setQualifiedIdentifier("source.CustomerNameParameters.customer");
		customerParameters.setQualifiedDataTypeName("source.Customer");
		customerParameters.setDocumentationChanged(documentationChanged);
		customerParameters
				.setSignatureElementPath("parameters:source.CustomerNameParameters/source.CustomerNameParameters.customer:source.Customer");
		parameterParameters.addParameter(customerParameters);

		// Attributes of NameParameters
		JavaParameter attrFirstName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrFirstName.setDataTypeName("String");
		attrFirstName.setQualifiedDataTypeName("java.lang.String");
		attrFirstName.setIdentifier("firstName");
		attrFirstName.setDocumentationChanged(documentationChanged);
		attrFirstName.setQualifiedIdentifier("source.Customer.firstName");
		attrFirstName
				.setSignatureElementPath("parameters:source.CustomerNameParameters/source.CustomerNameParameters.customer:source.Customer/source.Customer.firstName:java.lang.String");
		customerParameters.addParameter(attrFirstName);

		addDocumentation(attrFirstName, addresseeName, "COMPARISON", StringUtils.EMPTY,
				attrFirstName.getSignatureElementPath(), false);

		JavaParameter attrLastName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrLastName.setDataTypeName("String");
		attrLastName.setQualifiedDataTypeName("java.lang.String");
		attrLastName.setIdentifier("lastName");
		attrLastName.setQualifiedIdentifier("source.Customer.lastName");
		attrLastName
				.setSignatureElementPath("parameters:source.CustomerNameParameters/source.CustomerNameParameters.customer:source.Customer/source.Customer.lastName:java.lang.String");
		attrLastName.setDocumentationChanged(documentationChanged);
		customerParameters.addParameter(attrLastName);

		addDocumentation(attrLastName, addresseeName, "COMPARISON", StringUtils.EMPTY,
				attrLastName.getSignatureElementPath(), false);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		outputParameters.setIdentifier(StringUtils.EMPTY);
		outputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomer = createJavaParameterCustomer(addresseeName,
				documentationChanged, outputParameters, "Customer");

		addDocumentation(returnCustomer, addresseeName, "OBJECT", StringUtils.EMPTY,
				returnCustomer.getSignatureElementPath(), false);

		addDocumentation(returnCustomer.getComplexType().get(0), addresseeName,
				"ATTRIBUTE", "Won't be null, but could be an empty String",
				returnCustomer.getComplexType().get(0).getSignatureElementPath(), false);

		addDocumentation(returnCustomer.getComplexType().get(1), addresseeName,
				"ATTRIBUTE", "Won't be null, but could be an empty String",
				returnCustomer.getComplexType().get(1).getSignatureElementPath(), false);

		// Exception
		List<JavaParameters> exceptions = new ArrayList<JavaParameters>();

		JavaParameters ioExceptions = new JavaParameters(methodFindCustomerById,
				"Throws", Numerus.SINGULAR, false);
		ioExceptions.setIdentifier(StringUtils.EMPTY);
		ioExceptions.setQualifiedIdentifier(StringUtils.EMPTY);

		JavaParameter specialException = new JavaParameter(ioExceptions,
				Numerus.SINGULAR, false);
		specialException.setIdentifier("SpecialException");
		specialException.setDataTypeName("SpecialException");
		specialException.setQualifiedIdentifier("source.SpecialException");
		specialException.setQualifiedDataTypeName("source.SpecialException");
		specialException
				.setSignatureElementPath("source.SpecialException:source.SpecialException");
		specialException.setHasPublicAccessibleAttributes(true);

		addDocumentation(specialException, addresseeName, null, "In case of an error",
				specialException.getSignatureElementPath(), false);

		ioExceptions.addParameter(specialException);
		exceptions.add(ioExceptions);
		methodFindCustomerById.setExceptions(exceptions);

		return methodFindCustomerById;
	}

	public static JavaMethod createDocumentedAnyTestMethod(final String addresseeName,
			final TypeDeclaration customerServiceIntf, final boolean documentationChanged)
	{
		// Method
		final JavaMethod methodAnyTestMethod = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method", "None",
				Numerus.SINGULAR);
		final MethodDeclaration anyTestMethod = (MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(3);
		methodAnyTestMethod.setRefToASTNode(anyTestMethod);
		methodAnyTestMethod.setIdentifier("anyTestMethod");
		methodAnyTestMethod.setQualifiedIdentifier("anyTestMethod");
		methodAnyTestMethod.setDocumentationChanged(documentationChanged);

		addDocumentation(methodAnyTestMethod, addresseeName, "ACTION",
				"Test if a mixed Javadoc is correctly converted. And "
						+ StringUtils.NEW_LINE
						+ "if documentations are correct converted.", null, false);

		// Method's additional tags
		final TagElement tag = createTagElement(customerServiceIntf.getAST(),
				TagElement.TAG_THROWS, IllegalArgumentException.class.getSimpleName(),
				"In case of wrong parameter.");

		final List<TagElement> additionalTags = new ArrayList<TagElement>(1);
		additionalTags.add(tag);
		methodAnyTestMethod.setAdditionalTags(additionalTags);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodAnyTestMethod,
				"Parameters", Numerus.SINGULAR, false);
		inputParameters.setIdentifier(StringUtils.EMPTY);
		inputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodAnyTestMethod.setInputParameters(inputParameters);

		final JavaParameter param1 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param1.setDataTypeName("String");
		param1.setIdentifier("param1");
		param1.setQualifiedIdentifier("param1");
		param1.setQualifiedDataTypeName("java.lang.String");
		param1.setDocumentationChanged(documentationChanged);
		param1.setSignatureElementPath("param1:java.lang.String");
		inputParameters.addParameter(param1);

		final JavaParameter param2 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param2.setDataTypeName("int");
		param2.setIdentifier("param2");
		param2.setQualifiedIdentifier("param2");
		param2.setQualifiedDataTypeName("int");
		param2.setDocumentationChanged(documentationChanged);
		param2.setSignatureElementPath("param2:int");
		inputParameters.addParameter(param2);

		addDocumentation(param2, addresseeName, "OBJECT", "a number.",
				param2.getSignatureElementPath(), false);

		final JavaParameter param3 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param3.setDataTypeName("long");
		param3.setIdentifier("param3");
		param3.setQualifiedIdentifier("param3");
		param3.setQualifiedDataTypeName("long");
		param3.setDocumentationChanged(documentationChanged);
		param3.setSignatureElementPath("param3:long");
		inputParameters.addParameter(param3);

		addDocumentation(param3, addresseeName, null, "a long number.",
				param3.getSignatureElementPath(), false);

		final JavaParameter param4 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param4.setDataTypeName("float");
		param4.setIdentifier("param4");
		param4.setQualifiedIdentifier("param4");
		param4.setQualifiedDataTypeName("float");
		param4.setDocumentationChanged(documentationChanged);
		param4.setSignatureElementPath("param4:float");
		inputParameters.addParameter(param4);

		addDocumentation(
				param4,
				addresseeName,
				"ATTRIBUTE",
				"a float ({@link Float}) number. It is a floating point number; a number with a dot.",
				param4.getSignatureElementPath(), false);

		final JavaParameter param5 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param5.setDataTypeName("double");
		param5.setIdentifier("param5");
		param5.setQualifiedIdentifier("param5");
		param5.setQualifiedDataTypeName("double");
		param5.setDocumentationChanged(documentationChanged);
		param5.setSignatureElementPath("param5:double");
		inputParameters.addParameter(param5);

		addDocumentation(param5, addresseeName, null, "a double number.",
				param5.getSignatureElementPath(), false);

		final JavaParameter param6 = new JavaParameter(inputParameters, Numerus.SINGULAR,
				false);
		param6.setDataTypeName("String");
		param6.setIdentifier("param6");
		param6.setQualifiedIdentifier("param6");
		param6.setQualifiedDataTypeName("java.lang.String");
		param6.setDocumentationChanged(documentationChanged);
		param6.setSignatureElementPath("param6:java.lang.String");
		inputParameters.addParameter(param6);

		addDocumentation(param6, addresseeName, "ATTRIBUTE", "a very very long string.",
				param6.getSignatureElementPath(), false);

		// Return value
		final JavaParameters outputParameters = new JavaParameters(methodAnyTestMethod,
				"ReturnType", Numerus.SINGULAR, false);
		outputParameters.setIdentifier(StringUtils.EMPTY);
		outputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodAnyTestMethod.setOutputParameters(outputParameters);

		JavaParameter returnType = new JavaParameter(outputParameters, Numerus.PLURAL,
				false);
		returnType.setDataTypeName("List<Customer>");
		returnType.setQualifiedDataTypeName("java.util.List<source.Customer>");
		returnType.setIdentifier("List<Customer>");
		returnType.setQualifiedIdentifier("java.util.List<source.Customer>");
		returnType.setDocumentationChanged(documentationChanged);
		returnType
				.setSignatureElementPath("java.util.List<source.Customer>:java.util.List<source.Customer>");
		outputParameters.addParameter(returnType);

		addDocumentation(returnType, addresseeName, null, "a list.",
				returnType.getSignatureElementPath(), false);

		return methodAnyTestMethod;
	}

	/**
	 * 
	 * @param ast
	 * @param tagName
	 * @param qualifiedParamName
	 * @param docText
	 * @return
	 */
	private static TagElement createTagElement(final AST ast, final String tagName,
			final String qualifiedParamName, final String docText)
	{
		final TagElement tag = ast.newTagElement();

		if (!StringUtils.isBlank(tagName))
		{
			tag.setTagName(tagName);
		}

		@SuppressWarnings("unchecked")
		final List<ASTNode> fragments = tag.fragments();

		if (!StringUtils.isBlank(qualifiedParamName))
		{
			fragments.add(ast.newName(qualifiedParamName));
		}

		if (!StringUtils.isBlank(docText))
		{
			final TextElement text = ast.newTextElement();
			text.setText("In case of wrong parameter.");
			fragments.add(text);
		}

		return tag;
	}

	private static JavaParameter createJavaParameterCustomer(String addresseeName,
			boolean documentationChanged, JavaParameters outputParameters,
			String identifier)
	{
		JavaParameter returnCustomer = new JavaParameter(outputParameters,
				Numerus.SINGULAR, true);
		returnCustomer.setDataTypeName("Customer");
		returnCustomer.setQualifiedDataTypeName("source.Customer");
		returnCustomer.setIdentifier(identifier);
		returnCustomer.setQualifiedIdentifier("source.Customer");
		returnCustomer.setDocumentationChanged(documentationChanged);
		returnCustomer.setSignatureElementPath("source.Customer:source.Customer");
		outputParameters.addParameter(returnCustomer);

		JavaParameter returnCustomerFirstName = new JavaParameter(returnCustomer,
				Numerus.SINGULAR, false);
		returnCustomerFirstName.setDataTypeName("String");
		returnCustomerFirstName.setQualifiedDataTypeName("java.lang.String");
		returnCustomerFirstName.setIdentifier("firstName");
		returnCustomerFirstName.setQualifiedIdentifier("source.Customer.firstName");
		returnCustomerFirstName.setDocumentationChanged(documentationChanged);
		returnCustomerFirstName
				.setSignatureElementPath("source.Customer:source.Customer/source.Customer.firstName:java.lang.String");
		returnCustomer.addParameter(returnCustomerFirstName);

		JavaParameter returnCustomerLastName = new JavaParameter(returnCustomer,
				Numerus.SINGULAR, false);
		returnCustomerLastName.setDataTypeName("String");
		returnCustomerLastName.setQualifiedDataTypeName("java.lang.String");
		returnCustomerLastName.setIdentifier("lastName");
		returnCustomerLastName.setQualifiedIdentifier("source.Customer.lastName");
		returnCustomerLastName.setDocumentationChanged(documentationChanged);
		returnCustomerLastName
				.setSignatureElementPath("source.Customer:source.Customer/source.Customer.lastName:java.lang.String");
		returnCustomer.addParameter(returnCustomerLastName);
		return returnCustomer;
	}

	public static JavaMethod createEmptyDocumentedFindCustomerById(
			MethodDeclaration findCustByIdDecl, boolean documentationChanged)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method", null,
				Numerus.SINGULAR);
		methodFindCustomerById.setRefToASTNode(findCustByIdDecl);
		methodFindCustomerById.setIdentifier("findCustomersByName");
		methodFindCustomerById.setQualifiedIdentifier("findCustomersByName");
		methodFindCustomerById.setDocumentationChanged(documentationChanged);

		methodFindCustomerById
				.addDocpart(createEmptyDocumentation("findCustomersByName"));

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("NameParameters");
		parameterParameters.setIdentifier("parameters");
		parameterParameters.setQualifiedIdentifier("parameters");
		parameterParameters.setQualifiedDataTypeName("source.NameParameters");
		parameterParameters.setDocumentationChanged(documentationChanged);
		parameterParameters.addDocpart(createEmptyDocumentation("parameters"));
		inputParameters.addParameter(parameterParameters);

		// Attributes of NameParameters
		JavaParameter attrFirstName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrFirstName.setDataTypeName("String");
		attrFirstName.setQualifiedDataTypeName("java.lang.String");
		attrFirstName.setIdentifier("firstName");
		attrFirstName.setDocumentationChanged(documentationChanged);
		attrFirstName.addDocpart(createEmptyDocumentation("firstName"));
		parameterParameters.addParameter(attrFirstName);

		JavaParameter attrLastName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrLastName.setDataTypeName("String");
		attrLastName.setQualifiedDataTypeName("java.lang.String");
		attrLastName.setIdentifier("lastName");
		attrLastName.setDocumentationChanged(documentationChanged);
		attrLastName.addDocpart(createEmptyDocumentation("lastName"));
		parameterParameters.addParameter(attrLastName);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomerList = new JavaParameter(outputParameters,
				Numerus.PLURAL, false);
		returnCustomerList.setDataTypeName("List<Customer>");
		returnCustomerList.setQualifiedDataTypeName("java.util.List<source.Customer>");
		returnCustomerList.setDocumentationChanged(documentationChanged);
		returnCustomerList.addDocpart(createEmptyDocumentation(null));

		outputParameters.addParameter(returnCustomerList);

		// Exception
		List<JavaParameters> exceptions = new ArrayList<JavaParameters>();

		JavaParameters ioExceptions = new JavaParameters(methodFindCustomerById,
				"Exception", Numerus.SINGULAR, false);

		JavaParameter ioException = new JavaParameter(ioExceptions, Numerus.SINGULAR,
				false);
		ioException.setDocumentationChanged(documentationChanged);
		ioException.setIdentifier("IOException");
		// TODO
		// ioException.addDocpart(createEmptyDocumentation("IOException"));

		ioExceptions.addParameter(ioException);
		exceptions.add(ioExceptions);
		methodFindCustomerById.setExceptions(exceptions);

		return methodFindCustomerById;
	}

	public static JavaMethod createEmptyFindCustomerByName(
			MethodDeclaration findCustByIdDecl, boolean documentationChanged)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method", "None",
				Numerus.SINGULAR);
		methodFindCustomerById.setIdentifier("findCustomersByName");
		methodFindCustomerById.setQualifiedIdentifier("findCustomersByName");
		methodFindCustomerById.setDocumentationChanged(documentationChanged);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		inputParameters.setIdentifier(StringUtils.EMPTY);
		inputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("NameParameters");
		parameterParameters.setIdentifier("parameters");
		parameterParameters.setQualifiedIdentifier("parameters");
		parameterParameters.setQualifiedDataTypeName("source.NameParameters");
		parameterParameters.setDocumentationChanged(documentationChanged);
		parameterParameters.setSignatureElementPath("parameters:source.NameParameters");
		inputParameters.addParameter(parameterParameters);

		// Attributes of NameParameters
		JavaParameter attrFirstName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrFirstName.setDataTypeName("String");
		attrFirstName.setQualifiedDataTypeName("java.lang.String");
		attrFirstName.setIdentifier("firstName");
		attrFirstName.setQualifiedIdentifier("source.NameParameters.firstName");
		attrFirstName.setDocumentationChanged(documentationChanged);
		attrFirstName
				.setSignatureElementPath("parameters:source.NameParameters/source.NameParameters.firstName:java.lang.String");
		parameterParameters.addParameter(attrFirstName);

		JavaParameter attrLastName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrLastName.setDataTypeName("String");
		attrLastName.setQualifiedDataTypeName("java.lang.String");
		attrLastName.setIdentifier("lastName");
		attrLastName.setQualifiedIdentifier("source.NameParameters.lastName");
		attrLastName
				.setSignatureElementPath("parameters:source.NameParameters/source.NameParameters.lastName:java.lang.String");
		attrLastName.setDocumentationChanged(documentationChanged);
		parameterParameters.addParameter(attrLastName);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		outputParameters.setIdentifier(StringUtils.EMPTY);
		outputParameters.setQualifiedIdentifier(StringUtils.EMPTY);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomerList = new JavaParameter(outputParameters,
				Numerus.PLURAL, false);
		returnCustomerList.setDataTypeName("List<Customer>");
		returnCustomerList.setQualifiedDataTypeName("java.util.List<source.Customer>");
		returnCustomerList.setIdentifier("List<Customer>");
		returnCustomerList.setQualifiedIdentifier("java.util.List<source.Customer>");
		returnCustomerList.setDocumentationChanged(documentationChanged);
		returnCustomerList
				.setSignatureElementPath("java.util.List<source.Customer>:java.util.List<source.Customer>");
		outputParameters.addParameter(returnCustomerList);

		// Exception
		List<JavaParameters> exceptions = new ArrayList<JavaParameters>();

		JavaParameters ioExceptions = new JavaParameters(methodFindCustomerById,
				"Throws", Numerus.SINGULAR, false);
		ioExceptions.setQualifiedIdentifier(StringUtils.EMPTY);
		ioExceptions.setIdentifier(StringUtils.EMPTY);

		JavaParameter ioException = new JavaParameter(ioExceptions, Numerus.SINGULAR,
				false);
		ioException.setDataTypeName("IOException");
		ioException.setQualifiedDataTypeName("java.io.IOException");
		ioException.setSignatureElementPath("java.io.IOException:java.io.IOException");
		ioException.setIdentifier("IOException");
		ioException.setQualifiedIdentifier("java.io.IOException");

		ioExceptions.addParameter(ioException);
		exceptions.add(ioExceptions);
		methodFindCustomerById.setExceptions(exceptions);

		return methodFindCustomerById;
	}

	public static JavaMethod createNullDocumentedFindCustomerById(
			MethodDeclaration methodDeclaration, boolean documentationChanged)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method", null,
				Numerus.SINGULAR);
		methodFindCustomerById.setRefToASTNode(methodDeclaration);
		methodFindCustomerById.setIdentifier("findCustomersByName");

		methodFindCustomerById.addDocpart(createNullDocumentation("findCustomersByName"));

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("NameParameters");
		parameterParameters.setIdentifier("parameters");
		parameterParameters.setQualifiedIdentifier("parameters");
		parameterParameters.setQualifiedDataTypeName("source.NameParameters");
		parameterParameters.setDocumentationChanged(documentationChanged);
		parameterParameters.addDocpart(createNullDocumentation("parameters"));
		inputParameters.addParameter(parameterParameters);

		// Attributes of NameParameters
		JavaParameter attrFirstName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrFirstName.setDataTypeName("String");
		attrFirstName.setQualifiedDataTypeName("java.lang.String");
		attrFirstName.setIdentifier("firstName");
		attrFirstName.setDocumentationChanged(documentationChanged);
		attrFirstName.addDocpart(createNullDocumentation("firstName"));
		parameterParameters.addParameter(attrFirstName);

		JavaParameter attrLastName = new JavaParameter(parameterParameters,
				Numerus.SINGULAR, false);
		attrLastName.setDataTypeName("String");
		attrLastName.setQualifiedDataTypeName("java.lang.String");
		attrLastName.setIdentifier("lastName");
		attrLastName.setDocumentationChanged(documentationChanged);
		attrLastName.addDocpart(createNullDocumentation("lastName"));
		parameterParameters.addParameter(attrLastName);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomerList = new JavaParameter(outputParameters,
				Numerus.PLURAL, false);
		returnCustomerList.setDataTypeName("List<Customer>");
		returnCustomerList.setQualifiedDataTypeName("java.util.List<source.Customer>");
		returnCustomerList.setDocumentationChanged(documentationChanged);
		returnCustomerList.addDocpart(createNullDocumentation(null));

		outputParameters.addParameter(returnCustomerList);

		// Exception
		List<JavaParameters> exceptions = new ArrayList<JavaParameters>();

		JavaParameters ioExceptions = new JavaParameters(methodFindCustomerById,
				"Exception", Numerus.SINGULAR, false);

		JavaParameter ioException = new JavaParameter(ioExceptions, Numerus.SINGULAR,
				false);
		ioException.setDocumentationChanged(documentationChanged);
		ioException.setIdentifier("IOException");
		// TODO:
		// ioException.addDocpart(createNullDocumentation("IOException"));

		ioExceptions.addParameter(ioException);
		exceptions.add(ioExceptions);
		methodFindCustomerById.setExceptions(exceptions);

		return methodFindCustomerById;
	}

	/**
	 * Creates a new child-{@link Parameter} of the given <code>parameter</code> and a
	 * {@link Documentation} for it.
	 * 
	 * @param parameter
	 *            [DESTINATION] Used as parent-{@link SignatureElement} for the new
	 *            {@link Parameter}. This object is modified!
	 * @param addresseeName
	 *            [ATTRIBUTE] Used in the documentation
	 * @param thematicRoleName
	 *            [ATTRIBUTE] Used in the documentation
	 * @param docText
	 *            [ATTRIBUTE] Used in the documentation
	 * 
	 * @thematicgrid Putting Operations
	 */
	public static void addDocumentedParameter(Parameter parameter, String addresseeName,
			String thematicRoleName, String docText)
	{
		JavaParameter newParam = new JavaParameter(parameter, Numerus.PLURAL, false);
		newParam.setCategory("Parameters");
		newParam.setDataTypeName("List<Customer>");
		newParam.setQualifiedDataTypeName("java.util.List<source.Customer>");
		newParam.setIdentifier("records");

		parameter.addParameter(newParam);

		addDocumentation(newParam, addresseeName, thematicRoleName, docText,
				parameter.getSignatureElementPath(), false);
	}

	/**
	 * Adds a new {@link Documentation} with the given attributes to the given
	 * {@link SignatureElement}.
	 * 
	 * @param element
	 *            [DESTINATION]
	 * @param addresseeName
	 *            Â [ATTRIBUTE] Used in the new documentation
	 * @param thematicRole
	 *            [ATTRIBUTE] Used in the new documentation
	 * @param docText
	 *            [ATTRIBUTE] Used in the new documentation
	 * @param signatureElementPath
	 *            [ATTRIBUTE] Used in the new documentation
	 * @param describesErrorCase
	 *            [ATTRIBUTE] Used in the new documentation
	 * 
	 * @thematicgrid Putting Operations
	 */
	public static void addDocumentation(final SignatureElement element,
			final String addresseeName, final String thematicRole, final String docText,
			final String signatureElementPath, final boolean describesErrorCase)
	{
		List<Addressee> addressees = createReferenceAddresseeList(addresseeName);

		Map<Addressee, String> docs = createComparisonDocumentation(addresseeName);
		docs.put(addressees.get(0), docText);

		Documentation documentation = new Documentation();
		documentation.setAddresseeSequence(addressees);

		documentation.setThematicRole(DescribedItemUtils.findThematicRole(thematicRole));
		documentation.setSignatureElementIdentifier(signatureElementPath);
		documentation.setDocumentation(docs);
		documentation.setErrorCase(describesErrorCase);

		element.addDocpart(documentation);
	}

	/**
	 * Creates a new child-{@link Parameter} of the given <code>parameters</code> and a
	 * {@link Documentation} for it.
	 * 
	 * @param parameter
	 *            [DESTINATION] Used as parent-{@link SignatureElement} for the new
	 *            {@link Parameter}. This object is modified!
	 * @param addresseeName
	 *            [ATTRIBUTE] Used in the documentation
	 * @param thematicRoleName
	 *            [ATTRIBUTE] Used in the documentation
	 * @param docText
	 *            [ATTRIBUTE] Used in the documentation
	 * 
	 * @thematicgrid Putting Operations
	 */
	public static void addDocumentedParameter(Parameters parameters,
			String addresseeName, String thematicRoleName, String docText)
	{
		JavaParameter newParam = new JavaParameter(parameters, Numerus.PLURAL, false);
		newParam.setCategory("Parameters");
		newParam.setDataTypeName("List<Customer>");
		newParam.setQualifiedDataTypeName("java.util.List<source.Customer>");
		newParam.setQualifiedIdentifier("java.util.List<source.Customer>:java.util.List<source.Customer>");
		newParam.setIdentifier("records");

		parameters.addParameter(newParam);

		addDocumentation(newParam, addresseeName, thematicRoleName, docText,
				newParam.getSignatureElementPath(), false);
	}

	public static JavaMethod createFooMethod(MethodDeclaration getIdDecl,
			boolean documentationChanged, String addresseeName)
	{
		// Method
		JavaMethod methodFoo = new JavaMethod(SignatureElement.EMPTY_SIGNATURE_ELEMENT,
				"Method", null, Numerus.SINGULAR);
		methodFoo.setRefToASTNode(getIdDecl);
		methodFoo.setIdentifier("foo");
		methodFoo.setDocumentationChanged(documentationChanged);

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFoo, "Parameters",
				Numerus.SINGULAR, false);
		methodFoo.setInputParameters(inputParameters);

		JavaParameter customerParameter = createJavaParameterCustomer(addresseeName,
				documentationChanged, inputParameters, "customer");
		inputParameters.addParameter(customerParameter);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFoo, "ReturnType",
				Numerus.SINGULAR, false);
		methodFoo.setOutputParameters(outputParameters);

		return methodFoo;
	}

	public static JavaMethod createCheckMethod(MethodDeclaration getIdDecl,
			boolean documentationChanged, String addresseeName)
	{
		// Method
		JavaMethod methodCheck = new JavaMethod(SignatureElement.EMPTY_SIGNATURE_ELEMENT,
				"Method", null, Numerus.SINGULAR);
		methodCheck.setRefToASTNode(getIdDecl);
		methodCheck.setIdentifier("checkSomething");
		methodCheck.setDocumentationChanged(documentationChanged);

		addDocumentation(methodCheck, addresseeName, "RULE", "Check the beat.", null,
				false);

		return methodCheck;
	}

	public static JavaMethod createEmptyGetId(MethodDeclaration getIdDecl,
			boolean documentationChanged)
	{
		// Method
		JavaMethod methodFindCustomerById = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method", null,
				Numerus.SINGULAR);
		methodFindCustomerById.setRefToASTNode(getIdDecl);
		methodFindCustomerById.setIdentifier("get");
		methodFindCustomerById.setDocumentationChanged(documentationChanged);

		methodFindCustomerById
				.addDocpart(createEmptyDocumentation("findCustomersByName"));

		// Parameters
		JavaParameters inputParameters = new JavaParameters(methodFindCustomerById,
				"Parameters", Numerus.SINGULAR, false);
		methodFindCustomerById.setInputParameters(inputParameters);

		JavaParameter parameterParameters = new JavaParameter(inputParameters,
				Numerus.SINGULAR, true);
		parameterParameters.setDataTypeName("int");
		parameterParameters.setIdentifier("id");
		parameterParameters.setQualifiedDataTypeName("int");
		inputParameters.addParameter(parameterParameters);

		// Return value
		JavaParameters outputParameters = new JavaParameters(methodFindCustomerById,
				"ReturnType", Numerus.SINGULAR, false);
		methodFindCustomerById.setOutputParameters(outputParameters);

		JavaParameter returnCustomerList = new JavaParameter(outputParameters,
				Numerus.PLURAL, false);
		returnCustomerList.setDataTypeName("String");
		returnCustomerList.setQualifiedDataTypeName("java.lang.String");

		outputParameters.addParameter(returnCustomerList);

		return methodFindCustomerById;
	}

	public static JavaInterfaceArtifact createEmptyService(CompilationUnit cu,
			boolean documentationChanged)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, cu,
				Numerus.SINGULAR);
		result.setIdentifier("EmptyService.java");
		result.setQualifiedIdentifier(null);

		JavaInterface customerService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		TypeDeclaration customerServiceIntf = (TypeDeclaration) cu.types().get(0);
		customerService.setRefToASTNode(customerServiceIntf);

		List<JavaMethod> methods = new ArrayList<JavaMethod>();
		methods.add(createEmptyGetId((MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(0), documentationChanged));

		customerService.setOperations(methods);

		result.addInterface(customerService);

		return result;
	}

	public static JavaInterfaceArtifact createExampleService(CompilationUnit cu,
			boolean documentationChanged, String addressee)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, cu,
				Numerus.SINGULAR);
		result.setIdentifier("ExampleService.java");
		result.setQualifiedIdentifier(null);

		JavaInterface customerService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		TypeDeclaration customerServiceIntf = (TypeDeclaration) cu.types().get(0);
		customerService.setRefToASTNode(customerServiceIntf);

		List<JavaMethod> methods = new ArrayList<JavaMethod>();
		methods.add(createFooMethod((MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(0), documentationChanged, addressee));
		methods.add(createCheckMethod((MethodDeclaration) customerServiceIntf
				.bodyDeclarations().get(1), documentationChanged, addressee));

		customerService.setOperations(methods);

		result.addInterface(customerService);

		return result;
	}

	/**
	 * Creates an {@link JavaInterfaceArtifact} with one {@link JavaInterface} containing
	 * the result from {@link this#createDocumentedFindCustomerById(String)}.
	 * 
	 * @param addresseeName
	 *            [ATTRIBUTE]
	 * 
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static JavaInterfaceArtifact createCustomerService(String addresseeName,
			boolean documentationChanged, CompilationUnit cuCustomerService)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact", cuCustomerService,
				Numerus.SINGULAR);
		result.setIdentifier("CustomerService.java");
		result.setQualifiedIdentifier(null);

		JavaInterface customerService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		customerService.setIdentifier("CustomerService");
		customerService.setQualifiedIdentifier("CustomerService");
		TypeDeclaration customerServiceIntf = (TypeDeclaration) cuCustomerService.types()
				.get(0);
		customerService.setRefToASTNode(customerServiceIntf);

		List<JavaMethod> methods = new ArrayList<JavaMethod>();
		methods.add(createDocumentedFindCustomerByName(addresseeName,
				documentationChanged, customerServiceIntf));
		methods.add(createDocumentedFindCustomerByNameWithCustomerNames(addresseeName,
				customerServiceIntf, documentationChanged));
		methods.add(createDocumentedAnyTestMethod(addresseeName, customerServiceIntf,
				documentationChanged));

		customerService.setOperations(methods);

		result.addInterface(customerService);

		JavaInterface innerCustomerService = new JavaInterface(customerService,
				"Interface", Numerus.SINGULAR);
		innerCustomerService.setIdentifier("InnerCustomerService");
		innerCustomerService.setQualifiedIdentifier("InnerCustomerService");

		TypeDeclaration innerInterface = (TypeDeclaration) customerServiceIntf
				.bodyDeclarations().get(0);
		MethodDeclaration outerFindCustById = (MethodDeclaration) innerInterface
				.bodyDeclarations().get(0);

		List<JavaMethod> innerMethods = new ArrayList<JavaMethod>();
		innerMethods.add(createEmptyFindCustomerByName(outerFindCustById, false));
		innerCustomerService.setOperations(innerMethods);

		List<JavaInterface> innerInterfaces = new ArrayList<JavaInterface>();
		innerInterfaces.add(innerCustomerService);
		customerService.setInnerInterfaces(innerInterfaces);

		return result;
	}

	/**
	 * Creates an {@link JavaInterfaceArtifact} with one {@link JavaInterface} containing
	 * the result from {@link this#createDocumentedCheckInvariant(String, boolean,
	 * TypeDeclaration)}.
	 * 
	 * @param addresseeName
	 *            [ATTRIBUTE]
	 * 
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static JavaInterfaceArtifact createInvariantService(String addresseeName,
			CompilationUnit cuInvariantService, boolean documentationChanged)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, null,
				Numerus.SINGULAR);
		result.setIdentifier("InvariantService.java");
		result.setQualifiedIdentifier(null);
		result.setDocumentationChanged(documentationChanged);

		JavaInterface invariantService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		invariantService.setDocumentationChanged(documentationChanged);

		TypeDeclaration invariantServiceIntfc = (TypeDeclaration) cuInvariantService
				.types().get(0);
		invariantService.setRefToASTNode(invariantServiceIntfc);

		List<JavaMethod> methods = new ArrayList<JavaMethod>();
		methods.add(createDocumentedCheckInvariant(addresseeName, documentationChanged,
				invariantServiceIntfc));

		invariantService.setOperations(methods);

		result.addInterface(invariantService);

		return result;
	}

	/**
	 * Creates an {@link JavaInterfaceArtifact} with one {@link JavaInterface} containing
	 * the result from {@link this#createDocumentedTestInterface1(String, boolean,
	 * TypeDeclaration)}.
	 * 
	 * @param addressee
	 *            [ATTRIBUTE]
	 * @param cuInvariantService
	 * @param documentationChanged
	 * @param alternative
	 *            legal values are:
	 *            <ul>
	 *            <li>0: create method's documentation with role ACTION
	 *            <li>1: create method's documentation with role RULE
	 *            </ul>
	 * 
	 * @return [OBJECT]
	 * 
	 * @thematicgrid Creating Operations
	 */
	public static JavaInterfaceArtifact createTestInterface1(final Addressee addressee,
			final CompilationUnit cuInvariantService, final boolean documentationChanged,
			final int alternative)
	{
		final JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, null,
				Numerus.SINGULAR);
		result.setIdentifier("TestInterface1.java");
		result.setQualifiedIdentifier(null);
		result.setDocumentationChanged(documentationChanged);

		final JavaInterface testInterface1 = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		testInterface1.setDocumentationChanged(documentationChanged);

		final TypeDeclaration testInterface1Intfc = (TypeDeclaration) cuInvariantService
				.types().get(0);
		testInterface1.setRefToASTNode(testInterface1Intfc);

		final List<JavaMethod> methods = new ArrayList<JavaMethod>();

		switch (alternative)
		{
		case 0:
			methods.add(createDocumentedTestInterface1_Alt0(addressee,
					documentationChanged, testInterface1Intfc));
			break;
		case 1:
			methods.add(createDocumentedTestInterface1_Alt1(addressee,
					documentationChanged, testInterface1Intfc));
			break;
		default:
			throw new IllegalArgumentException("Illegal alternative \"" + alternative
					+ "\". Legal are: 0 and 1");
		}

		testInterface1.setOperations(methods);

		result.addInterface(testInterface1);

		return result;
	}

	private static JavaMethod createDocumentedTestInterface1_Alt0(
			final Addressee addressee, boolean documentationChanged,
			TypeDeclaration testInterface1)
	{
		final JavaMethod methodGetString = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method",
				"Getting Operations / Getter", Numerus.SINGULAR);
		final MethodDeclaration getStringMeth = (MethodDeclaration) testInterface1
				.bodyDeclarations().get(0);
		methodGetString.setRefToASTNode(getStringMeth);
		methodGetString.setIdentifier("getString");
		methodGetString.setDocumentationChanged(documentationChanged);

		TestUtils.addDocumentation(methodGetString, addressee, TestUtils.createSource(),
				"The Source", methodGetString.getIdentifier(), false);
		TestUtils.addDocumentation(methodGetString, addressee, TestUtils.createAction(),
				"The Action", methodGetString.getIdentifier(), false);

		// Return value
		final JavaParameters outputParameters = new JavaParameters(methodGetString,
				"ReturnType", Numerus.SINGULAR, false);
		methodGetString.setOutputParameters(outputParameters);

		final JavaParameter returnParam = new JavaParameter(outputParameters,
				Numerus.SINGULAR, false);
		returnParam.setDataTypeName("String");
		returnParam.setQualifiedDataTypeName("java.lang.String");
		returnParam.setDocumentationChanged(documentationChanged);
		outputParameters.addParameter(returnParam);

		TestUtils.addDocumentation(returnParam, addressee, null, "String",
				returnParam.getSignatureElementPath(), false);

		return methodGetString;
	}

	private static JavaMethod createDocumentedTestInterface1_Alt1(
			final Addressee addressee, boolean documentationChanged,
			TypeDeclaration testInterface1)
	{
		final JavaMethod methodGetString = new JavaMethod(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Method",
				"Checking Operations", Numerus.SINGULAR);
		final MethodDeclaration getStringMeth = (MethodDeclaration) testInterface1
				.bodyDeclarations().get(0);
		methodGetString.setRefToASTNode(getStringMeth);
		methodGetString.setIdentifier("getString");
		methodGetString.setDocumentationChanged(documentationChanged);

		TestUtils.addDocumentation(methodGetString, addressee, TestUtils.createSource(),
				"The Source", methodGetString.getIdentifier(), false);
		TestUtils.addDocumentation(methodGetString, addressee, TestUtils.createRule(),
				"The rule", methodGetString.getIdentifier(), false);

		// Return value
		final JavaParameters outputParameters = new JavaParameters(methodGetString,
				"ReturnType", Numerus.SINGULAR, false);
		methodGetString.setOutputParameters(outputParameters);

		final JavaParameter returnParam = new JavaParameter(outputParameters,
				Numerus.SINGULAR, false);
		returnParam.setDataTypeName("String");
		returnParam.setQualifiedDataTypeName("java.lang.String");
		returnParam.setDocumentationChanged(documentationChanged);
		outputParameters.addParameter(returnParam);

		TestUtils.addDocumentation(returnParam, addressee, null, "String",
				returnParam.getSignatureElementPath(), false);

		return methodGetString;
	}

	private static Documentation createEmptyDocumentation(String identifier)
	{
		Documentation doc = new Documentation();

		doc.setAddresseeSequence(new ArrayList<Addressee>());
		doc.setDocumentation(new HashMap<Addressee, String>());
		doc.setSignatureElementIdentifier(identifier);
		doc.setThematicRole(null);

		return doc;
	}

	private static Documentation createNullDocumentation(String identifier)
	{
		Documentation doc = new Documentation();

		doc.setAddresseeSequence(null);
		doc.setDocumentation(null);
		doc.setSignatureElementIdentifier(identifier);
		doc.setThematicRole(null);

		return doc;
	}

	public static JavaInterfaceArtifact createEmptyCustomerService(CompilationUnit cu,
			boolean documentationChanged)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, null,
				Numerus.SINGULAR);
		result.setIdentifier("EmptyService.java");
		result.setQualifiedIdentifier(null);

		result.addDocpart(createEmptyDocumentation(StringUtils.EMPTY));

		JavaInterface customerService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);

		TypeDeclaration customerServiceIntf = (TypeDeclaration) cu.types().get(0);
		customerService.setRefToASTNode(customerServiceIntf);
		customerService.setDocumentationChanged(documentationChanged);

		{
			customerService.addDocpart(createEmptyDocumentation("CustomerService"));

			List<JavaMethod> methods = new ArrayList<JavaMethod>();
			MethodDeclaration outerFindCustById = (MethodDeclaration) customerServiceIntf
					.bodyDeclarations().get(1);
			methods.add(createEmptyDocumentedFindCustomerById(outerFindCustById,
					documentationChanged));

			customerService.setOperations(methods);

			result.addInterface(customerService);
		}

		// Prepare the inner interface
		{
			JavaInterface innerCustomerService = new JavaInterface(result, "Interface",
					Numerus.SINGULAR);
			TypeDeclaration innerCustomerServiceDecl = (TypeDeclaration) customerServiceIntf
					.bodyDeclarations().get(0);
			innerCustomerService.setRefToASTNode(innerCustomerServiceDecl);

			innerCustomerService
					.addDocpart(createEmptyDocumentation("InnerCustomerService"));
			List<JavaMethod> innerMethods = new ArrayList<JavaMethod>();
			MethodDeclaration innerFindCustById = (MethodDeclaration) innerCustomerServiceDecl
					.bodyDeclarations().get(0);
			innerMethods.add(createEmptyDocumentedFindCustomerById(innerFindCustById,
					documentationChanged));
			innerCustomerService.setOperations(innerMethods);

			List<JavaInterface> innerInterfaces = new ArrayList<JavaInterface>();
			innerInterfaces.add(innerCustomerService);
			customerService.setInnerInterfaces(innerInterfaces);
		}

		return result;
	}

	public static JavaInterfaceArtifact createNullCustomerService(CompilationUnit cu,
			boolean documentationChanged)
	{
		JavaInterfaceArtifact result = new JavaInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, StringUtils.EMPTY, null,
				Numerus.SINGULAR);
		result.setIdentifier("CustomerService.java");
		result.setQualifiedIdentifier(null);

		result.addDocpart(createNullDocumentation(StringUtils.EMPTY));

		JavaInterface customerService = new JavaInterface(result, "Interface",
				Numerus.SINGULAR);
		TypeDeclaration customerServiceIntf = (TypeDeclaration) cu.types().get(0);
		customerService.setRefToASTNode(customerServiceIntf);
		customerService.setDocumentationChanged(documentationChanged);
		{
			customerService.addDocpart(createNullDocumentation("CustomerService"));

			List<JavaMethod> methods = new ArrayList<JavaMethod>();
			MethodDeclaration outerFindCustById = (MethodDeclaration) customerServiceIntf
					.bodyDeclarations().get(1);

			methods.add(createNullDocumentedFindCustomerById(outerFindCustById,
					documentationChanged));
			customerService.setOperations(methods);

			result.addInterface(customerService);
		}

		// Prepare the inner interface
		{
			JavaInterface innerCustomerService = new JavaInterface(result, "Interface",
					Numerus.SINGULAR);
			TypeDeclaration innerCustomerServiceDecl = (TypeDeclaration) customerServiceIntf
					.bodyDeclarations().get(0);
			innerCustomerService.setRefToASTNode(innerCustomerServiceDecl);
			innerCustomerService.setDocumentationChanged(documentationChanged);
			innerCustomerService
					.addDocpart(createNullDocumentation("InnerCustomerService"));
			List<JavaMethod> innerMethods = new ArrayList<JavaMethod>();
			MethodDeclaration innerFindCustById = (MethodDeclaration) innerCustomerServiceDecl
					.bodyDeclarations().get(0);
			innerMethods.add(createNullDocumentedFindCustomerById(innerFindCustById,
					documentationChanged));
			innerCustomerService.setOperations(innerMethods);

			List<JavaInterface> innerInterfaces = new ArrayList<JavaInterface>();
			innerInterfaces.add(innerCustomerService);
			customerService.setInnerInterfaces(innerInterfaces);
		}

		return result;
	}

	public static List<Documentation> createDocsForParseMethod(String addresseeName,
			String signatureElementIdentifier)
	{
		List<Addressee> addressees = ServiceManager.getInstance().getPersistenceService()
				.loadConfiguredAddressees();
		List<ThematicRole> roles = ServiceManager.getInstance().getPersistenceService()
				.loadThematicRoles();
		Addressee addressee = AddresseeUtils.findByName(addresseeName, addressees);

		List<Addressee> addresseeSequence = new ArrayList<Addressee>();
		addresseeSequence.add(addressee);

		List<Documentation> result = new ArrayList<Documentation>();

		Map<Addressee, String> docTextAction = new HashMap<Addressee, String>();
		docTextAction
				.put(addressee,
						"Reads the java- and javadoc code from the given <b>file and"
								+ JavaTestUtils.NEW_LINE
								+ "creates</b> the returned {@link JavaInterfaceArtifact} from it."
								+ JavaTestUtils.NEW_LINE + "Escape Test: Ö");
		Documentation docAction = new Documentation();
		docAction.setAddresseeSequence(addresseeSequence);
		docAction.setDocumentation(docTextAction);
		docAction.setThematicRole(ThematicRoleUtils.findRoleByName(
				ThematicRoleConstants.MANDATORY_ROLE_ACTION, roles));
		result.add(docAction);

		Map<Addressee, String> docTextSourceFormat = new HashMap<Addressee, String>();
		docTextSourceFormat.put(addressee,
				"Java and Javadoc according to their current specifications:"
						+ JavaTestUtils.NEW_LINE + JavaTestUtils.NEW_LINE
						+ "<a href=\"http://docs.oracle.com/javase/specs/\">Java</a>"
						+ JavaTestUtils.NEW_LINE + "Javadoc");
		Documentation docSourceFormat = new Documentation();
		docSourceFormat.setAddresseeSequence(addresseeSequence);
		docSourceFormat.setDocumentation(docTextSourceFormat);
		docSourceFormat.setThematicRole(ThematicRoleUtils.findRoleByName("SOURCE_FORMAT",
				roles));
		result.add(docSourceFormat);

		Map<Addressee, String> docTextInstrument1 = new HashMap<Addressee, String>();
		docTextInstrument1
				.put(addressee,
						"To parse the Java and Javadoc code, the parser provided by the Eclipse Java Development Tools is used.");
		Documentation docSourceInstrument1 = new Documentation();
		docSourceInstrument1.setAddresseeSequence(addresseeSequence);
		docSourceInstrument1.setDocumentation(docTextInstrument1);
		docSourceInstrument1.setThematicRole(ThematicRoleUtils.findRoleByName(
				"INSTRUMENT", roles));
		result.add(docSourceInstrument1);

		Map<Addressee, String> docTextInstrument2 = new HashMap<Addressee, String>();
		docTextInstrument2
				.put(addressee,
						"iDocIt! supports two different representations of thematicgrids in Javadoc:"
								+ JavaTestUtils.NEW_LINE
								+ JavaTestUtils.NEW_LINE
								+ "The simplified version is very compact, but supports only the addressee \"Developer\"."
								+ JavaTestUtils.NEW_LINE
								+ "The complex version supports all addressees, but uses a lot of HTML-code.");
		Documentation docSourceInstrument2 = new Documentation();
		docSourceInstrument2.setAddresseeSequence(addresseeSequence);
		docSourceInstrument2.setDocumentation(docTextInstrument2);
		docSourceInstrument2.setThematicRole(ThematicRoleUtils.findRoleByName(
				"INSTRUMENT", roles));
		result.add(docSourceInstrument2);

		return result;
	}
}
