const HttpServer = require("./httpServer.js");
const mongoDBServer = require("./mongodbServer.js");

var httpServer = new HttpServer();
var mongoDBServer = new DBServer("mongodb://localhost:27017/","DSDP4");

httpServer.launch();
mongoDBServer.launch();