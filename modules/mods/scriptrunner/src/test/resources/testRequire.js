//load('src/dist/scriptrunner/imports/jvm-npm.js');
var data = require('./module');

function test() {
	if (data && data.test === 'test') {
		return 'OK';
	}
	return 'FAIL';
}