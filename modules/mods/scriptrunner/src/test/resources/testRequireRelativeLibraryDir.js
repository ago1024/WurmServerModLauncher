//load('src/dist/scriptrunner/imports/jvm-npm.js');
var data = require('depend1');

function test() {
	if (data && data.depend1 === 'depend1' && data.depend2 == 'depend2') {
		return 'OK';
	}
	return 'FAIL';
}