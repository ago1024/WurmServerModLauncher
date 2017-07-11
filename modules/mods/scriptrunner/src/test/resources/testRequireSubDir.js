//load('src/dist/scriptrunner/imports/jvm-npm.js');
var data = require('./logdata/lib/main');

function test() {
	if (data && data.test === 'test') {
		return 'OK';
	}
	return 'FAIL';
}