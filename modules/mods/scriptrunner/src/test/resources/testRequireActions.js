var actions = require('actions');

function test() {
	if (actions.ActionParameters && actions.BehaviourParameters) {
		return "OK";
	}
	return "FAIL";
}