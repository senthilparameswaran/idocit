function def() {
	return true;
}

function isSingular(role) {
	return !isPlural(role);
}

function isPlural(role) {
	checkNotNull(role);
	
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
	checkNotNull(role);
	
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
	checkNotNull(role);
	
	if(thematicRoleContexts != null){
		for(var i = 0; i < thematicRoleContexts.size(); i++){
			var thematicRoleContext = thematicRoleContexts.get(i);
			
			if(thematicRoleContext.role.name == role){
				return true;
			}
		}
	}

	return false;

}

function info() {
	for (var attrib in thematicRoleContexts) {
		println('Found attrib ' + attrib);
	}
	return true;
}

function checkNotNull(object){
	if(object == null){
		throw "IllegalArgumentException: each predicate expects a value as parameter and not null!";
	}
}