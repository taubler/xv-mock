var vertx = require('vertx-js/vertx');
var eb = require("vertx-js/event_bus");

console.log("EB is " + eb);

eb.registerHandler('my.address', function(message, replier) {
  console.log('Received tag: ' + message);
  replier("Reply");
});