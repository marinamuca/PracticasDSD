const HttpServer = require("./httpServer.js");
const DBServer = require("./dbServer.js");

var httpServer = new HttpServer();
var dbServer = new DBServer("mongodb://localhost:27017/","DSDP4");

httpServer.launch();
dbServer.launch();