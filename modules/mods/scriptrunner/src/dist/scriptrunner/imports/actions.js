function ActionBehaviourParameters(a) {
	function log(message) {
		java.util.logging.Logger.getLogger('script.actions').info(message && message.toString() || 'null');
	}
	
	this.args = [].slice.call(a);
	//log(this.args);
}

ActionBehaviourParameters.prototype.getTile = function() {
	var args = this.args;
	var types = this.types;
	var isAction = !!this.action;
	
	if (args.length === 4 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number') {
		// public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		// public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile) {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			tile : args[3]
		};
	} else if (!isAction && args.length === 5 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number' && types[4] === 'number') {
		// public List<ActionEntry> getBehavioursFor(@Nonnull final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			tile : args[3],
			dir : args[4]
		};
	} else if (isAction && this.activeItem && args.length === 5 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number' && types[4] === 'number') {
		// public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			heightOffset : args[3],
			tile : args[4]
		};
	} else if (isAction && args.length === 5 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number' && types[4] === 'number') {
		// boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir, short num, float counter);
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			tile : args[3],
			dir : args[4]
		};
	} else if (args.length === 5 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'boolean'  && types[4] === 'number') {
		// public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		// public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			corner: args[3],
			tile : args[4]
		};
	} else if (isAction && args.length === 6 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number' && types[4] === 'number' && types[5] === 'number') {
		// boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, int dir, short num, float counter);
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			heightOffset : args[3],
			tile : args[4],
			dir : args[5]
		};
	} else if (args.length === 6 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'boolean'  && types[4] === 'number' && types[5] === 'number') {
		// public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		// public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			corner: args[3],
			tile : args[4],
			heightOffset : args[5]
		};
	} else if (args.length === 6 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && types[3] === 'number' && args[4] instanceof com.wurmonline.mesh.Tiles.TileBorderDirection && types[5] === 'number') {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			heightOffset : args[3],
			dir: args[4],
			borderId: args[5]
		};
	} else if (args.length === 5 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && args[3] instanceof com.wurmonline.mesh.Tiles.TileBorderDirection && types[4] === 'number') {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			dir: args[3],
			borderId: args[4]
		};
	} else if (args.length === 6 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && args[3] instanceof com.wurmonline.mesh.Tiles.TileBorderDirection && types[4] === 'boolean' && types[5] === 'number') {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			dir: args[3],
			border: args[4],
			heightOffset: args[5]
		};
	}
}

ActionBehaviourParameters.prototype.getItem = function() {
	var args = this.args;
	var types = this.types;

	if (args.length === 1 && args[0] instanceof com.wurmonline.server.items.Item) {
		return {
			item : args[0]
		};
	}
}

ActionBehaviourParameters.prototype.getWound = function() {
	var args = this.args;
	var types = this.types;

	if (args.length === 1 && args[0] instanceof com.wurmonline.server.bodys.Wound) {
		return {
			wound : args[0]
		};
	}
}

ActionBehaviourParameters.prototype.getCreature = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 1 && args[0] instanceof com.wurmonline.server.creatures.Creature) {
		return {
			creature : args[0]
		};
	}
}

ActionBehaviourParameters.prototype.getWall = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 1 && args[0] instanceof com.wurmonline.server.structures.Wall) {
		return {
			wall : args[0]
		};
	}
}

ActionBehaviourParameters.prototype.getFence = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 1 && args[0] instanceof com.wurmonline.server.structures.Fence) {
		return {
			fence : args[0]
		};
	}
}

ActionBehaviourParameters.prototype.getFloor = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 2 && types[0] === 'boolean' && args[1] instanceof com.wurmonline.server.structures.Floor) {
		return {
			onSurface : args[0],
			floor : args[1]
		};
	}
}

ActionBehaviourParameters.prototype.getBridgePart = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 2 && types[0] === 'boolean' && args[1] instanceof com.wurmonline.server.structures.BridgePart) {
		return {
			onSurface : args[0],
			bridgePart : args[1]
		};
	}
}

ActionBehaviourParameters.prototype.getBorder = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 6 && types[0] === 'number' && types[1] === 'number' && types[2] === 'boolean' && args[3] instanceof com.wurmonline.mesh.Tiles.TileBorderDirection && types[4] === 'boolean' && types[5] === 'number') {
		return {
			tilex : args[0],
			tiley : args[1],
			onSurface : args[2],
			dir : args[3],
			border : args[4],
			heightOffset : args[5]
		}; 
	}
}

ActionBehaviourParameters.prototype.getPlanet = function() {
	var args = this.args;
	var types = this.types;
	
	if (args.length === 1 && types[0] === 'number') {
		return {
			planet : args[0]
		};
	}
}

function BehaviourParameters(a) {
	ActionBehaviourParameters.call(this, a);
	
	function log(message) {
		java.util.logging.Logger.getLogger('script.actions.behaviour').info(message && message.toString() || 'null');
	}
	
	var args = this.args;
	this.performer = args.shift();
	
	if (args.length && args[0] instanceof com.wurmonline.server.items.Item && args.length > 1) {
		this.activeItem = args.shift();
	}
	
	this.types = args.map(function(arg) { return typeof arg; });
	//log(this.types);
}
BehaviourParameters.prototype = Object.create(ActionBehaviourParameters.prototype);
BehaviourParameters.prototype.constructor = BehaviourParameters;

function ActionParameters(a) {
	ActionBehaviourParameters.call(this, a);
	
	function log(message) {
		java.util.logging.Logger.getLogger('script.actions.action').info(message && message.toString() || 'null');
	}
	
	var args = this.args;
	this.action = args.shift();
	this.performer = args.shift();
	this.counter = args.pop();
	this.num = args.pop();
	
	if (args.length && args[0] instanceof com.wurmonline.server.items.Item && args.length > 1) {
		this.activeItem = args.shift();
	}
	
	this.types = args.map(function(arg) { return typeof arg; });
	//log(this.types);
}
ActionParameters.prototype = Object.create(ActionBehaviourParameters.prototype);
ActionParameters.prototype.constructor = ActionParameters;

module = (typeof module === 'undefined') ? {} : module;
module.exports = {
		ActionParameters : ActionParameters,
		BehaviourParameters : BehaviourParameters
};

