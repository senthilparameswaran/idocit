function defaultRule(role) {
	return true;
}

function isSingular(role) {
	println('isSingular() is not implemented yet!');
	return true;
}

function isPlural(role) {
	//First check the input-parameters:
	for (param in operation.inputParameters.parameters) {
		
	}
	//Next are the output-parameters:
	for (param in operation.inputParameters.parameters) {
		
	}

	//And eventually we're walking up the tree until we find the root:
	while ((parent = operation.parent) != SignatureElement.EMPTY_SIGNATURE_ELEMENT) {
		
	}
	//foreach param : operation.getInputParameters().getParameters()
	// foreach documentation : param.getDocumentations()
	//  ThematicRole role = documentation.getThematicRole()
	//  if (role.getNumerus() == Numerus.PLURAL) return true
	// //Done descending tree - next: Ascending.
	//while (SigElem parent = operation.getParent() != SignatureElement.EMPTY_SIGNATURE_ELEMENT)
	// foreach documentation : parent.getDocumentation();
	//  ThematicRole role = documentation.getThematicRole()
	//  if (role.getNumerus() == Numerus.PLURAL) return true
	return true;
}

function hasAttributes(role) {
	println('hasAttributes() is not implemented yet!');
	return true;
}

function exists(role) {
	println('exists() is not implemented yet!');
	return true;
}

function info() {
	for (var attrib in operation) {
		println('Found attrib ' + attrib);
	}
	return true;
}
