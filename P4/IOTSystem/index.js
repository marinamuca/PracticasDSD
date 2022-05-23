const HttpServer = require("./httpServer.js");
const MongoDBServer = require("./mongodbServer.js");

var mongoDBServer = new MongoDBServer("mongodb://localhost:27017/","DSDP4");
var httpServer = new HttpServer(mongoDBServer, 9090);
// var socketServer = new SockerIOServer(httpServer);


mongoDBServer.launch();
httpServer.launch();
// socketServer.launch();