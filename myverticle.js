var eb = vertx.eventBus();

console.log("EB is " + eb);

eb.consumer('my.address', function(msg) {
	  console.log("Received message: " + msg.body());
	  var ret = '{"statusCode": 201, "headers":{"foo":"bar"}';
	  ret = ret + ', "cookies":[{"name":"type", "value":"chocolate-chip", "path":"/"}]}';
	  msg.reply(ret);
	});