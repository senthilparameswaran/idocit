function defaultRule() {
	return true;
}

function isSingular(role) {
	if(thematicRoleContexts != null){
		for(var i = 0; i < thematicRoleContexts.size(); i++){
			var thematicRoleContext = thematicRoleContexts.get(i);
			
			if((thematicRoleContext.role.name == role) && (thematicRoleContext.getNumerus().name() == "SINGULAR")){
				return true;
			}
		}
	}

	return false;
}

function isPlural(role) {
	if(thematicRoleContexts != null){
		for(var i = 0; i < thematicRoleContexts.size(); i++){
			var thematicRoleContext = thematicRoleContexts.get(i);
			
			if((thematicRoleContext.role.name == role) && (thematicRoleContext.getNumerus().name() == "PLURAL")){
				return true;
			}
		}
	}

	return false;
}

function hasAttributes(role) {
	if(thematicRoleContexts != null){
		for(var i = 0; i < thematicRoleContexts.size(); i++){
			var thematicRoleContext = thematicRoleContexts.get(i);
			
			if((thematicRoleContext.role.name == role) && thematicRoleContext.hasPulicAccessableAttributes()){
				return true;
			}
		}
	}

	return false;
}

function exists(role) {
	println('exists() is not implemented yet!');
	return true;
}

function info() {
	for (var attrib in thematicRoleContexts) {
		println('Found attrib ' + attrib);
	}
	return true;
}
