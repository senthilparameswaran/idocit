/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
package de.akra.idocit.ui.composites.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pocui.core.actions.EmptyActionConfiguration;
import org.pocui.core.resources.EmptyResourceConfiguration;
import org.pocui.swt.composites.AbsComposite;
import org.pocui.swt.composites.ICompositeFactory;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.SignatureElement;
import de.akra.idocit.core.structure.ThematicRole;
import de.akra.idocit.core.structure.impl.TestInterface;
import de.akra.idocit.core.structure.impl.TestInterfaceArtifact;
import de.akra.idocit.core.structure.impl.TestOperation;
import de.akra.idocit.core.structure.impl.TestParameter;
import de.akra.idocit.core.structure.impl.TestParameters;
import de.akra.idocit.ui.composites.EditArtifactDocumentationComposite;
import de.akra.idocit.ui.composites.EditArtifactDocumentationCompositeSelection;

/**
 * Factory for {@link EditArtifactDocumentationComposite}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class EditArtifactDocumentationCompositeFactory
		implements
		ICompositeFactory<EmptyActionConfiguration, EmptyResourceConfiguration, EditArtifactDocumentationCompositeSelection>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbsComposite<EmptyActionConfiguration, EmptyResourceConfiguration, EditArtifactDocumentationCompositeSelection> createComposite(
			Composite pvParent)
	{
		EditArtifactDocumentationComposite editArtifactComp = new EditArtifactDocumentationComposite(
				pvParent, SWT.NONE);
		EditArtifactDocumentationCompositeSelection editArtifactCompSelection = new EditArtifactDocumentationCompositeSelection();

		InterfaceArtifact artifact = createInterfaceArtifact();
		artifact.addInterface(createInterfaceArtifact().getInterfaces().get(0));

		editArtifactCompSelection.setInterfaceArtifact(artifact);
		
		List<Addressee> addresseeList = new ArrayList<Addressee>();
		// Here we have to use the same roles as returned by DocumentationTestFactory.createDocumentation().
		Addressee addresseeDeveloper = new Addressee("Developer");
		Addressee addresseeArchitect = new Addressee("Tester");
		Addressee addresseeProjectManager = new Addressee("Manager");
		
		addresseeList.add(addresseeDeveloper);
		addresseeList.add(addresseeArchitect);
		addresseeList.add(addresseeProjectManager);
		
		List<ThematicRole> thematicRoleList = new ArrayList<ThematicRole>();
		ThematicRole roleAGENT = new ThematicRole("AGENT");
		ThematicRole roleACTION = new ThematicRole("ACTION");
		
		thematicRoleList.add(roleAGENT);
		thematicRoleList.add(roleACTION);

		
		editArtifactCompSelection.setAddresseeList(addresseeList);
		editArtifactCompSelection.setThematicRoleList(thematicRoleList);

		editArtifactComp.setSelection(editArtifactCompSelection);

		return editArtifactComp;
	}

	private InterfaceArtifact createInterfaceArtifact()
	{
		InterfaceArtifact artifact = new TestInterfaceArtifact(
				SignatureElement.EMPTY_SIGNATURE_ELEMENT, "Artifact");
		artifact.setIdentifier("test.wsdl");

		List<Interface> interfaceList = new Vector<Interface>();
		Interface interf = new TestInterface(artifact, "PortType") {
		};
		interf.setIdentifier("CustomerService");
		interfaceList.add(interf);

		List<Operation> operations = new Vector<Operation>();
		Operation op = new TestOperation(interf, "Operation");
		op.setIdentifier("find");
		operations.add(op);
		interf.setOperations(operations);

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage");
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part");
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		paramCust.setQualifiedDataTypeName("ns.Customer");
		paramCust.setSignatureElementPath("findIn.Cust(Customer)");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "");
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramCust.setQualifiedDataTypeName("int");
		paramId.setSignatureElementPath("findIn.Cust(Customer).id(int)");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "");
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramNameIn.setSignatureElementPath("findIn.Cust(Customer).name(String)");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new TestParameters(op, "OutputMessage");
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new TestParameter(outputParameters, "Part");
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		paramCust.setQualifiedDataTypeName("ns.Customer");
		paramCustOut.setSignatureElementPath("findOut.Cust(Customer)");
		outputParameters.addParameter(paramCustOut);

		Parameter paramIdOut = new TestParameter(paramCustOut, "");
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setSignatureElementPath("findOut.Cust(Customer).id(int)");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "");
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramCust.setQualifiedDataTypeName("java.lang.String");
		paramNameOut.setSignatureElementPath("findOut.Cust(Customer).name(String)");
		paramCustOut.addParameter(paramNameOut);

		Parameter test1 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "");
		test1.setIdentifier("id");
		test1.setDataTypeName("int");
		test1.setSignatureElementPath("findOut.Cust(Customer).id(int)");

		Parameter test2 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "");
		test2.setIdentifier("id");
		test2.setDataTypeName("int");
		test2.setSignatureElementPath("findOu2.Cust(Customer).id(int)");

		artifact.setInterfaces(interfaceList);

		return artifact;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmptyResourceConfiguration getResourceConfiguration()
	{
		return EmptyResourceConfiguration.getInstance();
	}
}